package net.badbird5907.teams.object;

import lombok.Getter;
import lombok.Setter;
import net.badbird5907.teams.TeamsPlus;
import net.badbird5907.teams.manager.PlayerManager;
import net.badbird5907.teams.manager.StorageManager;
import net.badbird5907.teams.manager.TeamsManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Getter
@Setter
public class PlayerData {
    private final UUID uuid;
    private String name;
    private UUID teamId = null; //no team by default

    private CopyOnWriteArrayList<String> pendingMessages = new CopyOnWriteArrayList<>();

    private ChatChannel currentChannel = ChatChannel.GLOBAL;

    private ConcurrentHashMap<UUID, Long> allyRequests = new ConcurrentHashMap<>();

    /**
     * teamid | seconds
     */
    private ConcurrentHashMap<UUID, Integer> pendingInvites = new ConcurrentHashMap<>();

    private UUID allyChatTeamId = null;

    private int kills = 0;

    public PlayerData(UUID uuid) {
        this.uuid = uuid;
        //this.name = Bukkit.getOfflinePlayer(uuid).getName();
    }

    public PlayerData onLoad() {
        name = Bukkit.getOfflinePlayer(uuid).getName();
        return this;
    }


    public PvPCheckResult canDamage(Player victim) {
        UUID uuid = null;
        if (victim.hasMetadata("NPC")) {
            /*
            AntiCombatLogHook inst = AntiCombatLogHook.getInstance();
            if (inst != null) {
                NPC npc = CitizensAPI.getNPCRegistry().getNPC(victim);
                CombatNPCTrait trait = npc.getTrait(CombatNPCTrait.class);
                UUID traitUUID = trait.getUuid();
                if (traitUUID != null) {
                    uuid = traitUUID;
                } else return PvPCheckResult.DISALLOW_OTHER;
            } else return PvPCheckResult.DISALLOW_OTHER;
             */
            return PvPCheckResult.ALLOWED; // Above would load data every time, would cause lag
            //return PvPCheckResult.DISALLOW_OTHER;
        }
        if (uuid == null) {
            uuid = victim.getUniqueId();
        }
        if (isInSameTeamAs(uuid)) { //same team
            Team team = TeamsManager.getInstance().getTeamById(this.teamId);
            if (TeamsPlus.getInstance().getConfig().getBoolean("pvp.pvp-team", false)) { //team pvp is disabled
                if (team.getTempPvPSeconds() > 0 && TeamsPlus.getInstance().getConfig().getBoolean("team.temp-pvp.enable"))
                    return PvPCheckResult.ALLOWED;
            } else {
                return PvPCheckResult.DISALLOW_TEAM; //disallow pvp as theyre in the same team and team pvp is off
            }
        } else {
            if (isAlly(uuid)) {
                if (!TeamsPlus.getInstance().getConfig().getBoolean("pvp.pvp-ally", false)) { // Ally PVP is disabled
                    return PvPCheckResult.DISALLOW_ALLY; //disallow pvp as ally
                }
            }
        }
        return PvPCheckResult.ALLOWED;
    }

    public boolean isInSameTeamAs(UUID player) {
        if (!isInTeam())
            return false;
        Team team = getPlayerTeam();
        if (team == null)
            return false;
        return team.getMembers().containsKey(player);
    }

    public boolean isInTeam() {
        return teamId != null;
    }

    public void update() {
        Iterator<Map.Entry<UUID, Integer>> iterator = pendingInvites.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<UUID, Integer> entry = iterator.next();
            iterator.remove();
            if (entry.getValue() == 0) {
                Team team = TeamsPlus.getInstance().getTeamsManager().getTeamById(entry.getKey());
                if (team == null) return;
                Bukkit.getPlayer(uuid).sendMessage(
                        Lang.INVITE_EXPIRED.toString(
                                team.getName()
                        ));
                return;
            }
            pendingInvites.put(entry.getKey(), entry.getValue() - 1);
        }
        updateAllyRequests();
    }

    public void save() {
        name = Bukkit.getOfflinePlayer(uuid).getName();
        StorageManager.getStorageHandler().saveData(this);
    }

    public void invite(Team team, String sender) {
        pendingInvites.put(team.getTeamId(), TeamsPlus.getInstance().getConfig().getInt("invite-seconds"));
        team.broadcast(Lang.INVITE_TEAM_MESSAGE.toString(sender, this.name));
        Component message = LegacyComponentSerializer.legacyAmpersand().deserialize(Lang.INVITE.toString(sender, team.getName()))
                .clickEvent(ClickEvent.runCommand("/team join " + team.getName()))
                .hoverEvent(HoverEvent.showText(LegacyComponentSerializer.legacyAmpersand().deserialize(
                        Lang.INVITE_HOVER.toString(team.getName()))));

        sendMessage(message);
    }

    public void sendMessage(Component component, boolean... offline) {
        if (Bukkit.getPlayer(uuid) != null) {
            Bukkit.getPlayer(uuid).sendMessage(component);
        } else {
            if (offline != null && offline.length >= 1 && offline[0]) {
                pendingMessages.add(TeamsPlus.getInstance().getMiniMessage().serialize(component));
            }
        }
    }

    public void sendMessage(String s, boolean... offline) {
        if (Bukkit.getPlayer(uuid) != null) {
            Bukkit.getPlayer(uuid).sendMessage(s);
        } else {
            if (offline != null && offline.length >= 1 && offline[0]) {
                s = s.replace("<", "\\<")
                        .replace(">", "\\>"); // makeshift escape for < and >, because we use minimessage

                s = TeamsPlus.getInstance().getMiniMessage().serialize(LegacyComponentSerializer.legacySection().deserialize(s));
                pendingMessages.add(s);
                save();
            }
        }
    }

    public void join(Player player) {
        if (!isInTeam()) {
            if (currentChannel == ChatChannel.TEAM)
                currentChannel = ChatChannel.GLOBAL;
        }

        for (String s : pendingMessages) {
            Component component = TeamsPlus.getInstance().getMiniMessage().deserialize(s)
                    .replaceText(TextReplacementConfig.builder()
                            .matchLiteral("\\<")
                            .replacement("<")
                            .build())
                    .replaceText(TextReplacementConfig.builder()
                            .matchLiteral("\\>")
                            .replacement(">")
                            .build());
            player.sendMessage(component); //TODO use Queue
        }
        pendingMessages = new CopyOnWriteArrayList<>();
        save();
    }

    public Team getPlayerTeam() {
        return TeamsPlus.getInstance().getTeamsManager().getPlayerTeam(uuid);
    }

    public boolean isEnemy(Player player) {
        PlayerData targetData = PlayerManager.getData(player);
        Team targetTeam = targetData.getPlayerTeam();
        Team playerTeam = getPlayerTeam();

        if (targetTeam == null || playerTeam == null) {
            return false;
        }
        return targetTeam.isEnemy(this) || playerTeam.isEnemy(targetData);

        /*
        boolean b = enemiedPlayers.containsKey(player.getUniqueId()) || targetData.getEnemiedPlayers().containsKey(uuid);
        if (!b)
            return false;
        if (targetData.isInTeam()) {
            if (targetData.getPlayerTeam().isEnemy(this))
                return true;
        }
        if (isInTeam())
            return getPlayerTeam().isEnemy(targetData);
        return false;
         */
    }

    public boolean isAlly(UUID uuid) {
        PlayerData targetData = PlayerManager.getDataLoadIfNeedTo(uuid);
        Team targetTeam = targetData.getPlayerTeam();
        Team playerTeam = getPlayerTeam();

        if (targetTeam == null || playerTeam == null) {
            return false;
        }
        return targetTeam.isAlly(this) || playerTeam.isAlly(targetData);
    }

    public boolean isAlly(Player player) {
        return isAlly(player.getUniqueId());
        /*
        //this data enemy, target enemy player, target enemy team,
        PlayerData targetData = PlayerManager.getData(player);
        boolean b = alliedPlayers.contains(player.getUniqueId()) || targetData.alliedPlayers.contains(uuid);
        if (!b)
            return false;
        if (targetData.isInTeam()) {
            if (targetData.getPlayerTeam().isAlly(this))
                return true;
        }
        if (isInTeam())
            return getPlayerTeam().isAlly(targetData);
        return false;
         */
    }
    /*
    public void neutralPlayer(UUID uuid) {
        if (!enemiedPlayers.containsKey(uuid) && !alliedPlayers.contains(uuid))
            return;
        String name = Bukkit.getOfflinePlayer(uuid).getName();
        this.enemiedPlayers.remove(uuid);
        this.alliedPlayers.remove(uuid);
        PlayerData data = PlayerManager.getDataLoadIfNeedTo(uuid);
        data.getEnemiedPlayers().remove(uuid);
        data.getAlliedPlayers().remove(uuid);
        data.sendMessage(Lang.PLAYER_NEUTRAL_PLAYER.toString(this.name), true);
        data.save();
        sendMessage(Lang.PLAYER_NEUTRAL_PLAYER.toString(name), true);
    }
    public void removeEnemyTeam(UUID uuid) {
        Team target = TeamsManager.getInstance().getTeamById(uuid);
        target.neutralPlayer(this.uuid);
    }
     */

    public boolean isOnline() {
        return Bukkit.getPlayer(uuid) != null;
    }

    public void joinTeam(Team team) {
        team.join(this);
    }

    public void leaveTeam() {
        getPlayerTeam().leave(this);
        setCurrentChannel(ChatChannel.GLOBAL);
        setAllyChatTeamId(null);
        setTeamId(null);
        save();
    }

    public void updateAllyRequests() {
        Iterator<Map.Entry<UUID, Long>> it = allyRequests.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<UUID, Long> pair = it.next();
            long time = pair.getValue();
            UUID id = pair.getKey();
            if (time <= System.currentTimeMillis()) {
                Team team = TeamsManager.getInstance().getTeamById(id);
                if (team == null) {
                    it.remove();
                    continue;
                } else {
                    team.broadcastToRanks(Lang.ALLY_REQUEST_DENY_TIMEOUT.toString(getName()), TeamRank.ADMIN, TeamRank.OWNER);
                    it.remove();
                }
            }
        }
    }

    public void onKill(Player player) {
        kills += 1;
    }
}
