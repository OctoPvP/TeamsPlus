package dev.badbird.teams.claims.chunkrenderer;

import dev.badbird.teams.TeamsPlus;
import lombok.Getter;
import net.badbird5907.blib.util.Cuboid;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Color;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ChunkBorderRenderer extends BukkitRunnable {
    @Getter
    private static List<UUID> players = new ArrayList<>();

    @Override
    public void run() {
        for (UUID player : players) {
            drawBorders(player);
        }
    }

    private void drawBorders(UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        if (player == null || !player.isValid()) {
            return;
        }
        Chunk chunk = player.getChunk();
        // TeamsPlus.getInstance().getLogger().info("Drawing borders for " + player.getName() + " at " + chunk.getX() + ", " + chunk.getZ());
        Particle.DustOptions dustOptions = new Particle.DustOptions(Color.RED, 1);
        int mY = (int) (player.getLocation().getY() - 20); // 20 blocks below the player
        int pY = (int) (player.getLocation().getY() + 20); // 20 blocks above the player
        int deltaY = pY - mY;
        drawChunk(chunk, pY, mY, deltaY, dustOptions, player);
    }
    private void drawChunk(Chunk chunk, int pY, int mY, int dY, Particle.DustOptions dustOptions, Player player) {
        Cuboid.CuboidDirection[] dir = new Cuboid.CuboidDirection[] {
                Cuboid.CuboidDirection.NORTH,
                Cuboid.CuboidDirection.SOUTH,
                Cuboid.CuboidDirection.EAST,
                Cuboid.CuboidDirection.WEST
        };
        for (int i = 0; i < dY; i += 2) {
            int y = mY + i;
            for (int k = 0; k < 17; k++) {
                for (Cuboid.CuboidDirection direction : dir) {
                    int x = chunk.getX() * 16;
                    int z = chunk.getZ() * 16;
                    switch (direction) {
                        case NORTH:
                            x += k;
                            break;
                        case SOUTH:
                            x += k;
                            z += 16;
                            break;
                        case EAST:
                            z += k;
                            break;
                        case WEST:
                            x += 16;
                            z += k;
                            break;
                    }
                    player.spawnParticle(Particle.DUST, x, y, z, 0, 0, 0, 0, 1, dustOptions);
                }
            }
        }
    }
}
