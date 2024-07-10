package dev.badbird.teams.menu.waypoint;

import dev.badbird.teams.TeamsPlus;
import dev.badbird.teams.object.Lang;
import dev.badbird.teams.object.Team;
import dev.badbird.teams.object.TeamWaypoint;
import dev.octomc.agile.menu.PaginatedMenu;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.PaginatedGui;
import lombok.RequiredArgsConstructor;
import net.badbird5907.blib.objects.TypeCallback;
import net.badbird5907.blib.util.QuestionConversation;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.conversations.Prompt;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
public class ListWaypointsMenu extends PaginatedMenu<PaginatedGui> {
    private final Team team;
    private final UUID uuid;

    private String searchTerm = "";

    @Override
    public List<GuiItem> getItems(Player player) {
        List<GuiItem> waypoints = new ArrayList<>();
        for (TeamWaypoint waypoint : team.getWaypoints()) {
            if (searchTerm != null && !searchTerm.isEmpty()) {
                if (waypoint.getName().toLowerCase().contains(searchTerm.toLowerCase())) {
                    waypoints.add(waypointBtn(waypoint));
                }
            } else waypoints.add(waypointBtn(waypoint));
        }
        return waypoints;
    }

    @Override
    public void addStaticButtons() {
        gui.setItem(52, ItemBuilder.from(Material.HOPPER)
                .name(Lang.WAYPOINT_FILTER_NAME.getComponent())
                .lore(Lang.WAYPOINT_FILTER_LORE.getComponentList())
                .asGuiItem(e -> {
                    Player player = (Player) e.getWhoClicked();
                    player.closeInventory();
                    TeamsPlus.getInstance().getConversationFactory().withFirstPrompt(
                                    new QuestionConversation(Lang.WAYPOINT_SEARCH_MESSAGE.toString(), (TypeCallback<Prompt, String>) s -> {
                                        searchTerm = s;
                                        open(player);
                                        return Prompt.END_OF_CONVERSATION;
                                    }))
                            .withLocalEcho(false)
                            .buildConversation(player)
                            .begin();
                }));
    }

    @Override
    public PaginatedGui createGui(Player player) {
        return Gui.paginated().title("Waypoints").rows(6).create();
    }

    private GuiItem waypointBtn(final TeamWaypoint waypoint) {
        return ItemBuilder.from(waypoint.getIcon())
                .name(Component.text(waypoint.getName(), NamedTextColor.WHITE))
                .lore(Lang.WAYPOINT_LORE.getComponentList(waypoint.getLocation().getX(), waypoint.getLocation().getY(), waypoint.getLocation().getZ(), waypoint.getLocation().getWorld().getName()))
                .asGuiItem(e -> {
                    Player player = (Player) e.getWhoClicked();
                    if (e.getClick().isShiftClick()) {
                        team.removeWaypoint(waypoint);
                        team.broadcast(Lang.WAYPOINT_DELETED_BROADCAST.toString(player.getName(), waypoint.getName()));
                        update(player);
                    } else new EditWaypointMenu(waypoint, team).open(player);
                });
    }
}
