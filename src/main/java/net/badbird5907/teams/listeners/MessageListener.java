package net.badbird5907.teams.listeners;

import net.badbird5907.teams.TeamsPlus;
import net.badbird5907.teams.manager.MessageManager;
import net.badbird5907.teams.manager.PlayerManager;
import net.badbird5907.teams.object.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class MessageListener implements Listener {
    @EventHandler(priority = EventPriority.LOWEST)
    public void onMessage(AsyncPlayerChatEvent event) {
        if (event.isCancelled())
            return;
        if (TeamsPlus.getInstance().getConfig().getBoolean("chat.custom-chat")) {
            PlayerData data = PlayerManager.getData(event.getPlayer().getUniqueId());
            if (data != null)
                event.setCancelled(true); //for custom chat handling
            else return;
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                onlinePlayer.sendMessage(MessageManager.formatGlobal(event.getMessage(), event.getPlayer(), onlinePlayer));
            }//TODO team/ally chat
        }
    }
}
