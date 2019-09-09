package io.purecore.core.api.type;

public class CoreCommand {


    String uuid;
    String string;

    public CoreCommand(String uuid, String string)
    {

        this.uuid=uuid;
        this.string=string;

    }

    public String getUuid() {
        return uuid;
    }
    public String getString() {
        return string;
    }
}