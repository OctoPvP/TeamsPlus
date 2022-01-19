package net.badbird5907.teams.commands.impl.managment;

import com.jonahseguin.drink.annotation.Command;
import com.jonahseguin.drink.annotation.Sender;
import com.jonahseguin.drink.annotation.Text;
import net.badbird5907.blib.command.CommandResult;
import net.badbird5907.teams.TeamsPlus;
import net.badbird5907.teams.manager.PlayerManager;
import net.badbird5907.teams.object.Lang;
import net.badbird5907.teams.object.Team;
import net.badbird5907.teams.object.player.PlayerData;
import org.bukkit.entity.Player;

public class JoinCommand {
    @Command(name = "join",aliases = {"accept","jointeam"},usage = "<team>",desc= "Join a team")
    public CommandResult execute(@Sender Player sender, Team targetTeam) {
        PlayerData data = PlayerManager.getData(sender);
        if (data.isInTeam()){
            sender.sendMessage(Lang.ALREADY_IN_TEAM.toString());
            return CommandResult.SUCCESS;
        }
        if (targetTeam == null){
            sender.sendMessage(Lang.TEAM_DOES_NOT_EXIST.toString());
        }
        if (data.getPendingInvites().get(targetTeam.getTeamId()) != null){
            data.getPendingInvites().remove(targetTeam.getTeamId());
        }
        data.joinTeam(targetTeam);
        return CommandResult.SUCCESS;
    }
}
