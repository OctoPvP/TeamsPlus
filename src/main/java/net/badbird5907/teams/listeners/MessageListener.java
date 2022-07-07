package net.badbird5907.teams.listeners;

import net.badbird5907.teams.TeamsPlus;
import net.badbird5907.teams.manager.MessageManager;
import net.badbird5907.teams.manager.PlayerManager;
import net.badbird5907.teams.manager.TeamsManager;
import net.badbird5907.teams.object.ChatChannel;
import net.badbird5907.teams.object.Lang;
import net.badbird5907.teams.object.PlayerData;
import net.badbird5907.teams.object.Team;
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
                    handleGlobal(data, event);
                    return;
                }
                event.setCancelled(true);
                Team targetTeam = TeamsManager.getInstance().getTeamById(data.getAllyChatTeamId());
                if (targetTeam == null) {
                    data.setAllyChatTeamId(null);
                    data.setCurrentChannel(ChatChannel.GLOBAL);
                    return;
                }
                targetTeam.broadcast(Lang.CHAT_FORMAT_ALLY.toString(event.getPlayer().getDisplayName(), senderTeam.getName(), event.getMessage()));
                return;
            }
            case TEAM -> {
                if (senderTeam == null) {
                    handleGlobal(data, event);
                    return;
                }
                event.setCancelled(true);
                senderTeam.broadcast(Lang.CHAT_FORMAT_TEAM.toString(event.getPlayer().getDisplayName(), event.getMessage()));
                return;
            }
            case GLOBAL -> {
                handleGlobal(data, event);
            }
        }
    }

    private void handleGlobal(PlayerData data, AsyncPlayerChatEvent event) {
        if (TeamsPlus.getInstance().getConfig().getBoolean("chat.enable")) {
            if (data != null)
                event.setCancelled(true); //for custom chat handling
            else return;
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                onlinePlayer.sendMessage(MessageManager.formatGlobal(event.getMessage(), event.getPlayer(), onlinePlayer));
            }//TODO team/ally chat
        }
    }
}
