package io.purecore.mcplugin.spigot.commandHandler;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Subcommand;
import io.purecore.api.call.ApiException;
import io.purecore.mcplugin.PluginException;
import io.purecore.mcplugin.spigot.Instance;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.command.CommandSender;

import java.io.IOException;

@CommandAlias("purecore")
public class FiveteenAndDown extends BaseCommand implements CommandHandler {

    Instance instance;

    public FiveteenAndDown(Instance instance){
        this.instance=instance;
    }

    @Subcommand("key")
    @CommandPermission("purecore.setup")
    @Description("updates your purecore key")
    public void key(CommandSender sender, String[] args) {
        if(args.length>0){
            instance.getConfig().set("key",args[0]);
            reloadProcedure(sender);
        } else {

            TextComponent icon = new TextComponent(" •  ");
            icon.setColor(ChatColor.BLUE);

            TextComponent message = new TextComponent("Please, provide a key with /purecore key <key>");
            message.setColor(ChatColor.GRAY);
            message.setHoverEvent(new HoverEvent( HoverEvent.Action.SHOW_TEXT, new Text("Need help? Click here to join our Discord server ʕ•ᴥ•ʔ") ));
            message.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://discord.gg/rhpcSnK"));

            icon.addExtra(message);

            sender.spigot().sendMessage(icon);
        }
    }

    @Subcommand("reload")
    @CommandPermission("purecore.reload")
    @Description("reloads purecore")
    public void reload(CommandSender sender) {
        reloadProcedure(sender);
    }

    public void reloadProcedure(CommandSender sender) {

        TextComponent icon = new TextComponent(" •  ");
        icon.setColor(ChatColor.BLUE);

        TextComponent message = new TextComponent("Reloading...");
        message.setColor(ChatColor.GRAY);
        message.setHoverEvent(new HoverEvent( HoverEvent.Action.SHOW_TEXT, new ComponentBuilder( "Your server is exchanging details with purecore ｡◕‿◕｡" ).create() ) );

        icon.addExtra(message);

        sender.spigot().sendMessage(icon);
        instance.getServer().getScheduler().runTask(instance, new Runnable() {
            @Override
            public void run() {
                try {
                    instance.reload();
                    instance.saveConfig();

                    TextComponent icon = new TextComponent(" ✔ ");
                    icon.setColor(ChatColor.BLUE);

                    TextComponent message = new TextComponent("Success! Thank you for using purecore.io!");
                    message.setColor(ChatColor.GRAY);
                    message.setHoverEvent(new HoverEvent( HoverEvent.Action.SHOW_TEXT, new ComponentBuilder( "Click here to join our Discord server (ɔ◕‿◕)ɔ ♥" ).create() ) );
                    message.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://discord.gg/rhpcSnK"));

                    icon.addExtra(message);

                    sender.spigot().sendMessage(icon);
                } catch (PluginException | IOException | ApiException e) {

                    TextComponent icon = new TextComponent(" ✖ ");
                    icon.setColor(ChatColor.RED);

                    TextComponent message = new TextComponent(e.getMessage());
                    message.setColor(ChatColor.GRAY);
                    message.setHoverEvent(new HoverEvent( HoverEvent.Action.SHOW_TEXT, new ComponentBuilder( "Need help? Click here to join our Discord server" ).create() ) );
                    message.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://discord.gg/rhpcSnK"));

                    icon.addExtra(message);

                    sender.spigot().sendMessage(icon);
                }
            }
        });
    }

}
