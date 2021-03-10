package io.purecore.mcplugin.bungee.events;

import io.purecore.api.call.ApiException;
import io.purecore.mcplugin.bungee.Instance;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BungeeHandler implements Listener {

    Plugin plugin;

    public BungeeHandler(Plugin plugin){
        this.plugin=plugin;
    }

    @EventHandler
    public void onPostLogin(PostLoginEvent event) {
        plugin.getProxy().getScheduler().runAsync(plugin, new Runnable() {
            @Override
            public void run() {
                try {
                    Instance.getCache().runExecutions();
                } catch (IOException | ApiException e) {
                    plugin.getLogger().log(Level.WARNING,"Error while executing commands: " + e.getMessage());
                }
            }
        });
    }
}
