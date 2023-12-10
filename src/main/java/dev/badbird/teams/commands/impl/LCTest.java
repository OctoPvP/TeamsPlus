package dev.badbird.teams.commands.impl;

import com.lunarclient.bukkitapi.LunarClientAPI;
import dev.badbird.teams.hooks.impl.LunarClientHook;
import dev.badbird.teams.manager.HookManager;
import lombok.SneakyThrows;
import net.octopvp.commander.annotation.Command;
import net.octopvp.commander.annotation.Required;
import net.octopvp.commander.annotation.Sender;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.Set;
import java.util.UUID;

public class LCTest {
    @Command(name = "isusinglunar", description = "Check if a player is using lunar")
    public void isUsingLunar(@Sender CommandSender sender, @Required Player player) {
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
