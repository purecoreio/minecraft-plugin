package io.purecore.plugin.bungee;

import io.purecore.api.Core;
import io.purecore.api.exception.ApiException;
import io.purecore.api.exception.CallException;
import io.purecore.api.instance.Instance;
import io.purecore.api.instance.Settings;
import io.purecore.api.key.Key;
import io.purecore.plugin.bungee.cmd.Handler;
import io.purecore.plugin.bungee.events.Sessions;
import io.purecore.plugin.util.Logging;
import io.purecore.plugin.util.Title;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main extends Plugin {

    public static Configuration keys = null;
    public static boolean debug = false;
    public static Settings settings = null;
    public static Plugin plugin = null;
    public static Map<String,Key> serverInterface = new HashMap<>();

    public static Core core = null;
    public static Instance instance = null;

    @Override
    public void onEnable() {
        super.onEnable();
        plugin=this;


        getProxy().getPluginManager().registerCommand(this, new Handler());

        try {
            this.loadConfig();
            this.loadInstance();
        } catch (Exception e) {
            Logging.showBigError(this.getLogger(),e.getMessage());
        }

    }

    public void loadConfig() throws IOException {
        if (!getDataFolder().exists()){
            getDataFolder().mkdir();
        }

        File configFile = new File(getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            try (InputStream in = getResourceAsStream("config.yml")) {
                Files.copy(in, configFile.toPath());
            } catch (IOException e) {
                Logging.logError(this.getLogger(), Logging.Class.STARTUP,"Error while saving config.yml: "+e.getMessage());
            }
        }
        File keysFile = new File(getDataFolder(), "keys.yml");
        if (!keysFile.exists()) {
            try (InputStream in = getResourceAsStream("keys.yml")) {
                Files.copy(in, keysFile.toPath());
            } catch (IOException e) {
                Logging.logError(this.getLogger(), Logging.Class.STARTUP,"Error while saving keys.yml: "+e.getMessage());
            }
        }

        keys = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(getDataFolder(), "keys.yml"));

    }

    public static void loadServers() throws ApiException, IOException, CallException {
        Logging.logInfo(Main.plugin.getLogger(), Logging.Class.STARTUP,"Syncing your server keys to act as an interface...");
        List<Key> keys = instance.asNetwork().getAllKeys();
        Map<String, ServerInfo> servers = Main.plugin.getProxy().getServers();
        Map<String,Key> matchingServers = new HashMap<>();
        for (Key key:keys) {
            for(Map.Entry<String, ServerInfo> entry : servers.entrySet()) {
                if (entry.getKey().toLowerCase().equals(key.getInstance().getName().toLowerCase())){
                    matchingServers.put(entry.getKey(),key);
                }
            }
        }
        Logging.logInfo(Main.plugin.getLogger(), Logging.Class.STARTUP,"Acting as an interface for "+matchingServers.size()+" server(s)");
        if(servers.size()>matchingServers.size()){
            if(servers.size()>1){
                Logging.logError(Main.plugin.getLogger(), Logging.Class.STARTUP,"This proxy instance is acting as interface for "+matchingServers.size() + ", but there are "+servers.size()+" servers available. Make sure to setup all your instances on your dashboard and rename them matching the name on your bungeecord config.yml file");
            } else {
                Logging.logError(Main.plugin.getLogger(), Logging.Class.STARTUP,"This proxy instance isn't acting as interface for any servers, but there is a server available. Make sure to setup all your instances on your dashboard and rename them matching the name on your bungeecord config.yml file");
            }
        }
        serverInterface=matchingServers;
    }

    public static void loadInstance() throws Exception {

        Logging.logInfo(Main.plugin.getLogger(), Logging.Class.STARTUP,"Reloading...");
        Main.plugin.getProxy().getPluginManager().unregisterListeners(Main.plugin);

        if(keys!=null){
            if(keys.getString("key")!=null && !keys.getString("key").equals("") && !keys.getString("key").equals("your_key_hash_here")){
                core = new Core(keys.getString("key"));
                instance = new Instance(core);
                if(instance.getType()!= Instance.Type.NTW){
                    throw new Exception("You are using a SVR key, since you're running a proxy instance, you must use a NTW key!");
                } else {
                    settings = instance.getDefaultSettings();
                    new Title(core,Main.plugin.getLogger(),instance,settings).showTitle();
                    if(settings.shouldCreateSessions()){
                        Main.plugin.getProxy().getPluginManager().registerListener(Main.plugin, new Sessions());
                    }
                    if(settings.shouldDebug()){
                        debug = true;
                    }
                    loadServers();
                }
            } else {
                throw new Exception("Please, execute /bcore setup <key> or save a key in your keys.yml file and do /bcore reload");
            }
        } else {
            throw new Exception("No key data received");
        }

    }
}
