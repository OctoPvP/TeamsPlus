package dev.badbird.teams.commands.provider;

import dev.badbird.teams.commands.CommandManager;
import dev.badbird.teams.commands.annotation.AllowOffline;
import dev.badbird.teams.manager.PlayerManager;
import dev.badbird.teams.object.Lang;
import dev.badbird.teams.object.PlayerData;
import net.octopvp.commander.bukkit.BukkitCommandSender;
import net.octopvp.commander.bukkit.annotation.DefaultSelf;
import net.octopvp.commander.command.CommandContext;
import net.octopvp.commander.command.CommandInfo;
import net.octopvp.commander.command.ParameterInfo;
import net.octopvp.commander.exception.CommandException;
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
        if (context.getCommandInfo().getCommander().getPlatform().isSenderParameter(parameterInfo)) {
            return PlayerManager.getData((UUID) context.getCommandSender().getIdentifier());
        } else {
            String arg = args.peek();
            if (arg == null) {
                if (parameterInfo.getParameter().isAnnotationPresent(DefaultSelf.class)) {
                    return PlayerManager.getData((UUID) context.getCommandSender().getIdentifier());
                }
                throw new CommandException(Lang.PLAYER_NOT_FOUND.toString());
            }
            Player player = Bukkit.getPlayer(args.pop());
            if (player == null) {
                if (parameterInfo.getParameter().isAnnotationPresent(AllowOffline.class)) {
                    return PlayerManager.getDataLoadIfNeedTo(arg);
                }
                throw new CommandException(Lang.PLAYER_NOT_FOUND.toString());
            }
            return PlayerManager.getData(player.getUniqueId());
        }
    }

    @Override
    public List<String> provideSuggestions(String input, String lastArg, CoreCommandSender sender) {
        return CommandManager.getCommander().getArgumentProviders().get(Player.class).provideSuggestions(input, lastArg, sender);
    }
}
