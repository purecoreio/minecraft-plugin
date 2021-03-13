package io.purecore.mcplugin.spigot.events;

import io.purecore.api.call.ApiException;
import io.purecore.api.punishment.PunishmentType;
import io.purecore.mcplugin.API;
import io.purecore.mcplugin.common.thirdPartyIntegration.AdvancedBan;
import me.leoko.advancedban.bukkit.event.PunishmentEvent;
import me.leoko.advancedban.bukkit.event.RevokePunishmentEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.json.JSONException;

import java.io.IOException;
import java.util.logging.Level;

public class AdvancedBanHandler implements Listener {

    private Plugin plugin;

    public AdvancedBanHandler(Plugin plugin){
        this.plugin=plugin;
        this.plugin.getLogger().log(Level.INFO,"Hooked into AdvancedBan");
    }

    @EventHandler
    public void onPunishment(PunishmentEvent event){
        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, new Runnable() {
            @Override
            public void run() {
                try {
                    AdvancedBan.handlePunishment(event.getPunishment());
                } catch (IOException | ApiException | JSONException e) {
                    plugin.getLogger().log(Level.WARNING, "There was an error while creating an AdvancedBan punishment: " + e.getMessage());
                }
            }
        });
    }

    @EventHandler
    public void onRemovePunishment(RevokePunishmentEvent event){
        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, new Runnable() {
            @Override
            public void run() {
                try {
                    AdvancedBan.handleRevokePunishment(event.getPunishment());
                } catch (IOException | ApiException | JSONException e) {
                    plugin.getLogger().log(Level.WARNING, "There was an error while removing an AdvancedBan punishment: " + e.getMessage());
                }
            }
        });
    }

}
