package net.badbird5907.teams.commands.impl.managment;

import com.jonahseguin.drink.annotation.Command;
import com.jonahseguin.drink.annotation.Flag;
import com.jonahseguin.drink.annotation.Sender;
import com.jonahseguin.drink.annotation.Text;
import net.badbird5907.blib.util.CC;
import net.badbird5907.teams.manager.PlayerManager;
import net.badbird5907.teams.manager.TeamsManager;
import net.badbird5907.teams.object.Lang;
import net.badbird5907.teams.object.PlayerData;
import net.badbird5907.teams.object.Team;
import net.badbird5907.teams.util.UUIDUtil;

import java.util.Arrays;
import java.util.List;

public class AllyCommand {
    @Command(name = "ally", desc = "Ally a team", usage = "<team>")
    public void ally(@Sender PlayerData sender, Team targetTeam) {
        /*
        List<String> args = Arrays.asList(arg1);
        boolean self = args.contains("self"), player = args.contains("player");
        String target = args.get(args.size() - 1);
        if (self && player) {
            sender.sendMessage(Lang.CANNOT_ALLY_PLAYER.toString());
            return;
        }
        if (self) {
            Team targetTeam = TeamsManager.getInstance().getTeamByName(target);
            if (targetTeam == null) {
                sender.sendMessage(Lang.TEAM_DOES_NOT_EXIST.toString());
                return;
            }
            sender.askAlly(targetTeam);
        } else {
            if (player) {
                if (!sender.isInTeam()) {
                    sender.sendMessage(Lang.MUST_BE_IN_TEAM.toString());
                    return;
                }
                PlayerData targetData = PlayerManager.getData(target);
                if (targetData == null) {
                    sender.sendMessage(Lang.PLAYER_NOT_FOUND.toString());
                    return;
                }
                sender.getPlayerTeam().requestToAllyPlayer(targetData);
                return;
            }

            //moved from here

        }
         */
        Team selfTeam = sender.getPlayerTeam();
        if (selfTeam == null) {
            sender.sendMessage(Lang.MUST_BE_IN_TEAM.toString());
            return;
        }
        if (selfTeam == targetTeam) {
            sender.sendMessage(Lang.CANNOT_ALLY_SELF.toString());
            return;
        }
        if (targetTeam == null) {
            sender.sendMessage(Lang.TEAM_DOES_NOT_EXIST.toString());
            return;
        }
        if (UUIDUtil.contains(targetTeam.getAllyRequests(), selfTeam.getTeamId())) {
            sender.sendMessage(Lang.ALREADY_SENT_ALLY_REQUEST.toString(targetTeam.getName()));
            return;
        }
        selfTeam.requestToAllyAnotherTeam(targetTeam);
    }
}
