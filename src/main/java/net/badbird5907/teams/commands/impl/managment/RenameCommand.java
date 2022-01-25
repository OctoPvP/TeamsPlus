package net.badbird5907.teams.commands.impl.managment;

import com.jonahseguin.drink.annotation.Command;
import com.jonahseguin.drink.annotation.Sender;
import net.badbird5907.teams.TeamsPlus;
import net.badbird5907.teams.object.Lang;
import net.badbird5907.teams.object.Team;
import org.bukkit.entity.Player;

public class RenameCommand {
    @Command(name = "rename", desc = "Rename your team", usage = "<name>")
    public void rename(@Sender Player sender, @Sender Team team, String name) {
        if (TeamsPlus.getInstance().getConfig().getStringList("team.blocked-names").contains(name.toLowerCase())) {
            sender.sendMessage(Lang.CANNOT_CREATE_TEAM_BLOCKED_NAME.toString());
            return;
        }
        team.rename(sender, name);
    }
}
