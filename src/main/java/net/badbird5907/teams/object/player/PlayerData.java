package net.badbird5907.teams.object.player;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.badbird5907.teams.TeamsPlus;
import net.badbird5907.teams.manager.PlayerManager;
import net.badbird5907.teams.manager.StorageManager;
import net.badbird5907.teams.manager.TeamsManager;
import net.badbird5907.teams.object.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

@Getter
@Setter
@RequiredArgsConstructor
public class PlayerData {
    private final UUID uuid;
    private String name;
    private UUID teamId = null; //no team by default

    private Map<UUID, EnemyLevel> enemiedPlayers = new HashMap<>();
    private Map<UUID,EnemyLevel> enemiedTeams = new HashMap<>();
    private Set<UUID> alliedPlayers = new HashSet<>();
    private Set<UUID> alliedTeams = new HashSet<>();
    private List<String> pendingMessages = new ArrayList<>();

    private ChatChannel currentChannel = ChatChannel.GLOBAL;

    /**
     * teamid | seconds
     */
    private Map<UUID,Integer> pendingInvites = new HashMap<>();

    public PvPCheckResult canDamage(Player victim){
        if (isInSameTeamAs(victim.getUniqueId())){ //same team
            Team team = TeamsManager.getInstance().getTeamById(this.teamId);
            if (!TeamsPlus.getInstance().getConfig().getBoolean("pvp.pvp-team")){ //team pvp is disabled
                if (team.getTempPvPSeconds() > 0 && TeamsPlus.getInstance().getConfig().getBoolean("team.temp-pvp.enable"))
                    return PvPCheckResult.ALLOWED;
            }else{
                return PvPCheckResult.DISALLOW_TEAM; //disallow pvp as theyre in the same team and team pvp is off
            }
        }else{
            if (isAlly(victim)){
                return PvPCheckResult.DISALLOW_ALLY; //disallow pvp as ally
            }
        }
        return PvPCheckResult.ALLOWED;
    }

    public boolean isInSameTeamAs(UUID player){
        if (!isInTeam())
            return false;
        PlayerData data = PlayerManager.getDataLoadIfNeedTo(player);
        if (!data.isInTeam())
            return false;
        return data.getTeamId().toString().equalsIgnoreCase(this.teamId.toString()); //ptsd
    }

    public boolean isInTeam(){
        boolean b = teamId != null;
        if (b){
            if (getPlayerTeam().getMembers().get(uuid) == null) { //incase something went wrong
                b = false;
                teamId = null;
            }
        }
        return false;
    }
    public void update(){
        pendingInvites.forEach((teamid,timeleft)->{
            pendingInvites.remove(teamid,timeleft);
            if (timeleft == 0){
                Bukkit.getPlayer(uuid).sendMessage(Lang.INVITE_EXPIRED.toString(TeamsPlus.getInstance().getTeamsManager().getTeamById(teamid).getName()));
                return;
            }
            pendingInvites.put(teamid,timeleft -1);
        });
    }
    public void save(){
        StorageManager.getStorageHandler().saveData(this);
    }
    public void invite(Team team,String sender){
        pendingInvites.put(team.getTeamId(),TeamsPlus.getInstance().getConfig().getInt("invite-seconds"));
        team.broadcast(Lang.INVITE_TEAM_MESSAGE.toString(sender,this.name));
        sendMessage(Lang.INVITE.toString(sender,team.getName()));
    }
    public void sendMessage(String s,boolean... offline){
        if (Bukkit.getPlayer(uuid) != null){
            Bukkit.getPlayer(uuid).sendMessage(s);
        }else{
            if (offline != null && offline.length >= 1 && offline[0]){
                pendingMessages.add(s);
            }
        }
    }
    public void join(Player player){
        if (!isInTeam()){
            if (currentChannel == ChatChannel.TEAM)
                currentChannel = ChatChannel.GLOBAL;
        }
        for (String pendingMessage : pendingMessages) {
            player.sendMessage(pendingMessage); //TODO use Queue
            pendingMessages.remove(pendingMessage);
        }
    }
    public Team getPlayerTeam(){
        return TeamsPlus.getInstance().getTeamsManager().getPlayerTeam(uuid);
    }
    public boolean isEnemy(Player player){
        //this data enemy, target enemy player, target enemy team,
        PlayerData targetData = PlayerManager.getData(player);
        boolean b = enemiedPlayers.containsKey(player.getUniqueId()) || targetData.getEnemiedPlayers().containsKey(uuid);
        if (!b)
            return false;
        if (targetData.isInTeam()){
            if (targetData.getPlayerTeam().isEnemy(this))
                return true;
        }
        if (isInTeam())
            return getPlayerTeam().isEnemy(targetData);
        return false;
    }
    public boolean isAlly(Player player){
        //this data enemy, target enemy player, target enemy team,
        PlayerData targetData = PlayerManager.getData(player);
        boolean b = alliedPlayers.contains(player.getUniqueId()) || targetData.alliedPlayers.contains(uuid);
        if (!b)
            return false;
        if (targetData.isInTeam()){
            if (targetData.getPlayerTeam().isAlly(this))
                return true;
        }
        if (isInTeam())
            return getPlayerTeam().isAlly(targetData);
        return false;
    }
    public void neutralPlayer(UUID uuid){
        if (!enemiedPlayers.containsKey(uuid) && !alliedPlayers.contains(uuid))
            return;
        String name = Bukkit.getOfflinePlayer(uuid).getName();
        this.enemiedPlayers.remove(uuid);
        this.alliedPlayers.remove(uuid);
        PlayerData data = PlayerManager.getDataLoadIfNeedTo(uuid);
        data.getEnemiedPlayers().remove(uuid);
        data.getAlliedPlayers().remove(uuid);
        data.sendMessage(Lang.PLAYER_NEUTRAL_PLAYER.toString(this.name),true);
        data.save();
        sendMessage(Lang.PLAYER_NEUTRAL_PLAYER.toString(name),true);
    }
    public void removeEnemyTeam(UUID uuid){
        Team target = TeamsManager.getInstance().getTeamById(uuid);
        target.neutralPlayer(this.uuid);
    }

    public void joinTeam(Team team){
        team.join(this);
    }
}
