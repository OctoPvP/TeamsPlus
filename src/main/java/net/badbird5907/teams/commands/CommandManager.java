package net.badbird5907.teams.commands;

import net.badbird5907.teams.TeamsPlus;
import net.badbird5907.teams.commands.annotation.Sender;
import net.badbird5907.teams.commands.impl.TeamsPlusCommand;
import net.badbird5907.teams.manager.PlayerManager;
import net.badbird5907.teams.manager.TeamsManager;
import net.badbird5907.teams.object.PlayerData;
import net.badbird5907.teams.object.Team;
import net.badbird5907.teams.util.PlayerDataSenderProvider;
import revxrsal.commands.bukkit.BukkitCommandHandler;

public class CommandManager {
    public static void init() {
        BukkitCommandHandler commandHandler = BukkitCommandHandler.create(TeamsPlus.getInstance());
        commandHandler
                .registerValueResolver(TeamsPlus.class, ctx -> TeamsPlus.getInstance())
                .registerValueResolver(PlayerData.class, ctx -> {
                    if (ctx.parameter().hasAnnotation(Sender.class))
                        return PlayerManager.getData(ctx.actor().getUniqueId());
                    else return PlayerManager.getData(ctx.pop());
                })
                .registerValueResolver(Team.class, ctx -> {
                    if (ctx.parameter().hasAnnotation(Sender.class))
                        return TeamsManager.getInstance().getPlayerTeam(ctx.actor());
                    else return TeamsManager.getInstance().getTeamByName(ctx.pop());
                })
                .registerValueResolver(String.class,ctx -> ctx.arguments().join(" "))
                .registerSenderResolver(new PlayerDataSenderProvider())
                .register(new TeamsPlusCommand())
        ;
    }
}
