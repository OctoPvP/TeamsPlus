package dev.badbird.teams.object;

import lombok.Getter;
import lombok.Setter;
import net.badbird5907.blib.util.StoredLocation;
import dev.badbird.teams.TeamsPlus;
import org.bukkit.ChatColor;
import org.bukkit.Material;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
public class Waypoint {
    public Waypoint(String name, UUID owner) {
        this.name = name;
        this.teamId = owner;
    }

    public Waypoint(String name, Team team) {
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
}
