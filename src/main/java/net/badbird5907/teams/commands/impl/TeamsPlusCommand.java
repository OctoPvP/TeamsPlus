package net.badbird5907.teams.commands.impl;


import com.jonahseguin.drink.annotation.Command;
import com.jonahseguin.drink.annotation.Sender;
import org.bukkit.command.CommandSender;

public class TeamsPlusCommand {

    @Command(name = "", desc = "Base teamsplus command")
    public void execute(@Sender CommandSender sender, String[] args) {
        sender.sendMessage("Hello World!");
    }
}
