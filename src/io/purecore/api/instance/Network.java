package io.purecore.api.instance;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.purecore.api.Core;
import io.purecore.api.exception.ApiException;
import io.purecore.api.exception.CallException;
import io.purecore.api.key.Key;
import io.purecore.api.punishment.Offence;
import io.purecore.api.request.ArrayRequest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Network extends Instance {

    public Core core;
    public String uuid;
    public String name;
    public Type type = Type.NTW;

    public Network(io.purecore.api.Core core, String uuid, String name, Type type) {
        super(core, uuid, name, type);
        this.core=core;
        this.uuid=uuid;
        this.name=name;
        this.type=type;
    }

    public Network(Core core, JsonObject json){
        super(core,null,null,Type.NTW);

        String uuid = null;
        if(!json.get("uuid").isJsonNull()){
            uuid=json.get("uuid").getAsString();
        }

        String name = null;
        if(!json.get("name").isJsonNull()){
            name=json.get("name").getAsString();
        }

        this.core=null;
        this.uuid=uuid;
        this.name=name;
        this.type=Type.NTW;
    }

    public Network(JsonObject json, Core core){
        super(core,json.get("uuid").getAsString(),json.get("name").getAsString(),Type.NTW);
        this.core=core;
        this.uuid=json.get("uuid").getAsString();
        this.name=json.get("name").getAsString();
        this.type=Type.NTW;
    }

    public List<Offence> getOffences() throws ApiException, IOException, CallException {
        List<Offence> finalList = new ArrayList<>();
        ArrayRequest request = new ArrayRequest(this.core, ArrayRequest.Call.GET_OFFENCE_LIST);
        JsonArray response = request.getResult();
        for (JsonElement offenceJson:response) {
            finalList.add(new Offence(offenceJson.getAsJsonObject()));
        }
        return finalList;
    }

    public List<Key> getAllKeys() throws ApiException, IOException, CallException {
        List<Key> keyList = new ArrayList<>();
        ArrayRequest request = new ArrayRequest(this.core, ArrayRequest.Call.GET_ALL_KEYS);
        JsonArray response = request.getResult();
        for (JsonElement key:response) {
            JsonObject keyJson = key.getAsJsonObject();
            keyList.add(new Key(keyJson));
        }
        return keyList;
    }



}
