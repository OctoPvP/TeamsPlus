package net.badbird5907.teams.util;

import com.jonahseguin.drink.argument.CommandArg;
import com.jonahseguin.drink.exception.CommandExitMessage;
import com.jonahseguin.drink.parametric.DrinkProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ArgsProvider extends DrinkProvider<String[]> {
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
    public String[] provide(@NotNull CommandArg arg, @NotNull List<? extends Annotation> annotations) throws CommandExitMessage {
        return arg.getArgs().getArgs().toArray(new String[0]);
    }

    @Override
    public String argumentDescription() {
        return "String array of arguments";
    }

    @Override
    public List<String> getSuggestions(@NotNull String prefix) {
        return Collections.emptyList();
    }
}
