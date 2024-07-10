package dev.badbird.teams.menu.waypoint;

import dev.badbird.teams.hooks.impl.LunarClientHook;
import dev.badbird.teams.manager.HookManager;
import dev.badbird.teams.object.Lang;
import dev.badbird.teams.object.Team;
import dev.badbird.teams.object.TeamWaypoint;
import dev.badbird.teams.util.ColorMapper;
import dev.badbird.teams.util.Utils;
import dev.octomc.agile.menu.PaginatedMenu;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.PaginatedGui;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static dev.badbird.teams.menu.LunarClientButton.LUNAR_CLIENT_BUTTON;

@SuppressWarnings("deprecation")
@RequiredArgsConstructor
public class SelectColorMenu extends PaginatedMenu<PaginatedGui> {
    private final TeamWaypoint waypoint;
    private final Team team;
    private final UUID uuid;


    @Override
    public List<GuiItem> getItems(Player player) {
        ChatColor[] values = ChatColor.values();
        List<GuiItem> items = new ArrayList<>();
        for (ChatColor value : values) {
            if (value.isFormat() || value == ChatColor.DARK_GRAY) continue;
            boolean selected = waypoint.getColor() == value;
            GuiItem item = ItemBuilder.from(ColorMapper.dyeFromChatColor(value))
                    .name(Lang.WAYPOINT_COLOR_SELECT_NAME.getComponent(value, Utils.enumToString(value)))
                    .lore(selected ? Lang.WAYPOINT_COLOR_SELECT_LORE_SELECTED.getComponentList() :
                            Lang.WAYPOINT_COLOR_SELECT_LORE_UNSELECTED.getComponentList())
                    .asGuiItem(e -> {
                        waypoint.setColor(value);
                        team.save();
                        team.updateWaypoints();
                        team.broadcast(Lang.WAYPOINT_COLOR_SET_BROADCAST.toString(player.getName(), waypoint.getName(), value, Utils.enumToString(value)));
                        new EditWaypointMenu(waypoint, team).open(player);
                    });
            items.add(item);
        }
        return items;
    }

    @Override
    public void addStaticButtons() {
        LunarClientHook hook = HookManager.getHook(LunarClientHook.class).orElse(null);
        if (hook == null || !hook.isEnabled() || hook.isOnLunarClient(uuid)) return;
        gui.setItem(0, LUNAR_CLIENT_BUTTON);
    }

    @Override
    public PaginatedGui createGui(Player player) {
        return Gui.paginated()
                .title("Select a color")
                .rows(6)
                .create();
    }
}