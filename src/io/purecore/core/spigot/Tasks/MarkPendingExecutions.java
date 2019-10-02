package io.purecore.core.spigot.Tasks;

import io.purecore.core.api.Core;
import io.purecore.core.api.exception.ServerApiError;
import io.purecore.core.api.type.CoreExecution;
import io.purecore.core.api.type.CoreKey;
import io.purecore.core.console.utils.Msgs;
import io.purecore.core.spigot.Main;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

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
                            try {
                                Bukkit.getScheduler().callSyncMethod( Main.plugin, () -> Bukkit.dispatchCommand( Bukkit.getConsoleSender(), execution.getCommand().getString())).get();
                                Core.markExecution(key,execution);
                                if(Main.debug){
                                    Msgs.showWarning(Main.logger,"command execution","execution #"+execution.getUuid()+" updated");
                                }
                            } catch (InterruptedException | ExecutionException e) {
                                Msgs.showError(Main.logger,"command execution","error while executing pending command: "+e.getMessage());
                            }
                        }

                    }

                } else { // the player doesn't need to be online in order to execute the command
                    try {
                        Bukkit.getScheduler().callSyncMethod( Main.plugin, () -> Bukkit.dispatchCommand( Bukkit.getConsoleSender(), execution.getCommand().getString())).get();
                        Core.markExecution(key,execution);
                        if(Main.debug){
                            Msgs.showWarning(Main.logger,"command execution","execution #"+execution.getUuid()+" updated");
                        }
                    } catch (InterruptedException | ExecutionException e) {
                        Msgs.showError(Main.logger,"command execution","error while executing pending command: "+e.getMessage());
                    }
                }
            }
        } catch (IOException | ServerApiError e) {
            e.printStackTrace();
        }
    }
}
