package dev.badbird.teams.claims;

import com.google.common.collect.ImmutableMap;
import dev.badbird.teams.TeamsPlus;
import dev.badbird.teams.claims.chunkrenderer.ChunkBorderRenderer;
import dev.badbird.teams.claims.chunkrenderer.ChunkRendererListener;
import dev.badbird.teams.listeners.ClaimListener;
import dev.badbird.teams.manager.TeamsManager;
import dev.badbird.teams.object.Team;
import lombok.Getter;
import net.badbird5907.blib.util.Cooldown;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.event.Listener;

import java.util.HashMap;
import java.util.Map;

public class ClaimHandler {
    @Getter
    private static ClaimHandler instance = new ClaimHandler();

    private ClaimHandler() {
    }

    private Map<Long, ClaimInfo> claimMap = new HashMap<>();

    @Getter
    private boolean enabled = false;

    public void init() {
        enabled = TeamsPlus.getInstance().getConfig().getBoolean("claim.enable", true);
        if (!enabled) {
            return;
        }
        Cooldown.createCooldown("claim-msg");
        Listener[] listeners = {
                new ChunkRendererListener(),
                new ClaimListener()
        };
        for (Listener listener : listeners) {
            TeamsPlus.getInstance().getServer().getPluginManager().registerEvents(listener, TeamsPlus.getInstance());
        }
        new ChunkBorderRenderer().runTaskTimerAsynchronously(TeamsPlus.getInstance(), 0, 10);
        loadClaims();
    }

    public void loadClaims() {
        Map<Long, ClaimInfo> cm = new HashMap<>();
        for (Team team : TeamsManager.getInstance().getTeams().values()) {
            cm.putAll(team.getClaims());
        }
        claimMap = cm;
    }

    public long hashChunk(int cx, int cz) {
        // 32 bits for x, 32 bits for z
        // long = 64 bits so we can use the upper 32 bits for x and the lower 32 bits for z
        return ((long) cz << 32) // shift chunk z 32 bits to left
                | (cx & 0xFFFFFFFFL); // write chunk x to right (lower) 32 bits
    }

    public long hashChunk(Chunk chunk) {
        return hashChunk(chunk.getX(), chunk.getZ());
    }

    public int getChunkX(long hash) {
        return (int) (hash >> 32);
    }

    public int getChunkZ(long hash) {
        return (int) (hash & 0xFFFFFFFFL);
    }

    public long hashChunk(Location location) {
        return hashChunk(location.getChunk());
    }

    public ClaimInfo getClaim(long hash) {
        return claimMap.get(hash);
    }

    public ClaimInfo getClaim(Chunk chunk) {
        return getClaim(hashChunk(chunk));
    }

    public ClaimInfo getClaim(Location location) {
        return getClaim(hashChunk(location));
    }

    public boolean isClaimed(long chunkHash) {
        return claimMap.containsKey(chunkHash);
    }

    public boolean isClaimed(Chunk chunk) {
        return isClaimed(hashChunk(chunk));
    }

    public void addClaim(ClaimInfo claim) {
        claimMap.put(claim.getChunkHash(), claim);
        if (!claim.isAdminClaim()) {
            Team team = TeamsManager.getInstance().getTeamById(claim.getOwningTeam());
            if (team != null) {
                team.getClaims().put(claim.getChunkHash(), claim);
            }
        }
    }

    public int getClaimCount() {
        return claimMap.size();
    }
}
