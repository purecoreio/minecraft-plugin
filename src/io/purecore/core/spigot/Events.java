package io.purecore.core.spigot;

import io.purecore.core.api.type.CoreKey;
import io.purecore.core.console.utils.Msgs;
import io.purecore.core.spigot.Tasks.CloseSpigotConnections;
import io.purecore.core.spigot.Tasks.CreateSpigotConnection;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.net.InetSocketAddress;
import java.util.Objects;
import java.util.UUID;

public class Events implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event)
    {

        UUID uuid = event.getPlayer().getUniqueId();
        String name = event.getPlayer().getName();
        InetSocketAddress ip = event.getPlayer().getAddress();
        CoreKey key = new CoreKey(Main.keys.getString("keys.server"));

        new CreateSpigotConnection(Main.plugin,Main.debug,Main.logger,ip,uuid,name,key).run();

    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event)
    {

        UUID uuid = event.getPlayer().getUniqueId();
        CoreKey key = new CoreKey(Main.keys.getString("keys.server"));

        new CloseSpigotConnections(Main.plugin, Main.debug,Main.logger,uuid,key).run();

    }

}
