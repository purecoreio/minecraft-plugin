package io.purecore.mcplugin;

import io.purecore.api.execution.SimplifiedExecution;

import java.util.List;

public class CacheData {

    public List<SimplifiedExecution> executions;

    public List<SimplifiedExecution> getExecutions(){
        return this.executions;
    }

    public void addExecution(SimplifiedExecution execution){
        this.executions.add(0,execution);
    }

    public void removeExecution(SimplifiedExecution execution){
        this.executions.remove(execution);
    }
    
}
