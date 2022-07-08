package net.badbird5907.teams.commands.impl.managment;

import net.badbird5907.blib.util.StoredLocation;
import net.badbird5907.teams.menu.waypoint.ListWaypointsMenu;
import net.badbird5907.teams.object.Team;
import net.badbird5907.teams.object.Waypoint;
import net.octopvp.commander.annotation.*;
import org.bukkit.Material;
import org.bukkit.entity.Player;

@Command(name = "teamwaypoint", description = "Team Waypoints")
public class WaypointCommands {
    @Command(name = "create", description = "Set a waypoint for your team.")
    public void waypoint(@Sender Player sender, @Sender Team team, @Name("waypoint") @Required @JoinStrings String waypoint) {
        Waypoint w = new Waypoint(waypoint);
        w.setLocation(new StoredLocation(sender.getLocation()));
        w.setIcon(Material.DIAMOND_BLOCK);
        w.setWorld(sender.getWorld().getName());
        team.getWaypoints().add(w);
    }

    @Command(name = "list", description = "List all waypoints for your team.")
    public void list(@Sender Player sender, @Sender Team team) {
        new ListWaypointsMenu(team, sender.getUniqueId()).open(sender);
    }
}
