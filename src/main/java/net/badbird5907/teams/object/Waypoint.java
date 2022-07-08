package net.badbird5907.teams.object;

import lombok.Getter;
import lombok.Setter;
import net.badbird5907.blib.util.StoredLocation;
import org.bukkit.Material;

@Getter
@Setter
public class Waypoint {
    public Waypoint(String name) {
        this.name = name;
    }
    private String name;
    private String world;

    private StoredLocation location;

    private Material icon;
}
