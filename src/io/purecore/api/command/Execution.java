package io.purecore.api.command;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.purecore.api.Core;
import io.purecore.api.exception.ApiException;
import io.purecore.api.exception.CallException;
import io.purecore.api.request.ObjectRequest;

import java.io.IOException;
import java.util.*;

public class Execution extends Core{

    public Core core;
    public String uuid;
    public Command command;
    public Context context;
    public List<String> instances;
    public boolean  needsOnline;
    public List<String> executedOn;
    public boolean executed;

    public Execution(Core core, String execution_id, Command command, Context command_context, List<String> execute_on, boolean needs_online, List<String> executed_on, boolean executed)
    {
        super(core.getKey());
        this.core=core;
        this.uuid=execution_id;
        this.command=command;
        this.context=command_context;
        this.instances=execute_on;
        this.needsOnline=needs_online;
        this.executedOn=executed_on;
        this.executed=executed;
    }

    public Execution(Core core, JsonObject json)
    {
        super(core.getKey());
        this.core = core;

        String execution_id = json.get("uuid").getAsString();
        JsonObject command_info = json.get("command").getAsJsonObject();

        // cmd info

        String cmdId = null;
        if(!command_info.get("cmdId").isJsonNull()){
            cmdId=command_info.get("cmdId").getAsString();
        }
        String cmdStr = null;
        if(!command_info.get("cmdString").isJsonNull()){
            cmdStr=command_info.get("cmdString").getAsString();
        }


        Command core_command = new Command(cmdId,cmdStr);


        Context command_context = new Context(null,null,null,null,null,null,null,null);
        if(!json.get("commandContext").isJsonNull()){

            JsonObject command_context_info = json.get("commandContext").getAsJsonObject();

            // danger zone

            /* TO-DO: PLAYER OBJString context_core_id = null;
            if(!command_context_info.get("core").isJsonNull()){
                context_core_id = command_context_info.get("core").getAsString();
            } */

            String context_core_username = null;
            if(!command_context_info.get("legacyUsername").isJsonNull()){
                context_core_username = command_context_info.get("legacyUsername").getAsString();
            }

            UUID context_core_uuid = null;
            if(!command_context_info.get("legacyUuid").isJsonNull()){
                context_core_uuid = UUID.fromString(command_context_info.get("legacyUuid").getAsString());
            }

            String origin = null;
            if(!command_context_info.get("originType").isJsonNull()){
                origin = command_context_info.get("originType").getAsString();
            }

            String origin_name = null;
            if(!command_context_info.get("originName").isJsonNull()){
                origin_name = command_context_info.get("originName").getAsString();
            }

            String origin_id = null;
            if(!command_context_info.get("originId").isJsonNull()){
                origin_id = command_context_info.get("originId").getAsString();
            }

            String caused_by = null;
            if(!command_context_info.get("causedBy").isJsonNull()){
                caused_by = command_context_info.get("causedBy").getAsString();
            }

            Double quantity = null;
            if(!command_context_info.get("quantity").isJsonNull()){
                quantity = Double.valueOf(command_context_info.get("quantity").getAsString());
            }

            command_context = new Context(null,context_core_username,context_core_uuid,origin,origin_name,origin_id,caused_by,quantity);
        }

        // ---

        List<String> instance_list = new ArrayList<String>();
        if(!json.get("instances").isJsonNull()){

            JsonArray instance_list_array = json.get("instances").getAsJsonArray();
            instance_list_array.forEach((st) ->
            {
                if (st.isJsonPrimitive())
                {
                    instance_list.add(st.getAsString());
                }
            });

        }

        boolean needsOnline = json.get("needsOnline").getAsBoolean();
        List<String> instance_list_executed = new ArrayList<String>();
        JsonArray instance_list_executed_array = json.get("executedOn").getAsJsonArray();
        instance_list_executed_array.forEach((st) ->
        {
            if (st.isJsonPrimitive())
            {
                instance_list_executed.add(st.getAsString());
            }
        });

        boolean executed = json.get("executed").getAsBoolean();

        this.needsOnline=needsOnline;
        this.uuid=execution_id;
        this.command=core_command;
        this.context=command_context;
        this.instances=instance_list;
        this.executedOn=instance_list_executed;
        this.executed=executed;
    }

    public String getUuid() {
        return uuid;
    }

    public Boolean isExecuted() {
        return executed;
    }

    public Command getCommand() {
        return command;
    }

    public Context getContext() {
        return context;
    }

    public List<String> getExecutedOn() {
        return executedOn;
    }

    public List<String> getInstances() {
        return instances;
    }

    public boolean needsOnline() {
        return needsOnline;
    }

    public boolean markAsExecuted() throws IOException, ApiException, CallException {

        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put("execution", this.uuid);

        JsonObject executionResult = new ObjectRequest(this.core, ObjectRequest.Call.MARK_EXECUTION, params).getResult();

        if(executionResult.has("success")){

            this.executed=executionResult.get("success").getAsBoolean();
            return this.executed;

        } else {

            this.executed=false;
            throw new ApiException(executionResult.get("error").getAsString());

        }

    }
}
