package net.badbird5907.teams.commands.impl.util;

import com.jonahseguin.drink.annotation.Command;
import com.jonahseguin.drink.annotation.Sender;
import com.jonahseguin.drink.annotation.Text;
import net.badbird5907.blib.util.Tasks;
import net.badbird5907.teams.TeamsPlus;
import net.badbird5907.teams.manager.PlayerManager;
import net.badbird5907.teams.manager.TeamsManager;
import net.badbird5907.teams.object.Lang;
import net.badbird5907.teams.object.PlayerData;
import net.badbird5907.teams.object.Team;
import org.bukkit.entity.Player;

import java.util.Locale;

public class CreateTeamCommand {
    @Command(name = "create", desc = "Create a new team", usage = "<name>")
    public void create(@Sender Player sender, String name) {
        PlayerData playerData = PlayerManager.getPlayers().get(sender.getUniqueId());
        if (playerData.getPlayerTeam() != null) {
            sender.sendMessage(Lang.ALREADY_IN_TEAM.toString());
            return;
        }
        if (TeamsPlus.getApi().getTeamsManager().getTeamByName(name) != null) {
            sender.sendMessage(Lang.TEAM_ALREADY_EXISTS.toString());
            return;
        }
        if (TeamsPlus.getInstance().getConfig().getStringList("team.blocked-names").contains(name.toLowerCase())) {
            sender.sendMessage(Lang.CANNOT_CREATE_TEAM_BLOCKED_NAME.toString());
            return;
        }
        Team team = new Team(name, sender.getPlayer().getUniqueId());
        playerData.setTeamId(team.getTeamId());
        Tasks.runAsync(() -> {
            team.save();
            TeamsManager.getInstance().getTeams().add(team);
            playerData.save();
        });
        sender.sendMessage(Lang.CREATED_TEAM.toString(team.getName()));
        return;
    }
}
