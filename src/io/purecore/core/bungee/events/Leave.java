package io.purecore.core.bungee.events;

import io.purecore.core.api.Core;
import io.purecore.core.api.exception.ServerApiError;
import io.purecore.core.api.type.CoreConnection;
import io.purecore.core.api.type.CoreKey;
import io.purecore.core.bungee.Main;
import io.purecore.core.console.utils.Msgs;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public class Leave implements Listener {

    @EventHandler
    public void onPlayerLeave (PlayerDisconnectEvent event){

        UUID uuid = event.getPlayer().getUniqueId();
        CoreKey key = new CoreKey(Main.keys.getString("keys.network"));

        try {
            List<CoreConnection> closedConnections = Core.closePlayerConnections(uuid, key);
            Msgs.showWarning("CONNECTION DESTRUCTION","Closed "+closedConnections.size()+" connections");
        } catch (IOException | ServerApiError e) {
            Msgs.showError("CONNECTION DESTRUCTION",e.getMessage());
        }

    }

}
