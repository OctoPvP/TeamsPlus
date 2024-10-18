package dev.badbird.teams.storage.impl;

import com.google.gson.JsonObject;
import dev.badbird.teams.TeamsPlus;
import dev.badbird.teams.object.PlayerData;
import dev.badbird.teams.object.Team;
import dev.badbird.teams.storage.StorageHandler;
import it.unimi.dsi.fastutil.longs.LongSet;
import lombok.SneakyThrows;
import net.badbird5907.blib.util.Logger;
import net.badbird5907.blib.util.Tasks;
import net.badbird5907.blib.utils.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.util.*;

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
    public void saveTeams(Collection<Team> teams) {
        for (Team team : teams) {
            saveTeam(team);
        }
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

    private File getClaimSaveFile(UUID teamId, UUID worldId) {
        return new File(TeamsPlus.getInstance().getDataFolder() + "/claims/" + worldId + "/" + teamId + ".json");
    }

    @Override
    public LongSet getClaimedChunks(UUID teamId, UUID worldId) {
        File file = getClaimSaveFile(teamId, worldId);
        if (!file.exists()) return null;
        JsonObject object = TeamsPlus.getGson().fromJson(FileUtils.readFileToString(file), JsonObject.class);
        return TeamsPlus.getGson().fromJson(object.get("chunks"), LongSet.class);
    }

    @Override
    public void saveClaimedChunks(UUID teamId, UUID worldId, LongSet claimedChunks) {
        File file = getClaimSaveFile(teamId, worldId);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        try {
            if (!file.exists())
                file.createNewFile();
            JsonObject object = new JsonObject();
            object.add("chunks", TeamsPlus.getGson().toJsonTree(claimedChunks));
            String json = TeamsPlus.getGson().toJson(object);
            Files.write(file.toPath(), json.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Map<UUID, LongSet> getClaimedChunksInWorld(UUID worldId) {
        Map<UUID, LongSet> map = new HashMap<>();
        File dir = new File(TeamsPlus.getInstance().getDataFolder() + "/claims/" + worldId);
        if (!dir.exists()) return map;
        for (File file : Objects.requireNonNull(dir.listFiles())) {
            UUID teamId = UUID.fromString(file.getName().substring(0, file.getName().length() - 5)); // .json
            LongSet set = getClaimedChunks(teamId, worldId);
            if (set != null)
                map.put(teamId, set);
        }
        return map;
    }

    @Override
    public void saveClaimedChunksInWorld(UUID worldId, Map<UUID, LongSet> claimedChunks) {
        for (Map.Entry<UUID, LongSet> entry : claimedChunks.entrySet()) {
            saveClaimedChunks(entry.getKey(), worldId, entry.getValue());
        }
    }
}
