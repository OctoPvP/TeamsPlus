package dev.badbird.teams.storage.impl;

import dev.badbird.teams.storage.StorageHandler;
import dev.badbird.teams.object.PlayerData;
import dev.badbird.teams.object.Team;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.UUID;

public class SQLStorageHandler implements StorageHandler { //TODO
    @Override
    public void init() {

    }

    @Override
    public void disable() {

    }

    @Override
    public @NotNull Set<Team> getTeams() {
        return null;
    }

    @Override
    public PlayerData getData(UUID player) {
        return null;
    }

    @Override
    public PlayerData getData(String name) {
        return null;
    }

    @Override
    public void saveData(PlayerData playerData) {

    }

    @Override
    public void saveTeam(Team team) {

    }

    @Override
    public void removeTeam(Team team) {

    }
}
