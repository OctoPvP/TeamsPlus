package net.badbird5907.teams.util;

import com.jonahseguin.drink.argument.CommandArg;
import com.jonahseguin.drink.exception.CommandExitMessage;
import com.jonahseguin.drink.parametric.DrinkProvider;
import net.badbird5907.teams.manager.PlayerManager;
import net.badbird5907.teams.object.Lang;
import net.badbird5907.teams.object.PlayerData;
import net.badbird5907.teams.object.Team;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.util.List;

public class TeamSenderProvider extends DrinkProvider<Team> {
    @Override
    public boolean doesConsumeArgument() {
        return false;
    }

    @Override
    public boolean isAsync() {
        return false;
    }

    @Nullable
    @Override
    public Team provide(@NotNull CommandArg arg, @NotNull List<? extends Annotation> annotations) throws CommandExitMessage {
        Player player = arg.getSenderAsPlayer();
        if (player == null) {
            throw new CommandExitMessage("You must be a player to use this command");
        }
        PlayerData data = PlayerManager.getData(player);
        if (!data.isInTeam())
            throw new CommandExitMessage(Lang.MUST_BE_IN_TEAM.toString());
        return data.getPlayerTeam();
    }

    @Override
    public String argumentDescription() {
        return "TeamProvider";
    }

    @Override
    public List<String> getSuggestions(@NotNull String prefix) {
        return null;
    }
}
