package io.purecore.plugin.spigot.events;

import io.purecore.plugin.spigot.Main;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.io.IOException;

public class Chat implements Listener {

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event){

        Player player = event.getPlayer();

        if(player==Main.waitingForKey){

            String msg = event.getMessage();
            event.setCancelled(true);

            if(msg.contains("cancel")){
                Main.waitingForKey=null;
                player.sendMessage(ChatColor.GREEN+""+ChatColor.RED+"✘"+ChatColor.GRAY+" Cancelled setup");

                player.playSound(player.getLocation(), Sound.BLOCK_CHEST_CLOSE,1,1);

            } else {

                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP,1,1);

                player.sendMessage(ChatColor.GREEN+""+ChatColor.BOLD+"✓"+ChatColor.GRAY+" Updated server key");
                Main.waitingForKey=null;
                player.sendMessage(ChatColor.GREEN+""+ChatColor.BOLD+"✓"+ChatColor.GRAY+" Setup complete, reloading core with the new keys");

                Main.keys.set("key",msg);

                try {
                    Main.keys.save(Main.keysFile);
                } catch (IOException e) {
                    player.sendMessage(ChatColor.GREEN+""+ChatColor.RED+"✘"+ChatColor.GRAY+" Error while saving the file");
                }

                Main.plugin.getServer().getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
                    @Override
                    public void run() {
                        player.performCommand("purecore reload");
                    }
                },0L);
            }

        }

    }

}
