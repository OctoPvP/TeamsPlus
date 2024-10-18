package dev.badbird.teams.storage;

import dev.badbird.teams.object.PlayerData;
import dev.badbird.teams.object.Team;
import it.unimi.dsi.fastutil.longs.LongSet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public interface StorageHandler {
    void init();

    void disable();

    @NotNull
    Set<Team> getTeams();

    @Nullable
    PlayerData getData(UUID player);

    @Nullable
    PlayerData getData(String name);

    void saveData(PlayerData playerData);

    void saveTeam(Team team);

    void saveTeams(Collection<Team> teams);

    void removeTeam(Team team);

    LongSet getClaimedChunks(UUID teamId, UUID worldId);

    void saveClaimedChunks(UUID teamId, UUID worldId, LongSet claimedChunks);

    Map<UUID, LongSet> getClaimedChunksInWorld(UUID worldId);

    void saveClaimedChunksInWorld(UUID worldId, Map<UUID, LongSet> claimedChunks);
}
