package dev.badbird.teams.menu.waypoint;


import dev.badbird.teams.TeamsPlus;
import dev.badbird.teams.object.Lang;
import dev.badbird.teams.object.Team;
import dev.badbird.teams.object.TeamWaypoint;
import dev.badbird.teams.util.Utils;
import dev.octomc.agile.menu.PaginatedMenu;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.PaginatedGui;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

import static dev.badbird.teams.util.ChatUtil.tr;

@RequiredArgsConstructor
public class EditIconMenu extends PaginatedMenu<PaginatedGui> {
    private final TeamWaypoint waypoint;
    private final Team team;

    @Override
    public List<GuiItem> getItems(Player player) {
        List<GuiItem> items = new ArrayList<>();
        for (Material allowedIcon : TeamsPlus.getInstance().getWaypointManager().getAllowedIcons()) {
            items.add(ItemBuilder.from(allowedIcon)
                    .name(Component.text(Utils.enumToString(allowedIcon), NamedTextColor.WHITE))
                    .lore(Lang.WAYPOINT_SELECT_ICON_LORE.getComponentList())
                    .asGuiItem(e -> {
                        waypoint.setIcon(allowedIcon);
                        //player.closeInventory();
                        team.broadcast(Lang.WAYPOINT_SELECT_ICON_BROADCAST.getComponent(
                                tr("player", player.name()),
                                tr("icon", Utils.enumToString(allowedIcon)),
                                tr("waypoint", waypoint.getName())
                        ));
                        new EditWaypointMenu(waypoint, team).open(player);
                    })
            );
        }
        return items;
    }

    @Override
    public PaginatedGui createGui(Player player) {
        return Gui.paginated()
                .title("Edit Icon")
                .rows(6)
                .create();
    }
}
