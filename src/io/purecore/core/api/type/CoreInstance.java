package io.purecore.core.api.type;

public class CoreInstance {


    private final Object InstanceType;

    public enum InstanceType {
        NTW,
        SVR,
        DEV
    }

    String uuid;
    String name;

    public CoreInstance(InstanceType type, String instance_uuid, String name)
    {

        this.InstanceType = type;
        this.uuid = instance_uuid;
        this.name = name;

    }

    public Object getInstanceType() {
        return InstanceType;
    }

    public String getName() {
        return name;
    }

    public String getUuid() {
        return uuid;
    }
}
