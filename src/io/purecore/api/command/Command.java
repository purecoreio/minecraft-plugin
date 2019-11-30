package io.purecore.api.command;

public class Command {

    public String uuid;
    public String string;

    public Command(String uuid, String string)
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
