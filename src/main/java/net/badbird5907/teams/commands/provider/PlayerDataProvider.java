package net.badbird5907.teams.commands.provider;

import net.badbird5907.teams.commands.CommandManager;
import net.badbird5907.teams.hooks.impl.VanishHook;
import net.badbird5907.teams.manager.PlayerManager;
import net.badbird5907.teams.object.Lang;
import net.badbird5907.teams.object.PlayerData;
import net.octopvp.commander.command.CommandContext;
import net.octopvp.commander.command.CommandInfo;
import net.octopvp.commander.command.ParameterInfo;
import net.octopvp.commander.exception.CommandParseException;
import net.octopvp.commander.provider.Provider;
import net.octopvp.commander.sender.CoreCommandSender;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Deque;
import java.util.List;

public class PlayerDataProvider implements Provider<PlayerData> {
    @Override
    public PlayerData provide(CommandContext context, CommandInfo commandInfo, ParameterInfo parameterInfo, Deque<String> args) {
        if (parameterInfo.getCommander().getPlatform().isSenderParameter(parameterInfo)) {
            return PlayerManager.getData(context.getCommandSender().getIdentifier());
        } else {
            Player target = Bukkit.getPlayer(args.pop());
            if (target == null || VanishHook.isVanished(target)) {
                return null;
            }
            return PlayerManager.getData(target);
        }
    }

    @Override
    public List<String> provideSuggestions(String input, String lastArg, CoreCommandSender sender) {
        return CommandManager.getCommander().getArgumentProviders().get(Player.class).provideSuggestions(input, lastArg, sender);
    }


}
