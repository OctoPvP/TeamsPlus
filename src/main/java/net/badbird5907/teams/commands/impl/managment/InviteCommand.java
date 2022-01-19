package net.badbird5907.teams.commands.impl.managment;

import com.jonahseguin.drink.annotation.Command;
import com.jonahseguin.drink.annotation.Sender;
import net.badbird5907.teams.TeamsPlus;
import net.badbird5907.teams.object.Lang;
import net.badbird5907.teams.object.Team;
import net.badbird5907.teams.object.player.PlayerData;
import org.bukkit.entity.Player;

public class InviteCommand {
    @Command(name = "invite", desc = "Invite a player to your team", usage = "<player>")
    public void execute(@Sender Player sender, PlayerData targetData) {
        Team senderTeam = TeamsPlus.getInstance().getTeamsManager().getPlayerTeam(sender.getPlayer().getUniqueId());
        if (senderTeam == null) {
            sender.sendMessage(Lang.MUST_BE_IN_TEAM.toString());
            return;
        }
        if (targetData == null) {
            sender.sendMessage(Lang.PLAYER_NOT_FOUND.toString());
            return;
        }
        if (targetData.getPendingInvites().containsKey(senderTeam.getTeamId())) {
            sender.sendMessage(Lang.INVITE_ALREADY_SENT.toString(targetData.getName()));
        } else {
            targetData.invite(senderTeam, sender.getName());
        }
        return;
    }
}
