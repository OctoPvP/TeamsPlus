package net.badbird5907.teams.commands.provider;

import net.badbird5907.teams.commands.CommandManager;
import net.badbird5907.teams.manager.PlayerManager;
import net.badbird5907.teams.object.PlayerData;
import net.octopvp.commander.command.CommandContext;
import net.octopvp.commander.command.CommandInfo;
import net.octopvp.commander.command.ParameterInfo;
import net.octopvp.commander.provider.Provider;
import net.octopvp.commander.sender.CoreCommandSender;
import org.bukkit.entity.Player;

import java.util.Deque;
import java.util.List;

public class PlayerDataProvider implements Provider<PlayerData> {
    @Override
    public PlayerData provide(CommandContext context, CommandInfo commandInfo, ParameterInfo parameterInfo, Deque<String> args) {
        if (parameterInfo.getCommander().getPlatform().isSenderParameter(parameterInfo)) {
            return PlayerManager.getData(context.getCommandSender().getIdentifier());
        } else {
            return PlayerManager.getData(args.pop());
        }
    }

    @Override
    public List<String> provideSuggestions(String input, String lastArg, CoreCommandSender sender) {
        return CommandManager.getCommander().getArgumentProviders().get(Player.class).provideSuggestions(input, lastArg, sender);
    }
}
