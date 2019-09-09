package io.purecore.core.bungee.Tasks;

import io.purecore.core.api.Core;
import io.purecore.core.api.exception.ServerApiError;
import io.purecore.core.api.type.CoreExecution;
import io.purecore.core.api.type.CoreKey;
import io.purecore.core.bungee.Main;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public class MarkPendingExecutions implements Runnable {

    @Override
    public void run() {
        CoreKey key = new CoreKey(Main.keys.getString("keys.network"));
        try {
            List<CoreExecution> executionlist = Core.getPendingExecutions(key);
            for (CoreExecution execution: executionlist) {

                if(execution.needsOnline()){ // checks if the player needs to be online in order to execute the command

                    String username = execution.getContext().getUsername();
                    UUID uuid = execution.getContext().getUuid();

                    for (ProxiedPlayer player:Main.plugin.getProxy().getPlayers()) {

                        if(player.getName().equals(username) ||player.getUniqueId()==uuid){ // if it finds the player it executes the command
                            if(Core.markExecution(key,execution)){
                                Main.plugin.getProxy().getPluginManager().dispatchCommand(Main.plugin.getProxy().getConsole(), execution.getCommand().getString());
                            } else {

                            }
                        }

                    }

                } else { // the player doesn't need to be online in order to execute the command

                    if(Core.markExecution(key,execution)){
                        Main.plugin.getProxy().getPluginManager().dispatchCommand(Main.plugin.getProxy().getConsole(), execution.getCommand().getString());
                    } else {

                    }

                }
            }
        } catch (IOException | ServerApiError e) {
            e.printStackTrace();
        }
    }
}
