package io.purecore.mcplugin.spigot.events;

import com.vexsoftware.votifier.model.VotifierEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import java.util.logging.Level;

public class NuVotifierHandler implements Listener {

    private Plugin plugin;

    public NuVotifierHandler(Plugin plugin){
        this.plugin=plugin;
        this.plugin.getLogger().log(Level.INFO,"Hooked into NuVotifier");
    }

    @EventHandler
    public void onEvent(VotifierEvent event){

    }

}
