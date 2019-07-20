package io.purecore.core.api.type;

public class CoreError {

    String error;
    String msg;

    public CoreError(String error, String msg)
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
