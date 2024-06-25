package dev.badbird.teams.commands;

import dev.badbird.teams.commands.annotation.TeamPermission;
import dev.badbird.teams.commands.provider.PlayerDataProvider;
import dev.badbird.teams.commands.provider.SenderProvider;
import dev.badbird.teams.commands.provider.TeamProvider;
import dev.badbird.teams.manager.PlayerManager;
import lombok.Getter;
import net.badbird5907.blib.command.Sender;
import dev.badbird.teams.TeamsPlus;
import dev.badbird.teams.object.Lang;
import dev.badbird.teams.object.PlayerData;
import dev.badbird.teams.object.Team;
import net.octopvp.commander.Commander;
import net.octopvp.commander.bukkit.BukkitCommander;
import net.octopvp.commander.exception.ValidateException;

import java.util.UUID;

public class CommandManager {
    @Getter
    private static Commander commander;

    public static void init() {
        commander = BukkitCommander.getCommander(TeamsPlus.getInstance())
                .registerPackage("dev.badbird.teams.commands.impl")
                .registerDependency(TeamsPlus.class, TeamsPlus.getInstance())
                .registerProvider(PlayerData.class, new PlayerDataProvider())
                .registerProvider(Sender.class, new SenderProvider())
                .registerProvider(Team.class, new TeamProvider())
                .registerCommandPreProcessor((context) -> {
                    if (context.getCommandInfo().isAnnotationPresent(TeamPermission.class)) {
                        TeamPermission annotation = context.getCommandInfo().getAnnotation(TeamPermission.class);
                        PlayerData playerData = PlayerManager.getData((UUID) context.getCommandSender().getIdentifier());
                        if (playerData == null) {
                            throw new ValidateException("PlayerData not loaded!");
                        }
                        Team team = playerData.getPlayerTeam();
                        if (team == null) throw new ValidateException(Lang.MUST_BE_IN_TEAM.toString());
                        if (!team.isAtLeast(playerData.getUuid(), annotation.value())) {
                            throw new ValidateException(Lang.NO_PERMISSION.toString(playerData.getName(), annotation.value().name()));
                        }
                    }
                })
        ;
    }
}
