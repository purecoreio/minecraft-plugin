package io.purecore.mcplugin.spigot.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import net.ess3.api.events.MuteStatusChangeEvent;
import org.bukkit.plugin.Plugin;

import java.util.logging.Level;

public class EssentialsHandler implements Listener {

    private Plugin plugin;

    public EssentialsHandler(Plugin plugin){
        this.plugin=plugin;
        this.plugin.getLogger().log(Level.INFO,"Hooked into EssentialsX");
    }

    @EventHandler
    public void onMute(MuteStatusChangeEvent event){

    }

}
