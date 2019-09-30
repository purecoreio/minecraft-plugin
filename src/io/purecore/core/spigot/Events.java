package io.purecore.core.spigot;

import io.purecore.core.api.type.CoreKey;
import io.purecore.core.console.utils.Msgs;
import io.purecore.core.spigot.Tasks.CloseSpigotConnections;
import io.purecore.core.spigot.Tasks.CreateSpigotConnection;
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

        if(Objects.requireNonNull(event.getPlayer().getAddress()).getAddress()!=null){

            UUID uuid = event.getPlayer().getUniqueId();
            String name = event.getPlayer().getName();
            InetSocketAddress ip = event.getPlayer().getAddress();
            CoreKey key = new CoreKey(Main.keys.getString("keys.server"));

            CreateSpigotConnection createSpigotConnection = new CreateSpigotConnection(Main.debug,Main.logger,ip,uuid,name,key);
            createSpigotConnection.start();

        } else {
            Msgs.showError(Main.logger,"CONNECTION CREATION","Received player address was invalid");
        }

    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event){

        UUID uuid = event.getPlayer().getUniqueId();
        CoreKey key = new CoreKey(Main.keys.getString("keys.server"));

        CloseSpigotConnections CloseSpigotConnections = new CloseSpigotConnections(Main.debug,Main.logger,uuid,key);
        CloseSpigotConnections.start();

    }

}
