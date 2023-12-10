package dev.badbird.teams.object;

import com.lunarclient.apollo.BukkitApollo;
import com.lunarclient.apollo.module.waypoint.Waypoint;
import dev.badbird.teams.TeamsPlus;
import lombok.Getter;
import lombok.Setter;
import net.badbird5907.blib.util.StoredLocation;
import org.bukkit.ChatColor;
import org.bukkit.Material;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
public class TeamWaypoint {
    public TeamWaypoint(String name, UUID owner) {
        this.name = name;
        this.teamId = owner;
    }

    public TeamWaypoint(String name, Team team) {
        this(name, team.getTeamId());
    }

    private String name;
    private String world;

    private StoredLocation location;

    private Material icon = TeamsPlus.getInstance().getWaypointManager().getDefaultIcon();

    private ChatColor color = ChatColor.AQUA;

    private Set<UUID> disabledPlayers = new HashSet<>();
    private UUID teamId;

    public Team getTeam() {
        return TeamsPlus.getInstance().getTeamsManager().getTeamById(teamId);
    }


    public Waypoint toLCWaypoint() {
        return Waypoint.builder()
                .name(name)
                .location(BukkitApollo.toApolloBlockLocation(location.getLocation()))
                .color(color.asBungee().getColor())
                .preventRemoval(false)
                .hidden(false)
                .build();
    }
}
