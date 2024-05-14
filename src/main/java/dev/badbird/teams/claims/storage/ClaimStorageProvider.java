package dev.badbird.teams.claims.storage;

import dev.badbird.teams.claims.ClaimHandler;
import dev.badbird.teams.claims.ClaimInfo;
import org.bukkit.Location;

public interface ClaimStorageProvider {
    default ClaimInfo getClaim(Location location) {
        return getClaim(ClaimHandler.getInstance().hashChunk(location));
    }
    ClaimInfo getClaim(long hash);
    void saveClaim(ClaimInfo info);
    boolean isClaimed(Location location);
}
