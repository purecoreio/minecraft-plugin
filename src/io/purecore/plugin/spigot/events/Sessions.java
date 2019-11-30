package io.purecore.plugin.spigot.events;

import io.purecore.api.connection.Connection;
import io.purecore.api.exception.ApiException;
import io.purecore.api.exception.CallException;
import io.purecore.api.user.Player;
import io.purecore.plugin.spigot.Main;
import io.purecore.plugin.util.Logging;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class Sessions implements Listener {

    @EventHandler
    public void PlayerJoinEvent(final PlayerJoinEvent event) throws CallException, ApiException, IOException {

        Main.plugin.getServer().getScheduler().runTaskAsynchronously(Main.plugin, new Runnable() {
            @Override
            public void run() {
                try {
                    Player player = new Player(Main.core,event.getPlayer().getName(),event.getPlayer().getUniqueId(),false);
                    Connection connInfo = Main.core.getInstance().openConnection(player, Objects.requireNonNull(event.getPlayer().getAddress()));
                    Logging.logError(Main.plugin.getLogger(), Logging.Class.SESSIONS, "Opened #"+connInfo.getUuid()+" by "+ connInfo.getPlayer().getUsername()+ " (from "+connInfo.getLocation().getRegion()+", "+connInfo.getLocation().getCountry()+")");
                } catch (ApiException | IOException | CallException e) {
                    Logging.logError(Main.plugin.getLogger(), Logging.Class.SESSIONS, "Error while creating session: "+e.getMessage());
                }
            }
        });

    }

    @EventHandler
    public void PlayerQuitEvent(PlayerQuitEvent event){

        Main.plugin.getServer().getScheduler().runTaskAsynchronously(Main.plugin, new Runnable() {
            @Override
            public void run() {
                try {
                    Player player = new Player(Main.core,event.getPlayer().getName(),event.getPlayer().getUniqueId(),false);
                    List<Connection> connections = Main.core.getInstance().closeConnections(player);
                    if(Main.settings.shouldDebug()){
                        for (Connection connection:connections) {
                            Logging.logError(Main.plugin.getLogger(), Logging.Class.SESSIONS, "Closed #"+connection.getUuid()+" (by "+ connection.getPlayer().getUsername()+")");
                        }
                    }
                } catch (ApiException | IOException | CallException e) {
                    Logging.logError(Main.plugin.getLogger(), Logging.Class.SESSIONS, "Error while closing session: "+e.getMessage());
                }
            }
        });

    }
}
