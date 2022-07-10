package net.badbird5907.teams.hooks.impl;

import com.lunarclient.bukkitapi.LunarClientAPI;
import com.lunarclient.bukkitapi.object.LCWaypoint;
import net.badbird5907.teams.TeamsPlus;
import net.badbird5907.teams.hooks.Hook;
import net.badbird5907.teams.object.Waypoint;
import net.badbird5907.teams.util.ColorMapper;
import org.bukkit.entity.Player;

import java.util.*;

public class LunarClientHook extends Hook {
    private static boolean enabled = false;
    private static Map<UUID, List<LCWaypoint>> waypointMap = new HashMap<>();
    public LunarClientHook() {
        super("LunarClient-API");
    }

    @Override
    public void init(TeamsPlus plugin) {
        enabled = true;
    }

    @Override
    public void disable(TeamsPlus plugin) {

    }

    public static void sendWaypoint(Player player, Waypoint waypoint) {
        if (!enabled) return;
        removeWaypoint(player, waypoint);
        LCWaypoint lcWaypoint = new LCWaypoint(waypoint.getName(), waypoint.getLocation().getLocation(), ColorMapper.fromChatColor(waypoint.getColor()).asRGB(), false);
        LunarClientAPI.getInstance().sendWaypoint(player, lcWaypoint);
    }

    public static void removeWaypoint(Player player, Waypoint waypoint) {
        if (!enabled) return;
        if (waypointMap.containsKey(player.getUniqueId())) {
            List<LCWaypoint> waypoints = waypointMap.get(player.getUniqueId());
            Iterator<LCWaypoint> iterator = waypoints.iterator();
            while (iterator.hasNext()) {
                LCWaypoint w = iterator.next();
                if (w.getName().equalsIgnoreCase(waypoint.getName())) {
                    LunarClientAPI.getInstance().removeWaypoint(player, w);
                    iterator.remove();
                }
            }
            //LunarClientAPI.getInstance().removeWaypoint(player, waypointMap.remove(player.getUniqueId()));
        } else LunarClientAPI.getInstance().removeWaypoint(player, new LCWaypoint(waypoint.getName(), waypoint.getLocation().getLocation(), ColorMapper.fromChatColor(waypoint.getColor()).asRGB(), false));
    }

    public static boolean isOnLunarClient(Player player) {
        if (enabled) return LunarClientAPI.getInstance().isRunningLunarClient(player);
        else return false;
    }

    public static boolean isOnLunarClient(UUID player) {
        if (enabled) return LunarClientAPI.getInstance().isRunningLunarClient(player);
        else return false;
    }
}
