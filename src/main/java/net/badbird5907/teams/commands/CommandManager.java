package net.badbird5907.teams.commands;

import lombok.Getter;
import net.badbird5907.blib.command.Sender;
import net.badbird5907.teams.TeamsPlus;
import net.badbird5907.teams.commands.provider.PlayerDataProvider;
import net.badbird5907.teams.commands.provider.SenderProvider;
import net.badbird5907.teams.commands.provider.TeamProvider;
import net.badbird5907.teams.object.PlayerData;
import net.badbird5907.teams.object.Team;
import net.octopvp.commander.Commander;
import net.octopvp.commander.bukkit.BukkitCommander;

public class CommandManager {
    @Getter
    private static Commander commander;

    public static void init() {
        commander = BukkitCommander.getCommander(TeamsPlus.getInstance())
                .registerPackage("net.badbird5907.teams.commands.impl")
                .registerDependency(TeamsPlus.class, TeamsPlus.getInstance())
                .registerProvider(PlayerData.class, new PlayerDataProvider())
                .registerProvider(Sender.class, new SenderProvider())
                .registerProvider(Team.class, new TeamProvider());
    }
}
