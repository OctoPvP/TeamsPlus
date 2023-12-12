package dev.badbird.teams.commands.impl;

import dev.badbird.teams.hooks.impl.LunarClientHook;
import dev.badbird.teams.manager.HookManager;
import net.octopvp.commander.annotation.Command;
import net.octopvp.commander.annotation.Sender;
import net.octopvp.commander.bukkit.annotation.DefaultSelf;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.geysermc.api.Geyser;
import org.geysermc.floodgate.api.FloodgateApi;

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

    @Command(name = "isf", description = "Check floodgate")
    public void floodgate(@Sender Player sender) {
        if (Bukkit.getPluginManager().isPluginEnabled("Geyser-Spigot"))
            sender.sendMessage("GeyserApi#isBedrockPlayer: " + Geyser.api().isBedrockPlayer(sender.getUniqueId()));
        if (Bukkit.getPluginManager().isPluginEnabled("floodgate"))
            sender.sendMessage("FloodgateApi#isFloodgatePlayer: " + FloodgateApi.getInstance().isFloodgatePlayer(sender.getUniqueId()));
    }
}
