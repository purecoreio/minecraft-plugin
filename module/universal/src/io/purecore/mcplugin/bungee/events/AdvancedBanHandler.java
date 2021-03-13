package io.purecore.mcplugin.bungee.events;

import io.purecore.api.call.ApiException;
import io.purecore.mcplugin.common.thirdPartyIntegration.AdvancedBan;
import me.leoko.advancedban.bungee.event.PunishmentEvent;
import me.leoko.advancedban.bungee.event.RevokePunishmentEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;
import org.json.JSONException;

import java.io.IOException;
import java.util.logging.Level;

public class AdvancedBanHandler implements Listener {

    Plugin plugin;

    public AdvancedBanHandler(Plugin plugin){
        this.plugin=plugin;
        this.plugin.getLogger().log(Level.INFO,"Hooked into AdvancedBan");
    }

    @EventHandler
    public void punishEvent(PunishmentEvent event){
        plugin.getProxy().getScheduler().runAsync(this.plugin, new Runnable() {
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
    public void revokePunishEvent(RevokePunishmentEvent event){
        plugin.getProxy().getScheduler().runAsync(this.plugin, new Runnable() {
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
