package io.purecore.core.api.util;

import com.google.gson.JsonObject;
import io.purecore.core.api.type.*;

import java.util.UUID;

public class JsonTransform {

    public static CoreConnection connectionFromJSON(JsonObject core_connection_json){

        // player
        JsonObject playerdata = core_connection_json.get("player").getAsJsonObject();
        CorePlayer player = new CorePlayer(playerdata.get("username").getAsString(), UUID.fromString(playerdata.get("uuid").getAsString()),playerdata.get("verified").getAsBoolean());

        // status
        JsonObject statusdata = core_connection_json.get("status").getAsJsonObject();
        String closedon = null;
        if(!statusdata.get("closedOn").isJsonNull()){
            closedon=statusdata.get("closedOn").getAsString();
        }
        CoreConnectionStatus status = new CoreConnectionStatus(statusdata.get("closed").getAsBoolean(),closedon,statusdata.get("openedOn").getAsString());

        // location
        JsonObject locationdata = core_connection_json.get("location").getAsJsonObject();
        CoreLocation location = new CoreLocation(locationdata.get("country").getAsString(),locationdata.get("region").getAsString(),locationdata.get("city").getAsString());

        // instance
        JsonObject instancedata = core_connection_json.get("instance").getAsJsonObject();
        String instancetypestring = instancedata.get("type").getAsString();
        CoreInstance.InstanceType instancetype;
        if(instancetypestring == "NTW"){
            instancetype = CoreInstance.InstanceType.NTW;
        } else if(instancetypestring == "SVR"){
            instancetype = CoreInstance.InstanceType.SVR;
        } else {
            instancetype = CoreInstance.InstanceType.DEV;
        }
        CoreInstance instance = new CoreInstance(instancetype,instancedata.get("uuid").getAsString(),instancedata.get("name").getAsString());

        // final format
        CoreConnection connection = new CoreConnection(core_connection_json.get("uuid").getAsString(),location,status,player,instance);
        return connection;
    }

}
