package net.badbird5907.teams.commands;

import lombok.Getter;
import net.badbird5907.blib.command.Sender;
import net.badbird5907.teams.TeamsPlus;
import net.badbird5907.teams.commands.annotation.TeamPermission;
import net.badbird5907.teams.commands.provider.PlayerDataProvider;
import net.badbird5907.teams.commands.provider.SenderProvider;
import net.badbird5907.teams.commands.provider.TeamProvider;
import net.badbird5907.teams.manager.PlayerManager;
import net.badbird5907.teams.object.Lang;
import net.badbird5907.teams.object.PlayerData;
import net.badbird5907.teams.object.Team;
import net.octopvp.commander.Commander;
import net.octopvp.commander.bukkit.BukkitCommander;
import net.octopvp.commander.exception.ValidateException;

public class CommandManager {
    @Getter
    private static Commander commander;

    public static void init() {
        commander = BukkitCommander.getCommander(TeamsPlus.getInstance())
                .registerPackage("net.badbird5907.teams.commands.impl")
                .registerDependency(TeamsPlus.class, TeamsPlus.getInstance())
                .registerProvider(PlayerData.class, new PlayerDataProvider())
                .registerProvider(Sender.class, new SenderProvider())
                .registerProvider(Team.class, new TeamProvider())
                .registerCommandPreProcessor((context) -> {
                    if (context.getCommandInfo().isAnnotationPresent(TeamPermission.class)) {
                        TeamPermission annotation = context.getCommandInfo().getAnnotation(TeamPermission.class);
                        PlayerData playerData = PlayerManager.getData(context.getCommandSender().getIdentifier());
                        if (playerData == null) {
                            throw new ValidateException("PlayerData not loaded!");
                        }
                        Team team = playerData.getPlayerTeam();
                        if (team == null) throw new ValidateException(Lang.MUST_BE_IN_TEAM.toString());
                        if (!team.isAtLeast(playerData.getUuid(), annotation.value())) {
                            throw new ValidateException(Lang.NO_PERMISSION.toString(playerData.getName()));
                        }
                    }
                })
        ;
    }
}
