package io.purecore.mcplugin.common.thirdPartyIntegration;

import io.purecore.api.call.ApiException;
import io.purecore.api.punishment.PunishmentType;
import io.purecore.mcplugin.API;
import litebans.api.Entry;
import org.json.JSONException;

import java.io.IOException;
import java.util.Date;
import java.util.UUID;

public class LiteBans {

    public static void handleEntryDelete(Entry entry) throws JSONException, ApiException, IOException {
        PunishmentType type = null;
        String id = String.valueOf(entry.getId());
        if(entry.getUuid()!=null){
            String playerId = UUID.fromString(entry.getUuid()).toString();
            String operatorId = null;
            if(entry.getExecutorUUID()!=null&&!entry.getExecutorUUID().toLowerCase().equals("console")){
                operatorId = UUID.fromString(entry.getExecutorUUID()).toString();
            }
            switch (entry.getType()){
                case "ban":
                    type = PunishmentType.Ban;
                    break;
                case "ipban":
                    type = PunishmentType.BanIP;
                    break;
                case "kick":
                    type = PunishmentType.Kick;
                    break;
                case "mute":
                    type = PunishmentType.Mute;
                    break;
                case "warning":
                case "warn":
                    type = PunishmentType.Warn;
                    break;
            }
            if(type!=null){
                API.getInstance().getInstance().unpunish("c21f73b997fac4d7", type, operatorId, playerId, id);
            }
        }
    }

    public static void handleEntryAdd(Entry entry) throws JSONException, ApiException, IOException {
        PunishmentType type = null;
        Date until = null;
        String id = String.valueOf(entry.getId());
        String reason = entry.getReason();
        if(entry.getUuid()!=null){
            String playerId = UUID.fromString(entry.getUuid()).toString();
            String operatorId = null;
            if(entry.getExecutorUUID()!=null&&!entry.getExecutorUUID().toLowerCase().equals("console")){
                operatorId = UUID.fromString(entry.getExecutorUUID()).toString();
            }
            switch (entry.getType()){
                case "ban":
                    type = PunishmentType.Ban;
                    if(entry.getDateEnd()>0) until = new Date(entry.getDateEnd());
                    break;
                case "ipban":
                    type = PunishmentType.BanIP;
                    if(entry.getDateEnd()>0) until = new Date(entry.getDateEnd());
                    break;
                case "kick":
                    type = PunishmentType.Kick;
                    if(entry.getDateEnd()>0) until = new Date(entry.getDateEnd());
                    break;
                case "mute":
                    type = PunishmentType.Mute;
                    if(entry.getDateEnd()>0) until = new Date(entry.getDateEnd());
                    break;
                case "warning":
                case "warn":
                    type = PunishmentType.Warn;
                    if(entry.getDateEnd()>0) until = new Date(entry.getDateEnd());
                    break;
            }
            if(type!=null){
                API.getInstance().getInstance().punish("c21f73b997fac4d7", type, operatorId, playerId, reason, id, until);
            }
        }
    }

}
