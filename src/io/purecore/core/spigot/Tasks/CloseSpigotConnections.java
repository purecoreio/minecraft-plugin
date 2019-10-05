package io.purecore.core.spigot.Tasks;

import io.purecore.core.api.Core;
import io.purecore.core.api.exception.ServerApiError;
import io.purecore.core.api.type.CoreConnection;
import io.purecore.core.api.type.CoreKey;
import io.purecore.core.console.utils.Msgs;
import org.bukkit.plugin.Plugin;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

public class CloseSpigotConnections implements Runnable {

    private Plugin plugin;
    private boolean debug;
    private Logger logger;
    private UUID uuid;
    private CoreKey key;

    public CloseSpigotConnections(Plugin plugin, boolean debug, Logger logger, UUID uuid, CoreKey key) {
        this.plugin = plugin;
        this.debug = debug;
        this.logger = logger;
        this.uuid = uuid;
        this.key = key;
    }

    @Override
    public void run() {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {
                try {
                    List<CoreConnection> closedConnections = Core.closePlayerConnections(uuid, key);
                    if(debug){
                        Msgs.showWarning(logger,"CONNECTION DESTRUCTION","Closed "+closedConnections.size()+" connection(s)");
                    }
                } catch (IOException | ServerApiError e) {
                    Msgs.showError(logger,"CONNECTION DESTRUCTION",e.getMessage());
                }
            }
        });
    }

}
