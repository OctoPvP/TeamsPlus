package net.badbird5907.teams.commands.impl.util;

import com.jonahseguin.drink.annotation.Command;
import com.jonahseguin.drink.annotation.Sender;
import com.jonahseguin.drink.annotation.Text;
import net.badbird5907.blib.util.Tasks;
import net.badbird5907.teams.TeamsPlus;
import net.badbird5907.teams.manager.PlayerManager;
import net.badbird5907.teams.object.Lang;
import net.badbird5907.teams.object.Team;
import net.badbird5907.teams.object.player.PlayerData;
import org.bukkit.entity.Player;

public class CreateTeamCommand {
    @Command(name = "create",desc = "Create a new team",usage = "<name>")
    public void create(@Sender Player sender, @Text String name){
        PlayerData playerData = PlayerManager.getPlayers().get(sender.getPlayer().getUniqueId());
        if (PlayerManager.getPlayers().get(sender.getPlayer().getUniqueId()).getTeamId() != null){
            sender.sendMessage(Lang.ALREADY_IN_TEAM.toString());
            return;
        }
        if (TeamsPlus.getApi().getTeamsManager().getTeamByName(name) != null){
            sender.sendMessage(Lang.TEAM_ALREADY_EXISTS.toString());
        }
        Team team = new Team(name,sender.getPlayer().getUniqueId());
        playerData.setTeamId(team.getTeamId());
        Tasks.runAsync(()->{
            team.save();
            playerData.save();
        });
        sender.sendMessage(Lang.CREATED_TEAM.toString(team.getName()));
        return;
    }
}
