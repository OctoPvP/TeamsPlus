package dev.badbird.teams.storage;

import dev.badbird.teams.object.PlayerData;
import dev.badbird.teams.object.Team;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;

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
}
