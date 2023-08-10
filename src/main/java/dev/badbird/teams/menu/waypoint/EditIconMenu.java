package dev.badbird.teams.menu.waypoint;

import dev.badbird.teams.object.Lang;
import dev.badbird.teams.object.Team;
import dev.badbird.teams.object.Waypoint;
import lombok.RequiredArgsConstructor;
import net.badbird5907.blib.menu.buttons.Button;
import net.badbird5907.blib.menu.menu.PaginatedMenu;
import net.badbird5907.blib.util.CC;
import net.badbird5907.blib.util.ItemBuilder;
import dev.badbird.teams.TeamsPlus;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class EditIconMenu extends PaginatedMenu {
    private final Waypoint waypoint;
    private final Team team;

    @Override
    public String getPagesTitle(Player player) {
        return "Edit Icon";
    }

    @Override
    public List<Button> getPaginatedButtons(Player player) {
        List<Button> list = new ArrayList<>();
        for (Material allowedIcon : TeamsPlus.getInstance().getWaypointManager().getAllowedIcons()) {
            list.add(new IconButton(allowedIcon));
        }
        return list;
    }

    @RequiredArgsConstructor
    private class IconButton extends Button {
        private final Material material;

        @Override
        public ItemStack getItem(Player player) {
            return new ItemBuilder(material)
                    .name(CC.WHITE + StringUtils.capitalize(material.name().toLowerCase().replace("_", " ")))
                    .lore(Lang.WAYPOINT_SELECT_ICON_LORE.getMessageList().stream().map(CC::translate).collect(Collectors.toList()))
                    .build();
        }

        @Override
        public int getSlot() {
            return 0;
        }

        @Override
        public void onClick(Player player, int slot, ClickType clickType, InventoryClickEvent event) {
            waypoint.setIcon(material);
            //player.closeInventory();
            team.broadcast(Lang.WAYPOINT_SELECT_ICON_BROADCAST.toString(player.getName(), waypoint.getName(), StringUtils.capitalize(material.name().toLowerCase().replace("_", " "))));
            new EditWaypointMenu(waypoint, team).open(player);
        }
    }
}
