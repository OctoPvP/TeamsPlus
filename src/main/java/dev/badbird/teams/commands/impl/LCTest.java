package dev.badbird.teams.commands.impl;

import dev.badbird.teams.hooks.impl.LunarClientHook;
import dev.badbird.teams.manager.HookManager;
import dev.badbird.teams.util.Permissions;
import net.octopvp.commander.annotation.Command;
import net.octopvp.commander.annotation.Permission;
import net.octopvp.commander.annotation.Required;
import net.octopvp.commander.annotation.Sender;
import net.octopvp.commander.bukkit.annotation.DefaultSelf;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LCTest {
    @Command(name = "isusinglunar", description = "Check if a player is using lunar")
    public void isUsingLunar(@Sender CommandSender sender, @DefaultSelf Player player) {
        HookManager.getHook(LunarClientHook.class).ifPresentOrElse(hook -> {
            sender.sendMessage("LunarClient hook is: " + (hook.isEnabled() ? "Enabled" : "Disabled"));
            if (hook.isEnabled()) {
                sender.sendMessage("Is " + player.getName() + " using lunar? " + (hook.isOnLunarClient(player) ? "Yes" : "No"));
            }
        }, () -> {
            sender.sendMessage("LunarClient hook is disabled.");
        });
    }
}
