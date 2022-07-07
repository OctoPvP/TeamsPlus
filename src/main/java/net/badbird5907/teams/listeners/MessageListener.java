package net.badbird5907.teams.listeners;

import net.badbird5907.teams.TeamsPlus;
import net.badbird5907.teams.hooks.Hook;
import net.badbird5907.teams.hooks.impl.CoreProtectHook;
import net.badbird5907.teams.manager.HookManager;
import net.badbird5907.teams.manager.MessageManager;
import net.badbird5907.teams.manager.PlayerManager;
import net.badbird5907.teams.manager.TeamsManager;
import net.badbird5907.teams.object.ChatChannel;
import net.badbird5907.teams.object.Lang;
import net.badbird5907.teams.object.PlayerData;
import net.badbird5907.teams.object.Team;
import net.coreprotect.CoreProtect;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class MessageListener implements Listener {
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onMessage(AsyncPlayerChatEvent event) {
        PlayerData data = PlayerManager.getData(event.getPlayer().getUniqueId());
        Team senderTeam = data.getPlayerTeam();
        if (senderTeam == null) {
            data.setAllyChatTeamId(null);
            data.setCurrentChannel(ChatChannel.GLOBAL);
        }
        switch (data.getCurrentChannel()) {
            case ALLY -> {
                if (data.getAllyChatTeamId() == null) {
                    data.setCurrentChannel(ChatChannel.GLOBAL);
                    MessageManager.handleGlobal(data, event.getPlayer(), event.getMessage());
                    return;
                }
                event.setCancelled(true);
                MessageManager.handleAlly(data, event.getPlayer(), senderTeam, event.getMessage());
                return;
            }
            case TEAM -> {
                if (senderTeam == null) {
                    data.setCurrentChannel(ChatChannel.GLOBAL);
                    MessageManager.handleGlobal(data, event.getPlayer(), event.getMessage());
                    return;
                }
                event.setCancelled(true);
                MessageManager.handleTeam(event.getPlayer(),event.getMessage(), senderTeam);
                return;
            }
            case GLOBAL -> {
                MessageManager.handleGlobal(data, event.getPlayer(), event.getMessage());
            }
        }
    }


}
