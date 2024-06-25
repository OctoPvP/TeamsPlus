package dev.badbird.teams.claims;

import dev.badbird.teams.manager.TeamsManager;
import dev.badbird.teams.object.Team;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * Represents a single chunk claim
 */
@Data
@RequiredArgsConstructor
public class ClaimInfo {
    private final UUID owningTeam;
    private final int chunkX;
    private final int chunkZ;

    public boolean isAdminClaim() {
        return owningTeam.getLeastSignificantBits() == 0 && owningTeam.getMostSignificantBits() == 0;
    }

    public ClaimInfo(ChunkWrapper wrapper, UUID owner) {
        this(owner, wrapper.getChunkX(), wrapper.getChunkZ());
    }

    public long getChunkHash() {
        return ClaimHandler.getInstance().hashChunk(chunkX, chunkZ);
    }

    public boolean canPlayerModify(UUID player, Team cachedTeam) {
        Team team = cachedTeam != null ? cachedTeam : TeamsManager.getInstance().getTeamById(owningTeam);
        if (team == null) return false;
        return team.getMembers().containsKey(player); // TODO: allow teams to configure if their allies can also modify
    }
}
