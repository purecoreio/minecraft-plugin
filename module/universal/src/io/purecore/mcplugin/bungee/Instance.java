package io.purecore.mcplugin.bungee;

import com.google.common.io.ByteStreams;
import io.purecore.api.Core;
import io.purecore.api.call.ApiException;
import io.purecore.api.execution.SimplifiedExecution;
import io.purecore.api.versioning.Game;
import io.purecore.api.versioning.GameSoftware;
import io.purecore.api.versioning.InvalidVersionFormatException;
import io.purecore.api.versioning.Version;
import io.purecore.mcplugin.API;
import io.purecore.mcplugin.PluginException;
import io.purecore.mcplugin.bungee.events.AdvancedBanHandler;
import io.purecore.mcplugin.bungee.events.BungeeHandler;
import io.purecore.mcplugin.bungee.events.LiteBansHandler;
import io.purecore.mcplugin.bungee.events.NuVotifierHandler;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.*;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Instance extends Plugin {

    private static Cache cache;
    private static Handler handler;
    private static Configuration configuration;

    public static Cache getCache(){
        return Instance.cache;
    }

    public static Configuration getConfig(){
        return Instance.configuration;
    }

    @Override
    public void onEnable() {
        super.onEnable();
        Core.setClientVersion(API.getCurrentRelease());
        try {

            if(handler==null){
                Instance.handler=new Handler(this.getLogger(),this);
            }

            getProxy().getPluginManager().registerListener(this, new BungeeHandler(this));

            if(this.getProxy().getPluginManager().getPlugin("LiteBans")!=null){
                new LiteBansHandler(this);
            }

            if(this.getProxy().getPluginManager().getPlugin("AdvancedBan")!=null){
                getProxy().getPluginManager().registerListener(this, new AdvancedBanHandler(this));
            }

            if(this.getProxy().getPluginManager().getPlugin("NuVotifier")!=null){
                getProxy().getPluginManager().registerListener(this, new NuVotifierHandler(this));
            }

            this.getLogger().log(Level.INFO,"purecore has been enabled. Feel free to join our Discord server: https://discord.gg/rhpcSnK");
            this.reload();
            ProxyServer.getInstance().getScheduler().runAsync(this, new Runnable() {
                @Override
                public void run() {
                    checkUpdates();
                }
            });
        } catch (PluginException e) {
            e.printStackTrace();
        }
    }

    public void reload() throws PluginException {

        try {

            if (!getDataFolder().exists()) {
                getDataFolder().mkdir();
            }

            // cache file

            File cacheFile = new File(getDataFolder(), "cache.json");
            if (!cacheFile.exists()) {
                cacheFile.createNewFile();
                try (InputStream is = getResourceAsStream("cache.json"); OutputStream os = new FileOutputStream(cacheFile)) {
                    ByteStreams.copy(is, os);
                }
            }

            Instance.cache=new Cache(cacheFile,this);

            // config file

            File configFile = new File(getDataFolder(), "config.yml");
            if (!configFile.exists()) {
                configFile.createNewFile();
                try (InputStream is = getResourceAsStream("config.yml"); OutputStream os = new FileOutputStream(configFile)) {
                    ByteStreams.copy(is, os);
                }
            }

            Instance.configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(configFile);

            String key = getConfig().getString("key");
            if(key!=null&&key.length()==64){

                API.instance=new Core(key, handler);
                API.instance.getInstance().update();

                this.getProxy().getScheduler().runAsync(this, new Runnable() {

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
                            getCache().runExecutions();
                        } catch (IOException | ApiException e) {
                            getLogger().log(Level.SEVERE,"error while updating local cache: "+e.getMessage());
                        }
                    }
                });

            } else {

                throw new PluginException("invalid key, please, use '/purecore key <key>' or paste the key under '/purecore/config.yml' and reload with '/purecore reload'");

            }

        } catch (IOException | ApiException | PluginException e) {

            throw new PluginException("error while creating a cache manager: "+e.getMessage());

        }
    }

    public void checkUpdates(){

        Plugin plugin = this;
        ProxyServer.getInstance().getScheduler().runAsync(this, new Runnable() {
            @Override
            public void run() {
                try {
                    getLogger().log(Level.INFO,"Checking version updates...");
                    String gameVersion = ProxyServer.getInstance().getGameVersion().split("-")[1].replaceAll("x","0");
                    Version version = API.getInstance().getVersionCandidate(Game.Minecraft, GameSoftware.BungeeCord,gameVersion);
                    io.purecore.mcplugin.bungee.Instance.checkVersion(version,plugin);
                } catch (IOException | ApiException e) {
                    getLogger().log(Level.WARNING,"Error while checking version updates: " + e.getMessage());
                }
            }
        });
    }

    public static void checkVersion(Version version, Plugin plugin){
        io.purecore.mcplugin.bungee.Updater updater = new io.purecore.mcplugin.bungee.Updater(plugin);
        Logger logger = plugin.getLogger();
        try {
            String gameVersion = ProxyServer.getInstance().getGameVersion().split("-")[1].replaceAll("x","0");
            if(version.isCompatible(Game.Minecraft, GameSoftware.BungeeCord, gameVersion)){
                if(!version.getId().equals(Core.getClientVersion())){
                    ProxyServer.getInstance().getScheduler().runAsync(plugin, new Runnable() {
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
                                        logger.log(Level.INFO,"The plugin has been updated, in order to see the changes, please, restart the proxy");
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
            e.printStackTrace();
        }
    }
}
