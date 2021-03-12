package io.purecore.mcplugin.spigot.events;

import io.purecore.api.call.ApiException;
import io.purecore.api.punishment.PunishmentType;
import io.purecore.mcplugin.API;
import me.leoko.advancedban.bukkit.event.PunishmentEvent;
import me.leoko.advancedban.bukkit.event.RevokePunishmentEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.json.JSONException;

import java.io.IOException;
import java.util.Date;
import java.util.UUID;
import java.util.logging.Level;

public class AdvancedBanHandler implements Listener {

    private Plugin plugin;

    public AdvancedBanHandler(Plugin plugin){
        this.plugin=plugin;
        this.plugin.getLogger().log(Level.INFO,"Hooked into AdvancedBan");
    }

    @EventHandler
    public void onPunishment(PunishmentEvent event){
        PunishmentType type = null;
        Date until = null;
        String id = event.getPunishment().getHexId();
        String reason = event.getPunishment().getReason();
        String playerId = event.getPunishment().getUuid();
        String operatorIdTemp = event.getPunishment().getOperator();
        String operatorId = null;
        if(operatorIdTemp!=null&& !operatorIdTemp.toLowerCase().equals("console")){
            operatorId = UUID.fromString(me.leoko.advancedban.manager.UUIDManager.get().getUUID(operatorIdTemp)).toString();
        }
        switch (event.getPunishment().getType()){
            case BAN:
                type = PunishmentType.Ban;
                break;
            case IP_BAN:
                type = PunishmentType.BanIP;
                break;
            case KICK:
                type = PunishmentType.Kick;
                break;
            case MUTE:
                type = PunishmentType.Mute;
                break;
            case WARNING:
                type = PunishmentType.Warn;
                break;
            case TEMP_BAN:
                type = PunishmentType.Ban;
                until = new Date(event.getPunishment().getEnd());
                break;
            case TEMP_MUTE:
                type = PunishmentType.Mute;
                until = new Date(event.getPunishment().getEnd());
                break;
            case TEMP_IP_BAN:
                type = PunishmentType.BanIP;
                until = new Date(event.getPunishment().getEnd());
                break;
            case TEMP_WARNING:
                type = PunishmentType.Warn;
                until = new Date(event.getPunishment().getEnd());
                break;
        }
        if(type!=null){
            PunishmentType finalType = type;
            String finalOperatorId = operatorId;
            Date finalUntil = until;
            Bukkit.getScheduler().runTaskAsynchronously(this.plugin, new Runnable() {
                @Override
                public void run() {
                    try {
                        API.getInstance().getInstance().punish("5cd40c80132cd11e", finalType, finalOperatorId, playerId, reason, id, finalUntil);
                    } catch (IOException | ApiException | JSONException e) {
                        plugin.getLogger().log(Level.WARNING, "There was an error while creating an AdvancedBan punishment: " + e.getMessage());
                    }
                }
            });
        }
    }

    @EventHandler
    public void onRemovePunishment(RevokePunishmentEvent event){
        PunishmentType type = null;
        String id = event.getPunishment().getHexId();
        String playerId = event.getPunishment().getUuid();
        String operatorIdTemp = event.getPunishment().getOperator();
        String operatorId = null;
        if(operatorIdTemp!=null&& !operatorIdTemp.toLowerCase().equals("console")){
            operatorId = UUID.fromString(me.leoko.advancedban.manager.UUIDManager.get().getUUID(operatorIdTemp)).toString();
        }
        switch (event.getPunishment().getType()){
            case BAN:
            case TEMP_BAN:
                type = PunishmentType.Ban;
                break;
            case IP_BAN:
            case TEMP_IP_BAN:
                type = PunishmentType.BanIP;
                break;
            case KICK:
                type = PunishmentType.Kick;
                break;
            case MUTE:
            case TEMP_MUTE:
                type = PunishmentType.Mute;
                break;
            case WARNING:
            case TEMP_WARNING:
                type = PunishmentType.Warn;
                break;
        }
        if(type!=null){
            PunishmentType finalType = type;
            String finalOperatorId = operatorId;
            Bukkit.getScheduler().runTaskAsynchronously(this.plugin, new Runnable() {
                @Override
                public void run() {
                    try {
                        API.getInstance().getInstance().unpunish("5cd40c80132cd11e", finalType, playerId, finalOperatorId, id);
                    } catch (IOException | ApiException | JSONException e) {
                        plugin.getLogger().log(Level.WARNING, "There was an error while removing an AdvancedBan punishment: " + e.getMessage());
                    }
                }
            });
        }
    }

}
