package dev.badbird.teams.commands.provider;

import dev.badbird.teams.commands.CommandManager;
import dev.badbird.teams.manager.TeamsManager;
import dev.badbird.teams.object.Lang;
import dev.badbird.teams.object.Team;
import net.octopvp.commander.command.CommandContext;
import net.octopvp.commander.command.CommandInfo;
import net.octopvp.commander.command.ParameterInfo;
import net.octopvp.commander.exception.MessageException;
import net.octopvp.commander.provider.Provider;
import net.octopvp.commander.sender.CoreCommandSender;

import java.util.Deque;
import java.util.List;
import java.util.UUID;

public class TeamProvider implements Provider<Team> {
    @Override
    public Team provide(CommandContext context, CommandInfo commandInfo, ParameterInfo parameterInfo, Deque<String> args) {
        if (CommandManager.getCommander().getPlatform().isSenderParameter(parameterInfo)) {
            Team team = TeamsManager.getInstance().getPlayerTeam((UUID) context.getCommandSender().getIdentifier());
            if (team == null && parameterInfo.isRequired()) {
                throw new MessageException(Lang.MUST_BE_IN_TEAM.toString());
            }
            return team;
        } else {
            return TeamsManager.getInstance().getTeamByName(args.pop());
        }
    }

    @Override
    public List<String> provideSuggestions(String input, String lastArg, CoreCommandSender sender) {
        return TeamsManager.getInstance().getTeams().stream().map(Team::getName).collect(java.util.stream.Collectors.toList());
    }
}
