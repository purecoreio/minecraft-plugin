package io.purecore.mcplugin.util;

import io.purecore.api.Core;
import io.purecore.api.instance.Instance;
import io.purecore.api.instance.Settings;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Title {

    private Core core;
    private Instance instance;
    private Logger logger;
    private Settings settings;

    public Title(Core core, Logger logger, Instance instance, Settings settings){
        this.core=core;
        this.instance=instance;
        this.logger=logger;
        this.settings=settings;
    }

    private String displayBoolean(boolean value){
        if(value){
            return "Yes";
        } else {
            return "No";
        }
    }

    public void showTitle(){

        this.logger.log(Level.INFO, " _ __   _   _  _ __  ___   ___  ___   _ __  ___ ");
        this.logger.log(Level.INFO, "| '_ \\ | | | || '__|/ _ \\ / __|/ _ \\ | '__|/ _ \\");
        this.logger.log(Level.INFO, "| |_) || |_| || |  |  __/| (__| (_) || |  |  __/");
        this.logger.log(Level.INFO, "| .__/  \\__,_||_|   \\___| \\___|\\___/ |_|   \\___|");
        this.logger.log(Level.INFO, "|_|                                             ");
        this.logger.log(Level.INFO, "");
        this.logger.log(Level.INFO, "→ Instance:");
        this.logger.log(Level.INFO, "    ● Name: "+instance.getName());
        this.logger.log(Level.INFO, "    ● UUID: "+instance.getId());
        this.logger.log(Level.INFO, "    ● Type: "+instance.getType().toString());
        this.logger.log(Level.INFO, "");
        this.logger.log(Level.INFO, "→ Settings:");
        this.logger.log(Level.INFO, "    ● Queue Check Delay: "+instance.getDefaultSettings().getCheckFrequency()+"s");
        this.logger.log(Level.INFO, "    ● Create Sessions: "+displayBoolean(settings.shouldCreateSessions()));
        this.logger.log(Level.INFO, "    ● Push Statistics: "+displayBoolean(settings.shouldPushStatistics()));
        this.logger.log(Level.INFO, "    ● Push Advancements: "+displayBoolean(settings.shouldPushAdvancements()));
        this.logger.log(Level.INFO, "    ● Show Debug Messages: "+displayBoolean(settings.shouldDebug()));
        this.logger.log(Level.INFO, "");

    }

    public List<String> getTitle(){
        ArrayList<String> lines = new ArrayList<>();

        lines.add("");
        lines.add("!purecore.io: the all-in-one server solution");
        lines.add("");
        lines.add("→ Instance:");
        lines.add("");
        lines.add("    ● Name: "+instance.getName());
        lines.add("    ● UUID: "+instance.getId());
        lines.add("    ● Type: "+instance.getType().toString());
        lines.add("");
        lines.add("");
        lines.add("→ Settings:");
        lines.add("");
        lines.add("    ● Queue Check Delay: "+instance.getDefaultSettings().getCheckFrequency()+"s");
        lines.add("    ● Create Sessions: "+displayBoolean(settings.shouldCreateSessions()));
        lines.add("    ● Push Statistics: "+displayBoolean(settings.shouldPushStatistics()));
        lines.add("    ● Push Advancements: "+displayBoolean(settings.shouldPushAdvancements()));
        lines.add("    ● Show Debug Messages: "+displayBoolean(settings.shouldDebug()));
        lines.add("");

        return lines;
    }

}
