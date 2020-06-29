package io.purecore.api.connection;

import com.google.gson.Gson;

public class ConnectionRequest {

    String username;
    String uuid;
    String ip;

    public ConnectionRequest(String username, String uuid, String ip){
        this.username=username;
        this.uuid=uuid;
        this.ip=ip;
    }

    public String toJson(){
        Gson gson = new Gson();
        return gson.toJson(this);
    }

}
