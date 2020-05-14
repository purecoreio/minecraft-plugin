package io.purecore.mcplugin.sponge;

import com.google.inject.Inject;
import io.purecore.api.Core;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.plugin.Plugin;

import java.util.logging.Logger;

@Plugin(id = "purecore", name = "purecore", version = "1.0", description = "purecore is a global data processor for the gaming industry. It helps keeping all the data from a lot of gaming communities organized. Our panel allows the community managers to monetize their communities as well as offer a lot of automated features thanks to big data")
public class Main {

    Core core = new Core();
    boolean debug=true;

    @Inject
    private Logger logger;

    @Listener
    public void onServerStart(GameStartedServerEvent event) {
        logger.info("Hey! Sorry! This is not ready yet! Feel free to submit your code in our bitbucket repo to help port purecore to sponge");
    }

    /*
    @Listener
    public void onPlayerJoin(ClientConnectionEvent.Join event, @Root Player player) {

        final InetSocketAddress ip = player.getConnection().getAddress();
        final UUID uuid = player.getUniqueId();
        final String username = player.getName();

        try {
            Connection connection = core.getInstance().asServer().openConnection(new io.purecore.api.user.Player(core,username,uuid,false),ip);
            if(debug){
                io.purecore.plugin.util.Logging.logInfo(logger, Logging.Class.SESSIONS, "Opened #"+connection.getUuid()+" for "+username+ " from "+connection.getLocation().getRegion()+", "+connection.getLocation().getCountry());
            }
        } catch (ApiException | IOException | CallException e) {
            io.purecore.plugin.util.Logging.logError(logger, Logging.Class.SESSIONS, "Error while opening a connection for "+username+": "+e.getMessage());
        }
    }

    @Listener
    public void onPlayerLeave(ClientConnectionEvent.Disconnect event, @Root Player player){

        final UUID uuid = player.getUniqueId();
        final String username = player.getName();

        try {
            List<Connection> closedConnections = core.getInstance().asServer().closeConnections(new io.purecore.api.user.Player(core,username,uuid,false));
            if(debug){
                io.purecore.plugin.util.Logging.logInfo(logger, Logging.Class.SESSIONS, "Closed "+closedConnections.size()+ " connections by "+username);
            }
        } catch (ApiException | IOException | CallException e) {
            io.purecore.plugin.util.Logging.logError(logger, Logging.Class.SESSIONS, "Error while closing "+username+"'s connection(s): "+e.getMessage());
        }

    }*/


}
