package io.purecore.api.key;

import com.google.gson.JsonObject;
import io.purecore.api.Core;
import io.purecore.api.exception.ApiException;
import io.purecore.api.exception.CallException;
import io.purecore.api.instance.Instance;
import io.purecore.api.request.ObjectRequest;

import java.io.IOException;

public class Key {

    private Type keyType;
    public enum Type {
        NTW,
        SVR,
        DEV,
        DSC,
        UKN
    }
    private String uuid;
    private String hash;
    private Instance instance;

    public Key(String hash){

        this.hash=hash;

    }

    public String regenHash() throws ApiException, IOException, CallException {

        Core core = new Core(this.hash);
        JsonObject request = new ObjectRequest(core, ObjectRequest.Call.REGEN_HASH).getResult();
        this.hash=request.get("hash").getAsString();
        return this.hash;

    }

}
