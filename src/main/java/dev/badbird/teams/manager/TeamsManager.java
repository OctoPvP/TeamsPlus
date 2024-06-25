package dev.badbird.teams.manager;

import dev.badbird.teams.object.Team;
import dev.badbird.teams.storage.StorageHandler;
import lombok.Getter;
import net.badbird5907.blib.util.Logger;
import dev.badbird.teams.TeamsPlus;
import org.apache.commons.lang3.StringEscapeUtils;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class TeamsManager {
    @Getter
    private Map<UUID, Team> teams = new ConcurrentHashMap<>(); //keep teams loaded in memory so we don't need to keep loading from data source
    // hashmap for O(1) lookup

    public TeamsManager() {
        loadTeams(StorageManager.getStorageHandler());
    }

    public static TeamsManager getInstance() {
        return TeamsPlus.getInstance().getTeamsManager();
    }

    public void loadTeams(StorageHandler storageHandler) {
        Set<Team> teamsSet = storageHandler.getTeams();
        this.teams.clear();
        teamsSet.forEach(team -> this.teams.put(team.getTeamId(), team));
    }

    public void saveTeams(StorageHandler storageHandler) {
        /*
        for (Team team : teams) {
            storageHandler.saveTeam(team);
        }
         */
        storageHandler.saveTeams(teams.values());
    }

    @Nullable
    public Team getTeamByName(String name) {
        String a = StringEscapeUtils.escapeJava(name);
        if (StorageManager.getStorageHandler().getClass().getName().toLowerCase().contains("sql")) //prevent sql injection attacks
            a = a.replace("'", "''"); //This is what StringEscapeUtils.escapeSql does in apache commons lang, was removed in lang3
        String finalA = a;
        return teams.values().stream().filter(team -> team != null && team.getName().equalsIgnoreCase(finalA)).findFirst().orElse(null);
    }

    @Nullable
    public Team getTeamById(UUID id) {
        return teams.get(id);
    }

    @Nullable
    public Team getPlayerTeam(UUID player) {
        return teams.values().stream().filter(team -> team.getMembers().keySet().stream().filter(id -> id.toString().equals(player.toString())).findFirst().orElse(null) != null).findFirst().orElse(null);
    }

    public void removeTeam(Team team) {
        teams.remove(team);
        StorageManager.getStorageHandler().removeTeam(team);
        //saveTeams(StorageManager.getStorageHandler());
    }

    public String getTeamName(UUID owningTeam) {
        return teams.get(owningTeam).getName();
    }
}
