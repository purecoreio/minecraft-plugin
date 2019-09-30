package io.purecore.core.bungee;

import io.purecore.core.api.type.CoreKey;
import io.purecore.core.bungee.Tasks.CloseBungeeConnections;
import io.purecore.core.bungee.Tasks.CreateBungeeConnection;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.net.InetSocketAddress;
import java.util.UUID;

public class Events implements Listener {


    @EventHandler
    public void onPostLogin(PostLoginEvent event){

        UUID uuid = event.getPlayer().getUniqueId();
        String name = event.getPlayer().getName();
        InetSocketAddress ip = event.getPlayer().getAddress();
        CoreKey key = new CoreKey(Main.keys.getString("keys.network"));

        CreateBungeeConnection openBungeeConnection = new CreateBungeeConnection(Main.debug,Main.logger,ip,uuid,name,key);
        openBungeeConnection.start();

    }

    @EventHandler
    public void onPlayerLeave(PlayerDisconnectEvent event){

        UUID uuid = event.getPlayer().getUniqueId();
        CoreKey key = new CoreKey(Main.keys.getString("keys.network"));

        CloseBungeeConnections closeBungeeConnections = new CloseBungeeConnections(Main.debug,Main.logger,uuid,key);
        closeBungeeConnections.start();

    }

}
