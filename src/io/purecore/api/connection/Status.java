package io.purecore.api.connection;

public class Status {

    private boolean closed;
    private String closed_on;
    private String opened_on;

    public Status(boolean closed, String closed_on, String opened_on)
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
