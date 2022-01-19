package net.badbird5907.teams.storage.impl;

import lombok.SneakyThrows;
import net.badbird5907.blib.util.Tasks;
import net.badbird5907.blib.utils.FileUtils;
import net.badbird5907.teams.object.Team;
import net.badbird5907.teams.TeamsPlus;
import net.badbird5907.teams.object.player.PlayerData;
import net.badbird5907.teams.storage.StorageHandler;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileNotFoundException;
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
        Bukkit.getLogger().warning("[Teams+] You are using flat file storage! It is highly advised to use a mongodb or SQL database.");
    }
    @Override
    public void disable() {}
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
            set.add(TeamsPlus.getGson().fromJson(json,Team.class));
        }
        return set;
    }
    @Override
    public PlayerData getData(UUID player) {
        File datafile = new File(TeamsPlus.getInstance().getDataFolder() + "/players/" + player + ".json");
        return TeamsPlus.getGson().fromJson(FileUtils.readFileToString(datafile),PlayerData.class);
    }
    @Override
    public PlayerData getData(String name) {
        OfflinePlayer op = Bukkit.getOfflinePlayer(name);
        if (op == null || !op.hasPlayedBefore())
            return null;
        UUID uuid = op.getUniqueId();
        return getData(uuid);
    }
    @Override
    public void saveData(PlayerData playerData) {
        File dataFile = new File(TeamsPlus.getInstance().getDataFolder() + "/players/" + playerData + ".json");
        if (TeamsPlus.isDisabling()) //you can't run a scheduler when a plugin is disabling
            saveData(playerData,dataFile);
        else Tasks.runAsync(()-> saveData(playerData,dataFile));
    }

    private void saveData(PlayerData data,File file){
        try {
            PrintStream ps = new PrintStream(file);
            ps.print(TeamsPlus.getGson().toJson(data));
            ps.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @SneakyThrows
    @Override
    public void saveTeam(Team team) {
        File teamFile = new File(TeamsPlus.getInstance().getDataFolder() + "/teams/" + team.getTeamId() + ".json");
        if (!teamFile.exists()){
            teamFile.createNewFile();
        }
        PrintStream ps = new PrintStream(teamFile);
        ps.print(TeamsPlus.getGson().toJson(team));
        ps.close();
    }
}
