package net.badbird5907.teams.object;

import lombok.Getter;
import lombok.Setter;
import net.badbird5907.blib.command.Sender;
import net.badbird5907.blib.objects.maps.pair.HashPairMap;
import net.badbird5907.blib.objects.tuple.Pair;
import net.badbird5907.blib.util.StoredLocation;
import net.badbird5907.teams.TeamsPlus;
import net.badbird5907.teams.manager.PlayerManager;
import net.badbird5907.teams.manager.StorageManager;
import net.badbird5907.teams.manager.TeamsManager;
import net.badbird5907.teams.util.UUIDUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

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
    private Map<UUID, String> alliedPlayers = new HashMap<>(); //save team/player names so we don't need to make extra db queries.
    private Map<UUID, String> alliedTeams = new HashMap<>();
    private Map<String, StoredLocation> waypoints = new HashMap<>();
    private transient Map<UUID, Long> allyRequests = new ConcurrentHashMap<>();
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
        if (allyRequests == null)
            allyRequests = new ConcurrentHashMap<>();
        {
            Iterator<Map.Entry<UUID, Long>> it = allyRequests.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<UUID, Long> pair = it.next();
                long time = pair.getValue();
                UUID id = pair.getKey();
                if (time <= System.currentTimeMillis()) {
                    Team team = TeamsManager.getInstance().getTeamById(id);
                    PlayerData data = PlayerManager.getDataLoadIfNeedTo(id);
                    if (team == null && data == null) {
                        it.remove();
                    } else {
                        if (team != null) {
                            team.broadcastToRanks(Lang.ALLY_REQUEST_DENY_TIMEOUT.toString(getName()), TeamRank.ADMIN, TeamRank.OWNER);
                        } else {
                            data.sendMessage(Lang.ALLY_REQUEST_DENY_TIMEOUT.toString(getName()), true);
                            data.save();
                        }
                        it.remove();
                    }
                }
            }
        }
    }

    public void save() {
        StorageManager.getStorageHandler().saveTeam(this);
    }

    public void rename(Player sender, String name) {
        broadcast(Lang.TEAM_RENAME.toString(sender.getName(), name));
        this.name = name;
        save();
    }

    public void broadcast(String message) {
        members.forEach((uuid, rank) -> {
            if (Bukkit.getPlayer(uuid) != null)
                Bukkit.getPlayer(uuid).sendMessage(message);
        });
    }

    public void broadcastToPermissionLevelAndAbove(int level, String message) {
        members.forEach((uuid, rank) -> {
            if (rank.getPermissionLevel() >= level) {
                if (Bukkit.getPlayer(uuid) != null)
                    Bukkit.getPlayer(uuid).sendMessage(message);
            }
        });
    }

    public boolean isEnemy(PlayerData data) {
        if (data.getEnemiedTeams().containsKey(data.getTeamId()))
            return true;
        return UUIDUtil.contains(enemiedPlayers, data.getUuid()) || (data.isInTeam() && UUIDUtil.contains(enemiedTeams, data.getPlayerTeam().getTeamId()));
    }

    public boolean isAlly(PlayerData data) {
        return data.getAlliedTeams().contains(data.getTeamId()) || (data.getPlayerTeam() != null && UUIDUtil.contains(data.getPlayerTeam().getAlliedTeams(), this.teamId));
        //return alliedPlayers.contains(data.getUuid()) || (data.isInTeam() && alliedPlayers.contains(data.getPlayerTeam().getTeamId()));
    }

    public void join(PlayerData data) {
        members.put(data.getUuid(), TeamRank.MEMBER);
        if (isEnemy(data) || isAlly(data))
            neutralPlayer(data.getUuid());
        broadcast(Lang.TEAM_JOINED.toString(data.getName()));
    }

    public void neutralPlayer(UUID uuid, boolean... broadcast) {
        if (!UUIDUtil.contains(enemiedPlayers, uuid))
            return;
        PlayerData data = PlayerManager.getDataLoadIfNeedTo(uuid);
        neutralPlayer(data, broadcast);
    }

    public void neutralPlayer(PlayerData data, boolean... broadcast) {
        if (!UUIDUtil.contains(enemiedPlayers, data.getUuid()))
            return;
        this.enemiedPlayers.remove(data.getUuid());
        data.getEnemiedTeams().remove(this.teamId);
        data.getAlliedTeams().remove(this.teamId);
        if (broadcast.length > 0 && broadcast[0] || broadcast.length == 0) {
            data.sendMessage(Lang.PLAYER_NEUTRAL_TEAM.toString(this.name), true);
            broadcast(Lang.TEAM_NEUTRAL_PLAYER.toString(name));
        }
        data.save();
    }

    public void neutralTeam(UUID uuid) {
        if (!UUIDUtil.contains(enemiedTeams, uuid) && !UUIDUtil.contains(alliedTeams, uuid))
            return;
        //String name = PlayerUtil.getPlayerName(uuid);
        Team team = TeamsManager.getInstance().getTeamById(uuid);
        neutralTeam(team);
    }

    public void neutralTeam(Team team, boolean... broadcast) {
        UUID uuid = team.getTeamId();
        UUIDUtil.remove(enemiedPlayers, uuid);
        UUIDUtil.remove(alliedTeams, uuid);
        UUIDUtil.remove(team.getEnemiedPlayers(), teamId);
        UUIDUtil.remove(team.getEnemiedTeams(), teamId);
        UUIDUtil.remove(team.getAlliedTeams(), teamId);
        if (broadcast.length > 0 && broadcast[0] || broadcast.length == 0) {
            team.broadcast(Lang.TEAM_NEUTRAL_TEAM.toString(this.name));
            broadcast(Lang.TEAM_NEUTRAL_TEAM.toString(name));
        }
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

    public void broadcastToRanks(String message, TeamRank rank, TeamRank... ranks) {
        List<TeamRank> list = new ArrayList<>();
        list.add(rank);
        list.addAll(Arrays.asList(ranks));
        members.forEach((uuid, teamRank) -> {
            if (list.contains(teamRank) && Bukkit.getPlayer(uuid) != null) {
                Bukkit.getPlayer(uuid).sendMessage(message);
            }
        });
    }

    public void requestToAlly(Team otherTeam) {
        broadcastToRanks(Lang.TEAM_ALLY_TEAM_ASK.toString(otherTeam.getName()), TeamRank.OWNER, TeamRank.ADMIN);
        long timestamp = System.currentTimeMillis() + (TeamsPlus.getInstance().getConfig().getInt("ally.request-timeout") * 1000L);
        allyRequests.put(otherTeam.getTeamId(), timestamp);
    }

    public void requestToAllyAnotherTeam(Team otherTeam) {
        if (UUIDUtil.contains(allyRequests, otherTeam.getTeamId())) { //other team already sent request to this team
            neutralTeam(otherTeam, false);
            allyTeam(otherTeam, true); //ally team
            UUIDUtil.remove(allyRequests, otherTeam.getTeamId());
            UUIDUtil.remove(otherTeam.getAllyRequests(), teamId);
            return;
        }
        otherTeam.requestToAlly(this);
        broadcast(Lang.ALLY_SENT_REQUEST.toString(otherTeam.getName()));
    }

    public void allyTeam(Team otherTeam, boolean... broadcast) {
        alliedTeams.put(otherTeam.getTeamId(), otherTeam.getName());
        otherTeam.alliedTeams.put(this.teamId, this.name);
        if (broadcast.length > 0 && broadcast[0] || broadcast.length == 0) {
            broadcast(Lang.ALLY_SUCCESS.toString(otherTeam.getName()));
            otherTeam.broadcast(Lang.ALLY_SUCCESS.toString(this.name));
        }
    }

    public void requestToAlly(PlayerData player) {
        if (player.isOnline()) {
            broadcastToRanks(Lang.PLAYER_ALLY_TEAM_ASK.toString(player.getName()), TeamRank.OWNER, TeamRank.ADMIN);
            long timestamp = System.currentTimeMillis() + (TeamsPlus.getInstance().getConfig().getInt("ally.request-timeout") * 1000L);
            if (allyRequests == null)
                allyRequests = new ConcurrentHashMap<>();
            allyRequests.put(player.getUuid(), timestamp);
        } else
            return;
    }

    public void requestToAllyPlayer(PlayerData player) {
        if (UUIDUtil.contains(allyRequests, player.getUuid())) { //other team already sent request to this team
            neutralPlayer(player);
            allyPlayer(player, true); //ally team
            UUIDUtil.remove(allyRequests, player.getUuid());
            return;
        }
        player.requestToAlly(this);
        broadcast(Lang.ALLY_SENT_REQUEST.toString(player.getName()));
    }

    public void allyPlayer(PlayerData player, boolean... broadcast) {
        alliedPlayers.put(player.getUuid(), player.getName());
        player.getAlliedTeams().add(this.teamId);
        if (broadcast.length > 0 && broadcast[0] || broadcast.length == 0) {
            broadcast(Lang.ALLY_SUCCESS.toString(player.getName()));
            player.sendMessage(Lang.ALLY_SUCCESS.toString(this.name));
        }
    }
}
