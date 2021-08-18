package net.badbird5907.teams.commands.managment;

import net.badbird5907.blib.command.BaseCommand;
import net.badbird5907.blib.command.Command;
import net.badbird5907.blib.command.CommandResult;
import net.badbird5907.blib.command.Sender;
import net.badbird5907.teams.TeamsPlus;
import net.badbird5907.teams.manager.PlayerManager;
import net.badbird5907.teams.object.Lang;
import net.badbird5907.teams.object.Team;
import net.badbird5907.teams.object.player.PlayerData;

public class JoinCommand extends BaseCommand {
    @Command(name = "teamsplus.join",aliases = {"teamsplus.accept","teamsplus.jointeam"},cooldown = 10,usage = "&cUsage: /team join <team>",playerOnly = true)
    public CommandResult execute(Sender sender,String[] args){
        if (args.length != 1){
            return CommandResult.INVALID_ARGS;
        }
        PlayerData data = PlayerManager.getData(sender);
        if (data.isInTeam()){
            sender.sendMessage(Lang.ALREADY_IN_TEAM.toString());
            return CommandResult.SUCCESS;
        }
        String target = args[0];
        Team targetTeam = TeamsPlus.getInstance().getTeamsManager().getTeamByName(target);
        if (data.getPendingInvites().get(targetTeam.getTeamId()) != null){
            data.getPendingInvites().remove(targetTeam.getTeamId());
        }
        data.joinTeam(targetTeam);
        return CommandResult.SUCCESS;
    }
}
