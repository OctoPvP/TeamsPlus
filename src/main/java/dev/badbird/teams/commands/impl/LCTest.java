package dev.badbird.teams.commands.impl;

import dev.badbird.teams.hooks.impl.LunarClientHook;
import dev.badbird.teams.manager.HookManager;
import net.badbird5907.anticombatlog.relocate.blib.command.Command;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.geysermc.api.Geyser;
import org.geysermc.floodgate.api.FloodgateApi;
import org.incendo.cloud.annotations.processing.CommandContainer;

@CommandContainer
public class LCTest {
    @Command(name = "isusinglunar <target>", description = "Check if a player is using lunar")
    public void isUsingLunar(CommandSender sender, Player target) {
        HookManager.getHook(LunarClientHook.class).ifPresentOrElse(hook -> {
            sender.sendMessage("LunarClient hook is: " + (hook.isEnabled() ? "Enabled" : "Disabled"));
            if (hook.isEnabled()) {
                sender.sendMessage("Is " + target.getName() + " using lunar? " + (hook.isOnLunarClient(target) ? "Yes" : "No"));
            }
        }, () -> {
            sender.sendMessage("LunarClient hook is disabled.");
        });
    }

    @Command(name = "isf <player>", description = "Check floodgate")
    public void floodgate(CommandSender sender, Player player) {
        if (Bukkit.getPluginManager().isPluginEnabled("Geyser-Spigot"))
            sender.sendMessage("GeyserApi#isBedrockPlayer: " + Geyser.api().isBedrockPlayer(player.getUniqueId()));
        if (Bukkit.getPluginManager().isPluginEnabled("floodgate"))
            sender.sendMessage("FloodgateApi#isFloodgatePlayer: " + FloodgateApi.getInstance().isFloodgatePlayer(player
                    .getUniqueId()));
    }
}
