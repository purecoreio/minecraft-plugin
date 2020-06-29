package io.purecore.mcplugin.spigot;

import io.purecore.api.Core;
import io.purecore.api.sockets.CoreSocket;
import io.purecore.mcplugin.util.Log;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

public class Main extends JavaPlugin {

    CoreSocket core = null;
    YamlConfiguration keys;
    Log log;

    @Override
    public void onEnable() {
        super.onEnable();
        log = new Log(this.getLogger());
        try{
            reloadCore();
        } catch (InvalidConfigurationException | IOException | URISyntaxException e) {
            log.logError("(while enabling purecore) "+e.getLocalizedMessage());
        }
    }

    public void reloadCore() throws IOException, InvalidConfigurationException, URISyntaxException {
        String key = this.getKey();
        if(key!=null && key.length()==64){
            if(core == null){
                core = new Core(key).getSocket(log.getLogger());
                log.showTitle("Connecting to purecore");
            } else {
                core.goodbye();
                core = new Core(key).getSocket(log.getLogger());
                log.logInfo("Reconnecting to purecore...");
            }
        } else {
            log.showTitle("Please, setup purecore with `/purecore setup`. If  you are using a proxy (such as Bungeecord), make sure purecore it's installed on it first, then match the names of your purecore instances with your instance name on the proxy");
        }
    }

    void setKey(String key){
        keys.set("key",key);
    };

    String getKey() throws IOException, InvalidConfigurationException {
        File keysFile = new File(getDataFolder(), "keys.yml");
        if (!keysFile.exists()) {
            keysFile.getParentFile().mkdirs();
            saveResource("keys.yml", false);
        }

        keys = new YamlConfiguration();
        keys.load(keysFile);
        return keys.getString("key");
    }
}
