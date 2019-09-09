package io.purecore.core.api.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.purecore.core.api.type.*;

import java.util.ArrayList;
import java.util.List;
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

    public static CoreExecution executionFromJSON(JsonObject core_execution_json){
        JsonObject execution  = core_execution_json;
        String execution_id = execution.get("uuid").getAsString(); // ok
        JsonObject command_info = execution.get("command").getAsJsonObject();
        CoreCommand core_command = new CoreCommand(command_info.get("uuid").getAsString(),command_info.get("string").getAsString()); // ok
        JsonObject command_context_info = execution.get("context").getAsJsonObject();

        // danger zone
        String context_core_id = null;
        if(!command_context_info.get("core").isJsonNull()){
            context_core_id = command_context_info.get("core").getAsString();
        }

        String context_core_username = null;
        if(!command_context_info.get("username").isJsonNull()){
            context_core_username = command_context_info.get("username").getAsString();
        }

        UUID context_core_uuid = null;
        if(!command_context_info.get("uuid").isJsonNull()){
            context_core_uuid = UUID.fromString(command_context_info.get("uuid").getAsString());
        }

        String origin = null;
        if(!command_context_info.get("origin").isJsonNull()){
            origin = command_context_info.get("origin").getAsString();
        }

        String origin_name = null;
        if(!command_context_info.get("origin_name").isJsonNull()){
            origin_name = command_context_info.get("origin_name").getAsString();
        }

        String origin_id = null;
        if(!command_context_info.get("origin_id").isJsonNull()){
            origin_id = command_context_info.get("origin_id").getAsString();
        }

        String caused_by = null;
        if(!command_context_info.get("caused_by").isJsonNull()){
            caused_by = command_context_info.get("caused_by").getAsString();
        }

        Double quantity = null;
        if(!command_context_info.get("quantity").isJsonNull()){
            quantity = Double.valueOf(command_context_info.get("quantity").getAsString());
        }

        // ---

        CoreCommandContext command_context = new CoreCommandContext(context_core_id,context_core_username,context_core_uuid,origin,origin_name,origin_id,caused_by,quantity);
        List<String> instance_list = new ArrayList<String>();
        JsonArray instance_list_array = execution.get("instances").getAsJsonArray();
        instance_list_array.forEach((st) ->
        {
            if (st.isJsonPrimitive())
            {
                instance_list.add(st.getAsString());
            }
        });
        boolean needsOnline = execution.get("needsOnline").getAsBoolean();
        List<String> instance_list_executed = new ArrayList<String>();
        JsonArray instance_list_executed_array = execution.get("executedOn").getAsJsonArray();
        instance_list_executed_array.forEach((st) ->
        {
            if (st.isJsonPrimitive())
            {
                instance_list_executed.add(st.getAsString());
            }
        });
        boolean executed = execution.get("executed").getAsBoolean();

        CoreExecution final_core_execution = new CoreExecution(execution_id,core_command,command_context,instance_list,needsOnline,instance_list_executed,executed,quantity);
        return final_core_execution;
    }

}
