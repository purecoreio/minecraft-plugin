package io.purecore.mcplugin.common.thirdPartyIntegration;

import io.purecore.api.call.ApiException;
import io.purecore.api.punishment.PunishmentType;
import io.purecore.mcplugin.API;
import me.leoko.advancedban.utils.Punishment;
import org.json.JSONException;

import java.io.IOException;
import java.util.Date;
import java.util.UUID;

public class AdvancedBan {

    public static void handleRevokePunishment(Punishment punishment) throws JSONException, ApiException, IOException {
        PunishmentType type = null;
        String id = punishment.getHexId();
        String playerId = punishment.getUuid();
        String operatorIdTemp = punishment.getOperator();
        String operatorId = null;
        if(operatorIdTemp!=null&& !operatorIdTemp.toLowerCase().equals("console")){
            operatorId = UUID.fromString(me.leoko.advancedban.manager.UUIDManager.get().getUUID(operatorIdTemp)).toString();
        }
        switch (punishment.getType()){
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
            API.getInstance().getInstance().unpunish("5cd40c80132cd11e", type, playerId, operatorId, id);
        }
    }

    public static void handlePunishment(Punishment punishment) throws JSONException, ApiException, IOException {
        PunishmentType type = null;
        Date until = null;
        String id = punishment.getHexId();
        String reason = punishment.getReason();
        String playerId = punishment.getUuid();
        String operatorIdTemp = punishment.getOperator();
        String operatorId = null;
        if(operatorIdTemp!=null&& !operatorIdTemp.toLowerCase().equals("console")){
            operatorId = UUID.fromString(me.leoko.advancedban.manager.UUIDManager.get().getUUID(operatorIdTemp)).toString();
        }
        switch (punishment.getType()){
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
                until = new Date(punishment.getEnd());
                break;
            case TEMP_MUTE:
                type = PunishmentType.Mute;
                until = new Date(punishment.getEnd());
                break;
            case TEMP_IP_BAN:
                type = PunishmentType.BanIP;
                until = new Date(punishment.getEnd());
                break;
            case TEMP_WARNING:
                type = PunishmentType.Warn;
                until = new Date(punishment.getEnd());
                break;
        }
        if(type!=null){
            API.getInstance().getInstance().punish("5cd40c80132cd11e", type, operatorId, playerId, reason, id, until);
        }
    }

}
