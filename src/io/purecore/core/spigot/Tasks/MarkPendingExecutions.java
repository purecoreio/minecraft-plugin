package io.purecore.core.spigot.Tasks;

import io.purecore.core.api.Core;
import io.purecore.core.api.exception.ServerApiError;
import io.purecore.core.api.type.CoreExecution;
import io.purecore.core.api.type.CoreKey;
import io.purecore.core.spigot.Main;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public class MarkPendingExecutions implements Runnable {

    private CoreKey key;

    public MarkPendingExecutions(CoreKey key){
        this.key=key;
    }

    @Override
    public void run() {

        try {
            List<CoreExecution> executionlist = Core.getPendingExecutions(key);
            for (CoreExecution execution: executionlist) {

                if(execution.needsOnline()){ // checks if the player needs to be online in order to execute the command

                    String username = execution.getContext().getUsername();
                    UUID uuid = execution.getContext().getUuid();

                    for (Player player:Main.plugin.getServer().getOnlinePlayers()) {

                        if(player.getName().equals(username) ||player.getUniqueId()==uuid){ // if it finds the player it executes the command
                            if(Core.markExecution(key,execution)){
                                Bukkit.dispatchCommand( Bukkit.getConsoleSender(), execution.getCommand().getString() );
                            }
                        }

                    }

                } else { // the player doesn't need to be online in order to execute the command

                    if(Core.markExecution(key,execution)){
                        Bukkit.dispatchCommand( Bukkit.getConsoleSender(), execution.getCommand().getString() );
                    }

                }
            }
        } catch (IOException | ServerApiError e) {
            e.printStackTrace();
        }
    }
}
