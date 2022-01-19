package net.badbird5907.teams.object;

import lombok.Getter;
import lombok.Setter;
import net.badbird5907.blib.command.Sender;
import net.badbird5907.blib.objects.maps.pair.HashPairMap;
import net.badbird5907.blib.objects.tuple.Pair;
import net.badbird5907.blib.util.PlayerUtil;
import net.badbird5907.blib.util.StoredLocation;
import net.badbird5907.teams.TeamsPlus;
import net.badbird5907.teams.manager.PlayerManager;
import net.badbird5907.teams.manager.StorageManager;
import net.badbird5907.teams.manager.TeamsManager;
import net.badbird5907.teams.object.player.PlayerData;
import org.bukkit.Bukkit;

import java.util.*;

@Getter
@Setter
public class Team {
    private final UUID teamId = UUID.randomUUID();
    private String name;
    private Map<UUID, TeamRank> members = new HashMap<>();
    private UUID owner;
    private TeamSettings settings = new TeamSettings();
    private Map<UUID, EnemyLevel> enemiedPlayers = new HashMap<>();
    private HashPairMap<UUID, EnemyLevel, String> enemiedTeams = new HashPairMap<>();
    private Set<UUID> alliedPlayers = new HashSet<>(); //save team/player names so we don't need to make extra db queries.
    private Map<UUID, String> alliedTeams = new HashMap<>();
    private Map<String, StoredLocation> waypoints = new HashMap<>();
    private transient int tempPvPSeconds = -1;
    /**
     * bool - player
     */
    private Pair<UUID, Boolean> currentAllyChat = new Pair<>(null, false);

    public Team(String name, UUID owner) {
        this.name = name;
        this.owner = owner;
        members.put(owner, TeamRank.OWNER);
    }

    public void update() {
        if (tempPvPSeconds != -1)
            tempPvPSeconds--;
    }

    public void save() {
        StorageManager.getStorageHandler().saveTeam(this);
    }

    public void broadcast(String message) {
        members.forEach((uuid, rank) -> {
            if (Bukkit.getPlayer(uuid) != null)
                Bukkit.getPlayer(uuid).sendMessage(message);
        });
    }

    public boolean isEnemy(PlayerData data) {
        if (data.getEnemiedTeams().containsKey(data.getTeamId()))
            return true;
        return enemiedPlayers.containsKey(data.getUuid()) || (data.isInTeam() && enemiedTeams.containsKey(data.getPlayerTeam().getTeamId()));
    }

    public boolean isAlly(PlayerData data) {
        if (data.getAlliedTeams().contains(data.getTeamId()))
            return true;
        return alliedPlayers.contains(data.getUuid()) || (data.isInTeam() && alliedPlayers.contains(data.getPlayerTeam().getTeamId()));
    }

    public void join(PlayerData data) {
        members.put(data.getUuid(), TeamRank.MEMBER);
        if (isEnemy(data) || isAlly(data))
            neutralPlayer(data.getUuid());
        broadcast(Lang.TEAM_JOINED.toString());
    }

    public void neutralPlayer(UUID uuid) {
        if (!enemiedPlayers.containsKey(uuid) && !alliedPlayers.contains(uuid))
            return;
        String name = PlayerUtil.getPlayerName(uuid);
        this.enemiedPlayers.remove(uuid);
        this.alliedPlayers.remove(uuid);
        PlayerData data = PlayerManager.getDataLoadIfNeedTo(uuid);
        data.getEnemiedTeams().remove(this.teamId);
        data.getAlliedTeams().remove(this.teamId);
        data.sendMessage(Lang.PLAYER_NEUTRAL_TEAM.toString(this.name), true);
        data.save();
        broadcast(Lang.TEAM_NEUTRAL_PLAYER.toString(name));
    }

    public void neutralTeam(UUID uuid) {
        if (!enemiedTeams.containsKey(uuid) && !alliedTeams.containsKey(uuid))
            return;
        String name = PlayerUtil.getPlayerName(uuid);
        this.enemiedTeams.remove(uuid);
        this.alliedTeams.remove(uuid);
        Team team = TeamsManager.getInstance().getTeamById(uuid);
        team.getEnemiedTeams().remove(teamId);
        team.getAlliedTeams().remove(teamId);
        team.broadcast(Lang.TEAM_NEUTRAL_TEAM.toString(this.name));
        broadcast(Lang.TEAM_NEUTRAL_TEAM.toString(name));
    }

    public void enableTempPvP(Sender sender) {
        if (TeamsPlus.getInstance().getConfig().getBoolean("team.temp-pvp.enable")) {
            tempPvPSeconds = TeamsPlus.getInstance().getConfig().getInt("team.temp-pvp.seconds");
            broadcast(Lang.TEMP_PVP_ENABLED.toString(sender, tempPvPSeconds));
        }
    }

    public void playerLeave(PlayerData data) {
        members.remove(data.getUuid());
        data.sendMessage(Lang.LEFT_TEAM.toString());
        broadcast(Lang.PLAYER_LEAVE_TEAM.toString(data.getName()));
    }
}
