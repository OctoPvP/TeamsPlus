package net.badbird5907.teams.commands;

import com.jonahseguin.drink.CommandService;
import com.jonahseguin.drink.Drink;
import net.badbird5907.blib.command.Sender;
import net.badbird5907.teams.TeamsPlus;
import net.badbird5907.teams.commands.impl.TeamsPlusCommand;
import net.badbird5907.teams.commands.impl.managment.InviteCommand;
import net.badbird5907.teams.commands.impl.managment.JoinCommand;
import net.badbird5907.teams.commands.impl.util.CreateTeamCommand;
import net.badbird5907.teams.commands.impl.util.ReloadCommand;
import net.badbird5907.teams.commands.impl.util.TeamInfoCommand;
import net.badbird5907.teams.object.Team;
import net.badbird5907.teams.object.player.PlayerData;
import net.badbird5907.teams.util.PlayerDataProvider;
import net.badbird5907.teams.util.SenderProvider;
import net.badbird5907.teams.util.TeamProvider;

public class CommandManager {
    public static void init() {
        CommandService drink = Drink.get(TeamsPlus.getInstance());
        drink.bind(Sender.class).toProvider(new SenderProvider());
        drink.bind(TeamsPlus.class).toInstance(TeamsPlus.getInstance());
        drink.bind(PlayerData.class).toProvider(new PlayerDataProvider());
        drink.bind(Team.class).toProvider(new TeamProvider());
        drink.register(new TeamsPlusCommand(), "teamsplus", "teams+", "team", "teams")
                .registerSub(new CreateTeamCommand())
                .registerSub(new TeamInfoCommand())
                .registerSub(new InviteCommand())
                .registerSub(new JoinCommand())
                .registerSub(new ReloadCommand())
        ;
        drink.registerCommands();
    }
}
