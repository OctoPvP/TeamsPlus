package net.badbird5907.teams.manager;

import lombok.Getter;
import net.badbird5907.blib.command.Sender;
import net.badbird5907.blib.util.Tasks;
import net.badbird5907.teams.object.PlayerData;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class PlayerManager {
    @Getter
    private static final Map<UUID, PlayerData> players = new HashMap<>();

    public static void join(Player player) {
        if (players.get(player.getUniqueId()) != null)
            return;
        Tasks.runAsync(() -> {
            PlayerData data = StorageManager.getStorageHandler().getData(player.getUniqueId());
            players.put(player.getUniqueId(), data);
            data.join(player);
        });
    }

    public static void leave(Player player) {
        if (players.get(player.getUniqueId()) == null)
            return;
        Tasks.runAsync(() -> {
            StorageManager.getStorageHandler().saveData(players.get(player.getUniqueId()));
            players.remove(player.getUniqueId());
        });
    }

    public static PlayerData getData(UUID uuid) {
        return players.get(uuid);
    }

    public static PlayerData getData(Player player) {
        return getData(player.getUniqueId());
    }

    public static PlayerData getData(Sender sender) {
        return getData(sender.getPlayer().getUniqueId());
    }

    public static PlayerData getData(String name) {
        Optional<Map.Entry<UUID, PlayerData>> entry = players.entrySet().stream().filter(e1 -> e1 != null && e1.getValue() != null && e1.getValue().getName().equalsIgnoreCase(name)).findFirst();
        return entry.map(Map.Entry::getValue).orElse(null);
    }

    public static PlayerData getDataLoadIfNeedTo(String player) {
        PlayerData playerData = getData(player);
        if (playerData != null)
            return playerData;
        return StorageManager.getStorageHandler().getData(player);
    }

    public static PlayerData getDataLoadIfNeedTo(UUID uuid) {
        PlayerData playerData = getData(uuid);
        if (playerData != null)
            return playerData;
        return StorageManager.getStorageHandler().getData(uuid);
    }
}
