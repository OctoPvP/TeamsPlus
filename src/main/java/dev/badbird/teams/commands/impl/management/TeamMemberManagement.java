package dev.badbird.teams.commands.impl.management;

import dev.badbird.teams.commands.annotation.Sender;
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
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotation.specifier.Greedy;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.CommandDescription;
import org.incendo.cloud.annotations.processing.CommandContainer;
import org.incendo.cloud.processors.cooldown.annotation.Cooldown;

import java.time.temporal.ChronoUnit;

@CommandContainer
@Command("teams|team")
public class TeamMemberManagement {
    @Command("transfer <target>")
    @CommandDescription("Transfer team ownership to another player")
    @Cooldown(duration = 5, timeUnit = ChronoUnit.MINUTES)
    @TeamPermission(TeamRank.OWNER)
    public void transfer(@Sender Player sender, @Sender PlayerData senderData, @Sender Team team, @Argument("target") PlayerData targetData) {
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

    @Command("promote <target>")
    @CommandDescription("Promote a member's rank")
    @Cooldown(duration = 1, timeUnit = ChronoUnit.SECONDS)
    @TeamPermission(TeamRank.ADMIN)
    public void promote(@Sender Player sender, @Sender PlayerData senderData, @Sender Team team, @AllowOffline @Argument PlayerData target) {
        if (sender.getUniqueId().equals(target.getUuid())) {
            sender.sendMessage(Lang.TEAM_PROMOTE_FAILED_CANNOT_PROMOTE_SELF.toString());
            return;
        }
        team.promote(target, senderData);
    }

    @Command("demote <target>")
    @CommandDescription("Demote a member's rank")
    @Cooldown(duration = 1, timeUnit = ChronoUnit.SECONDS)
    @TeamPermission(TeamRank.ADMIN)
    public void demote(@Sender Player sender, @Sender PlayerData senderData, @Sender Team team, @AllowOffline @Argument PlayerData target) {
        if (sender.getUniqueId().equals(target.getUuid())) {
            sender.sendMessage(Lang.TEAM_DEMOTE_FAILED_CANNOT_DEMOTE_SELF.toString());
            return;
        }
        team.demote(target, senderData);
    }

    @Command("pinfo|playerinfo <target>")
    @CommandDescription("View player's team info")
    public void pInfo(@Sender CommandSender sender, @AllowOffline @Argument PlayerData target) {
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

    @Command("kick <target> <reason>")
    @CommandDescription("Kick a player from your team.")
    @Cooldown(duration = 10, timeUnit = ChronoUnit.SECONDS)
    @TeamPermission(TeamRank.MODERATOR)
    public void kick(@Sender Player sender, @Sender PlayerData playerData, @Sender Team team, @Argument("target") @AllowOffline PlayerData targetData, @Greedy @Argument String reason) {
        if (team.isAtLeast(targetData.getUuid(), team.getRank(sender.getUniqueId()))) {
            sender.sendMessage(Lang.CANNOT_KICK_SAME_RANK_OR_HIGHER.toString());
            return;
        }
        team.kick(targetData, playerData, reason);
    }

    @Command("invite <target>")
    @CommandDescription("Invite a player to your team")
    @Cooldown(duration = 1, timeUnit = ChronoUnit.SECONDS)
    @TeamPermission(TeamRank.MODERATOR)
    public void invite(@Sender Player sender, @Sender Team senderTeam, @Argument("target") PlayerData target) {
        int maxSize = TeamsPlus.getInstance().getConfig().getInt("team.max-size", -1);
        if (maxSize > 0) {
            int senderSize = senderTeam.getMembers().size();
            if (senderSize >= maxSize) {
                sender.sendMessage(Lang.TEAM_MAX_SENDER.toString(senderSize, maxSize));
                return;
            }
        }
        if (target.getUuid().equals(sender.getUniqueId())) {
            sender.sendMessage(Lang.CANNOT_INVITE_SELF.toString());
            return;
        }
        if (target.getPendingInvites().containsKey(senderTeam.getTeamId())) {
            sender.sendMessage(Lang.INVITE_ALREADY_SENT.toString(target.getName()));
        } else {
            target.invite(senderTeam, sender.getName());
        }
    }

    @Command("join|accept|jointeam <target>")
    @CommandDescription("Join a team")
    public void join(@Sender Player sender, @Argument("target") Team target) {
        PlayerData data = PlayerManager.getData(sender);
        if (data.isInTeam()) {
            sender.sendMessage(Lang.ALREADY_IN_TEAM.toString());
            return;
        }
        if (target == null) {
            sender.sendMessage(Lang.TEAM_DOES_NOT_EXIST.toString());
            return;
        }
        int maxSize = TeamsPlus.getInstance().getConfig().getInt("team.max-size", -1);
        if (maxSize > 0 && target.getMembers().size() >= maxSize) {
            sender.sendMessage(Lang.TEAM_MAX_RECEIVER.toString(target.getMembers().size(), maxSize));
            return;
        }
        if (data.getPendingInvites().get(target.getTeamId()) != null) {
            data.getPendingInvites().remove(target.getTeamId());
            data.joinTeam(target);
        } else {
            sender.sendMessage(Lang.NO_INVITE.toString(target.getName()));
        }
    }
}
