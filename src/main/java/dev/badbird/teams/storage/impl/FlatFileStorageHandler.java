package dev.badbird.teams.storage.impl;

import dev.badbird.teams.storage.StorageHandler;
import lombok.SneakyThrows;
import net.badbird5907.blib.util.Logger;
import net.badbird5907.blib.util.Tasks;
import net.badbird5907.blib.utils.FileUtils;
import dev.badbird.teams.TeamsPlus;
import dev.badbird.teams.object.PlayerData;
import dev.badbird.teams.object.Team;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * flat file bad
 */
@Deprecated
public class FlatFileStorageHandler implements StorageHandler {
    @Override
    public void init() {
        Logger.error("You are using flat file storage! It is highly advised to use a mongodb or SQL database.");
    }

    @Override
    public void disable() {
    }

    //TODO do this all async, not doing it because data race
    @SneakyThrows
    @Override
    public @NotNull Set<Team> getTeams() {
        Set<Team> set = new HashSet<>();
        File dir = new File(TeamsPlus.getInstance().getDataFolder() + "/teams/");
        if (!dir.exists())
            dir.mkdirs();
        for (File file : dir.listFiles()) {
            String json = FileUtils.readFileToString(file);
            set.add(TeamsPlus.getGson().fromJson(json, Team.class));
        }
        return set;
    }

    @Override
    public PlayerData getData(UUID player) {
        File datafile = new File(TeamsPlus.getInstance().getDataFolder() + "/players/" + player.toString() + ".json");
        if (!datafile.getParentFile().exists()) {
            datafile.getParentFile().mkdirs();
        }
        if (!datafile.exists()) {
            PlayerData data = new PlayerData(player);
            data.onLoad();
            data.save();
            return data;
        }
        return
                TeamsPlus.getGson().fromJson(
                        FileUtils.readFileToString(
                                datafile),
                        PlayerData.class)
                        .onLoad();
    }

    @Override
    public PlayerData getData(String name) {
        OfflinePlayer op = Bukkit.getOfflinePlayer(name);
        UUID uuid = op.getUniqueId();
        File datafile = new File(TeamsPlus.getInstance().getDataFolder() + "/players/" + uuid + ".json");
        if (!datafile.exists()) return null;
        return getData(uuid);
    }

    @Override
    public void saveData(PlayerData playerData) {
        File dataFile = new File(TeamsPlus.getInstance().getDataFolder() + "/players/" + playerData.getUuid() + ".json");
        if (TeamsPlus.isDisabling()) //you can't run a scheduler when a plugin is disabling
            saveData(playerData, dataFile);
        else Tasks.runAsync(() -> saveData(playerData, dataFile));
    }

    private void saveData(PlayerData data, File file) {
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        try {
            if (!file.exists())
                file.createNewFile();
            PrintStream ps = new PrintStream(file);
            ps.print(TeamsPlus.getGson().toJson(data));
            ps.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SneakyThrows
    @Override
    public void saveTeam(Team team) {
        File teamFile = new File(TeamsPlus.getInstance().getDataFolder() + "/teams/" + team.getTeamId() + ".json");
        if (!teamFile.exists()) {
            teamFile.createNewFile();
        }
        PrintStream ps = new PrintStream(teamFile);
        ps.print(TeamsPlus.getGson().toJson(team));
        ps.close();
    }

    @Override
    public void removeTeam(Team team) {
        try {
            File teamFile = new File(TeamsPlus.getInstance().getDataFolder() + "/teams/" + team.getTeamId() + ".json");
            Logger.debug("[1] Removing team " + team.getTeamId());
            if (teamFile.exists()) {
                Logger.debug("Removing team " + team.getTeamId());
                Logger.debug("Success: %1", teamFile.delete());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
