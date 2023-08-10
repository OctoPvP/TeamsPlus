package dev.badbird.teams.hooks.impl;

import dev.badbird.teams.TeamsPlus;
import dev.badbird.teams.hooks.Hook;
import org.bukkit.entity.Player;
import org.bukkit.metadata.MetadataValue;

public class VanishHook extends Hook {
    private static boolean enableHook = true;

    public VanishHook() {
        super("");
    }

    public static boolean isVanished(Player player) {
        if (enableHook) {
            for (MetadataValue meta : player.getMetadata("vanished")) {
                if (meta.asBoolean()) return true;
            }
        }
        return false;
    }

    @Override
    public void init(TeamsPlus plugin) {

    }

    @Override
    public void disable(TeamsPlus plugin) {

    }

    @Override
    public void reload() {
        enableHook = TeamsPlus.getInstance().getConfig().getBoolean("enable-vanish-hook", true);
    }
}
