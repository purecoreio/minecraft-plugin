package io.purecore.mcplugin.spigot.commandHandler;

import org.bukkit.command.CommandSender;

public interface CommandHandler {

    void key(CommandSender sender, String[] args);
    void reload(CommandSender sender);
    void reloadProcedure(CommandSender sender);

}
