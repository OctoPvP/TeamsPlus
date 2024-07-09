package dev.badbird.teams.commands.impl;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.processing.CommandContainer;

@CommandContainer
public class TestCmd {
    @Command("location <target>")
    public void test(
            CommandSender sender,
            @Argument("target") Location target) {
        sender.sendMessage(target.getWorld().getName() + " | " + target.getX() + " | " + target.getY() + " | " + target.getZ());
    }
}
