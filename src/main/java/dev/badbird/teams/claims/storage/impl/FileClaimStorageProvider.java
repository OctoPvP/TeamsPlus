package dev.badbird.teams.claims.storage.impl;

import dev.badbird.teams.claims.ClaimInfo;
import dev.badbird.teams.claims.storage.ClaimStorageProvider;
import org.bukkit.Location;

public class FileClaimStorageProvider implements ClaimStorageProvider {
    @Override
    public ClaimInfo getClaim(long hash) {
        return null;
    }

    @Override
    public void saveClaim(ClaimInfo info) {

    }

    @Override
    public boolean isClaimed(Location location) {
        return false;
    }
}
