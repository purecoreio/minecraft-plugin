package io.purecore.mcplugin.spigot.events;

import io.purecore.api.call.ApiException;
import io.purecore.api.punishment.PunishmentType;
import io.purecore.mcplugin.API;
import net.ess3.api.IUser;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
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

                        IUser player = event.getAffected();
                        String playerID = null;
                        if(player!= null){
                            if(player.getBase()!=null){
                                playerID = player.getBase().getUniqueId().toString();
                            } else {
                                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(player.getName());
                                if(offlinePlayer.hasPlayedBefore()){
                                    playerID = offlinePlayer.getUniqueId().toString();
                                }
                            }
                        }

                        if(playerID!=null){
                            IUser controller = event.getController();

                            String controllerID = null;
                            if(controller!=null && controller.getBase() != null){
                                controllerID = controller.getBase().getUniqueId().toString();
                            }
                            API.getInstance().getInstance().punish("be70c5573f92a3a9", PunishmentType.Mute, controllerID, playerID, event.getReason(),null,finalUntil);
                        }

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
                        IUser player = event.getAffected();
                        String playerID = null;
                        if(player!= null){
                            if(player.getBase()!=null){
                                playerID = player.getBase().getUniqueId().toString();
                            } else {
                                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(player.getName());
                                if(offlinePlayer.hasPlayedBefore()){
                                    playerID = offlinePlayer.getUniqueId().toString();
                                }
                            }
                        }

                        if(playerID!=null){
                            boolean skip = false;
                            if(event.getTimestamp().isPresent()){
                                if(event.getTimestamp().get() < new Date().getTime()){
                                    skip = true;
                                    // this is an unmute event caused by the due date of the mute,
                                    // since this data is already present on the punishment and it
                                    // shouldn't be considered a manual unmute, we just skip it
                                }
                            }
                            if(!skip){
                                IUser controller = event.getController();

                                String controllerID = null;
                                if(controller!=null && controller.getBase() != null){
                                    controllerID = controller.getBase().getUniqueId().toString();
                                }
                                API.getInstance().getInstance().unpunish("be70c5573f92a3a9", PunishmentType.Mute, playerID, controllerID,null);
                            }
                        }

                    } catch (IOException | ApiException | JSONException e) {
                        plugin.getLogger().log(Level.WARNING, "There was an error while removing an EssentialsX punishment: " + e.getMessage());
                    }
                }
            });
        }
    }

}
