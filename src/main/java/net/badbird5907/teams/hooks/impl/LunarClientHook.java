package net.badbird5907.teams.hooks.impl;

import com.lunarclient.bukkitapi.LunarClientAPI;
import net.badbird5907.teams.TeamsPlus;
import net.badbird5907.teams.hooks.Hook;
import org.bukkit.entity.Player;

import java.util.UUID;

public class LunarClientHook extends Hook {
    private static boolean enabled = false;
    public LunarClientHook() {
        super("LunarClient-API");
    }

    @Override
    public void init(TeamsPlus plugin) {
        enabled = true;
    }

    @Override
    public void disable(TeamsPlus plugin) {

    }

    public static boolean isOnLunarClient(Player player) {
        if (enabled) return LunarClientAPI.getInstance().isRunningLunarClient(player);
        else return false;
    }

    public static boolean isOnLunarClient(UUID player) {
        if (enabled) return LunarClientAPI.getInstance().isRunningLunarClient(player);
        else return false;
    }
}
