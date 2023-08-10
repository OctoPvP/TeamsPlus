package dev.badbird.teams.listeners;

import dev.badbird.teams.manager.PlayerManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class SessionListener implements Listener {
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        PlayerManager.join(event.getPlayer());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        PlayerManager.leave(event.getPlayer());
    }

    @EventHandler
    public void onPreJoin(AsyncPlayerPreLoginEvent event) {
        PlayerManager.preJoin(event);
    }
}
