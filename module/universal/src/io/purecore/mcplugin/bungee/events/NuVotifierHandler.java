package io.purecore.mcplugin.bungee.events;

import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;

import java.util.logging.Level;

public class NuVotifierHandler implements Listener {

    Plugin plugin;

    public NuVotifierHandler(Plugin plugin){
        this.plugin=plugin;
        this.plugin.getLogger().log(Level.INFO,"Hooked into NuVotifier");
    }

}
