package dev.badbird.teams.claims;

import dev.badbird.teams.manager.TeamsManager;
import dev.badbird.teams.object.Team;
import net.badbird5907.blib.objects.tuple.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import org.bukkit.HeightMap;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.block.CraftBlock;
import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class ClaimMapRenderer extends MapRenderer {

    public record MapResolution(byte level) {
        /*
        Level 0/4 : 128×128 blocks (each map pixel represents 1 block)
        Level 1/4 : 256×256 blocks (2×2 blocks per map pixel)
        Level 2/4 : 512×512 blocks (4×4 blocks per map pixel)
        Level 3/4 : 1024×1024 blocks (8×8 blocks per map pixel)
        Level 4/4 : 2048×2048 blocks (16×16 blocks per map pixel)
         */

        public int getSize() {
            return 128 << level;
        }

        public int getPixelsPerBlock() {
            return 1 << level;
        }
    }

    @Override
    public void render(@NotNull MapView map, @NotNull MapCanvas canvas, @NotNull Player player) {
        // map.setTrackingPosition(false);

        // TODO: only render every couple of ticks
        // clean up the map

        World world = player.getWorld();
        int centerX = (int) player.getX();
        int centerZ = (int) player.getZ();
        map.setCenterX(centerX);
        map.setCenterZ(centerZ);
        WorldClaimTracker tracker = ClaimHandler.getInstance().getTracker(world);


        // byte mapLevel = map.getScale().getValue();
        MapResolution resolution = new MapResolution((byte) 0x00);
        int mapSize = resolution.getSize();
        int pixelsPerBlock = resolution.getPixelsPerBlock();

        int startX = centerX - mapSize / 2;
        int startZ = centerZ - mapSize / 2;
        int endX = centerX + mapSize / 2;
        int endZ = centerZ + mapSize / 2;

        Team playerTeam = TeamsManager.getInstance().getPlayerTeam(player.getUniqueId());

        // go through each few blocks (pixelsPerBlock) and get the block color
        for (int x = startX; x < endX; x += pixelsPerBlock) {
            for (int z = startZ; z < endZ; z += pixelsPerBlock) {
                Pair<MapColor, MapColor.Brightness> colorBrightnessPair;
                ChunkWrapper chunk = new ChunkWrapper(x >> 4, z >> 4, world.getUID());
                if (tracker.isClaimed(chunk.getHash())) {
                    UUID claimingTeam = tracker.getClaimingTeam(chunk.getHash());
                    boolean isOwnTeam = playerTeam != null && playerTeam.getTeamId().equals(claimingTeam);
                    colorBrightnessPair = new Pair<>(isOwnTeam ? MapColor.COLOR_LIGHT_GREEN : MapColor.NETHER, MapColor.Brightness.HIGH);
                } else {
                    colorBrightnessPair = getBlockColor(world, x, z, world.hasCeiling());
                }
                MapColor color = colorBrightnessPair.getValue0();
                MapColor.Brightness brightness = colorBrightnessPair.getValue1();

                // set the pixels
                for (int i = 0; i < pixelsPerBlock; i++) {
                    for (int j = 0; j < pixelsPerBlock; j++) {
                        canvas.setPixel(x + i - startX, z + j - startZ, color.getPackedId(brightness));
                    }
                }
            }
        }

//        int chunkSize = 16 * pixelsPerBlock;
//        for (int x = 0; x < mapSize; x++) {
//            for (int z = 0; z < mapSize; z++) {
//                int worldX = startX + x;
//                int worldZ = startZ + z;
//                int chunkX = worldX >> 4;
//                int chunkZ = worldZ >> 4;
//                long hash = ClaimHandler.getInstance().hashChunk(chunkX, chunkZ);
//
//                if (worldX % chunkSize == 0 || (worldX + 1) % chunkSize == 0 || worldZ % chunkSize == 0 || (worldZ + 1) % chunkSize == 0) {
//                    Color color = tracker.isClaimed(hash) ? Color.RED : Color.GRAY;
//                    canvas.setPixelColor(x, z, color);
//                }
//            }
//        }
    }

    private Pair<MapColor, MapColor.Brightness> getBlockColor(World world, int x, int z, boolean ceil) {
        if (ceil) {
            return new Pair<>(MapColor.COLOR_GRAY, MapColor.Brightness.NORMAL);
        }
        HeightMap heightMap = HeightMap.WORLD_SURFACE;
        Block highest = world.getHighestBlockAt(x, z, heightMap);
        BlockState nmsState = ((CraftBlock) highest).getNMS();
        CraftWorld bukkitWorld = (CraftWorld) highest.getWorld();
        ServerLevel nmsWorld = bukkitWorld.getHandle();
        BlockPos pos = new BlockPos(x, highest.getY(), z);
        // On land above water, a block's color is darker if placed at a lower elevation than the block north of it, or brighter if placed at a higher elevation than the block north of it. Maps also show ground up to about 15 blocks below the surface of the water as slightly lighter blue, to show where the ground rises.
        // double g = (e - d) * 4.0 / (double)(i + 4) + ((double)(o + p & 1) - 0.5) * 0.4;
        int height = highest.getY();
        int northHeight = world.getHighestBlockAt(x, z - 1).getY();
        MapColor.Brightness brightness = MapColor.Brightness.NORMAL;
        if (height < northHeight) {
            int delta = Math.abs(northHeight - height);
            int lowestThreshold = 10; // TODO figure out how to do this properly
            brightness = delta < lowestThreshold ? MapColor.Brightness.LOW : MapColor.Brightness.LOWEST;
        } else if (height > northHeight) {
            brightness = MapColor.Brightness.HIGH;
        }

        return new Pair<>(
                nmsState.getMapColor(nmsWorld, pos),
                brightness
        );
    }
}
