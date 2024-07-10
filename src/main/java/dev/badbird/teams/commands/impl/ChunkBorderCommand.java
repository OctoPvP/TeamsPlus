package dev.badbird.teams.commands.impl;


import dev.badbird.teams.claims.chunkrenderer.ChunkBorderRenderer;
import dev.badbird.teams.commands.annotation.Sender;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.CommandDescription;
import org.incendo.cloud.annotations.processing.CommandContainer;

@CommandContainer
public class ChunkBorderCommand {
    @Command("chunkborder|cb|chunkborders")
    @CommandDescription("Toggle chunk border visibility")
    public void chunkBorder(@Sender Player sender) {
        if (ChunkBorderRenderer.getPlayers().contains(sender.getUniqueId())) {
            ChunkBorderRenderer.getPlayers().remove(sender.getUniqueId());
            sender.sendMessage("Chunk borders disabled");
        } else {
            ChunkBorderRenderer.getPlayers().add(sender.getUniqueId());
            sender.sendMessage("Chunk borders enabled");
        }
    }
}
