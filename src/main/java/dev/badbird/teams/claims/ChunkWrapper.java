package dev.badbird.teams.claims;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.UUID;


@Data
@AllArgsConstructor
public class ChunkWrapper {
    private int chunkX;
    private int chunkZ;
    private UUID world;

    public long getHash() {
        return ClaimHandler.getInstance().hashChunk(chunkX, chunkZ);
    }

    public ChunkWrapper(Location location) {
        this(location.getChunk());
    }

    public ChunkWrapper(Chunk chunk) {
        this(chunk.getX(), chunk.getZ(), chunk.getWorld().getUID());
    }

    public ChunkWrapper(long hash, UUID world) {
        this(ClaimHandler.getInstance().getChunkX(hash), ClaimHandler.getInstance().getChunkZ(hash), world);
    }

    public Chunk getChunk(World world) {
        return world.getChunkAt(chunkX, chunkZ);
    }
}
