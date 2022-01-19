package net.badbird5907.teams.commands.impl.util;

import com.jonahseguin.drink.annotation.Command;
import com.jonahseguin.drink.annotation.Sender;
import com.jonahseguin.drink.annotation.Text;
import net.badbird5907.blib.util.CC;
import net.badbird5907.blib.util.PlayerUtil;
import net.badbird5907.blib.utils.StringUtils;
import net.badbird5907.teams.TeamsPlus;
import net.badbird5907.teams.object.Lang;
import net.badbird5907.teams.object.Team;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.concurrent.atomic.AtomicInteger;

public class TeamInfoCommand {
    @Command(name = "info",desc = "Get information about a team",usage = "[team]")
    public void info(@Sender Player sender, @Text String target) {
        if (target == null || target.isEmpty()){
            Team team = TeamsPlus.getInstance().getTeamsManager().getPlayerTeam(sender.getPlayer().getUniqueId());
            if (team == null) {
                sender.sendMessage(Lang.MUST_BE_IN_TEAM.toString());
                return;
            }
            sendTeamInfo(sender, team);
            return;
        }
        Team targetTeam = TeamsPlus.getInstance().getTeamsManager().getTeamByName(target);
        if (targetTeam == null) {
            sender.sendMessage(Lang.TEAM_DOES_NOT_EXIST.toString());
            return;
        }
        sendTeamInfo(sender, targetTeam);
    }

    private static void sendTeamInfo(CommandSender sender, Team targetTeam){
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
