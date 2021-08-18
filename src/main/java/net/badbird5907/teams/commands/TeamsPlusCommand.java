package net.badbird5907.teams.commands;

import net.badbird5907.blib.command.BaseCommand;
import net.badbird5907.blib.command.Command;
import net.badbird5907.blib.command.CommandResult;
import net.badbird5907.blib.command.Sender;
import net.badbird5907.blib.util.CC;
import net.badbird5907.blib.util.Cooldown;
import net.badbird5907.blib.util.PlayerUtil;
import net.badbird5907.blib.util.Tasks;
import net.badbird5907.blib.utils.StringUtils;
import net.badbird5907.teams.TeamsPlus;
import net.badbird5907.teams.manager.PlayerManager;
import net.badbird5907.teams.manager.StorageManager;
import net.badbird5907.teams.object.Lang;
import net.badbird5907.teams.object.Team;
import net.badbird5907.teams.object.player.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.concurrent.atomic.AtomicInteger;

public class TeamsPlusCommand extends BaseCommand {
    public TeamsPlusCommand(){
        super();
    }
    @Command(name = "teamsplus",aliases = {"teams+","team","teams"},usage = "&cUsage: /team create <name>")
    public CommandResult execute(Sender sender, String[] args) {
        return CommandResult.SUCCESS;
    }
    @Command(name = "teamsplus.create",playerOnly = true)
    public CommandResult create(Sender sender,String[] args){
        PlayerData playerData = PlayerManager.getPlayers().get(sender.getPlayer().getUniqueId());
        if (PlayerManager.getPlayers().get(sender.getPlayer().getUniqueId()).getTeamId() != null){
            sender.sendMessage(Lang.ALREADY_IN_TEAM);
            return CommandResult.SUCCESS;
        }
        if (args.length != 1){
            return CommandResult.INVALID_ARGS;
        }
        if (TeamsPlus.getApi().getTeamsManager().getTeamByName(args[0]) != null){
            sender.sendMessage(Lang.TEAM_ALREADY_EXISTS.toString());
        }
        Team team = new Team(args[0],sender.getPlayer().getUniqueId());
        playerData.setTeamId(team.getTeamId());
        Tasks.runAsync(()->{
            team.save();
            playerData.save();
        });
        sender.sendMessage(Lang.CREATED_TEAM.toString(team.getName()));
        return CommandResult.SUCCESS;
    }
    @Command(name = "teamsplus.info",cooldown = 2,usage = "&cUsage: /team info <team>")
    public CommandResult info(Sender sender,String[] args) {
        if (args.length == 1) {
            Team targetTeam = TeamsPlus.getInstance().getTeamsManager().getTeamByName(args[0]);
            if (targetTeam == null) {
                sender.sendMessage(Lang.TEAM_DOES_NOT_EXIST.toString());
                return CommandResult.SUCCESS;
            }
            sendTeamInfo(sender, targetTeam);
        } else if (args.length == 0) {
            if (!(sender.getCommandSender() instanceof Player))
                return CommandResult.PLAYER_ONLY;
            Team team = TeamsPlus.getInstance().getTeamsManager().getPlayerTeam(sender.getPlayer().getUniqueId());
            if (team == null) {
                sender.sendMessage(Lang.MUST_BE_IN_TEAM.toString());
                return CommandResult.SUCCESS;
            }
            sendTeamInfo(sender, team);
        } else {
            sendUsage(sender);
        }
        return CommandResult.SUCCESS;
    }

    private static void sendTeamInfo(Sender sender,Team targetTeam){
        OfflinePlayer owner = Bukkit.getPlayer(targetTeam.getOwner());
        String enemies;
        if (targetTeam.getSettings().isShowEnemies()){
            StringBuilder sb = new StringBuilder();
            targetTeam.getEnemiedTeams().forEach((uuid,level,name) -> sb.append(Lang.TEAM_INFO_ENEMIED_TEAM_ENTRY.toString(name)));
            targetTeam.getEnemiedPlayers().forEach(((uuid, enemyLevel) -> sb.append(Lang.TEAM_INFO_ENEMIED_PLAYER_ENTRY.toString(PlayerUtil.getPlayerName(uuid)))));
            enemies = StringUtils.replacePlaceholders(Lang.TEAM_INFO_ENEMIES_LIST.toString((targetTeam.getEnemiedTeams().size() + targetTeam.getEnemiedPlayers().size()),sb.toString()));
        }else{
            enemies = CC.GREEN + targetTeam.getEnemiedTeams().size() + targetTeam.getEnemiedPlayers().size();
        }
        String allies;
        if (targetTeam.getSettings().isShowAllies()){
            StringBuilder sb = new StringBuilder();
            targetTeam.getAlliedTeams().forEach((uuid,name)-> sb.append(Lang.TEAM_INFO_ALLIES_TEAM_ENTRY.toString(name)));
            targetTeam.getAlliedPlayers().forEach((uuid)-> sb.append(Lang.TEAM_INFO_ALLIES_PLAYER_ENTRY.toString(PlayerUtil.getPlayerName(uuid))));
            allies = StringUtils.replacePlaceholders(Lang.TEAM_INFO_ALLIES_LIST.toString((targetTeam.getAlliedTeams().size() + targetTeam.getAlliedPlayers().size()),sb.toString()));
        }else allies = CC.GREEN + (targetTeam.getAlliedTeams().size() + targetTeam.getAlliedPlayers().size());
        String members;
        int membersAll = targetTeam.getMembers().size();
        AtomicInteger membersOnline = new AtomicInteger();
        StringBuilder sb = new StringBuilder();
        targetTeam.getMembers().forEach((uuid,role)->{
            if (Bukkit.getPlayer(uuid) != null) {
                membersOnline.getAndIncrement();
                sb.append(Lang.TEAM_INFO_ONLINE_MEMBER_ENTRY.toString(PlayerUtil.getPlayerName(uuid)));
            }else{
                sb.append(Lang.TEAM_INFO_OFFLINE_MEMBER_ENTRY.toString(PlayerUtil.getPlayerName(uuid)));
            }
        });
        members = Lang.TEAM_INFO_MEMBERS_LIST.toString(membersOnline,membersAll,sb.toString());
        String message = Lang.TEAM_INFO_MESSAGE.toString(targetTeam.getName(),owner.getName(),allies,enemies,members);
        sender.sendMessage(CC.translate(message));
    }
}
