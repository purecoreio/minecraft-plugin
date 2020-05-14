package io.purecore.mcplugin.spigot.events;

import com.vexsoftware.votifier.model.Vote;
import com.vexsoftware.votifier.model.VotifierEvent;
import io.purecore.api.exception.ApiException;
import io.purecore.api.exception.CallException;
import io.purecore.api.user.Player;
import io.purecore.api.voting.VotingSite;
import io.purecore.mcplugin.spigot.Main;
import io.purecore.mcplugin.util.Logging;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.io.IOException;

public class Votifier implements Listener {
    @EventHandler
    public void onVote(VotifierEvent event){
        if(!Main.bungee){
            Vote vote = event.getVote();
            VotingSite votingSite = new VotingSite(vote.getServiceName());
            boolean gotPLayer = false;
            try {
                Player player = new Player(Main.core, vote.getUsername());
                gotPLayer=true;
                player.vote(votingSite);
                Logging.logInfo(Main.plugin.getLogger(), Logging.Class.VOTES, "successfully registered vote");
            } catch (ApiException | IOException | CallException e) {
                if(gotPLayer){
                    Logging.logError(Main.plugin.getLogger(), Logging.Class.VOTES,"Got an error while sending the data from a vote executed in the site '"+vote.getServiceName()+"' by '"+vote.getUsername()+"': "+e.getMessage());
                } else {
                    Logging.logError(Main.plugin.getLogger(), Logging.Class.VOTES,"Got an error while sending the data from a vote executed in the site '"+vote.getServiceName()+"' by '"+vote.getUsername()+" (unknown)': "+e.getMessage());
                }
            }
        } else {
            Logging.logInfo(Main.plugin.getLogger(), Logging.Class.VOTES, "Ignoring vote, as it should be recorded by the bungeecord instance");
        }
    }
}
