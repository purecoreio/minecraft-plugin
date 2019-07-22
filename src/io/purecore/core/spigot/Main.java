package io.purecore.core.spigot;

import io.purecore.core.console.utils.Title;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

public class Main extends JavaPlugin {

    private Title title = new Title();
    public static Plugin plugin = null;
    public static boolean debug = false;
    public static Configuration config = null;

    // custom .yml

    public static FileConfiguration keys = null;

    // log

    public static Logger logger;

    @Override
    public void onEnable() {
        title.showTitle("starting up spigot instance");

        // startup
        plugin=this;
        logger=getLogger();

        // config
        plugin.saveDefaultConfig();
        config=plugin.getConfig();

        // keys
        createKeyConfig();
        if(keys==null||config==null){
            io.purecore.core.console.utils.Msgs.showError(logger,"KEYS","Error while loading keys/settings config file, please, review your write/read permissions and your config structure");
        } else {

            if(config.getBoolean("settings.debug")){
                debug=true;
            }

            getServer().getPluginManager().registerEvents(new Events(), this);
        }

    }
    @Override
    public void onDisable() {

        // funny gag xd lmao so funny
        List<String> givenList = Arrays.asList("sketit", "bye bye lol", "see ya", "taluego", "despacito says goodbye to u","I'm in love with the coco","drink water!","prolly nobody reads this UwU","UwU more like OwO hehe","dab on the creepers","Meme review will Will Smith (inc)");
        Random rand = new Random();
        String randomgoodbye = givenList.get(rand.nextInt(givenList.size()));

        // actually disabling
        title.showTitle(randomgoodbye);

    }

    // other .yml files

    private void createKeyConfig() {
        File keysfile = new File(getDataFolder(), "keys.yml");
        if (!keysfile.exists()) {
            keysfile.getParentFile().mkdirs();
            saveResource("keys.yml", false);
        }

        keys= new YamlConfiguration();
        try {
            keys.load(keysfile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

}
