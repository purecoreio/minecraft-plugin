package io.purecore.plugin.bungee.events;

import io.purecore.api.connection.Connection;
import io.purecore.api.exception.ApiException;
import io.purecore.api.exception.CallException;
import io.purecore.api.user.Player;
import io.purecore.plugin.bungee.Main;
import io.purecore.plugin.util.Logging;
import net.md_5.bungee.api.event.ServerConnectedEvent;
import net.md_5.bungee.api.event.ServerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.io.IOException;
import java.util.List;

public class Sessions implements Listener {

    @EventHandler
    public void serverSwitch(ServerConnectedEvent e){
        Main.plugin.getProxy().getScheduler().runAsync(Main.plugin, () -> {
            try {
                if(Main.serverInterface.containsKey(e.getServer().getInfo().getName())){
                    Connection connInfo = Main.serverInterface.get(e.getServer().getInfo().getName()).getInstance().openConnection(e.getPlayer());
                    if(Main.debug){
                        Logging.logInfo(Main.plugin.getLogger(), Logging.Class.SESSIONS, "Opened #"+connInfo.getUuid()+" by "+ connInfo.getPlayer().getUsername()+ " (from "+connInfo.getLocation().getRegion()+", "+connInfo.getLocation().getCountry()+")");
                    }
                }
            } catch (ApiException | IOException | CallException ex) {
                Logging.logError(Main.plugin.getLogger(), Logging.Class.SESSIONS,"Error while opening a connection for "+e.getPlayer().getName()+ ": "+ex.getMessage());
            }
        });
    }

    @EventHandler
    public void leaveServer(ServerDisconnectEvent e){
        Main.plugin.getProxy().getScheduler().runAsync(Main.plugin, () -> {
            try {
                if(Main.serverInterface.containsKey(e.getPlayer().getServer().getInfo().getName())){

                    Player player = new Player(Main.core,e.getPlayer().getName(),e.getPlayer().getUniqueId(),false);
                    List<Connection> connectionList = Main.serverInterface.get(e.getPlayer().getServer().getInfo().getName()).getInstance().closeConnections(player);
                    if(Main.debug){
                        for (Connection connection:connectionList) {
                            Logging.logInfo(Main.plugin.getLogger(), Logging.Class.SESSIONS, "Closed #"+connection.getUuid()+" (by "+ connection.getPlayer().getUsername()+")");
                        }
                    }
                }
            } catch (ApiException | IOException | CallException ex) {
                Logging.logError(Main.plugin.getLogger(), Logging.Class.SESSIONS,"Error while closing the connections for "+e.getPlayer().getName()+ ": "+ex.getMessage());
            }
        });
    }

}
