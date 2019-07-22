package io.purecore.core.bungee;

import io.purecore.core.api.Core;
import io.purecore.core.api.exception.ServerApiError;
import io.purecore.core.api.type.CoreConnection;
import io.purecore.core.api.type.CoreKey;
import io.purecore.core.console.utils.Msgs;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.UUID;

public class Events implements Listener {


    @EventHandler
    public void onPostLogin(PostLoginEvent event){

        UUID uuid = event.getPlayer().getUniqueId();
        String name = event.getPlayer().getName();
        InetSocketAddress ip = event.getPlayer().getAddress();
        CoreKey key = new CoreKey(Main.keys.getString("keys.network"));

        ProxyServer.getInstance().getScheduler().runAsync(Main.plugin, () -> { // async call
            try{
                CoreConnection connection = Core.newConnection(ip,uuid,name,key);
                if(Main.debug){
                    Msgs.showWarning(Main.logger,"CONNECTION CREATION","Opened connection #"+connection.getUuid());
                }
            } catch (IOException | ServerApiError e) {
                Msgs.showError(Main.logger,"CONNECTION CREATION",e.getMessage());
            }
        });

    }

    @EventHandler
    public void onPlayerLeave (PlayerDisconnectEvent event){

        UUID uuid = event.getPlayer().getUniqueId();
        CoreKey key = new CoreKey(Main.keys.getString("keys.network"));

        ProxyServer.getInstance().getScheduler().runAsync(Main.plugin, () -> { // async call
            try {
                List<CoreConnection> closedConnections = Core.closePlayerConnections(uuid, key);
                if(Main.debug){
                    Msgs.showWarning(Main.logger,"CONNECTION DESTRUCTION","Closed "+closedConnections.size()+" connection(s)");
                }
            } catch (IOException | ServerApiError e) {
                Msgs.showError(Main.logger,"CONNECTION DESTRUCTION",e.getMessage());
            }
        });

    }

}
