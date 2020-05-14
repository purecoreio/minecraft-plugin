package io.purecore.mcplugin.spigot;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import io.purecore.api.Core;
import io.purecore.api.exception.ApiException;
import io.purecore.api.exception.CallException;
import io.purecore.api.instance.Instance;
import io.purecore.api.instance.Settings;
import io.purecore.api.punishment.Offence;
import io.purecore.mcplugin.spigot.events.Chat;
import io.purecore.mcplugin.spigot.events.Sessions;
import io.purecore.mcplugin.spigot.tasks.MarkPending;
import io.purecore.mcplugin.util.Logging;
import io.purecore.mcplugin.util.Title;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;
import java.util.logging.Level;

public class Main extends JavaPlugin implements PluginMessageListener {

    // setup
    public static Player waitingForKey = null;

    // runtime pending values

    public static int repeaterId;
    public static List<Inventory> punishmentGUIs = new ArrayList<Inventory>();
    public static Map<Inventory, List<io.purecore.api.user.Player>> playerElements = new HashMap<Inventory, List<io.purecore.api.user.Player>>();
    public static Map<Inventory, List<Offence>> selectedOffences = new HashMap<Inventory, List<Offence>>();


    // keys

    public static FileConfiguration keys;
    public static File keysFile;
    private static boolean firstStart = true;

    // instance

    public static Plugin plugin;
    public static Core core;
    public static Instance instance;
    public static Settings settings;

    // bungee
    public static boolean bungee = false;

    // task
    static BukkitTask checkPendingCommands = null;

    // pu

    @Override
    public void onEnable() {

        super.onEnable();

        plugin = this;

        this.getServer().getMessenger().registerIncomingPluginChannel(this,"purecore:setup",this);

        Boolean configBungee = false;
        try {
            configBungee = this.getServer().spigot().getConfig().getBoolean("settings.bungeecord");
        } catch (NullPointerException e){
            Logging.logError(this.getLogger(), Logging.Class.STARTUP,"Couldn't check if the server is running under a bungeecord instance, is your spigot.yml missing or corrupted?");
        }

        if(!this.getServer().getOnlineMode() && !configBungee){
            Logging.logError(this.getLogger(), Logging.Class.STARTUP,"Your server is running under an offline mode server and doesn't have the bungeecord setting activated. You need to enable bungeecord in offline mode servers if  you want to use purecore, as we only accept data from verified player uuids");
            this.getServer().getPluginManager().disablePlugin(this);
        } else if(this.getServer().getOnlineMode()){
            Logging.logInfo(this.getLogger(),Logging.Class.STARTUP, "This server is running on an online mode server, executing startup procedure");
            this.initialStartup();
        } else if(!this.getServer().getOnlineMode() && configBungee){
            bungee=true;
            Logging.logInfo(this.getLogger(),Logging.Class.STARTUP, "This server is part of a bungeecord network. Install purecore on your bungeecord instance. No sessions will be created from here, as all the session managment is handled by your proxy in this case");
            this.initialStartup();
        }

    }

    private void initialStartup(){

        keysFile = new File(getDataFolder(), "keys.yml");
        if (!keysFile.exists()) {
            boolean created = keysFile.getParentFile().mkdirs();
            saveResource("keys.yml", false);
        }

        saveDefaultConfig();

        Objects.requireNonNull(this.getCommand("info")).setExecutor(new Commands());
        Objects.requireNonNull(this.getCommand("reload")).setExecutor(new Commands());
        Objects.requireNonNull(this.getCommand("punish")).setExecutor(new Commands());
        Objects.requireNonNull(this.getCommand("regenkey")).setExecutor(new Commands());

        // loads everything else

        enablePlugin(null);

    }

    private static void loadKeys() {


        keys = new YamlConfiguration();
        try {
            keys.load(keysFile);
        } catch (IOException | InvalidConfigurationException e) {
            Logging.logError(Main.plugin.getLogger(), Logging.Class.STARTUP,"Error while creating or loading keys.yml");
        }

        core = new Core(keys.getString("key"));

    }

    static void enablePlugin(CommandSender sender)  {

        // close inventories
        for (Inventory inventory:punishmentGUIs) {
            for (HumanEntity player: inventory.getViewers()) {
                player.closeInventory();
            }
        }

        // reload keys
        loadKeys();

        // unregister all
        Logging.logInfo(Main.plugin.getLogger(), Logging.Class.STARTUP,"Reloading plugin");
        HandlerList.unregisterAll(Main.plugin);
        Main.plugin.getServer().getPluginManager().registerEvents(new Chat(), Main.plugin);
        Main.plugin.getServer().getPluginManager().registerEvents(new io.purecore.mcplugin.spigot.events.Inventory(), Main.plugin);

        // plugin installed
        if(Main.plugin.getServer().getPluginManager().getPlugin("Votifier")!=null||Main.plugin.getServer().getPluginManager().getPlugin("NuVotifier")!=null){
            Logging.logInfo(Main.plugin.getLogger(), Logging.Class.VOTES,"Hooked into NuVotifier");
            Main.plugin.getServer().getPluginManager().registerEvents(new io.purecore.mcplugin.spigot.events.Votifier(), Main.plugin);
        }

        // loads needed
        Main.plugin.getServer().getScheduler().runTaskAsynchronously(Main.plugin, new Runnable() {
            @Override
            public void run() {

                LinkedHashMap<String, Instance> instanceInfo = null;
                Bukkit.getScheduler().cancelTasks(Main.plugin);

                try {

                    instance = core.getInstance();
                    settings = instance.getDefaultSettings();

                    if(firstStart){
                        new Title(core,plugin.getLogger(), instance, settings).showTitle();
                        firstStart=false;
                    } else {
                        Logging.logError(Main.plugin.getLogger(), Logging.Class.STARTUP,"Plugin reloaded");
                    }

                    if(settings.shouldCreateSessions()&&!bungee){
                        Main.plugin.getServer().getPluginManager().registerEvents(new Sessions(), Main.plugin);
                        if(settings.shouldDebug()){
                            Logging.logError(Main.plugin.getLogger(), Logging.Class.SESSIONS, "Listening to join/quit events");
                        }
                    }

                    // start repeating tasks

                    Main.repeaterId = Main.plugin.getServer().getScheduler().scheduleSyncRepeatingTask(Main.plugin, new Runnable() {
                        @Override
                        public void run() {

                            if(checkPendingCommands!=null){
                                checkPendingCommands.cancel();
                            }

                            checkPendingCommands = Main.plugin.getServer().getScheduler().runTaskAsynchronously(Main.plugin,new MarkPending(core)); // command execution querying

                        }
                    }, 0L, instance.getDefaultSettings().getCheckFrequency());

                    // sender notify

                    if(sender!=null){
                        if(sender instanceof Player){
                            Main.plugin.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
                                @Override
                                public void run() {
                                    Objects.requireNonNull(((Player) sender).getPlayer()).playSound(Objects.requireNonNull(((Player) sender).getPlayer()).getLocation(), Sound.BLOCK_BEACON_ACTIVATE,1,1);
                                    Objects.requireNonNull(((Player) sender).getPlayer()).performCommand("purecore");
                                }
                            }, 0L);
                        }
                    }

                } catch (ApiException | IOException | CallException e) {

                    instance=null;
                    core=null;
                    Logging.logError(Main.plugin.getLogger(), Logging.Class.STARTUP,e.getMessage());

                    if(sender!=null){
                        if(sender instanceof Player){
                            Main.plugin.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
                                @Override
                                public void run() {
                                    Objects.requireNonNull(((Player) sender).getPlayer()).playSound(Objects.requireNonNull(((Player) sender).getPlayer()).getLocation(), Sound.ENTITY_SNOWBALL_THROW,1,1);
                                    Objects.requireNonNull(((Player) sender).getPlayer()).sendMessage(ChatColor.RED+"✘"+ChatColor.GRAY+" "+e.getMessage());
                                }
                            }, 0L);
                        }
                    }
                }
            }
        });

    }

    @Override
    public void onDisable() {
        super.onDisable();
        HandlerList.unregisterAll(this);
        Logging.logError(this.getLogger(), Logging.Class.STARTUP,"Disabling plugin, are you running on an offline mode server?");
    }

    @Override
    public void onPluginMessageReceived(String s, Player player, byte[] bytes) {
        try{
            if(s.equals("purecore:setup")){
                ByteArrayDataInput data = ByteStreams.newDataInput(bytes);
                String subchannel = data.readUTF();
                if(subchannel.equals("key")){
                    String key = data.readUTF();
                    try {
                        if(!core.getKey().equals(key)){
                            Main.keys.set("key",key);
                            Main.keys.save(Main.keysFile);
                            Main.enablePlugin(null);
                        }
                    } catch (NullPointerException e){
                        // the key is null, therefore we set it up
                        Main.keys.set("key",key);
                        Main.keys.save(Main.keysFile);
                        Main.enablePlugin(null);
                    }
                }
            }
        } catch (IOException e) {
            Logging.logError(Main.plugin.getLogger(),Logging.Class.STARTUP,"Error while updating the key hash from a bungeecord update: "+e.getMessage());
        }
    }
}
