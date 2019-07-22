package io.purecore.core.spigot;

import io.purecore.core.api.Core;
import io.purecore.core.api.exception.ServerApiError;
import io.purecore.core.api.type.CoreConnection;
import io.purecore.core.api.type.CoreKey;
import io.purecore.core.console.utils.Msgs;
import net.md_5.bungee.event.EventHandler;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.List;
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

            Bukkit.getScheduler().runTaskAsynchronously(Main.plugin, () -> { // async call
                try{
                    CoreConnection connection = Core.newConnection(ip,uuid,name,key);
                    if(Main.debug){
                        Msgs.showWarning(Main.logger,"CONNECTION CREATION","Opened connection #"+connection.getUuid());
                    }
                } catch (IOException | ServerApiError e) {
                    Msgs.showError(Main.logger,"CONNECTION CREATION",e.getMessage()+". Please, review your keys.yml file and make sure to review your server and network keys");
                }
            });

        } else {
            Msgs.showError(Main.logger,"CONNECTION CREATION","Received player address was invalid");
        }

    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event){

        UUID uuid = event.getPlayer().getUniqueId();
        CoreKey key = new CoreKey(Main.keys.getString("keys.server"));

        Bukkit.getScheduler().runTaskAsynchronously(Main.plugin, () -> { // async call
            try {
                List<CoreConnection> closedConnections = Core.closePlayerConnections(uuid, key);
                if(Main.debug){
                    Msgs.showWarning(Main.logger,"CONNECTION DESTRUCTION","Closed "+closedConnections.size()+" connection(s)");
                }
            } catch (IOException | ServerApiError e) {
                Msgs.showError(Main.logger,"CONNECTION DESTRUCTION",e.getMessage()+". Please, review your keys.yml file and make sure to review your server and network keys");
            }
        });

    }

}
