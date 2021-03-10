package io.purecore.mcplugin.spigot.events;

import io.purecore.api.call.ApiException;
import io.purecore.mcplugin.API;
import io.purecore.mcplugin.spigot.Instance;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import org.json.JSONException;

import java.io.IOException;
import java.util.Objects;
import java.util.logging.Level;

public class BukkitHandler implements Listener {

    private Plugin plugin;

    public BukkitHandler(Plugin plugin){
        this.plugin=plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event){
        Bukkit.getServer().getScheduler().runTaskAsynchronously(this.plugin, new Runnable() {
            @Override
            public void run() {
                try {
                    API.instance.getInstance().connect(event.getPlayer().getUniqueId().toString(),event.getPlayer().getName(), Objects.requireNonNull(event.getPlayer().getAddress()).getAddress().toString().replace("/",""));
                    Instance.getCache().runExecutions();
                } catch (IOException | ApiException | JSONException | NullPointerException e) {
                    plugin.getLogger().log(Level.WARNING, "There was an error while creating a player connection: " + e.getMessage());
                }
            }
        });
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event){
        Bukkit.getServer().getScheduler().runTaskAsynchronously(this.plugin, new Runnable() {
            @Override
            public void run() {
                try {
                    API.instance.getInstance().disconnect(event.getPlayer().getUniqueId().toString());
                    if(event.getPlayer().isBanned()){

                    }
                } catch (IOException | ApiException | JSONException e) {
                    plugin.getLogger().log(Level.WARNING, "There was an error while closing a player connection: " + e.getMessage());
                }
            }
        });
    }

}
