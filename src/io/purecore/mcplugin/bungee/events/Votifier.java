package io.purecore.mcplugin.bungee.events;

import com.vexsoftware.votifier.bungee.events.VotifierEvent;
import com.vexsoftware.votifier.model.Vote;
import io.purecore.api.exception.ApiException;
import io.purecore.api.exception.CallException;
import io.purecore.api.user.Player;
import io.purecore.api.voting.VotingSite;
import io.purecore.mcplugin.bungee.Main;
import io.purecore.mcplugin.util.Logging;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.io.IOException;

public class Votifier implements Listener {

    @EventHandler
    public void vote(VotifierEvent e){
        Vote vote = e.getVote();
        VotingSite votingSite = new VotingSite(vote.getServiceName());
        boolean gotPlayer = false;
        try {
            Player player = new Player(Main.core, vote.getUsername());
            gotPlayer=true;
            player.vote(votingSite);
            Logging.logInfo(Main.plugin.getLogger(), Logging.Class.VOTES, "successfully registered vote");
        } catch (ApiException | IOException | CallException err) {
            if(gotPlayer){
                Logging.logError(Main.plugin.getLogger(), Logging.Class.VOTES,"Got an error while sending the data from a vote executed in the site '"+vote.getServiceName()+"' by '"+vote.getUsername()+"': "+err.getMessage());
            } else {
                Logging.logError(Main.plugin.getLogger(), Logging.Class.VOTES,"Got an error while sending the data from a vote executed in the site '"+vote.getServiceName()+"' by '"+vote.getUsername()+"' (unknown internally): "+err.getMessage());
            }
        }
    }

}
