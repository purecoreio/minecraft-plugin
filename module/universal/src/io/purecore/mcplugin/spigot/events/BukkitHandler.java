package io.purecore.mcplugin.spigot.events;

import io.purecore.api.call.ApiException;
import io.purecore.api.punishment.PunishmentType;
import io.purecore.mcplugin.API;
import io.purecore.mcplugin.spigot.Instance;
import org.bukkit.BanEntry;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandSendEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.RemoteServerCommandEvent;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.plugin.Plugin;
import org.json.JSONException;

import java.io.IOException;
import java.util.Date;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Level;

public class BukkitHandler implements Listener {

    private Plugin plugin;

    public BukkitHandler(Plugin plugin){
        this.plugin=plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event){
        Bukkit.getServer().getScheduler().runTaskAsynchronously(this.plugin, new Runnable() {
            @Override
            public void run() {
                try {
                    API.instance.getInstance().connect(event.getPlayer().getUniqueId().toString(),event.getPlayer().getName(), Objects.requireNonNull(event.getPlayer().getAddress()).getAddress().toString().replace("/",""));
                    Instance.getCache().runExecutions();
                } catch (IOException | ApiException | JSONException | NullPointerException e) {
                    plugin.getLogger().log(Level.WARNING, "There was an error while creating a player connection: " + e.getMessage());
                }
            }
        });
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event){
        Bukkit.getServer().getScheduler().runTaskAsynchronously(this.plugin, new Runnable() {
            @Override
            public void run() {
                try {
                    API.instance.getInstance().disconnect(event.getPlayer().getUniqueId().toString());
                } catch (IOException | ApiException | JSONException e) {
                    plugin.getLogger().log(Level.WARNING, "There was an error while closing a player connection: " + e.getMessage());
                }
            }
        });
    }

    @EventHandler
    public void onKick(PlayerKickEvent event){
        PunishmentType type = null;
        String reason = event.getReason();
        Date until = null;
        BanList list = Bukkit.getBanList(BanList.Type.NAME);
        Set<BanEntry> entrySet = list.getBanEntries();
        String playerId = event.getPlayer().getUniqueId().toString();
        String modId = null;

        if(event.getPlayer().isBanned()){
            type = PunishmentType.Ban;
            for (BanEntry banEntry : entrySet) {
                if(banEntry.getTarget().contains(event.getPlayer().getName())) {
                    Player player = Bukkit.getPlayer(banEntry.getSource());
                    if(player!=null){
                        modId = player.getUniqueId().toString();
                    }
                    until = banEntry.getExpiration();
                }
            }
        } else {
            type = PunishmentType.Kick;
        }

        PunishmentType finalType = type;
        String finalModId = modId;
        Date finalUntil = until;
        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, new Runnable() {
            @Override
            public void run() {
                try {
                    API.getInstance().getInstance().punish("85b624b154b8abf0", finalType, finalModId, playerId, reason, null, finalUntil);
                } catch (IOException | ApiException | JSONException e) {
                    plugin.getLogger().log(Level.WARNING, "There was an error while creating a LiteBans punishment: " + e.getMessage());
                }
            }
        });

    }

    @EventHandler
    public void pardonEventPlayer(PlayerCommandSendEvent event){
        for (String command:event.getCommands()) {
            this.unbanHandler(event.getPlayer(),command);
        }
    }

    @EventHandler
    public void pardonEventConsole(ServerCommandEvent event){
        this.unbanHandler(null,event.getCommand());
    }

    @EventHandler
    public void pardonEventConsoleRemote(RemoteServerCommandEvent event){
        this.unbanHandler(null,event.getCommand());
    }

    private void unbanHandler(Player player, String command){
        if((player == null || player.hasPermission(command)) && command.startsWith("minecraft:pardon ") || command.startsWith("pardon ") || (Instance.isEssentialsEnabled() && !Instance.isLiteBansEnabled() && !Instance.isAdvancedbanEnabled() && (command.startsWith("unban "))) || (Instance.isEssentialsEnabled() && command.startsWith("essentials:unban"))){

            String moderatorId = null;
            if(player!=null) moderatorId = player.getUniqueId().toString();

            String[] parts = command.split("\\s+");
            if(parts.length==2){
                String username = parts[1];

                BanList list = Bukkit.getBanList(BanList.Type.NAME);
                Set<BanEntry> entrySet = list.getBanEntries();

                for (BanEntry banEntry : entrySet) {
                    if(banEntry.getTarget().contains(username)){
                        OfflinePlayer bannedPlayer = Bukkit.getOfflinePlayer(banEntry.getTarget());
                        String bannedId = bannedPlayer.getUniqueId().toString();
                        if(bannedPlayer.isBanned()){
                            String finalModeratorId = moderatorId;
                            Bukkit.getScheduler().runTaskAsynchronously(this.plugin, new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        API.getInstance().getInstance().unpunish("85b624b154b8abf0", PunishmentType.Ban, bannedId, finalModeratorId, null);
                                    } catch (IOException | ApiException | JSONException e) {
                                        plugin.getLogger().log(Level.WARNING, "There was an error while creating a LiteBans punishment: " + e.getMessage());
                                    }
                                }
                            });
                        }
                    }
                }
            }
        }
    }

}
