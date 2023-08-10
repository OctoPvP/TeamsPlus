package dev.badbird.teams.manager;

import dev.badbird.teams.hooks.impl.LunarClientHook;
import dev.badbird.teams.object.PlayerData;
import dev.badbird.teams.object.Team;
import dev.badbird.teams.object.Waypoint;
import lombok.Getter;
import lombok.Setter;
import net.badbird5907.blib.util.Logger;
import net.badbird5907.blib.util.Tasks;
import dev.badbird.teams.TeamsPlus;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class WaypointManager implements Listener {
    private static WaypointManager instance;

    private List<Material> allowedIcons = new ArrayList<>();

    private Material defaultIcon = Material.NAME_TAG;

    public WaypointManager() {
        instance = this;
    }

    public void init(TeamsPlus plugin) {
        defaultIcon = Material.getMaterial(plugin.getConfig().getString("waypoint.default-icon", "NAME_TAG"));
        allowedIcons.clear();
        for (String s : plugin.getConfig().getStringList("waypoint.allowed-icons")) {
            Material material = Material.getMaterial(s.toUpperCase());
            if (material == null) {
                Logger.error("Could not find material \"" + s.toUpperCase() + "\"");
                continue;
            }
            allowedIcons.add(material);
        }

        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onJoin(PlayerJoinEvent event) {
        Tasks.runLater(() -> {
            if (Bukkit.getPlayer(event.getPlayer().getUniqueId()) != null)
                updatePlayerWaypoints(event.getPlayer());
        }, 20L);
    }

    public void updatePlayerWaypoints(Player player) {
        PlayerData data = PlayerManager.getData(player);
        Team team = data.getPlayerTeam();
        if (team == null || !LunarClientHook.isOnLunarClient(player)) return;
        for (Waypoint waypoint : team.getWaypoints()) {
            if (waypoint.getDisabledPlayers() != null && waypoint.getDisabledPlayers().contains(player.getUniqueId()))
                continue;
            LunarClientHook.sendWaypoint(player, waypoint);
        }
    }

    public void removeWaypoint(Waypoint waypoint) { // Hides waypoint visually from all players
        waypoint.getTeam().getMembers().forEach((k, v)-> {
            Player player = Bukkit.getPlayer(k);
            if (player != null) {
                LunarClientHook.removeWaypoint(player, waypoint);
            }
        });
    }

    public void hideWaypointFromPlayer(Player player, Waypoint waypoint) {
        LunarClientHook.removeWaypoint(player, waypoint);
    }
}
