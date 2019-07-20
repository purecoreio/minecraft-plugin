package io.purecore.core.api.type;

import java.util.UUID;

public class CorePlayer {

    String username;
    UUID uuid;
    boolean verified;

    public CorePlayer(String username, UUID uuid, boolean verified)
    {

        this.username = username;
        this.uuid = uuid;
        this.verified = verified;

    }

    public String getUsername() {
        return username;
    }

    public UUID getUuid() {
        return uuid;
    }

    public boolean isVerified() {
        return verified;
    }
}
