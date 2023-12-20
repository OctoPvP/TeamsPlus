package dev.badbird.teams.manager;

import dev.badbird.teams.object.Team;
import dev.badbird.teams.storage.StorageHandler;
import lombok.Getter;
import net.badbird5907.blib.util.Logger;
import dev.badbird.teams.TeamsPlus;
import dev.badbird.teams.util.UUIDUtil;
import org.apache.commons.lang3.StringEscapeUtils;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class TeamsManager {
    @Getter
    private Set<Team> teams = new HashSet<>(); //keep teams loaded in memory so we don't need to keep loading from data source

    public TeamsManager() {
        loadTeams(StorageManager.getStorageHandler());
    }

    public static TeamsManager getInstance() {
        return TeamsPlus.getInstance().getTeamsManager();
    }

    public void loadTeams(StorageHandler storageHandler) {
        this.teams = storageHandler.getTeams();
    }

    public void saveTeams(StorageHandler storageHandler) {
        /*
        for (Team team : teams) {
            storageHandler.saveTeam(team);
        }
         */
        storageHandler.saveTeams(teams);
    }

    @Nullable
    public Team getTeamByName(String name) {
        String a = StringEscapeUtils.escapeJava(name);
        if (StorageManager.getStorageHandler().getClass().getName().toLowerCase().contains("sql")) //prevent sql injection attacks
            a = a.replace("'", "''"); //This is what StringEscapeUtils.escapeSql does in apache commons lang, was removed in lang3
        String finalA = a;
        return teams.stream().filter(team -> team != null && team.getName().equalsIgnoreCase(finalA)).findFirst().orElse(null);
    }

    @Nullable
    public Team getTeamById(UUID id) {
        return teams.stream().filter(team -> UUIDUtil.equals(team.getTeamId(), id)).findFirst().orElse(null); //i like to compare the uuid using tostring because sometimes UUID == UUID returns false :shrug:
    }

    @Nullable
    public Team getPlayerTeam(UUID player) {
        return teams.stream().filter(team -> team.getMembers().keySet().stream().filter(id -> id.toString().equals(player.toString())).findFirst().orElse(null) != null).findFirst().orElse(null);
    }

    public void removeTeam(Team team) {
        for (Team team1 : teams) {
            Logger.debug("1 - " + team1.getName());
        }
        teams.remove(team);
        StorageManager.getStorageHandler().removeTeam(team);
        for (Team team1 : teams) {
            Logger.debug("2 - " + team1.getName());
        }
        //saveTeams(StorageManager.getStorageHandler());
    }
}
