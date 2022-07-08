package net.badbird5907.teams.commands.impl.managment;

import net.badbird5907.teams.commands.annotation.AllowOffline;
import net.badbird5907.teams.commands.annotation.TeamPermission;
import net.badbird5907.teams.object.Lang;
import net.badbird5907.teams.object.PlayerData;
import net.badbird5907.teams.object.Team;
import net.badbird5907.teams.object.TeamRank;
import net.octopvp.commander.annotation.*;
import net.octopvp.commander.bukkit.annotation.PlayerOnly;
import org.bukkit.entity.Player;

@Command(name = "teams", aliases = {"teamsplus", "team"}, description = "Main TeamsPlus command")
public class TeamMemberManagement {
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
    public void invite(@Sender Player sender, @Sender Team senderTeam, PlayerData targetData) {
        if (targetData == null) {
            sender.sendMessage(Lang.PLAYER_NOT_FOUND.toString());
            return;
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
}
