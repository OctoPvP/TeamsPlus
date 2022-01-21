package net.badbird5907.teams.commands.impl;


import com.jonahseguin.drink.annotation.Command;
import com.jonahseguin.drink.annotation.Sender;
import net.badbird5907.blib.util.CC;
import net.badbird5907.teams.TeamsPlus;
import net.badbird5907.teams.commands.impl.util.TeamInfoCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TeamsPlusCommand {

    @Command(name = "", desc = "Base teamsplus command")
    public void execute(@Sender CommandSender sender) {
        sender.sendMessage(CC.GREEN + "TeamsPlus V." + TeamsPlus.getInstance().getDescription().getVersion());
        sender.sendMessage(CC.AQUA + "For help, do /teamsplus help");
    }
}
