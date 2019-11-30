package io.purecore.api.punishment;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.purecore.api.instance.Network;

public class Offence {

    public String uuid;
    public enum Type {
        GMP,
        CHT,
        UNK
    }
    public Type type;
    public Network network;
    public String name;
    public String description;
    public int negativePoints;

    public Offence(String uuid, Type type, Network network, String name, String description, int negativePoints){
        this.uuid=uuid;
        this.type=type;
        this.network=network;
        this.name=name;
        this.description=description;
        this.negativePoints=negativePoints;
    }

    public Offence(JsonObject json){
        this.uuid=json.get("uuid").getAsString();
        this.type=Type.valueOf(json.get("type").getAsString());
        this.network=new Network(json.get("network").getAsJsonObject());
        this.name=json.get("name").getAsString();
        this.description=json.get("description").getAsString();
        this.negativePoints=Integer.parseInt(json.get("negativePoints").getAsString());
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getNegativePoints() {
        return negativePoints;
    }

    public String getId() {
        return uuid;
    }

    public Type getType() {
        return type;
    }

    public Network getNetwork() {
        return network;
    }
}
