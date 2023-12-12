package dev.badbird.teams.hooks.impl;

import com.lunarclient.apollo.Apollo;
import com.lunarclient.apollo.BukkitApollo;
import com.lunarclient.apollo.module.team.TeamMember;
import com.lunarclient.apollo.module.team.TeamModule;
import com.lunarclient.apollo.module.waypoint.Waypoint;
import com.lunarclient.apollo.module.waypoint.WaypointModule;
import dev.badbird.teams.TeamsPlus;
import dev.badbird.teams.hooks.Hook;
import dev.badbird.teams.manager.TeamsManager;
import dev.badbird.teams.object.Team;
import dev.badbird.teams.object.TeamWaypoint;
import lombok.Getter;
import net.badbird5907.blib.util.Logger;
import net.badbird5907.blib.util.Tasks;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.awt.*;
import java.util.List;
import java.util.*;

public class LunarClientHook extends Hook {
    @Getter
    private boolean enabled = false;
    private Map<UUID, List<Waypoint>> waypointMap = new HashMap<>();
    private WaypointModule waypointModule;
    private TeamModule teamModule;
    private Color teamDisplayColor = Color.GREEN;

    public LunarClientHook() {
        super("Apollo-Bukkit");
    }

    @Override
    public void init(TeamsPlus plugin) {
        if (!plugin.getConfig().getBoolean("lunar.enable", true))
            return;
        enabled = true;
        waypointModule = Apollo.getModuleManager().getModule(WaypointModule.class);
        teamModule = Apollo.getModuleManager().getModule(TeamModule.class);
        reload();
        if (plugin.getConfig().getBoolean("lunar.team-display.enable", true)) {
            teamDisplayColor = Color.decode(plugin.getConfig().getString("lunar.team-display.color", "#00ff44"));
            Tasks.runAsyncTimer(() -> TeamsManager.getInstance().getTeams().forEach(this::refreshMembers), 1L, 1L);
        }
    }

    @Override
    public void disable(TeamsPlus plugin) {

    }

    @Override
    public void reload() {
        TeamsPlus plugin = TeamsPlus.getInstance();
        if (!plugin.getConfig().getBoolean("lunar.enable", true)) {
            enabled = false;
            return;
        }
        teamDisplayColor = Color.decode(plugin.getConfig().getString("lunar.team-display.color", "#00ff44"));
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


    public TeamMember createTeamMember(Player member) {
        Location location = member.getLocation();
        return TeamMember.builder()
                .playerUuid(member.getUniqueId())
                .displayName(member.displayName())
                .markerColor(teamDisplayColor)
                .location(BukkitApollo.toApolloLocation(location))
                .build();
    }

    public void refreshMembers(Team team) { // TODO: show allies at a range of X blocks (configurable)
        if (teamModule == null || !enabled) return;
        List<TeamMember> teammates = team.getMembers().keySet().stream().filter((uuid) -> {
                    Player player = Bukkit.getPlayer(uuid);
                    return player != null && player.isOnline() && !VanishHook.isVanished(player) && player.getGameMode() != GameMode.SPECTATOR;
                })
                .map((uuid) -> createTeamMember(Objects.requireNonNull(Bukkit.getPlayer(uuid)))).toList();
        team.getMembers().keySet().forEach((member) -> Apollo.getPlayerManager().getPlayer(member).ifPresent(apolloPlayer -> teamModule.updateTeamMembers(apolloPlayer, teammates)));
    }
}
