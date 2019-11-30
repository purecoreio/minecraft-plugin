package io.purecore.core.bungee;

import io.purecore.core.bungee.Tasks.MarkPendingExecutions;
import io.purecore.core.utils.console.Msgs;
import io.purecore.core.utils.console.Title;
import net.md_5.bungee.api.plugin.Plugin;

import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class Main extends Plugin {

    private static Title titlemanager = new Title();
    private static Configuration config = null;
    public static Configuration keys = null;
    static Logger logger = null;
    static boolean debug;
    public static Plugin plugin = null;

    @Override
    public void onEnable() {
        super.onEnable();

        // plugin startup

        titlemanager.showTitle("starting up bungeecord instance");
        plugin=this;
        logger=getLogger();

        // config starter/prev loading

        Boolean configresult = updateConfig();

        // keys starter/prev loading and checking

        Boolean keysresult = updateKeys();

        // check files creation

        if(!configresult&&!keysresult){

            Msgs.showError(logger,"LOADER","couldn't load the basic config files (keys.yml and/or config.yml)");
            this.onDisable();

        } else {

            if(config.getBoolean("settings.debug")){
                debug=true;
            }

            getProxy().getPluginManager().registerListener(this, new Events());
            getProxy().getScheduler().schedule(this, new MarkPendingExecutions(), 1, 30, TimeUnit.SECONDS); // execute pending commands every 30 seconds


        }
    }

    @Override
    public void onDisable() {

        // funny gag xd lmao so funny
        List<String> givenList = Arrays.asList("sketit", "bye bye lol", "see ya", "taluego", "despacito says goodbye to u","I'm in love with the coco","drink water!","prolly nobody reads this UwU","UwU more like OwO hehe","dab on the creepers","Meme review will Will Smith (inc)");
        Random rand = new Random();
        String randomgoodbye = givenList.get(rand.nextInt(givenList.size()));

        // actually disabling
        titlemanager.showTitle(randomgoodbye);
        super.onDisable();

    }

    // .yml files copying

    private boolean updateConfig(){

        boolean newkid = false; // used for checking if the file is new later
        if (!getDataFolder().exists()){ // check if plugin folder config exists
            boolean mkdir = getDataFolder().mkdir();
        }
        File file = new File(getDataFolder(), "config.yml"); // load future file
        if (!file.exists()) { // check if the file is already on the folder (if negative)
            try (InputStream in = getResourceAsStream("config.yml")) { // load the config data from the plugin res
                newkid = true;
                Files.copy(in, file.toPath()); // copy the data from res and paste the file into the folder
            } catch (IOException e) {
                Msgs.showError(logger,"CONFIG",e.getMessage()); // error, proly not enough perms or disk size
            }
        }

        try {
            config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(getDataFolder(), "config.yml")); // load config into config public variable
            if(newkid){
                config.set("update.timestamp", System.currentTimeMillis());
            }
            return true;
        } catch (IOException e) {
            Msgs.showError(logger,"CONFIG",e.getMessage()); // error, proly not enough perms or disk size
            return false;
        }
    }

    private boolean updateKeys(){
        boolean newkid = false; // used for checking if the file is new later
        if (!getDataFolder().exists()){ // check if plugin folder config exists
            getDataFolder().mkdir();
        }
        File file = new File(getDataFolder(), "keys.yml"); // load future file
        if (!file.exists()) {
            try (InputStream in = getResourceAsStream("keys.yml")) {
                newkid = true;
                Files.copy(in, file.toPath());
            } catch (IOException e) {
                Msgs.showError(logger,"KEYS",e.getMessage());
            }
        }

        try {
            keys = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(getDataFolder(), "keys.yml")); // load config into config public variable
            if(newkid){
                keys.set("update.timestamp", System.currentTimeMillis());
            }
            return true;
        } catch (IOException e) {
            Msgs.showError(logger,"KEYS",e.getMessage()); // error, proly not enough perms or disk size
            return false;
        }

    }


}
