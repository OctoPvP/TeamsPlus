package net.badbird5907.teams.hooks.impl;

import lombok.Getter;
import lombok.SneakyThrows;
import net.badbird5907.teams.TeamsPlus;
import net.badbird5907.teams.hooks.Hook;
import net.badbird5907.teams.manager.PlayerManager;
import net.badbird5907.teams.manager.TeamsManager;
import net.badbird5907.teams.object.ChatChannel;
import net.badbird5907.teams.object.PlayerData;
import net.badbird5907.teams.object.Team;
import net.coreprotect.CoreProtect;
import net.coreprotect.CoreProtectAPI;
import net.coreprotect.config.Config;
import net.coreprotect.listener.player.PlayerChatListener;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.RegisteredListener;

import java.io.File;

public class CoreProtectHook extends Hook {
    private static YamlConfiguration coreProtectConfig;

    @Getter
    private boolean enabled;

    public CoreProtectHook() {
        super("CoreProtect");
    }

    @Override
    public void init(TeamsPlus plugin) {
        reload();
        if (!coreProtectConfig.getBoolean("enable-hook")) return;

        if (Config.getGlobal().PLAYER_MESSAGES && coreProtectConfig.getBoolean("modify-chat-logs")) {
            // Unregister the CoreProtect chat listener, so we can use our own chat listener.
            for (RegisteredListener registeredListener : HandlerList.getRegisteredListeners(CoreProtect.getInstance())) {
                if (registeredListener.getListener().getClass().equals(PlayerChatListener.class)) {
                    HandlerList.unregisterAll(registeredListener.getListener());
                    enabled = true;
                }
            }
        }
    }

    public void logChat(Player player, String message, ChatChannel chatChannel) {
        if (!enabled) return;
        String prefix = "";
        if (chatChannel == ChatChannel.TEAM) {
            PlayerData data = PlayerManager.getData(player.getUniqueId());
            prefix = "[Team] [" + data.getPlayerTeam().getName() + "] |";
        } else if (chatChannel == ChatChannel.GLOBAL) {
            prefix = "[Global] |";
        } else if (chatChannel == ChatChannel.ALLY) {
            PlayerData data = PlayerManager.getData(player.getUniqueId());
            Team allyTeam = TeamsManager.getInstance().getTeamById(data.getAllyChatTeamId());
            if (allyTeam == null) prefix = "[Ally] [Unknown] |";
            else prefix = "[Ally] [" + allyTeam.getName() + "] |";
        }
        CoreProtect.getInstance().getAPI().logChat(player, prefix + message);
    }

    @SneakyThrows
    @Override
    public void reload() {
        super.reload();
        File file = new File(TeamsPlus.getInstance().getDataFolder() + "/hooks/CoreProtect.yml");
        if (!file.getParentFile().exists())
            file.getParentFile().mkdirs();
        if (!file.exists()) {
            TeamsPlus.getInstance().saveResource("CoreProtect.yml", false);
        }
        coreProtectConfig = new YamlConfiguration();
        coreProtectConfig.load(file);
    }

    @Override
    public void disable(TeamsPlus plugin) {

    }
}
