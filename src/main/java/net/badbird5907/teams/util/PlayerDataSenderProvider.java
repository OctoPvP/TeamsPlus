package net.badbird5907.teams.util;

import net.badbird5907.teams.manager.PlayerManager;
import net.badbird5907.teams.object.PlayerData;
import org.jetbrains.annotations.NotNull;
import revxrsal.commands.command.CommandActor;
import revxrsal.commands.command.ExecutableCommand;
import revxrsal.commands.process.SenderResolver;

public class PlayerDataSenderProvider implements SenderResolver {

    @Override
    public boolean isCustomType(Class<?> aClass) {
        return PlayerData.class.isAssignableFrom(aClass);
    }

    @Override
    public @NotNull Object getSender(@NotNull Class<?> aClass, @NotNull CommandActor commandActor, @NotNull ExecutableCommand executableCommand) {
        return PlayerManager.getData(commandActor.getUniqueId());
    }
}
