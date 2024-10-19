package dev.badbird.teams.commands.impl;

import dev.badbird.teams.commands.annotation.Sender;
import dev.badbird.teams.menu.ConfirmMenu;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.map.MapView;
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

    @Command("test")
    public void test(CommandSender sender) {
        ConfirmMenu doThis = new ConfirmMenu("do this", val -> {
            sender.sendMessage(val + "");
        });
        doThis.setPermanent(true);
        doThis.open((Player) sender);
    }

    @Command("testsender <target>")
    public void ts(@Sender Player sender, Player target) {
        sender.sendMessage(sender.getName() + " | " + target.getName());
    }

}
