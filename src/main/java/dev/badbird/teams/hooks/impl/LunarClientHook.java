package dev.badbird.teams.hooks.impl;

import com.lunarclient.apollo.Apollo;
import com.lunarclient.apollo.BukkitApollo;
import com.lunarclient.apollo.event.ApolloListener;
import com.lunarclient.apollo.event.EventBus;
import com.lunarclient.apollo.event.Listen;
import com.lunarclient.apollo.event.player.ApolloRegisterPlayerEvent;
import com.lunarclient.apollo.module.waypoint.Waypoint;
import com.lunarclient.apollo.module.waypoint.WaypointModule;
import dev.badbird.teams.TeamsPlus;
import dev.badbird.teams.hooks.Hook;
import dev.badbird.teams.object.TeamWaypoint;
import lombok.Getter;
import net.badbird5907.blib.util.Logger;
import org.bukkit.entity.Player;

import java.util.*;

public class LunarClientHook extends Hook {
    @Getter
    private boolean enabled = false;
    private Map<UUID, List<Waypoint>> waypointMap = new HashMap<>();
    private WaypointModule waypointModule;

    public LunarClientHook() {
        super("Apollo-Bukkit");
    }

    @Override
    public void init(TeamsPlus plugin) {
        enabled = true;
        waypointModule = Apollo.getModuleManager().getModule(WaypointModule.class);
        EventBus.getBus().register(new ApolloListener() {
            @Listen
            public void onApolloRegister(ApolloRegisterPlayerEvent e) {
                ((Player) e.getPlayer()).sendMessage("You have joined using LunarClient!");
            }
        });
    }

    @Override
    public void disable(TeamsPlus plugin) {

    }

    public void sendWaypoint(Player player, TeamWaypoint teamWaypoint) {
        if (!enabled || waypointModule == null) return;
        removeWaypoint(player, teamWaypoint);
        // Waypoint Waypoint = new Waypoint(teamWaypoint.getName(), teamWaypoint.getLocation().getLocation(), ColorMapper.fromChatColor(teamWaypoint.getColor()).asRGB(), false, !teamWaypoint.getDisabledPlayers().contains(player.getUniqueId()));
        // LunarClientAPI.getInstance().sendWaypoint(player, Waypoint);
        BukkitApollo.runForPlayer(player, apolloPlayer -> {
            // TODO: bulk send waypoints to team with Recipients.of()
            this.waypointModule.displayWaypoint(apolloPlayer, teamWaypoint.toLCWaypoint());
        });
    }

    public void removeWaypoint(Player player, TeamWaypoint waypoint) {
        if (!enabled || waypointModule == null) return;
        if (waypointMap.containsKey(player.getUniqueId())) {
            List<Waypoint> waypoints = waypointMap.get(player.getUniqueId());
            Iterator<Waypoint> iterator = waypoints.iterator();
            while (iterator.hasNext()) {
                Waypoint w = iterator.next();
                if (w.getName().equalsIgnoreCase(waypoint.getName())) {
                    // LunarClientAPI.getInstance().removeWaypoint(player, w);
                    BukkitApollo.runForPlayer(player, apolloPlayer -> {
                        this.waypointModule.removeWaypoint(apolloPlayer, w);
                    });
                    iterator.remove();
                }
            }
            // LunarClientAPI.getInstance().removeWaypoint(player, waypointMap.remove(player.getUniqueId()));
        } else {
            // LunarClientAPI.getInstance().removeWaypoint(player, new Waypoint(waypoint.getName(), waypoint.getLocation().getLocation(), ColorMapper.fromChatColor(waypoint.getColor()).asRGB(), false, !waypoint.getDisabledPlayers().contains(player.getUniqueId())));
            BukkitApollo.runForPlayer(player, apolloPlayer -> {
                this.waypointModule.removeWaypoint(apolloPlayer, waypoint.toLCWaypoint());
            });
        }
    }

    public boolean isOnLunarClient(Player player) {
        return isOnLunarClient(player.getUniqueId());
    }

    public boolean isOnLunarClient(UUID player) {
        if (enabled) {
            Logger.debug("Checking if %1 is on lunar client", player);
            return Apollo.getPlayerManager().hasSupport(player);
        }
        Logger.debug("Lunar client hook is disabled, returning false");
        return false;
    }
}
