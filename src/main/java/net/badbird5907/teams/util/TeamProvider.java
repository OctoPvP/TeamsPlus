package net.badbird5907.teams.util;

import com.jonahseguin.drink.argument.CommandArg;
import com.jonahseguin.drink.exception.CommandExitMessage;
import com.jonahseguin.drink.parametric.DrinkProvider;
import net.badbird5907.teams.manager.TeamsManager;
import net.badbird5907.teams.object.Team;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.stream.Collectors;

public class TeamProvider extends DrinkProvider<Team> {
    @Override
    public boolean doesConsumeArgument() {
        return true;
    }

    @Override
    public boolean isAsync() {
        return false;
    }

    @Nullable
    @Override
    public Team provide(@NotNull CommandArg arg, @NotNull List<? extends Annotation> annotations) throws CommandExitMessage {
        return TeamsManager.getInstance().getTeamByName(arg.get());
    }

    @Override
    public String argumentDescription() {
        return "Team";
    }

    @Override
    public List<String> getSuggestions(@NotNull String prefix) {
        return TeamsManager.getInstance().getTeams().stream().filter(t -> t.getName().toLowerCase().contains(prefix.toLowerCase())).map(Team::getName).collect(Collectors.toList());
    }
}
