package io.purecore.core.spigot.Tasks;

import io.purecore.core.api.Core;
import io.purecore.core.api.exception.ServerApiError;
import io.purecore.core.api.type.CoreExecution;
import io.purecore.core.api.type.CoreKey;
import io.purecore.core.console.utils.Msgs;
import io.purecore.core.spigot.Main;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class MarkPendingExecutions implements Runnable {

    private Plugin plugin;
    private CoreKey key;

    public MarkPendingExecutions(Plugin plugin,CoreKey key){
        this.key=key;
        this.plugin=plugin;
    }

    @Override
    public void run() {

        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {
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
                                    plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                Core.markExecution(key,execution);
                                                plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), execution.getCommand().getString());
                                            } catch (IOException | ServerApiError e) {
                                                Msgs.showError(Main.logger,"command execution","error while marking command: "+e.getMessage());
                                            }
                                        }
                                    },0L);

                                    if(Main.debug){
                                        Msgs.showWarning(Main.logger,"command execution","execution #"+execution.getUuid()+" updated");
                                    }
                                }
                            }

                        } else { // the player doesn't need to be online in order to execute the command

                            plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        Core.markExecution(key,execution);
                                        plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), execution.getCommand().getString());
                                    } catch (IOException | ServerApiError e) {
                                        Msgs.showError(Main.logger,"command execution","error while marking command: "+e.getMessage());
                                    }
                                }
                            },0L);

                            if(Main.debug){
                                Msgs.showWarning(Main.logger,"command execution","execution #"+execution.getUuid()+" updated");
                            }
                        }
                    }
                } catch (IOException | ServerApiError e) {
                    Msgs.showError(Main.logger,"command execution","error while getting pending commands: "+e.getMessage());
                }
            }
        });
    }
}
