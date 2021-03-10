package io.purecore.mcplugin.bungee;

import io.purecore.api.call.ApiException;
import io.purecore.api.execution.SimplifiedExecution;
import io.purecore.api.versioning.Version;
import io.purecore.mcplugin.API;
import net.md_5.bungee.api.plugin.Plugin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Handler implements io.purecore.api.event.Handler {

    private Logger logger;
    private Plugin plugin;

    public Handler(Logger logger, Plugin plugin){
        this.logger=logger;
        this.plugin=plugin;
    }

    @Override
    public void onConnected() {
        this.logger.log(Level.INFO,"connected and authenticated");
    }

    @Override
    public void onConnecting() {
        this.logger.log(Level.INFO,"connecting");
    }

    @Override
    public void onAuthenticating() {
        this.logger.log(Level.INFO,"authenticating");
    }

    @Override
    public void onReconnecting() {
        this.logger.log(Level.FINE,"reconnecting");
    }

    @Override
    public void onConnectError() {
        this.logger.log(Level.FINE,"connection error");
    }

    @Override
    public void onSocketCreated() {
        this.logger.log(Level.INFO,"a data pipe has been created");
    }

    @Override
    public void onDisconnected() {
        this.logger.log(Level.WARNING,"disconnected");
    }

    @Override
    public void onNewExecutions() {
        this.logger.log(Level.INFO,"received pending executions notification");
        plugin.getProxy().getScheduler().runAsync(this.plugin, new Runnable() {
            @Override
            public void run() {
                try {
                    ArrayList<SimplifiedExecution> executions;
                    if(Instance.getCache().getExecutions().size()>0){
                        executions = API.instance.getInstance().getPendingExecutions(Instance.getCache().getExecutions().get(0));
                    } else {
                        executions = API.instance.getInstance().getPendingExecutions();
                    }
                    Instance.getCache().addExecutions(executions);
                    logger.log(Level.INFO, "Received "+executions.size() + " pending execution(s)");
                    Instance.getCache().runExecutions();
                } catch (IOException | ApiException e) {
                    logger.log(Level.WARNING,"Error while querying pending executions: " + e.getMessage());
                }
            }
        });
    }

    @Override
    public void onNewVersion(Version version) {
        Instance.checkVersion(version,this.plugin);
    }
}
