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

public class InviteCommand extends BaseCommand {
    @Command(name = "teamsplus.invite",cooldown = 3,usage = "&cUsage: /team invite <player>",playerOnly = true)
    public CommandResult execute(Sender sender, String[] args){
        if (args.length != 1){
            return CommandResult.INVALID_ARGS;
        }
        Team senderTeam = TeamsPlus.getInstance().getTeamsManager().getPlayerTeam(sender.getPlayer().getUniqueId());
        PlayerData targetData = PlayerManager.getData(args[0]);
        if (senderTeam == null){
            sender.sendMessage(Lang.MUST_BE_IN_TEAM.toString());
            return CommandResult.SUCCESS;
        }
        if (targetData == null){
            return CommandResult.PLAYER_NOT_FOUND; //already sends message with this
        }
        if (targetData.getPendingInvites().containsKey(senderTeam.getTeamId())){
            sender.sendMessage(Lang.INVITE_ALREADY_SENT.toString(targetData.getName()));
        }else{
            targetData.invite(senderTeam,sender.getName());
        }
        return CommandResult.SUCCESS;
    }
}
