package io.purecore.mcplugin.spigot.events;

import io.purecore.api.call.ApiException;
import io.purecore.api.punishment.PunishmentType;
import io.purecore.mcplugin.API;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import net.ess3.api.events.MuteStatusChangeEvent;
import org.bukkit.plugin.Plugin;
import org.json.JSONException;

import java.io.IOException;
import java.util.Date;
import java.util.logging.Level;

public class EssentialsHandler implements Listener {

    private Plugin plugin;

    public EssentialsHandler(Plugin plugin){
        this.plugin=plugin;
        this.plugin.getLogger().log(Level.INFO,"Hooked into EssentialsX");
    }

    @EventHandler
    public void onMute(MuteStatusChangeEvent event){
        if(event.getValue()){
            Date until = null;
            if(event.getTimestamp().isPresent()){
                until = new Date(event.getTimestamp().get());
            }
            Date finalUntil = until;
            Bukkit.getScheduler().runTaskAsynchronously(this.plugin, new Runnable() {
                @Override
                public void run() {
                    try {
                        API.getInstance().getInstance().punish("be70c5573f92a3a9", PunishmentType.Mute, event.getController().getBase().getUniqueId().toString(),event.getAffected().getBase().getUniqueId().toString(),event.getReason(),null, finalUntil);
                    } catch (IOException | ApiException | JSONException e) {
                        plugin.getLogger().log(Level.WARNING, "There was an error while creating an EssentialsX punishment: " + e.getMessage());
                    }
                }
            });
        } else {
            Bukkit.getScheduler().runTaskAsynchronously(this.plugin, new Runnable() {
                @Override
                public void run() {
                    try {
                        API.getInstance().getInstance().unpunish("be70c5573f92a3a9", PunishmentType.Mute, event.getController().getBase().getUniqueId().toString(),event.getAffected().getBase().getUniqueId().toString(),null);
                    } catch (IOException | ApiException | JSONException e) {
                        plugin.getLogger().log(Level.WARNING, "There was an error while removing an EssentialsX punishment: " + e.getMessage());
                    }
                }
            });
        }
    }

}
