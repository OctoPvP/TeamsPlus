package net.badbird5907.teams.commands;

import com.jonahseguin.drink.CommandService;
import com.jonahseguin.drink.Drink;
import net.badbird5907.blib.command.Sender;
import net.badbird5907.teams.TeamsPlus;
import net.badbird5907.teams.commands.impl.TeamsPlusCommand;
import net.badbird5907.teams.commands.impl.managment.AllyCommand;
import net.badbird5907.teams.commands.impl.managment.InviteCommand;
import net.badbird5907.teams.commands.impl.managment.JoinCommand;
import net.badbird5907.teams.commands.impl.managment.RenameCommand;
import net.badbird5907.teams.commands.impl.util.CreateTeamCommand;
import net.badbird5907.teams.commands.impl.util.ReloadCommand;
import net.badbird5907.teams.commands.impl.util.TeamInfoCommand;
import net.badbird5907.teams.object.PlayerData;
import net.badbird5907.teams.object.Team;
import net.badbird5907.teams.util.*;

public class CommandManager {
    public static void init() {
        CommandService drink = Drink.get(TeamsPlus.getInstance());
        drink.bind(Sender.class).toProvider(new SenderProvider());
        drink.bind(TeamsPlus.class).toInstance(TeamsPlus.getInstance());
        drink.bind(PlayerData.class).toProvider(new PlayerDataProvider());
        drink.bind(PlayerData.class).annotatedWith(com.jonahseguin.drink.annotation.Sender.class).toProvider(new PlayerDataSenderProvider());
        drink.bind(Team.class).toProvider(new TeamProvider());
        drink.bind(Team.class).annotatedWith(com.jonahseguin.drink.annotation.Sender.class).toProvider(new TeamSenderProvider());
        drink.bind(String[].class).toProvider(new ArgsProvider());
        drink.register(new TeamsPlusCommand(), "teamsplus", "teams+", "team", "teams")
                .registerSub(new CreateTeamCommand())
                .registerSub(new TeamInfoCommand())
                .registerSub(new InviteCommand())
                .registerSub(new JoinCommand())
                .registerSub(new ReloadCommand())
                .registerSub(new AllyCommand())
                .registerSub(new RenameCommand())
        ;
        drink.registerCommands();
    }
}
