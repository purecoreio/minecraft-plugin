package io.purecore.api.instance;

public class Settings extends Instance{

    private int checkFrequency;
    private boolean pushAdvancements;
    private boolean pushStatistics;
    private boolean createSessions;
    private boolean debug;

    public Settings (Instance instance, Integer checkFrequency, boolean pushAdvancements, boolean pushStatistics, boolean createSessions, boolean debug){

        super(instance.getCore(),instance.getId(),instance.getName(),instance.getType());
        this.checkFrequency=checkFrequency;
        this.pushAdvancements=pushAdvancements;
        this.pushStatistics=pushStatistics;
        this.createSessions=createSessions;
        this.debug=debug;

    }

    public int getCheckFrequency() {
        return checkFrequency;
    }

    public boolean shouldCreateSessions() {
        return createSessions;
    }

    public boolean shouldPushAdvancements() {
        return pushAdvancements;
    }

    public boolean shouldPushStatistics() {
        return pushStatistics;
    }

    public boolean shouldDebug() {
        return debug;
    }

}
