package io.purecore.core.bungee.events;

import io.purecore.core.api.Core;
import io.purecore.core.api.exception.ServerApiError;
import io.purecore.core.api.type.CoreConnection;
import io.purecore.core.api.type.CoreKey;
import io.purecore.core.bungee.Main;
import io.purecore.core.console.utils.Msgs;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.UUID;

public class Join implements Listener
{

    @EventHandler
    public void onPreLogin(LoginEvent event){

        UUID uuid = event.getConnection().getUniqueId();
        String name = event.getConnection().getName();
        InetSocketAddress ip = event.getConnection().getAddress();
        CoreKey key = new CoreKey(Main.keys.getString("keys.network"));

        try{
            CoreConnection connection = Core.newConnection(ip,uuid,name,key);
            Msgs.showWarning("CONNECTION CREATION","Opened connection #"+connection.getUuid());
        } catch (IOException | ServerApiError e) {
            Msgs.showError("CONNECTION CREATION",e.getMessage());
        }

    }

}
