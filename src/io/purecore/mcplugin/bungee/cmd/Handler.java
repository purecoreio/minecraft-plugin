package io.purecore.mcplugin.bungee.cmd;

import io.purecore.api.exception.ApiException;
import io.purecore.api.exception.CallException;
import io.purecore.mcplugin.bungee.Main;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class Handler extends Command {
    public Handler() {
        super("bpc");
    }

    @Override
    public void execute(CommandSender commandSender, String[] strings) {
        if(strings.length==0 || strings[0].equals("help")){
            if(commandSender.hasPermission("purecore.help")) {
                commandSender.sendMessage(new TextComponent(ChatColor.BLUE + "→ setup " + ChatColor.GRAY + "/bpc setup <key>"));
                commandSender.sendMessage(new TextComponent(ChatColor.BLUE + "→ reload " + ChatColor.GRAY + "/bpc reload"));
                commandSender.sendMessage(new TextComponent(ChatColor.BLUE + "→ sync servers " + ChatColor.GRAY + "/bpc sync"));
            } else {
                commandSender.sendMessage(new TextComponent(ChatColor.RED + "✘" +ChatColor.GRAY + " You don't have the permission purecore.help. You might wanna execute this from your proxy console"));
            }
        } else if(strings[0].equals("sync")) {
            if(commandSender.hasPermission("purecore.sync")) {
                commandSender.sendMessage(new TextComponent(ChatColor.BLUE + "→" + ChatColor.GRAY + " syncing server list"));
                try {
                    Main.loadServers(true);
                } catch (ApiException | IOException | CallException e) {
                    commandSender.sendMessage(new TextComponent(ChatColor.RED + "✘" +ChatColor.GRAY + " Error: "+e.getMessage()));
                }
            } else {
                commandSender.sendMessage(new TextComponent(ChatColor.RED + "✘" +ChatColor.GRAY + " You don't have the permission purecore.sync. You might wanna execute this from your proxy console"));
            }
        } else if(strings[0].equals("setup")){
            if(commandSender.hasPermission("purecore.setup")){
                if(strings.length<2){
                    commandSender.sendMessage(new TextComponent(ChatColor.RED + "✘" +ChatColor.GRAY + " The correct usage for this command is /bcore setup <key>"));
                } else {
                    Main.keys.set("key",strings[1]);
                    try {
                        ConfigurationProvider.getProvider(YamlConfiguration.class).save(Main.keys, new File(Main.plugin.getDataFolder(), "keys.yml"));
                        commandSender.sendMessage(new TextComponent(ChatColor.BLUE + "→" + ChatColor.GRAY + " updated keys.yml"));
                        Main.loadInstance();
                    } catch (Exception e) {
                        commandSender.sendMessage(new TextComponent(ChatColor.RED + "✘" +ChatColor.GRAY + " Error: "+e.getMessage()));
                    }
                }
            } else {
                commandSender.sendMessage(new TextComponent(ChatColor.RED + "✘" +ChatColor.GRAY + " You don't have the permission purecore.setup. You might wanna execute this from your proxy console"));
            }
        } else if(strings[0].equals("reload")){
            if(commandSender.hasPermission("purecore.reload")){
                try {
                    commandSender.sendMessage(new TextComponent(ChatColor.BLUE + "→" + ChatColor.GRAY + " Reloading. Keep in mind that the offline settings are not reloaded this way, you should use /pc setup <key> if  you changed your key"));
                    Main.loadInstance();
                } catch (Exception e) {
                    commandSender.sendMessage(new TextComponent(ChatColor.RED + "✘" +ChatColor.GRAY + " Error: "+e.getMessage()));
                }
            } else {
                commandSender.sendMessage(new TextComponent(ChatColor.RED + "✘" +ChatColor.GRAY + " You don't have the permission purecore.reload. You might wanna execute this from your proxy console"));
            }
        } else {
            commandSender.sendMessage(new TextComponent(ChatColor.BLUE + "→" + ChatColor.GRAY + " Are you lost? Use /bpc help"));
        }
    }
}
