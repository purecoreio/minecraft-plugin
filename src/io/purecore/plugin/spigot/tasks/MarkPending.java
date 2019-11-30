package io.purecore.plugin.spigot.tasks;

import io.purecore.api.Core;
import io.purecore.api.command.Context;
import io.purecore.api.command.Execution;
import io.purecore.api.exception.ApiException;
import io.purecore.api.exception.CallException;
import io.purecore.plugin.spigot.Main;
import io.purecore.plugin.util.Logging;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MarkPending implements Runnable {

    private Core core;

    public MarkPending(Core core){
        this.core=core;
    }

    @Override
    public void run() {

        try {

            // gets player list / execution list

            List<io.purecore.api.user.Player> playerList = new ArrayList<>();

            for(Player player: Bukkit.getServer().getOnlinePlayers()){

                playerList.add(new io.purecore.api.user.Player(Main.core,player.getName(),player.getUniqueId(),false));

            }
            
            List<Execution> executions = core.getInstance().getPendingExecutions(playerList);

            // check executions

            for (Execution execution:executions) {

                Context context = execution.getContext();

                if(execution.needsOnline()){

                    if(Bukkit.getServer().getPlayer(context.getUsername())!=null||Bukkit.getServer().getPlayer(context.getUuid())!=null){

                        // executes command

                        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
                            @Override
                            public void run() {
                                String cmd = execution.getCommand().getString();
                                Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(),cmd);
                            }
                        }, 0L);

                        execution.markAsExecuted();

                        if(Main.settings.shouldDebug()){
                            Logging.logInfo(Main.plugin.getLogger(), Logging.Class.EXECUTIONS, "The execution #"+execution.getUuid()+" was successfully executed");
                        }

                    }

                } else {

                    Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
                        @Override
                        public void run() {
                            String cmd = execution.getCommand().getString();
                            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(),cmd);
                        }
                    }, 0L);

                    execution.markAsExecuted();

                    if(Main.settings.shouldDebug()){
                        Logging.logInfo(Main.plugin.getLogger(), Logging.Class.EXECUTIONS, "The execution #"+execution.getUuid()+" was successfully executed");
                    }

                }
            }

        } catch (ApiException | IOException | UnsupportedOperationException | CallException e) {

            Logging.logError(Main.plugin.getLogger(), Logging.Class.EXECUTIONS, e.getMessage());

        }
    }
}
