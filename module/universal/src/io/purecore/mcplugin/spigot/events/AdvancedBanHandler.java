package io.purecore.mcplugin.spigot.events;

import me.leoko.advancedban.bukkit.event.PunishmentEvent;
import me.leoko.advancedban.bukkit.event.RevokePunishmentEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import java.util.logging.Level;

public class AdvancedBanHandler implements Listener {

    private Plugin plugin;

    public AdvancedBanHandler(Plugin plugin){
        this.plugin=plugin;
        this.plugin.getLogger().log(Level.INFO,"Hooked into AdvancedBan");
    }

    @EventHandler
    public void onPunishment(PunishmentEvent event){

    }

    @EventHandler
    public void onRemovePunishment(RevokePunishmentEvent event){

    }

}
