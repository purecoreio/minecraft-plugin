package io.purecore.mcplugin.bungee.task;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import io.purecore.mcplugin.bungee.Main;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class BroadcastSettings implements Runnable {
    @Override
    public void run() {
        
        Collection<ProxiedPlayer> proxiedPlayers = ProxyServer.getInstance().getPlayers();
        
        // will try to establish a socket list to synced instances
        Map<String,ProxiedPlayer> matchingServers = new HashMap<>();
        for (ProxiedPlayer proxiedPlayer:proxiedPlayers) {
            if(!matchingServers.containsKey(proxiedPlayer.getServer().getInfo().getName())&& Main.serverInterface.containsKey(proxiedPlayer.getServer().getInfo().getName())){
                matchingServers.put(proxiedPlayer.getServer().getInfo().getName(),proxiedPlayer);
            }
        }
        
        // will broadcast the key to each instance from the player socket
        for(Map.Entry<String, ProxiedPlayer> entry : matchingServers.entrySet()) {

            ByteArrayDataOutput out = ByteStreams.newDataOutput();
            out.writeUTF( "key" );
            out.writeUTF( Main.serverInterface.get(entry.getKey()).getHash() );
            entry.getValue().getServer().getInfo().sendData("purecore:setup",out.toByteArray());

        }
        
    }
}
