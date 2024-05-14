package dev.badbird.teams.commands.impl;

import dev.badbird.teams.claims.chunkrenderer.ChunkBorderRenderer;
import net.octopvp.commander.annotation.Command;
import net.octopvp.commander.annotation.Sender;
import org.bukkit.entity.Player;

public class ChunkBorderCommand {
    @Command(name = "chunkborder", description = "Toggle chunk border visibility", aliases = {"cb", "chunkborders"})
    public void chunkBorder(@Sender Player player) {
        if (ChunkBorderRenderer.getPlayers().contains(player.getUniqueId())) {
            ChunkBorderRenderer.getPlayers().remove(player.getUniqueId());
            player.sendMessage("Chunk borders disabled");
        } else {
            ChunkBorderRenderer.getPlayers().add(player.getUniqueId());
            player.sendMessage("Chunk borders enabled");
        }
    }
}
