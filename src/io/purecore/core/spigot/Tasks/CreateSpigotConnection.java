package io.purecore.core.spigot.Tasks;

import io.purecore.core.api.Core;
import io.purecore.core.api.exception.ServerApiError;
import io.purecore.core.api.type.CoreConnection;
import io.purecore.core.api.type.CoreKey;
import io.purecore.core.console.utils.Msgs;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.UUID;
import java.util.logging.Logger;

public class CreateSpigotConnection extends Thread {
    private boolean debug;
    private Logger logger;
    private InetSocketAddress ip;
    private UUID uuid;
    private String name;
    private CoreKey key;

    public CreateSpigotConnection(boolean debug, Logger logger, InetSocketAddress ip, UUID uuid, String name, CoreKey key) {
        this.debug = debug;
        this.logger = logger;
        this.ip = ip;
        this.uuid = uuid;
        this.name = name;
        this.key = key;
    }

    @Override
    public void run() {
        try{
            CoreConnection connection = Core.newConnection(ip,uuid,name,key);
            if(debug){
                Msgs.showWarning(logger,"CONNECTION CREATION","Opened connection #"+connection.getUuid());
            }
        } catch (IOException | ServerApiError e) {
            Msgs.showError(logger,"CONNECTION CREATION",e.getMessage()+". Please, review your keys.yml file and make sure to review your server and network keys");
        }
    }
}
