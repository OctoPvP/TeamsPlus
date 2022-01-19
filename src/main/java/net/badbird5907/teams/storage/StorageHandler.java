package net.badbird5907.teams.storage;

import net.badbird5907.teams.object.Team;
import net.badbird5907.teams.object.player.PlayerData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
}
