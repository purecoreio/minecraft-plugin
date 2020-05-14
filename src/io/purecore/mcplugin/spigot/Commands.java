package io.purecore.mcplugin.spigot;

import io.purecore.api.exception.ApiException;
import io.purecore.api.exception.CallException;
import io.purecore.api.voting.VotingSiteConfig;
import io.purecore.mcplugin.spigot.gui.CreatePunishment;
import io.purecore.mcplugin.util.Title;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static io.purecore.mcplugin.spigot.Main.*;


public class Commands implements CommandExecutor {

    private void sendWarning(CommandSender sender){
        if(sender instanceof  Player){
            Objects.requireNonNull(((Player) sender).getPlayer()).playSound(((Player) sender).getLocation(), Sound.ENTITY_CHICKEN_EGG,1,1);
        }
        sender.sendMessage("You don't have enough permissions to execute this command");
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(s.equals("c")||s.equals("pc")||s.equals("core")||s.equals("purecore")||s.equals("p")){
            if(strings.length==0){
                if(commandSender.hasPermission("purecore.info")){
                    if(Main.core!=null&&Main.instance!=null){
                        List<String> lines = new Title(Main.core,Main.plugin.getLogger(),Main.instance,Main.settings).getTitle();

                        if(commandSender instanceof Player){

                            Player player = ((Player) commandSender).getPlayer();
                            if(player!=null){
                                for (String line:lines) {
                                    if(line.contains("!")){

                                        String[] parts = line.split(":");
                                        player.sendMessage(ChatColor.YELLOW+""+ChatColor.BOLD+""+ChatColor.UNDERLINE+parts[0]+ChatColor.RESET+""+ChatColor.GRAY+parts[1]);

                                    } else if(line.contains("●")){

                                        String[] parts = line.split("●");
                                        String[] optparts = parts[1].split(":");
                                        String optinfo;
                                        if(optparts[1].contains("Yes")){
                                            optinfo=ChatColor.WHITE+optparts[0]+ChatColor.RESET+""+ChatColor.GREEN+optparts[1];
                                        } else if(optparts[1].contains("No")){
                                            optinfo=ChatColor.WHITE+optparts[0]+ChatColor.RESET+""+ChatColor.RED+optparts[1];
                                        } else {
                                            optinfo=ChatColor.WHITE+optparts[0]+ChatColor.RESET+""+ChatColor.GRAY+optparts[1];
                                        }
                                        player.sendMessage(ChatColor.YELLOW+"    ●"+ChatColor.RESET+""+ChatColor.WHITE+optinfo);
                                    } else {
                                        player.sendMessage(ChatColor.YELLOW+""+ChatColor.UNDERLINE+line);
                                    }
                                }
                            }

                        } else {
                            for (String line:lines) {
                                commandSender.sendMessage(line);
                            }
                        }
                    } else {
                        commandSender.sendMessage(ChatColor.YELLOW+""+ChatColor.BOLD+"→"+ChatColor.GRAY+" Invalid values, please, setup core with "+ChatColor.YELLOW+"/purecore setup");
                    }
                } else {
                    sendWarning(commandSender);
                }
            } else {
                if(strings[0].equals("vote")){
                    if(commandSender.hasPermission("purecore.vote")) {
                        Bukkit.getScheduler().runTaskAsynchronously(Main.plugin, new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    List<VotingSiteConfig> sites = Main.instance.asNetwork().getVotingSitesConfig();
                                    for (VotingSiteConfig config:sites) {
                                        commandSender.sendMessage(ChatColor.YELLOW+""+ChatColor.BOLD+"→ "+ChatColor.YELLOW+config.votingSite.name);
                                        commandSender.sendMessage(ChatColor.GRAY+""+ChatColor.BOLD+"       → "+ChatColor.GRAY+ChatColor.UNDERLINE+config.url.toString());
                                        commandSender.sendMessage("");
                                    }
                                } catch (ApiException | IOException | CallException e) {
                                    commandSender.sendMessage(ChatColor.RED+"✘"+ChatColor.GRAY+" "+e.getMessage());
                                }
                            }
                        });
                    }
                } else if (strings[0].equals("regenkey")){
                    if(commandSender.hasPermission("purecore.key.regen")){

                        Bukkit.getScheduler().runTaskAsynchronously(Main.plugin, new Runnable() {
                            @Override
                            public void run() {

                                try {

                                    String newKey = Main.instance.getKeyLegacy().regenHash();
                                    Main.keys.set("key",newKey);
                                    Main.keys.save(Main.keysFile);

                                    Bukkit.getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
                                        @Override
                                        public void run() {

                                            if(commandSender instanceof Player){
                                                Objects.requireNonNull(((Player) commandSender).getPlayer()).playSound(((Player) commandSender).getLocation(),Sound.BLOCK_BEACON_DEACTIVATE,1,1);
                                            }
                                            Main.enablePlugin(commandSender);

                                        }
                                    });

                                } catch (ApiException | IOException | CallException e) {
                                    commandSender.sendMessage(ChatColor.RED+"✘"+ChatColor.GRAY+" "+e.getMessage());
                                }
                            }
                        });

                    }
                }
                if(strings[0].equals("reload")){
                    if(commandSender.hasPermission("purecore.reload")){
                        if(commandSender instanceof Player){
                            ((Player) commandSender).getPlayer().playSound(((Player) commandSender).getLocation(),Sound.BLOCK_BEACON_DEACTIVATE,1,1);
                        }
                        Main.enablePlugin(commandSender);
                    }
                }
                if(strings[0].equals("punish")){
                    if(commandSender.hasPermission("purecore.punishment.create")){
                        if(strings.length<2){
                            commandSender.sendMessage(ChatColor.YELLOW+""+ChatColor.BOLD+"→"+ChatColor.GRAY+" You need to specify a player to punish with "+ChatColor.YELLOW+"/purecore punish <username>");
                        } else {
                            if(commandSender instanceof Player){

                                Bukkit.getServer().getScheduler().runTaskAsynchronously(Main.plugin, new Runnable() {
                                    @Override
                                    public void run() {
                                        try {

                                            io.purecore.api.user.Player player = new io.purecore.api.user.Player(Main.core, strings[1]);
                                            io.purecore.api.user.Player moderator = new io.purecore.api.user.Player(Main.core, Objects.requireNonNull(((Player) commandSender).getPlayer()).getName());

                                            List<io.purecore.api.user.Player> playerElementList = new ArrayList<>();
                                            playerElementList.add(player);
                                            playerElementList.add(moderator);

                                            CreatePunishment createPunishmentGui = new CreatePunishment();

                                            Bukkit.getScheduler().scheduleSyncDelayedTask(Main.plugin, new Runnable() {
                                                @Override
                                                public void run() {
                                                    playerElements.put(createPunishmentGui.getInventory(),playerElementList);
                                                    Objects.requireNonNull(((Player) commandSender).getPlayer()).openInventory(createPunishmentGui.getInventory());
                                                    createPunishmentGui.loadData(Main.instance);
                                                }
                                            });

                                        } catch (ApiException | IOException | CallException e) {

                                            commandSender.sendMessage(ChatColor.YELLOW+""+ChatColor.BOLD+"→"+ChatColor.GRAY+" Couldn't find this player in the database");
                                            e.printStackTrace();

                                        }
                                    }
                                });
                            } else {
                                commandSender.sendMessage(ChatColor.YELLOW+""+ChatColor.BOLD+"→"+ChatColor.GRAY+" This command isn't available from the console");
                            }
                        }
                    }
                } else if(strings[0].equals("setup")){

                    if(commandSender.hasPermission("purecore.setup")){
                        if(!bungee){
                            if(strings.length<3){

                                if(commandSender instanceof  Player){
                                    Objects.requireNonNull(((Player) commandSender).getPlayer()).playSound(((Player) commandSender).getLocation(), Sound.ENTITY_CHICKEN_EGG,1,1);

                                    commandSender.sendMessage(ChatColor.YELLOW+""+ChatColor.BOLD+"CORE SETUP");
                                    commandSender.sendMessage(ChatColor.YELLOW+""+ChatColor.BOLD+"→"+ChatColor.GRAY+" Please, enter your "+ChatColor.YELLOW+"server key");
                                    commandSender.sendMessage(ChatColor.YELLOW+""+ChatColor.BOLD+"→"+ChatColor.GRAY+" You can cancel the setup by typing \"cancel\" on the chat");
                                    Main.waitingForKey=((Player) commandSender).getPlayer();
                                } else {
                                    commandSender.sendMessage(ChatColor.YELLOW+""+ChatColor.BOLD+"CORE SETUP");
                                    commandSender.sendMessage(ChatColor.YELLOW+""+ChatColor.BOLD+"→"+ChatColor.GRAY+" Use "+ChatColor.YELLOW+"/purecore setup key <server-key>");
                                }


                            } else {

                                if(strings[1].equals("key")){

                                    Main.keys.set("key",strings[2]);
                                    if(commandSender instanceof Player){
                                        Main.enablePlugin(commandSender);
                                    } else {
                                        Main.enablePlugin(null);
                                    }

                                } else {
                                    if(commandSender instanceof Player){
                                        Objects.requireNonNull(((Player) commandSender).getPlayer()).playSound(((Player) commandSender).getLocation(), Sound.ENTITY_CHICKEN_EGG,1,1);
                                    }
                                    commandSender.sendMessage("Incorrect syntax, you need to execute this command with /core setup [key]");
                                }
                            }
                        } else {
                            if(commandSender instanceof Player){
                                Objects.requireNonNull(((Player) commandSender).getPlayer()).playSound(((Player) commandSender).getLocation(), Sound.ENTITY_ARROW_SHOOT,1,1);
                            }
                            if(Main.instance!=null){
                                commandSender.sendMessage(ChatColor.RED+"✘"+ChatColor.GRAY+" You're running under a bungeecord instance. Setup your proxy instance and it will autoconfigure all your sub-servers automatically. "+ChatColor.GREEN+ "This instance seems to be configured as "+instance.getName());
                            } else {
                                commandSender.sendMessage(ChatColor.RED+"✘"+ChatColor.GRAY+" You're running under a bungeecord instance. Setup your proxy instance and it will autoconfigure all your sub-servers automatically. "+ChatColor.RED+ "This instance isn't configured, is the server name not matching with your purecore server name? Is your proxy instance active?");
                            }
                        }


                    } else {
                        sendWarning(commandSender);
                    }
                }
            }
            return true;
        } else {
            return false;
        }
    }
}
