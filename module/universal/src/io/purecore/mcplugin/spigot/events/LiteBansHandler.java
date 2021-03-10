package io.purecore.mcplugin.spigot.events;

import litebans.api.Entry;
import litebans.api.Events;
import org.bukkit.plugin.Plugin;

import java.util.logging.Level;

public class LiteBansHandler extends Events.Listener {

    private Plugin plugin;

    public LiteBansHandler(Plugin plugin){
        this.plugin=plugin;
        this.plugin.getLogger().log(Level.INFO,"Hooked into LiteBans");
    }

    public void entryAdded(Entry entry) {

    }

    public void entryRemoved(Entry entry) {

    }

}
