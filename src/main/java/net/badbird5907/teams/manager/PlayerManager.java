package net.badbird5907.teams.manager;

import lombok.Getter;
import net.badbird5907.blib.command.Sender;
import net.badbird5907.blib.util.Logger;
import net.badbird5907.blib.util.Tasks;
import net.badbird5907.teams.TeamsPlus;
import net.badbird5907.teams.object.Team;
import net.badbird5907.teams.object.TeamRank;
import net.badbird5907.teams.object.player.PlayerData;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class PlayerManager {
    @Getter
    private static Map<UUID, PlayerData> players = new HashMap<>();
    public static void join(Player player){
        if (players.get(player.getUniqueId()) != null)
            return;
        Tasks.runAsync(()-> {
            PlayerData data = StorageManager.getStorageHandler().getData(player.getUniqueId());
            players.put(player.getUniqueId(), data);
            if (data.getName().equals(player.getName()))
                return;
            data.join(player);
            Logger.info("%1 has changed their name from %2 to %1. updating data...",player.getName(),data.getName());
            data.setName(player.getName());
            if (data.isInTeam()){
                if (!data.getPlayerTeam().getMembers().get(player.getUniqueId()).getValue0().equalsIgnoreCase(player.getName())){
                    TeamRank rank = data.getPlayerTeam().getMembers().get(player.getUniqueId()).getValue1();
                    data.getPlayerTeam().getMembers().remove(player.getUniqueId());
                    data.getPlayerTeam().getMembers().put(player.getUniqueId(),player.getName(),rank);
                }
            }
            for (Team team : TeamsPlus.getInstance().getTeamsManager().getTeams()) { //not made for like 10k teams lol
                team.getAlliedPlayers().forEach(((uuid, s) -> {
                    if (uuid.toString().equalsIgnoreCase(player.getUniqueId().toString()) && !s.equalsIgnoreCase(player.getName())){
                        team.getAlliedPlayers().remove(uuid);
                        team.getAlliedPlayers().put(uuid,player.getName());
                    }
                }));
                team.getEnemiedPlayers().forEach((uuid,level,name)->{
                    if (uuid.toString().equalsIgnoreCase(player.getUniqueId().toString()) && name != player.getName()){
                        team.getEnemiedPlayers().remove(uuid);
                        team.getEnemiedPlayers().put(uuid,level,player.getName());
                    }
                });
            }
        });
    }
    public static void leave(Player player){
        if (players.get(player.getUniqueId()) == null)
            return;
        Tasks.runAsync(()-> {
            StorageManager.getStorageHandler().saveData(players.get(player.getUniqueId()));
            players.remove(player.getUniqueId());
        });
    }
    public static PlayerData getData(UUID uuid){
        return players.get(uuid);
    }
    public static PlayerData getData(Player player){
        return getData(player.getUniqueId());
    }
    public static PlayerData getData(Sender sender){
        return getData(sender.getPlayer().getUniqueId());
    }
    public static PlayerData getData(String name){
        Optional<Map.Entry<UUID, PlayerData>> entry = players.entrySet().stream().filter(e1 -> e1.getValue().getName().equalsIgnoreCase(name)).findFirst();
        return entry.map(Map.Entry::getValue).orElse(null);
    }
    public static PlayerData getDataLoadIfNeedTo(String player){
        PlayerData playerData = getData(player);
        if (playerData != null)
            return playerData;
        return StorageManager.getStorageHandler().getData(player);
    }
    public static PlayerData getDataLoadIfNeedTo(UUID uuid){
        PlayerData playerData = getData(uuid);
        if (playerData != null)
            return playerData;
        return StorageManager.getStorageHandler().getData(uuid);
    }
}
