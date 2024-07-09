package dev.badbird.teams.commands.impl.management;

import dev.badbird.teams.TeamsPlus;
import dev.badbird.teams.commands.annotation.Sender;
import dev.badbird.teams.menu.waypoint.ListWaypointsMenu;
import dev.badbird.teams.object.Lang;
import dev.badbird.teams.object.Team;
import dev.badbird.teams.object.TeamWaypoint;
import net.badbird5907.blib.util.StoredLocation;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotation.specifier.Greedy;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.CommandDescription;
import org.incendo.cloud.annotations.processing.CommandContainer;

@CommandContainer
@Command("teamwaypoint|twp|tw")
@CommandDescription("Team Waypoints")
public class WaypointCommands {
    @Command("create <name>")
    @CommandDescription("Set a waypoint for your team.")
    // @Cooldown(duration = 5, timeUnit = ChronoUnit.SECONDS)
    // @TeamPermission(TeamRank.TRUSTED)
    public void waypoint(@Sender CommandSender cs, @Sender Team team, @Argument @Greedy String name) {
        if (cs instanceof Player sender) {
            if (team.getWaypoints().stream().anyMatch(w -> w.getName().equalsIgnoreCase(name))) {
                sender.sendMessage(Lang.WAYPOINT_EXISTS.toString());
                return;
            }
            TeamWaypoint w = new TeamWaypoint(name, team);
            w.setLocation(new StoredLocation(sender.getLocation()));
            w.setWorld(sender.getWorld().getName());
            team.getWaypoints().add(w);
            team.broadcast(Lang.WAYPOINT_CREATED.toString(sender.getName(), name));
            team.updateWaypoints();
            team.save();
        }
    }

    @Command("createpos <location> <name>")
    @CommandDescription("Set a waypoint for your team.")
    // @TeamPermission(TeamRank.TRUSTED)
    public void waypoint(@Sender CommandSender cs, @Sender Team team, @Argument Location location, @Argument @Greedy String name) {
        if (cs instanceof Player sender) {
            if (team.getWaypoints().stream().anyMatch(w -> w.getName().equalsIgnoreCase(name))) {
                sender.sendMessage(Lang.WAYPOINT_EXISTS.toString());
                return;
            }
            TeamWaypoint w = new TeamWaypoint(name, team);
            w.setLocation(new StoredLocation(location));
            team.getWaypoints().add(w);
            team.broadcast(Lang.WAYPOINT_CREATED.toString(sender.getName(), name));
            team.updateWaypoints();
            team.save();
        }
    }

    @Command("listwp")
    @CommandDescription("List all waypoints for your team.")
    public void list(@Sender CommandSender cs, @Sender Team team) {
        if (cs instanceof Player sender) {
            TeamsPlus.getInstance().getWaypointManager().updatePlayerWaypoints(sender);
            new ListWaypointsMenu(team, sender.getUniqueId()).open(sender);
        }
    }
}
