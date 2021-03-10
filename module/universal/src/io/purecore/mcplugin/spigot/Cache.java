package io.purecore.mcplugin.spigot;

import com.google.gson.Gson;
import io.purecore.api.call.ApiException;
import io.purecore.api.execution.ExecutionSuccess;
import io.purecore.api.execution.SimplifiedExecution;
import io.purecore.mcplugin.CacheData;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

public class Cache {

    public CacheData data;
    public File file;
    public Plugin plugin;

    public Cache(File file, Plugin plugin) throws FileNotFoundException {
        this.file = file;
        this.plugin=plugin;
        this.data = new CacheData();
        this.data.executions=new ArrayList<>();
        CacheData cache = new Gson().fromJson(new FileReader(file), CacheData.class);
        this.data.executions=cache.executions;
    }

    public Cache(File file, net.md_5.bungee.api.plugin.Plugin plugin) throws FileNotFoundException {
        this.file = file;
        this.data = new CacheData();
        this.data.executions=new ArrayList<>();
        CacheData cache = new Gson().fromJson(new FileReader(file), CacheData.class);
        this.data.executions=cache.executions;
    }

    public void addExecutions(List<SimplifiedExecution> executions) throws IOException {
        for (int i = 0; i < executions.size(); i++) {
            SimplifiedExecution execution = executions.get(executions.size()-1-i);
            if(!this.data.executions.contains(execution)){
                this.data.executions.add(0,execution);
            }
        }
        this.save();
    }

    public void removeExecutions(List<SimplifiedExecution> toRemove) throws IOException {
        for (SimplifiedExecution simplifiedExecution : toRemove) {
            this.data.executions.remove(simplifiedExecution);
        }
        this.save();
    }

    private void save() throws IOException {
        FileWriter writer = new FileWriter(file);
        writer.write(this.asString());
        writer.close();
    }

    private String asString(){
        return new Gson().toJson(this.data);
    }

    public List<SimplifiedExecution> getExecutions() {
        return data.executions;
    }

    public void runExecutions() throws IOException, ApiException {
        List<SimplifiedExecution> toExecute = new ArrayList<>();
        for (SimplifiedExecution execution:this.data.executions) {
            if(execution.hasTemplate()){
                if(execution.getTemplate().requiresOnline() && execution.getContext() != null && (execution.getContext().getId()!=null || execution.getContext().getUsername()!=null)){
                    if((execution.getContext().getId()!=null && plugin.getServer().getPlayer(UUID.fromString(execution.getContext().getId()))!=null) || (execution.getContext().getUsername()!=null && plugin.getServer().getPlayer(execution.getContext().getUsername())!=null)){
                        toExecute.add(execution);
                    }
                } else {
                    toExecute.add(execution);
                }
            } else {
                toExecute.add(execution);
            }
        }
        if(toExecute.size()>0) {
            ExecutionSuccess result = new ExecutionSuccess(toExecute);
            this.removeExecutions(toExecute);
            plugin.getServer().getScheduler().runTask(plugin, () -> {
                plugin.getLogger().log(Level.INFO,"Executed "+result.success.size()+" execution(s), skipped " + result.fail.size());
                for (SimplifiedExecution execution:result.success) {
                    Bukkit.dispatchCommand(plugin.getServer().getConsoleSender(), execution.string);
                }
            });
        }
    }

}
