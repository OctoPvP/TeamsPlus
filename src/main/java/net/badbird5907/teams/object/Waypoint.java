package net.badbird5907.teams.object;

import lombok.Getter;
import lombok.Setter;
import net.badbird5907.blib.util.StoredLocation;
import net.badbird5907.teams.TeamsPlus;
import org.bukkit.ChatColor;
import org.bukkit.Material;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
public class Waypoint {
    public Waypoint(String name) {
        this.name = name;
    }
    private String name;
    private String world;

    private StoredLocation location;

    private Material icon = TeamsPlus.getInstance().getWaypointManager().getDefaultIcon();

    private ChatColor color = ChatColor.AQUA;

    private Set<UUID> disabledPlayers = new HashSet<>();
}
