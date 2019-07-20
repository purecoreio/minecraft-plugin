package io.purecore.core.api.type;

public class CoreConnectionStatus {

    boolean closed;
    String closed_on;
    String opened_on;

    public CoreConnectionStatus(boolean closed, String closed_on, String opened_on)
    {

        this.closed=closed;
        this.closed_on=closed_on;
        this.opened_on=opened_on;

    }

    public boolean isClosed() {
        return closed;
    }

    public String getClosed_on() {
        return closed_on;
    }

    public String getOpened_on() {
        return opened_on;
    }
}
