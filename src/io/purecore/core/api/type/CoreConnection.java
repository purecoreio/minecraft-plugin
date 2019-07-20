package io.purecore.core.api.type;

import java.util.UUID;

public class CoreConnection {


    String uuid;
    CoreLocation location;
    CoreConnectionStatus status;
    CorePlayer player;
    CoreInstance instance;

    public CoreConnection(String instance_uuid, CoreLocation location, CoreConnectionStatus status, CorePlayer player, CoreInstance instance)
    {

        this.uuid=instance_uuid;
        this.location=location;
        this.status=status;
        this.player=player;
        this.instance=instance;

    }

    public String getUuid() {
        return uuid;
    }

    public CoreConnectionStatus getStatus() {
        return status;
    }

    public CoreInstance getInstance() {
        return instance;
    }

    public CoreLocation getLocation() {
        return location;
    }

    public CorePlayer getPlayer() {
        return player;
    }
}
