package io.purecore.plugin.spigot;

import io.purecore.api.Core;
import io.purecore.api.exception.ApiException;
import io.purecore.api.exception.CallException;
import io.purecore.api.instance.Instance;
import io.purecore.api.instance.Settings;
import io.purecore.api.punishment.Offence;
import io.purecore.plugin.spigot.events.Chat;
import io.purecore.plugin.spigot.events.Sessions;
import io.purecore.plugin.spigot.tasks.MarkPending;
import io.purecore.plugin.util.Logging;
import io.purecore.plugin.util.Title;
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

import java.io.File;
import java.io.IOException;
import java.util.*;

public class Main extends JavaPlugin {

    // runtime pending values

    public static int repeaterId;
    public static List<Inventory> punishmentGUIs = new ArrayList<Inventory>();
    public static Map<Inventory, List<io.purecore.api.user.Player>> playerElements = new HashMap<Inventory, List<io.purecore.api.user.Player>>();
    public static Map<Inventory, List<Offence>> selectedOffences = new HashMap<Inventory, List<Offence>>();

    // setup

    public static Player waitingForKey;

    // keys

    public static FileConfiguration keys;
    public static File keysFile;
    private static boolean firstStart = true;

    // instance

    public static Plugin plugin;
    public static Core core;
    public static Instance instance;
    public static Settings settings;

    @Override
    public void onEnable() {

        super.onEnable();
        this.plugin = this;

        if(!this.getServer().getOnlineMode()){
            this.getServer().getPluginManager().disablePlugin(this);
        }

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
        Main.plugin.getServer().getPluginManager().registerEvents(new io.purecore.plugin.spigot.events.Inventory(), Main.plugin);

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

                    if(settings.shouldCreateSessions()){
                        Main.plugin.getServer().getPluginManager().registerEvents(new Sessions(), Main.plugin);
                        if(settings.shouldDebug()){
                            Logging.logError(Main.plugin.getLogger(), Logging.Class.SESSIONS, "Listening to join/quit events");
                        }
                    }

                    // start repeating tasks

                    Main.repeaterId = Main.plugin.getServer().getScheduler().scheduleSyncRepeatingTask(Main.plugin, new Runnable() {
                        @Override
                        public void run() {

                            Main.plugin.getServer().getScheduler().runTaskAsynchronously(Main.plugin,new MarkPending(core)); // command execution querying

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
}
