package io.purecore.api.connection;

import com.google.gson.JsonObject;
import io.purecore.api.Core;
import io.purecore.api.instance.Instance;
import io.purecore.api.location.Location;
import io.purecore.api.user.Player;

import java.util.UUID;

public class Connection extends Core {

    Core core;
    String uuid;
    Location location;
    Status status;
    Player player;
    Instance instance;

    public Connection(Core core, String instance_uuid, Location location, Status status, Player player, Instance instance)
    {

        super(core.getKey());

        this.core=core;

        this.uuid=instance_uuid;
        this.location=location;
        this.status=status;
        this.player=player;
        this.instance=instance;
    }

    public Connection(Core core, JsonObject json){

        super(core.getKey());

        this.core=core;

        // player
        JsonObject playerdata = json.get("player").getAsJsonObject();
        io.purecore.api.user.Player player = new io.purecore.api.user.Player(this.core, playerdata.get("username").getAsString(), UUID.fromString(playerdata.get("uuid").getAsString()),playerdata.get("verified").getAsBoolean());

        // status
        JsonObject statusdata = json.get("status").getAsJsonObject();
        String closedon = null;
        boolean closed = false;
        if(!statusdata.get("closedOn").isJsonNull()){
            closed = true;
            closedon=statusdata.get("closedOn").getAsString();
        }
        Status status = new Status(closed,closedon,statusdata.get("openedOn").getAsString());

        // location
        JsonObject locationdata = json.get("location").getAsJsonObject();
        Location location = new Location(locationdata.get("country").getAsString(),locationdata.get("region").getAsString(),locationdata.get("city").getAsString());

        // instance
        JsonObject instancedata = json.get("instance").getAsJsonObject();
        String instancetypestring = instancedata.get("type").getAsString();
        Instance.Type instancetype = Instance.Type.valueOf(instancetypestring);
        Instance instance = new Instance(this.core,instancedata.get("uuid").getAsString(),instancedata.get("name").getAsString(),instancetype);

        // final format
        this.uuid=json.get("uuid").getAsString();
        this.location=location;
        this.status=status;
        this.player=player;
        this.instance=instance;
    }

    public Instance getInstance() {
        return instance;
    }

    public Location getLocation() {
        return location;
    }

    public Player getPlayer() {
        return player;
    }

    public Status getStatus() {
        return status;
    }

    public String getUuid() {
        return uuid;
    }
}
