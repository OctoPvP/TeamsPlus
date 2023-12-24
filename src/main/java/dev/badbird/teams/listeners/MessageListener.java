package dev.badbird.teams.listeners;

import dev.badbird.teams.manager.MessageManager;
import dev.badbird.teams.manager.PlayerManager;
import dev.badbird.teams.object.ChatChannel;
import dev.badbird.teams.object.PlayerData;
import dev.badbird.teams.object.Team;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class MessageListener implements Listener {
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onMessage(AsyncChatEvent event) {
        PlayerData data = PlayerManager.getData(event.getPlayer().getUniqueId());
        Team senderTeam = data.getPlayerTeam();
        if (senderTeam == null) {
            data.setAllyChatTeamId(null);
            data.setCurrentChannel(ChatChannel.GLOBAL);
        }
        String message = LegacyComponentSerializer.legacySection().serialize(event.message());
        switch (data.getCurrentChannel()) {
            case ALLY -> {
                if (data.getAllyChatTeamId() == null) {
                    data.setCurrentChannel(ChatChannel.GLOBAL);
                    if (MessageManager.handleGlobal(data, event.getPlayer(), message)) {
                        event.setCancelled(true);
                    }
                    return;
                }
                event.setCancelled(true);
                MessageManager.handleAlly(data, event.getPlayer(), senderTeam, message);
                return;
            }
            case TEAM -> {
                if (senderTeam == null) {
                    data.setCurrentChannel(ChatChannel.GLOBAL);
                    if (MessageManager.handleGlobal(data, event.getPlayer(), message)) {
                        event.setCancelled(true);
                    }
                    return;
                }
                event.setCancelled(true);
                MessageManager.handleTeam(event.getPlayer(),message, senderTeam);
                return;
            }
            case GLOBAL -> {
                if (MessageManager.handleGlobal(data, event.getPlayer(), message)) {
                    event.setCancelled(true);
                }
            }
        }
    }


}
