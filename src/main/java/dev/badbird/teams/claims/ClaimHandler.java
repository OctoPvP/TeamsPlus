package dev.badbird.teams.claims;

import dev.badbird.teams.TeamsPlus;
import dev.badbird.teams.claims.chunkrenderer.ChunkBorderRenderer;
import dev.badbird.teams.claims.chunkrenderer.ChunkRendererListener;
import dev.badbird.teams.claims.storage.ClaimStorageProvider;
import lombok.Getter;
import org.bukkit.Chunk;
import org.bukkit.Location;

@Getter
public class ClaimHandler {
    @Getter
    private static ClaimHandler instance = new ClaimHandler();
    private ClaimHandler() {}

    private ClaimStorageProvider storageProvider;

    public void init() {
        TeamsPlus.getInstance().getServer().getPluginManager().registerEvents(new ChunkRendererListener(), TeamsPlus.getInstance());
        new ChunkBorderRenderer().runTaskTimerAsynchronously(TeamsPlus.getInstance(), 0, 10);
    }

    public long hashChunk(int cx, int cz) {
        return ((long) cz << 32) // shift cz 32 bits to left
                | (cx & 0xFFFFFFFFL); // write cx to right (lower) 32 bits
    }

    public long hashChunk(Chunk chunk) {
        return hashChunk(chunk.getX(), chunk.getZ());
    }

    public long getChunkFromBlock(int block) {
        // shift block 4 bits to right to get our chunk
        return block >> 4;
    }

    public long hashChunk(Location location) {
        return hashChunk(location.getChunk());
    }
}
