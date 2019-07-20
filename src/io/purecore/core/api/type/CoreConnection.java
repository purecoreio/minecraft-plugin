package io.purecore.core.api.type;

import java.util.UUID;

public class CoreInstance {


    UUID uuid;
    CoreLocation location;
    CoreConnectionStatus status;
    CorePlayer player;
    CoreInstance instance;

    public CoreInstance(String error, String msg)
    {

        this.error = error;
        this.msg = msg;

    }

    public String getError() {
        return error;
    }

    public String getMsg() {
        return msg;
    }
}
