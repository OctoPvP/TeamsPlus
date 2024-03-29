package dev.badbird.teams.commands.provider;

import dev.badbird.teams.commands.CommandManager;
import dev.badbird.teams.commands.annotation.AllowOffline;
import dev.badbird.teams.manager.PlayerManager;
import dev.badbird.teams.object.PlayerData;
import net.octopvp.commander.bukkit.BukkitCommandSender;
import net.octopvp.commander.command.CommandContext;
import net.octopvp.commander.command.CommandInfo;
import net.octopvp.commander.command.ParameterInfo;
import net.octopvp.commander.provider.Provider;
import net.octopvp.commander.sender.CoreCommandSender;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Deque;
import java.util.List;
import java.util.UUID;

public class PlayerDataProvider implements Provider<PlayerData> {
    @Override
    public PlayerData provide(CommandContext context, CommandInfo commandInfo, ParameterInfo parameterInfo, Deque<String> args) {
        if (parameterInfo.getCommander().getPlatform().isSenderParameter(parameterInfo)) {
            return PlayerManager.getData((UUID) context.getCommandSender().getIdentifier());
        } else {
            String s = args.pop();
            Player target = Bukkit.getPlayer(s);
            if (target == null) {
                if (parameterInfo.getParameter().isAnnotationPresent(AllowOffline.class)) {
                    args.addFirst(s);
                    return null; // return null to invoke provideDefault
                }
                return null;
            }
            BukkitCommandSender sender = (BukkitCommandSender) context.getCommandSender();
            if (sender.getSender() instanceof Player senderPlayer) {
                if (!senderPlayer.canSee(target)) {
                    args.addFirst(s);
                    return null;
                }
            }
            PlayerData data = PlayerManager.getData(target);
            if (data == null) {
                args.addFirst(s);
                return null;
            }
            return data;
        }
    }

    @Override
    public PlayerData provideDefault(CommandContext context, CommandInfo commandInfo, ParameterInfo parameterInfo, Deque<String> args) {
        Player target = Bukkit.getPlayer(args.pop());
        if (target == null) {
            if (parameterInfo.getParameter().isAnnotationPresent(AllowOffline.class)) {
                return PlayerManager.getDataLoadIfNeedTo(args.pop());
            }
        }
        return Provider.super.provideDefault(context, commandInfo, parameterInfo, args);
    }

    @Override
    public List<String> provideSuggestions(String input, String lastArg, CoreCommandSender sender) {
        return CommandManager.getCommander().getArgumentProviders().get(Player.class).provideSuggestions(input, lastArg, sender);
    }
}
