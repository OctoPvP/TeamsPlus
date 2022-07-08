package net.badbird5907.teams.storage.impl;

import net.badbird5907.teams.object.PlayerData;
import net.badbird5907.teams.object.Team;
import net.badbird5907.teams.storage.StorageHandler;
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
