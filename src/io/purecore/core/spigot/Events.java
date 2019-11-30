package io.purecore.core.spigot;

import io.purecore.core.api.type.CoreKey;
import io.purecore.core.spigot.Tasks.CloseSpigotConnections;
import io.purecore.core.spigot.Tasks.CreateSpigotConnection;
import io.purecore.core.spigot.Tasks.UpdateAdvancements;
import org.bukkit.advancement.Advancement;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.net.InetSocketAddress;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static io.purecore.core.utils.advancements.getAdvancements.getAdvancementList;

public class Events implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event)
    {

        UUID uuid = event.getPlayer().getUniqueId();
        String name = event.getPlayer().getName();
        InetSocketAddress ip = event.getPlayer().getAddress();
        CoreKey key = new CoreKey(Main.keys.getString("keys.server"));

        new CreateSpigotConnection(Main.plugin,Main.debug,Main.logger,ip,uuid,name,key).run();

    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event)
    {

        UUID uuid = event.getPlayer().getUniqueId();
        CoreKey key = new CoreKey(Main.keys.getString("keys.server"));

        new CloseSpigotConnections(Main.plugin, Main.debug,Main.logger,uuid,key).run();

        Main.plugin.getServer().getScheduler().runTaskAsynchronously(Main.plugin, new Runnable() {

            @Override
            public void run() {
                List<Advancement> completedAdvancements = getAdvancementList(Main.plugin,event.getPlayer());
                CoreKey key = new CoreKey(Main.keys.getString("keys.server"));

                new UpdateAdvancements(Main.plugin, key, event.getPlayer(), completedAdvancements).run();
            }

        });

    }

    @EventHandler
    public void onPlayerAdvancement(PlayerAdvancementDoneEvent event){

        List<Advancement> completedAdvancements = getAdvancementList(Main.plugin,event.getPlayer());
        CoreKey key = new CoreKey(Main.keys.getString("keys.server"));

        new UpdateAdvancements(Main.plugin, key, event.getPlayer(), completedAdvancements).run();

    }

}
