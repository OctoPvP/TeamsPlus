package dev.badbird.teams.commands.impl.managment;

import net.badbird5907.blib.util.Logger;
import dev.badbird.teams.TeamsPlus;
import dev.badbird.teams.commands.annotation.AllowOffline;
import dev.badbird.teams.commands.annotation.TeamPermission;
import dev.badbird.teams.manager.PlayerManager;
import dev.badbird.teams.menu.ConfirmMenu;
import dev.badbird.teams.object.Lang;
import dev.badbird.teams.object.PlayerData;
import dev.badbird.teams.object.Team;
import dev.badbird.teams.object.TeamRank;
import dev.badbird.teams.util.Utils;
import net.kyori.adventure.text.Component;
import net.octopvp.commander.annotation.*;
import net.octopvp.commander.bukkit.annotation.PlayerOnly;
import org.bukkit.entity.Player;

import java.util.concurrent.TimeUnit;

@Command(name = "teams", aliases = {"teamsplus", "team"}, description = "Main TeamsPlus command")
public class TeamMemberManagement {
    @Command(name = "transfer", description = "Transfer team ownership to another player")
    @PlayerOnly
    @Cooldown(value = 5, unit = TimeUnit.MINUTES)
    @TeamPermission(TeamRank.OWNER)
    public void transfer(@Sender Player sender, @Sender PlayerData senderData, @Sender Team team, @Name("target") PlayerData targetData) {
        Team targetTeam = targetData.getPlayerTeam();
        if (targetTeam == null || !targetTeam.getTeamId().equals(team.getTeamId())) {
            sender.sendMessage(Lang.TEAM_TRANSFER_FAILED_TARGET_NOT_IN_TEAM.toString(targetData.getName()));
            return;
        }
        if (targetData.getUuid().equals(sender.getUniqueId())) {
            sender.sendMessage(Lang.TEAM_TRANSFER_FAILED_CANNOT_TRANSFER_TO_SELF.toString());
            return;
        }
        new ConfirmMenu("transfer ownership of your team", (b)-> {
            if (b) {
                team.transferOwnership(targetData, senderData);
            } else sender.sendMessage(Lang.CANCELED.toString());
            sender.closeInventory();
        }).open(sender);
    }

    @Command(name = "promote", description = "Promote a member's rank")
    @PlayerOnly
    @Cooldown(1)
    @TeamPermission(TeamRank.ADMIN)
    public void promote(@Sender Player sender, @Sender PlayerData senderData, @Sender @Required Team team, @AllowOffline @Required PlayerData target) {
        if (sender.getUniqueId().equals(target.getUuid())) {
            sender.sendMessage(Lang.TEAM_PROMOTE_FAILED_CANNOT_PROMOTE_SELF.toString());
            return;
        }
        team.promote(target, senderData);
    }

    @Command(name = "demote", description = "Demote a member's rank")
    @PlayerOnly
    @Cooldown(1)
    @TeamPermission(TeamRank.ADMIN)
    public void demote(@Sender Player sender, @Sender PlayerData senderData, @Sender @Required Team team, @AllowOffline @Required PlayerData target) {
        if (sender.getUniqueId().equals(target.getUuid())) {
            sender.sendMessage(Lang.TEAM_DEMOTE_FAILED_CANNOT_DEMOTE_SELF.toString());
            return;
        }
        team.demote(target, senderData);
    }

    @Command(name = "pinfo", aliases = "playerinfo", description = "View player's team info")
    @PlayerOnly
    public void pInfo(@Sender Player sender, @AllowOffline PlayerData target) {
        Team team = target.getPlayerTeam();
        Logger.debug("team: " + team + " | " + target.getName());
        boolean inTeam = team != null;
        try {
            Component component = Lang.PLAYER_INFO.getComponent(
                    target.getName(),
                    (inTeam ? team.getName() : Lang.PLAYER_NOT_IN_TEAM.toString()),
                    (inTeam ? Utils.enumToString(team.getRank(target.getUuid())) : Lang.PLAYER_NOT_IN_TEAM.toString()),
                    (target.getKills())
            );
            sender.sendMessage(component);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Command(name = "kick", description = "Kick a player from your team.")
    @Cooldown(10)
    @PlayerOnly
    @TeamPermission(TeamRank.MODERATOR)
    public void kick(@Sender Player sender, @Sender PlayerData playerData, @Sender Team team, @Name("target") @AllowOffline PlayerData targetData, @JoinStrings @Required String reason) {
        if (team.isAtLeast(targetData.getUuid(), team.getRank(sender.getUniqueId()))) {
            sender.sendMessage(Lang.CANNOT_KICK_SAME_RANK_OR_HIGHER.toString());
            return;
        }
        team.kick(targetData, playerData, reason);
    }

    @Command(name = "invite", description = "Invite a player to your team", usage = "<player>")
    @Cooldown(1)
    @PlayerOnly
    @TeamPermission(TeamRank.MODERATOR)
    public void invite(@Sender Player sender, @Sender Team senderTeam, PlayerData targetData, @Dependency TeamsPlus teamsPlus) {
        int maxSize = teamsPlus.getConfig().getInt("team.max-size", -1);
        if (maxSize > 0) {
            int senderSize = senderTeam.getMembers().size();
            if (senderSize >= maxSize) {
                sender.sendMessage(Lang.TEAM_MAX_SENDER.toString(senderSize, maxSize));
                return;
            }
        }
        if (targetData.getUuid().equals(sender.getUniqueId())) {
            sender.sendMessage(Lang.CANNOT_INVITE_SELF.toString());
            return;
        }
        if (targetData.getPendingInvites().containsKey(senderTeam.getTeamId())) {
            sender.sendMessage(Lang.INVITE_ALREADY_SENT.toString(targetData.getName()));
        } else {
            targetData.invite(senderTeam, sender.getName());
        }
    }

    @Command(name = "join", aliases = {"accept", "jointeam"}, description = "Join a team")
    public void join(@Sender Player sender, Team targetTeam, @Dependency TeamsPlus teamsPlus) {
        PlayerData data = PlayerManager.getData(sender);
        if (data.isInTeam()) {
            sender.sendMessage(Lang.ALREADY_IN_TEAM.toString());
            return;
        }
        if (targetTeam == null) {
            sender.sendMessage(Lang.TEAM_DOES_NOT_EXIST.toString());
            return;
        }
        int maxSize = teamsPlus.getConfig().getInt("team.max-size", -1);
        if (maxSize > 0 && targetTeam.getMembers().size() >= maxSize) {
            sender.sendMessage(Lang.TEAM_MAX_RECEIVER.toString(targetTeam.getMembers().size(), maxSize));
            return;
        }
        if (data.getPendingInvites().get(targetTeam.getTeamId()) != null) {
            data.getPendingInvites().remove(targetTeam.getTeamId());
            data.joinTeam(targetTeam);
        } else {
            sender.sendMessage(Lang.NO_INVITE.toString(targetTeam.getName()));
        }
    }
}
