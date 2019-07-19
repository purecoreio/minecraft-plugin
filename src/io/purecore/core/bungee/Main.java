package io.purecore.core.bungee;

import com.google.gson.Gson;
import io.purecore.core.console.utils.Msgs;
import io.purecore.core.console.utils.Title;
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

public class Main extends Plugin {

    public static Title titlemanager = new Title();
    public static Msgs msgmanager = new Msgs();
    public static Configuration config = null;
    public static Configuration keys = null;

    public static Gson gson = new Gson();

    @Override
    public void onEnable() {
        super.onEnable();

        // plugin startup

        titlemanager.showTitle("starting up plugin");

        // config starter/prev loading

        Boolean configresult = updateConfig();

        // keys starter/prev loading and checking

        Boolean keysresult = updateKeys();

        // check files creation

        if(!configresult&&!keysresult){

            msgmanager.showError("LOADER","couldn't load the basic config files (keys.yml and/or config.yml)");
            this.onDisable();

        } else {



        }



    }

    @Override
    public void onDisable() {

        // funny gag xd lmao so funny
        List<String> givenList = Arrays.asList("sketit", "bye bye lol", "see ya", "taluego", "despacito says goodbye to u");
        Random rand = new Random();
        String randomgoodbye = givenList.get(rand.nextInt(givenList.size()));

        // actually disabling
        titlemanager.showTitle(randomgoodbye);
        super.onDisable();

    }

    /*
       _____             __ _         ______ _ _
      / ____|           / _(_)       |  ____(_) |
     | |     ___  _ __ | |_ _  __ _  | |__   _| | ___  ___
     | |    / _ \| '_ \|  _| |/ _` | |  __| | | |/ _ \/ __|
     | |___| (_) | | | | | | | (_| | | |    | | |  __/\__ \
      \_____\___/|_| |_|_| |_|\__, | |_|    |_|_|\___||___/
                               __/ |
                              |___/
     */


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

                msgmanager.showError("CONFIG",e.getMessage()); // error, proly not enough perms or disk size

            }
        }

        try {

            config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(getDataFolder(), "config.yml")); // load config into config public variable

            if(newkid){
                config.set("update.timestamp", System.currentTimeMillis());
            }

            msgmanager.showWarning("CONFIG","loaded config");

            return true;

        } catch (IOException e) {

            msgmanager.showError("CONFIG",e.getMessage()); // error, proly not enough perms or disk size
            return false;

        }

    }

    private boolean updateKeys(){

        boolean newkid = false; // used for checking if the file is new later

        if (!getDataFolder().exists()){ // check if plugin folder config exists
            boolean mkdir = getDataFolder().mkdir();
        }

        File file = new File(getDataFolder(), "keys.yml"); // load future file

        if (!file.exists()) { // check if the file is already on the folder (if negative)
            try (InputStream in = getResourceAsStream("keys.yml")) { // load the config data from the plugin res

                newkid = true;

                Files.copy(in, file.toPath()); // copy the data from res and paste the file into the folder
            } catch (IOException e) {

                msgmanager.showError("KEYS",e.getMessage()); // error, proly not enough perms or disk size

            }
        }

        try {

            keys = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(getDataFolder(), "keys.yml")); // load config into config public variable

            if(newkid){
                keys.set("update.timestamp", System.currentTimeMillis());
            }

            msgmanager.showWarning("CONFIG","loaded keys");

            return true;

        } catch (IOException e) {

            msgmanager.showError("KEYS",e.getMessage()); // error, proly not enough perms or disk size
            return false;

        }

    }


}
