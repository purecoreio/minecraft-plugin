package io.purecore.core.api.type;

import java.util.UUID;

public class CoreCommandContext {

    String core_id;
    String username;
    UUID uuid;
    String origin;
    String origin_name;
    String origin_id;
    String caused_by;
    Double quantity;

    public CoreCommandContext(String core_id, String username, UUID uuid, String origin, String origin_name, String origin_id, String caused_by, Double quantity)
    {

        this.core_id=core_id;
        this.username=username;
        this.uuid=uuid;
        this.origin=origin;
        this.origin_name=origin_name;
        this.origin_id=origin_id;
        this.caused_by=caused_by;
        this.quantity=quantity;

    }

    public UUID getUuid() {
        return uuid;
    }

    public Double getQuantity() {
        return quantity;
    }

    public String getCaused_by() {
        return caused_by;
    }

    public String getCore_id() {
        return core_id;
    }

    public String getOrigin() {
        return origin;
    }

    public String getOrigin_id() {
        return origin_id;
    }

    public String getOrigin_name() {
        return origin_name;
    }

    public String getUsername() {
        return username;
    }
}
