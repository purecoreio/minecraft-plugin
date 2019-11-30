package io.purecore.api.command;

import java.util.UUID;

public class Context {

    public String core_id;
    public String username;
    public UUID uuid;
    public String origin;
    public String origin_name;
    public String origin_id;
    public String caused_by;
    public Double quantity;

    public Context(String coreid, String username, UUID uuid, String origin, String origin_name, String origin_id, String caused_by, Double quantity)
    {

        this.core_id=coreid;
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
