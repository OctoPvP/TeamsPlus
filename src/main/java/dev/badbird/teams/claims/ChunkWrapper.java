package dev.badbird.teams.claims;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;


@Data
@AllArgsConstructor
public class ChunkWrapper {
    private int chunkX;
    private int chunkZ;

    public long getHash() {
        return ClaimHandler.getInstance().hashChunk(chunkX, chunkZ);
    }

    public ChunkWrapper(Location location) {
        this(location.getChunk());
    }

    public ChunkWrapper(Chunk chunk) {
        this(chunk.getX(), chunk.getZ());
    }

    public ChunkWrapper(long hash) {
        this(ClaimHandler.getInstance().getChunkX(hash), ClaimHandler.getInstance().getChunkZ(hash));
    }

    public Chunk getChunk(World world) {
        return world.getChunkAt(chunkX, chunkZ);
    }
}
