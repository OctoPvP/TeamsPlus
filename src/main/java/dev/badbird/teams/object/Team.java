package dev.badbird.teams.object;

import dev.badbird.teams.TeamsPlus;
import dev.badbird.teams.claims.ChunkWrapper;
import dev.badbird.teams.claims.ClaimHandler;
import dev.badbird.teams.claims.ClaimInfo;
import dev.badbird.teams.claims.ClaimResult;
import dev.badbird.teams.hooks.impl.LunarClientHook;
import dev.badbird.teams.hooks.impl.VanishHook;
import dev.badbird.teams.manager.HookManager;
import dev.badbird.teams.manager.PlayerManager;
import dev.badbird.teams.manager.StorageManager;
import dev.badbird.teams.manager.TeamsManager;
import dev.badbird.teams.util.Utils;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import static dev.badbird.teams.util.ChatUtil.tr;

@Getter
@Setter
public class Team {
    private final UUID teamId = UUID.randomUUID();
    private String name;
    private ConcurrentHashMap<UUID, TeamRank> members = new ConcurrentHashMap<>();
    private UUID owner;

    private TeamSettings settings = new TeamSettings();
    private ConcurrentHashMap<UUID, String> enemiedTeams = new ConcurrentHashMap<>();
    private ConcurrentHashMap<UUID, String> alliedTeams = new ConcurrentHashMap<>();

    private CopyOnWriteArrayList<TeamWaypoint> waypoints = new CopyOnWriteArrayList<>();
    private transient Map<Long, ClaimInfo> claims = new HashMap<>();
    private transient ConcurrentHashMap<UUID, Long> allyRequests = new ConcurrentHashMap<>();
    private transient int tempPvPSeconds = -1;

    public Team(String name, UUID owner) {
        this.name = name;
        this.owner = owner;
        members.put(owner, TeamRank.OWNER);
    }

    public void update() {
        if (tempPvPSeconds != -1) tempPvPSeconds--;
        if (allyRequests == null) allyRequests = new ConcurrentHashMap<>();
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
                        team.broadcastToRanks(Lang.ALLY_REQUEST_DENY_TIMEOUT.getComponent(tr("name", getName())), TeamRank.ADMIN, TeamRank.OWNER);
                    } else {
                        data.sendMessage(Lang.ALLY_REQUEST_DENY_TIMEOUT.getComponent(tr("name", getName())), true);
                        data.save();
                    }
                    it.remove();
                }
            }
        }
    }

    public void save() {
        StorageManager.getStorageHandler().saveTeam(this);
    }

    public void rename(Player sender, String name) {
        broadcast(Lang.TEAM_RENAME.getComponent(tr("name", sender.getName()), tr("new_name", name)));
        this.name = name;
        save();
    }

    public void broadcast(Component message) {
        broadcast(message, false);
    }

    public void broadcast(Component message, boolean offline) {
        members.forEach((uuid, rank) -> {
            Player player = Bukkit.getPlayer(uuid);
            if (player == null) {
                if (offline) {
                    PlayerData data = PlayerManager.getDataLoadIfNeedTo(uuid);
                    if (data != null) {
                        data.sendMessage(message, true);
                    }
                }
            } else {
                player.sendMessage(message);
            }
        });
    }

    public void broadcastToPermissionLevelAndAbove(int level, String message) {
        members.forEach((uuid, rank) -> {
            if (rank.getPermissionLevel() >= level) {
                if (Bukkit.getPlayer(uuid) != null) Bukkit.getPlayer(uuid).sendMessage(message);
            }
        });
    }

    public boolean isEnemy(PlayerData data) {
        return data.isInTeam() && enemiedTeams.containsKey(data.getPlayerTeam().getTeamId());
    }

    public boolean isEnemy(Team team) {
        return enemiedTeams.containsKey(team.getTeamId());
    }

    public boolean isAlly(PlayerData data) {
        return data.getPlayerTeam() != null && data.getPlayerTeam().getAlliedTeams().containsKey(this.teamId);
    }

    public boolean isAlly(Team otherTeam) {
        return alliedTeams.containsKey(otherTeam.getTeamId());
    }

    public void join(PlayerData data) {
        members.put(data.getUuid(), TeamRank.RECRUIT);
        data.setTeamId(getTeamId());
        data.setAllyChatTeamId(null);
        broadcast(Lang.TEAM_JOINED.getComponent(tr("name", data.getName())));
        save();
        data.save();
    }

    public void neutralTeam(UUID uuid) {
        if (!enemiedTeams.containsKey(uuid) && !alliedTeams.containsKey(uuid)) return;
        //String name = PlayerUtil.getPlayerName(uuid);
        Team team = TeamsManager.getInstance().getTeamById(uuid);
        neutralTeam(team);
    }

    public void neutralTeam(Team team, boolean... broadcast) {
        UUID uuid = team.getTeamId();
        alliedTeams.remove(uuid);
        enemiedTeams.remove(uuid);
        team.getEnemiedTeams().remove(teamId);
        team.getAlliedTeams().remove(teamId);
        if (broadcast.length > 0 && broadcast[0] || broadcast.length == 0) {
            team.broadcast(Lang.TEAM_NEUTRAL_TEAM.getComponent(tr("team", name)));
            broadcast(Lang.TEAM_NEUTRAL_TEAM.getComponent(tr("team", team.getName())));
        }
        save();
        team.save();
    }

    public void enableTempPvP(CommandSender sender) {
        if (TeamsPlus.getInstance().getConfig().getBoolean("team.temp-pvp.enable")) {
            tempPvPSeconds = TeamsPlus.getInstance().getConfig().getInt("team.temp-pvp.seconds");
            broadcast(Lang.TEMP_PVP_ENABLED.getComponent(tr("name", sender.name()), tr("time", tempPvPSeconds)));
        }
    }

    public void leave(PlayerData data) {
        if (owner.equals(data.getUuid())) {
            data.sendMessage(Lang.CANNOT_LEAVE_OWN_TEAM.getComponent());
            return;
        }
        members.remove(data.getUuid());
        data.setTeamId(null);
        data.sendMessage(Lang.LEFT_TEAM.getComponent());
        broadcast(Lang.PLAYER_LEAVE_TEAM.getComponent(tr("name", data.getName())));
        save();
    }

    public void broadcastToRanks(String message, TeamRank rank, TeamRank... ranks) {
        List<TeamRank> list = new ArrayList<>();
        list.add(rank);
        list.addAll(Arrays.asList(ranks));
        members.forEach((uuid, teamRank) -> {
            Player p = Bukkit.getPlayer(uuid);
            if (list.contains(teamRank) && p != null) {
                p.sendMessage(message);
            }
        });
    }

    public void broadcastToRanks(Component message, TeamRank rank, TeamRank... ranks) {
        List<TeamRank> list = new ArrayList<>();
        list.add(rank);
        list.addAll(Arrays.asList(ranks));
        members.forEach((uuid, teamRank) -> {
            Player p = Bukkit.getPlayer(uuid);
            if (list.contains(teamRank) && p != null) {
                p.sendMessage(message);
            }
        });
    }

    public void requestToAlly(Team otherTeam) {
        Component message = Lang.TEAM_ALLY_TEAM_ASK.getComponent(
                        tr("name", otherTeam.getName()
                        ))
                .clickEvent(ClickEvent.runCommand("/team ally " + otherTeam.getName()))
                .hoverEvent(HoverEvent.showText(
                        Lang.TEAM_ALLY_TEAM_ASK_HOVER.getComponent(
                                tr("name", otherTeam.getName())
                        )));
        broadcastToRanks(message, TeamRank.OWNER, TeamRank.ADMIN);
        long timestamp = System.currentTimeMillis() + (TeamsPlus.getInstance().getConfig().getInt("ally.request-timeout") * 1000L);
        allyRequests.put(otherTeam.getTeamId(), timestamp);
    }

    public void requestToAllyAnotherTeam(Team otherTeam) {
        if (allyRequests.containsKey(otherTeam.getTeamId())) { //other team already sent request to this team
            neutralTeam(otherTeam, false);
            allyTeam(otherTeam, true); //ally team
            allyRequests.remove(otherTeam.getTeamId());
            otherTeam.getAllyRequests().remove(teamId);
            return;
        }
        otherTeam.requestToAlly(this);
        broadcast(Lang.ALLY_SENT_REQUEST.getComponent(tr("target", otherTeam.getName())));
    }

    public void allyTeam(Team otherTeam, boolean... broadcast) {
        neutralTeam(otherTeam, false);
        alliedTeams.put(otherTeam.getTeamId(), otherTeam.getName());
        otherTeam.alliedTeams.put(this.teamId, this.name);
        if (broadcast.length > 0 && broadcast[0] || broadcast.length == 0) {
            broadcast(Lang.ALLY_SUCCESS.getComponent(tr("team", otherTeam.getName())));
            otherTeam.broadcast(Lang.ALLY_SUCCESS.getComponent(tr("team", this.name)));
        }
        save();
    }

    public void enemyTeam(Team otherTeam, boolean... broadcast) {
        neutralTeam(otherTeam, false);
        enemiedTeams.put(otherTeam.getTeamId(), otherTeam.getName());
        otherTeam.enemiedTeams.put(this.teamId, this.name);
        if (broadcast.length > 0 && broadcast[0] || broadcast.length == 0) {
            broadcast(Lang.TEAM_ENEMY_TEAM.getComponent(tr("team", otherTeam.getName())));
            otherTeam.broadcast(Lang.TEAM_ENEMY_TEAM.getComponent(tr("team", this.name)));
        }
        save();
    }

    public void disband() {
        Component message = Lang.TEAM_DISBANDED.getComponent(tr("team", name));
        members.entrySet().removeIf((entry) -> {
            PlayerData playerData = PlayerManager.getDataLoadIfNeedTo(entry.getKey());
            if (playerData != null) {
                playerData.sendMessage(message, true);
                playerData.setTeamId(null);
                playerData.save();
            }
            return true;
        });
        PlayerData data = PlayerManager.getDataLoadIfNeedTo(owner);
        data.setTeamId(null);
        data.save();
        owner = null;
        Iterator<Map.Entry<UUID, String>> iterator1 = enemiedTeams.entrySet().iterator();
        while (iterator1.hasNext()) {
            Team team = TeamsManager.getInstance().getTeamById(iterator1.next().getKey());
            if (team != null) team.getEnemiedTeams().remove(teamId);
            iterator1.remove();
        }
        Iterator<Map.Entry<UUID, String>> iterator2 = alliedTeams.entrySet().iterator();
        while (iterator2.hasNext()) {
            Team team = TeamsManager.getInstance().getTeamById(iterator2.next().getKey());
            if (team != null) team.getAlliedTeams().remove(teamId);
            iterator2.remove();
        }
        TeamsManager.getInstance().removeTeam(this); // We'll let java gc handle this
        //save();
    }

    public boolean isAtLeast(Player player, TeamRank rank) {
        return isAtLeast(player.getUniqueId(), rank);
    }

    public boolean isAtLeast(UUID uuid, TeamRank rank) {
        int requiredPermissionLevel = rank.getPermissionLevel();
        int playerPermissionLevel = members.get(uuid).getPermissionLevel();
        return playerPermissionLevel >= requiredPermissionLevel;
    }

    public TeamRank getRank(UUID uuid) {
        return members.get(uuid);
    }

    public void setRank(UUID uuid, TeamRank rank) {
        if (!members.containsKey(uuid)) return;
        members.put(uuid, rank);
        save();
    }

    public void kick(PlayerData target, PlayerData sender, String reason) {
        members.remove(target.getUuid());
        target.setTeamId(null);
        target.setCurrentChannel(ChatChannel.GLOBAL);
        target.setAllyChatTeamId(null);
        target.sendMessage(Lang.KICKED_FROM_TEAM.getComponent(tr("reason", reason)), true);
        target.save();

        broadcast(Lang.PLAYER_KICKED.getComponent(
                tr("target", target.getName()),
                tr("sender", sender.getName()),
                tr("reason", reason)
        ));
        save();
    }

    public void updateWaypoints() {
        members.forEach((k, v) -> {
            Player player = Bukkit.getPlayer(k);
            if (player != null) {
                TeamsPlus.getInstance().getWaypointManager().updatePlayerWaypoints(player);
            }
        });
    }

    public void removeWaypoint(TeamWaypoint waypoint) {
        waypoints.remove(waypoint);
        save();
        HookManager.getHook(LunarClientHook.class).ifPresent(hook -> {
            members.forEach((k, v) -> {
                Player player = Bukkit.getPlayer(k);
                if (player != null) {
                    hook.removeWaypoint(player, waypoint);
                }
            });
        });
    }

    public void transferOwnership(PlayerData target, PlayerData ownerData) {
        owner = target.getUuid();
        members.put(target.getUuid(), TeamRank.OWNER);
        members.put(ownerData.getUuid(), TeamRank.ADMIN);
        broadcast(
                Lang.TEAM_TRANSFER_BROADCAST.getComponent(
                        tr("sender", ownerData.getName()),
                        tr("target", target.getName())
                )
        );
        save();
    }

    public void transferOwnership(PlayerData target, String invoker) {
        owner = target.getUuid();
        members.put(target.getUuid(), TeamRank.OWNER);
        broadcast(
                Lang.TEAM_TRANSFER_BROADCAST.getComponent(
                        tr("sender", invoker),
                        tr("target", target.getName())
                )
        );
        save();
    }

    public void promote(PlayerData target, PlayerData sender) {
        TeamRank currentRank = members.get(target.getUuid());
        if (currentRank == null) {
            sender.sendMessage(Lang.TEAM_PROMOTE_FAILED_NOT_IN_SAME_TEAM.getComponent(
                    tr("target", target.getName())
            ));
            return;
        }
        TeamRank nextRank = TeamRank.getRank(currentRank.getPermissionLevel() + 1);
        if (nextRank.getPermissionLevel() >= getRank(sender.getUuid()).getPermissionLevel()) {
            sender.sendMessage(Lang.TEAM_PROMOTE_FAILED_CANNOT_PROMOTE_HIGHER.getComponent());
            return;
        }
        members.put(target.getUuid(), nextRank);
        broadcast(
                Lang.TEAM_PROMOTE_BROADCAST.getComponent(
                        tr("sender", sender.getName()),
                        tr("target", target.getName()),
                        tr("rank", Utils.enumToString(nextRank))
                )
        );
        save();
    }

    public void demote(PlayerData target, PlayerData sender) {
        TeamRank currentRank = members.get(target.getUuid());
        if (currentRank == null) {
            sender.sendMessage(Lang.TEAM_DEMOTE_FAILED_NOT_IN_SAME_TEAM.getComponent(tr("target", target.getName())));
            return;
        }
        if (currentRank == TeamRank.RECRUIT) {
            sender.sendMessage(Lang.TEAM_CANNOT_DEMOTE_LOWER.getComponent());
            return;
        }
        TeamRank nextRank = TeamRank.getRank(currentRank.getPermissionLevel() - 1);
        if (nextRank.getPermissionLevel() >= getRank(sender.getUuid()).getPermissionLevel()) {
            sender.sendMessage(Lang.TEAM_DEMOTE_FAILED_CANNOT_DEMOTE_HIGHER.getComponent());
            return;
        }
        members.put(target.getUuid(), nextRank);
        broadcast(
                Lang.TEAM_DEMOTE_BROADCAST.getComponent(
                        tr("sender", sender.getName()),
                        tr("target", target.getName()),
                        tr("rank", Utils.enumToString(nextRank))
                )
        );
        save();
    }


    public int getTotalOnline() {
        return members.keySet().stream().filter(uuid -> {
            Player p = Bukkit.getPlayer(uuid);
            return p != null && p.isOnline() && !VanishHook.isVanished(p);
        }).toList().size();
    }

    public ClaimResult claim(Player sender, Location location) {
        long hash = ClaimHandler.getInstance().hashChunk(location);
        if (ClaimHandler.getInstance().isClaimed(hash)) {
            return new ClaimResult(false, Lang.CLAIM_ALREADY_CLAIMED.getComponent());
        }
        // TODO: add a claim cost

        ClaimInfo claim = new ClaimInfo(new ChunkWrapper(location), teamId);
        ClaimHandler.getInstance().addClaim(claim);

        return new ClaimResult(true, Lang.CLAIM_SUCCESS.getComponent());
    }
}
