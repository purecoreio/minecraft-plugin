package io.purecore.api.instance;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.purecore.api.Core;
import io.purecore.api.command.Execution;
import io.purecore.api.connection.Connection;
import io.purecore.api.exception.ApiException;
import io.purecore.api.exception.CallException;
import io.purecore.api.request.ArrayRequest;
import io.purecore.api.request.ObjectRequest;
import io.purecore.api.user.Player;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.*;

public class Instance extends io.purecore.api.Core {

    private Core core;
    private String uuid;
    private String name;
    private Type instanceType;
    public enum Type {
        NTW,
        SVR,
        UNK
    }

    public Instance(Core core, String uuid, String name, Type type) {
        super(core.getKey());
        this.core=core;
        this.uuid=uuid;
        this.name=name;
        this.instanceType=type;
    }

    public Connection openConnection(Player player, InetSocketAddress address) throws ApiException, IOException, CallException {

        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put("ip", address.getAddress().getHostAddress());
        params.put("uuid", player.getUUID().toString());
        params.put("username", player.getUsername());

        JsonObject result = new ObjectRequest(this.core, ObjectRequest.Call.CONNECTION_CREATE, params).getResult();
        return new Connection(this.core,result);

    }



    public List<Connection> closeConnections(Player player) throws ApiException, IOException, CallException {

        List<Connection> connectionList = new ArrayList<>();

        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put("uuid", player.getUUID().toString());

        JsonArray result = new ArrayRequest(this.core, ArrayRequest.Call.CLOSE_ACTIVE_CONNECTIONS, params).getResult();
        for (JsonElement connectionJson:result) {
            connectionList.add(new Connection(this.core,connectionJson.getAsJsonObject()));
        }

        return connectionList;

    }

    public Instance(Core core) throws ApiException, IOException, CallException {

        super(core.getKey());
        this.core=core;

        ObjectRequest request = new ObjectRequest(this.core, ObjectRequest.Call.INSTANCE_GET);
        JsonElement response = request.getResult();
        if(response.getAsJsonObject().has("server")){

            this.instanceType=Type.SVR;
            JsonObject responseJson = response.getAsJsonObject().get("server").getAsJsonObject();
            this.uuid = responseJson.get("uuid").getAsString();
            this.name = responseJson.get("name").getAsString();

        } else if(response.getAsJsonObject().has("network")){

            this.instanceType=Type.NTW;
            JsonObject responseJson = response.getAsJsonObject().get("network").getAsJsonObject();
            this.uuid = responseJson.get("uuid").getAsString();
            this.name = responseJson.get("name").getAsString();

        } else {

            this.instanceType=Type.UNK;
            this.uuid = null;
            this.name = null;

        }

    }

    public List<Execution> getPendingExecutions() throws ApiException, IOException, CallException {

        List<Execution> pendingExecutions = new ArrayList<>();

        JsonArray executionResult = new ArrayRequest(this.core, ArrayRequest.Call.GET_PENDING_EXECUTIONS).getResult();
        for (JsonElement jsonExecution:executionResult) {
            if(jsonExecution.isJsonObject()){
                pendingExecutions.add(new Execution(this.core, jsonExecution.getAsJsonObject()));
            }
        }

        return pendingExecutions;

    }

    public List<Execution> getPendingExecutions(List<Player> playerList) throws ApiException, IOException, CallException {

        List<Execution> pendingExecutions = new ArrayList<>();
        List<HashMap<String, String>> preSerializedList = new ArrayList<>();

        for (Player player:playerList) {

            String username = player.getUsername();
            UUID uuid = player.getUUID();

            HashMap<String, String> playerInfo = new HashMap<>();
            playerInfo.put("uuid",uuid.toString());
            playerInfo.put("username",username);
            preSerializedList.add(playerInfo);

        }

        String playerListSerialized = new Gson().toJson(preSerializedList);

        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put("players", playerListSerialized);

        JsonArray executionResult = new ArrayRequest(this.core, ArrayRequest.Call.GET_PENDING_EXECUTIONS_REDUCED, params).getResult();

        for (JsonElement jsonExecution:executionResult) {
            if(jsonExecution.isJsonObject()){
                pendingExecutions.add(new Execution(this.core, jsonExecution.getAsJsonObject()));
            }
        }

        return pendingExecutions;
    }

    public Settings getDefaultSettings(){
        return new Settings(this,1200,false,false,true,true);
    }

    public Network asNetwork(){
        return new Network(this.core,this.uuid,this.name,this.instanceType);
    }

    public Server asServer(){
        return new Server(this.core,this.uuid,this.name,this.instanceType);
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return uuid;
    }

    public Core getCore() {
        return core;
    }

    public Type getType() {
        return instanceType;
    }
}
