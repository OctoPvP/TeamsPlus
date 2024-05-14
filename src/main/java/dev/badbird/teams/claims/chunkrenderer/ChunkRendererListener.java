package dev.badbird.teams.claims.chunkrenderer;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class ChunkRendererListener implements Listener {
    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        ChunkBorderRenderer.getPlayers().remove(event.getPlayer().getUniqueId());
    }
}
