package io.purecore.core.api.type;

import java.util.List;

public class CoreExecution {

    String uuid;
    // TO-DO creation
    CoreCommand command;
    CoreCommandContext context;
    List<String> instances;
    boolean  needsOnline;
    List<String> executedOn;
    boolean executed;

    public CoreExecution(String execution_id, CoreCommand command, CoreCommandContext command_context, List<String> execute_on, boolean needs_online, List<String> executed_on, boolean executed, Double quantity)
    {

        this.uuid=execution_id;
        this.command=command;
        this.context=command_context;
        this.instances=execute_on;
        this.needsOnline=needs_online;
        this.executedOn=executed_on;
        this.executed=executed;

    }

    public String getUuid() {
        return uuid;
    }

    public Boolean isExecuted() {
        return executed;
    }

    public CoreCommand getCommand() {
        return command;
    }

    public CoreCommandContext getContext() {
        return context;
    }

    public List<String> getExecutedOn() {
        return executedOn;
    }

    public List<String> getInstances() {
        return instances;
    }

    public boolean needsOnline() {
        return needsOnline;
    }
}
