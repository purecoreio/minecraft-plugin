package io.purecore.mcplugin.spigot;

import co.aikar.commands.PaperCommandManager;
import io.purecore.api.Core;
import io.purecore.api.call.ApiException;
import io.purecore.api.execution.SimplifiedExecution;
import io.purecore.api.versioning.Game;
import io.purecore.api.versioning.GameSoftware;
import io.purecore.api.versioning.InvalidVersionFormatException;
import io.purecore.api.versioning.Version;
import io.purecore.mcplugin.API;
import io.purecore.mcplugin.PluginException;
import io.purecore.mcplugin.spigot.events.BukkitHandler;
import io.purecore.mcplugin.spigot.events.EssentialsHandler;
import io.purecore.mcplugin.spigot.events.LiteBansHandler;
import io.purecore.mcplugin.spigot.events.NuVotifierHandler;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Instance extends JavaPlugin {

    private static Handler handler;
    private static Cache cache;
    private static File cacheFile;

    private boolean hasLiteBansHandler;

    public static io.purecore.mcplugin.spigot.Cache getCache(){
        return Instance.cache;
    }
    public static File getCacheFile(){
        return Instance.cacheFile;
    }

    @Override
    public void onDisable() {
        super.onDisable();
        API.instance=null;
    }

    @Override
    public void onEnable() {

        super.onEnable();
        Core.setClientVersion(API.getCurrentRelease());
        this.saveDefaultConfig();

        // integration handlers

        if(this.getServer().getPluginManager().isPluginEnabled("AdvancedBan")){
            if(!hasLiteBansHandler){
                hasLiteBansHandler=true;
                new LiteBansHandler(this);
            }
        }

        if(this.getServer().getPluginManager().isPluginEnabled("LiteBans")){
            if(!hasLiteBansHandler){
                hasLiteBansHandler=true;
                new LiteBansHandler(this);
            }
        }

        if(this.getServer().getPluginManager().isPluginEnabled("Essentials")){
            this.getServer().getPluginManager().registerEvents(new EssentialsHandler(this), this);
        }

        if(this.getServer().getPluginManager().isPluginEnabled("Votifier")){
            this.getServer().getPluginManager().registerEvents(new NuVotifierHandler(this), this);
        }

        this.getServer().getPluginManager().registerEvents(new BukkitHandler(this), this);

        // core handler

        if(Instance.handler==null){
            Instance.handler=new Handler(this.getLogger(), this);
        }

        // commands

        PaperCommandManager manager = new PaperCommandManager(this);
        manager.registerCommand(new CommandHandler(this));

        // core start

        try {
            this.reload();
            this.getLogger().log(Level.INFO,"purecore has been enabled. Feel free to join our Discord server: https://discord.gg/rhpcSnK");
            this.checkUpdates();
        } catch (PluginException | ApiException | IOException e) {
            this.getLogger().log(Level.SEVERE,"error while enabling: " + e.getMessage());
        }
    }

    public void checkUpdates(){

        Plugin plugin = this;
        Bukkit.getScheduler().runTaskAsynchronously(this, new Runnable() {
            @Override
            public void run() {
                try {
                    getLogger().log(Level.INFO,"Checking version updates...");
                    String gameVersion = Bukkit.getVersion();
                    if(gameVersion.contains("MC:")){
                        gameVersion = gameVersion.split("(?<=MC:) ")[1].replaceAll("\\s+","").replaceAll("\\)","");
                    }
                    Version version = API.getInstance().getVersionCandidate(Game.Minecraft, GameSoftware.Spigot,gameVersion);
                    Instance.checkVersion(version,plugin);
                } catch (IOException | ApiException e) {
                    getLogger().log(Level.WARNING,"Error while checking version updates: " + e.getMessage());
                }
            }
        });
    }

    public static void checkVersion(Version version, Plugin plugin){
        Updater updater = new Updater(plugin);
        Logger logger = plugin.getLogger();
        String gameVersion = Bukkit.getVersion();
        if(gameVersion.contains("MC:")){
            gameVersion = gameVersion.split("(?<=MC:) ")[1].replaceAll("\\s+","").replaceAll("\\)","");
        }
        try {
            if(version.isCompatible(Game.Minecraft,GameSoftware.Spigot,gameVersion)){
                if(!version.getId().equals(Core.getClientVersion())){
                    Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
                        @Override
                        public void run() {
                            logger.log(Level.INFO,"Downloading version #"+version.getId());
                            try {
                                byte[] data = updater.update(version);
                                try{
                                    logger.log(Level.INFO,"Downloaded, checking integrity");
                                    boolean check = updater.checkIntegrity(version,data.length);
                                    if(check){
                                        logger.log(Level.INFO,"The file integrity has been validated, installing");
                                        updater.install(data);
                                        logger.log(Level.INFO,"The plugin has been updated, in order to see the changes, please, reload the server");
                                        // Core.setClientVersion(version.getId());
                                    } else {
                                        logger.log(Level.WARNING,"Invalid integrity check");
                                    }
                                } catch (Exception e){
                                    logger.log(Level.WARNING,"Error while checking integrity: "+e.getMessage());
                                }
                            } catch (IOException | URISyntaxException e) {
                                logger.log(Level.WARNING,"Error while downloading: "+e.getMessage());
                            }
                        }
                    });
                }
            }
        } catch (InvalidVersionFormatException e) {
            logger.log(Level.WARNING,"Couldn't determine version compatibility for #"+version.getId()+": "+e.getMessage());
        }
    }

    public void reload() throws PluginException, IOException, ApiException {

        try {
            Instance.cacheFile = new File(getDataFolder(), "cache.json");
            if (!cacheFile.exists()) {
                cacheFile.getParentFile().mkdirs();
                saveResource("cache.json", false);
            }
            Instance.cache=new io.purecore.mcplugin.spigot.Cache(cacheFile,this);
            String key = this.getConfig().getString("key");
            if(key!=null&&key.length()==64){
                API.instance=new Core(key, Instance.handler);
                API.instance.getInstance().update();
                this.getServer().getScheduler().runTaskAsynchronously(this, new Runnable() {
                    @Override
                    public void run() {
                        try {
                            ArrayList<SimplifiedExecution> newExecutions;
                            if(cache.getExecutions().size()>0){
                                newExecutions = API.instance.getInstance().getPendingExecutions(cache.getExecutions().get(0));
                            } else {
                                newExecutions = API.instance.getInstance().getPendingExecutions();
                            }
                            cache.addExecutions(newExecutions);
                            Instance.getCache().runExecutions();
                        } catch (IOException | ApiException e) {
                            getLogger().log(Level.SEVERE,"error while updating local cache: "+e.getMessage());
                        }
                    }
                });
            } else {
                throw new PluginException("invalid key, please, use '/purecore key <key>' or paste the key under '/purecore/config.yml' and reload with '/purecore reload'");
            }
        } catch (FileNotFoundException e) {
            throw new PluginException("error while creating a cache manager: "+e.getMessage());
        }


    }
}
