package net.badbird5907.teams.commands.impl.managment;

import com.jonahseguin.drink.annotation.Command;
import com.jonahseguin.drink.annotation.Sender;
import net.badbird5907.teams.manager.PlayerManager;
import net.badbird5907.teams.object.Lang;
import net.badbird5907.teams.object.PlayerData;
import net.badbird5907.teams.object.Team;
import org.bukkit.entity.Player;

public class JoinCommand {
    @Command(name = "join", aliases = {"accept", "jointeam"}, usage = "<team>", desc = "Join a team")
    public void execute(@Sender Player sender, Team targetTeam) {
        PlayerData data = PlayerManager.getData(sender);
        if (data.isInTeam()) {
            sender.sendMessage(Lang.ALREADY_IN_TEAM.toString());
            return;
        }
        if (targetTeam == null) {
            sender.sendMessage(Lang.TEAM_DOES_NOT_EXIST.toString());
            return;
        }
        if (data.getPendingInvites().get(targetTeam.getTeamId()) != null) {
            data.getPendingInvites().remove(targetTeam.getTeamId());
            data.joinTeam(targetTeam);
        } else {
            sender.sendMessage(Lang.NO_INVITE.toString(targetTeam.getName()));
        }
        return;
    }
}
