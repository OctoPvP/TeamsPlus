package net.badbird5907.teams.util;

import com.jonahseguin.drink.argument.CommandArg;
import com.jonahseguin.drink.exception.CommandExitMessage;
import com.jonahseguin.drink.parametric.DrinkProvider;
import net.badbird5907.teams.manager.PlayerManager;
import net.badbird5907.teams.object.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.stream.Collectors;

public class PlayerDataProvider extends DrinkProvider<PlayerData> {
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
    public PlayerData provide(@NotNull CommandArg arg, @NotNull List<? extends Annotation> annotations) throws CommandExitMessage {
        return PlayerManager.getData(arg.get());
    }

    @Override
    public String argumentDescription() {
        return "PlayerData";
    }

    @Override
    public List<String> getSuggestions(@NotNull String prefix) {
        return Bukkit.getOnlinePlayers().stream().map(HumanEntity::getName).filter(name -> name.toLowerCase().startsWith(prefix.toLowerCase())).collect(Collectors.toList());
    }
}
