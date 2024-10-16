package dev.badbird.teams.commands.provider;

import dev.badbird.teams.commands.CmdInjectException;
import dev.badbird.teams.commands.annotation.Sender;
import dev.badbird.teams.manager.TeamsManager;
import dev.badbird.teams.object.Team;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.incendo.cloud.annotations.parser.Parser;
import org.incendo.cloud.annotations.suggestion.Suggestions;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.context.CommandInput;
import org.incendo.cloud.exception.CommandExecutionException;
import org.incendo.cloud.injection.ParameterInjector;
import org.incendo.cloud.services.type.ConsumerService;
import org.incendo.cloud.util.annotation.AnnotationAccessor;

import java.util.stream.Stream;

public class TeamParser implements ParameterInjector<CommandSender, Team> {

    @Parser(suggestions = "suggestTeams")
    public @NonNull Team parse(@NonNull CommandContext<@NonNull CommandSender> commandContext, @NonNull CommandInput commandInput) {
        if (commandInput.hasRemainingInput()) {
            String str = commandInput.readString();
            Team team = TeamsManager.getInstance().getTeamByName(str);
            if (team == null) {
                throw new RuntimeException(
                        "Cannot find team with name " + str
                );
            }
            return team;
        }
        throw new CommandExecutionException(new RuntimeException("No team name provided"));
    }

    @Override
    public @Nullable Team create(@NonNull CommandContext<CommandSender> context, @NonNull AnnotationAccessor annotationAccessor) {
        CommandSender sender = context.sender();

        if (sender instanceof Player player) {
            if (annotationAccessor.annotation(Sender.class) != null) {
                System.out.println("Getting team from player");
                Team t = TeamsManager.getInstance().getPlayerTeam(player.getUniqueId());
                if (t == null) {
                    // throw new IllegalArgumentException("Must be in a team to use this command.");
                    //throw new InjectionException("<red>You must be in a team to use this command.", new Exception());
                    player.sendRichMessage("<red>You must be in a team to use this command.");
                    ConsumerService.interrupt();
                }
                return t;
            }
            return null;
        }
        throw new CmdInjectException("Must be a player!");
    }

    @Suggestions("suggestTeams")
    public Stream<String> suggestTeams(CommandContext<CommandSender> context, String input) {
        return TeamsManager.getInstance().getTeams().values().stream().map(Team::getName);
    }

    /*
    @Override
    public Team provide(CommandContext context, CommandInfo commandInfo, ParameterInfo parameterInfo, Deque<String> args) {
        if (false) { // CommandManager.getCommander().getPlatform().isSenderParameter(parameterInfo)
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
        return TeamsManager.getInstance().getTeams()
                .values().stream()
                .map(Team::getName).collect(java.util.stream.Collectors.toList());
    }
     */
}
