package dev.badbird.teams.menu.waypoint;

import dev.badbird.teams.hooks.impl.LunarClientHook;
import dev.badbird.teams.manager.HookManager;
import dev.badbird.teams.object.Lang;
import dev.badbird.teams.object.Team;
import dev.badbird.teams.object.TeamWaypoint;
import lombok.RequiredArgsConstructor;
import net.badbird5907.blib.menu.buttons.Button;
import net.badbird5907.blib.menu.buttons.PlaceholderButton;
import net.badbird5907.blib.menu.buttons.impl.BackButton;
import net.badbird5907.blib.menu.menu.Menu;
import net.badbird5907.blib.util.CC;
import net.badbird5907.blib.util.ItemBuilder;
import dev.badbird.teams.util.ColorMapper;
import dev.badbird.teams.util.Utils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@RequiredArgsConstructor
public class SelectColorMenu extends Menu {
    private final TeamWaypoint waypoint;
    private final UUID uuid;
    private final Team team;


    @Override
    public List<Button> getButtons(Player player) {
        List<Button> buttons = new ArrayList<>();
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < 40; i++) {
                      /*
                if (i % 9 == 8) {
                list.add(i);
            }
                         */

            if (i >= 10 && i <= 16 || i >= 19 && i <= 25 || i >= 28 && i <= 34) {
                list.add(i);
            }
        }
        buttons.add(new PlaceholderButton() {
            @Override
            public int[] getSlots() {
                return genPlaceholderSpots(IntStream.range(1, 45), list.stream().mapToInt(Integer::intValue).toArray());
            }
        });
        List<Integer> clone = new ArrayList<>(list);
        for (ChatColor value : ChatColor.values()) {
            if (!value.isColor())
                continue;
            if (value == ChatColor.DARK_GRAY)
                continue;
            int slot = clone.remove(0);
            buttons.add(new ColorButton(value, waypoint.getColor() == value, slot));
        }
        return buttons;
    }

    @Override
    public String getName(Player player) {
        return "Select Waypoint Color";
    }

    @Override
    public List<Button> getToolbarButtons() {
        LunarClientHook hook = HookManager.getHook(LunarClientHook.class).orElse(null);
        if (hook != null && hook.isOnLunarClient(uuid)) return null;
        return Arrays.asList(new ListWaypointsMenu.LunarClientButton());
    }

    @Override
    public Button getBackButton(Player player) {
        return new BackButton() {
            @Override
            public void clicked(Player player, int slot, ClickType clickType, InventoryClickEvent event) {
                new EditWaypointMenu(waypoint, team).open(player);
            }

            @Override
            public int getSlot() {
                return 40;
            }
        };
    }

    @RequiredArgsConstructor
    private class ColorButton extends Button {
        private final ChatColor color;
        private final boolean selected;

        private final int slot;

        @Override
        public ItemStack getItem(Player player) {
            Material mat = ColorMapper.dyeFromChatColor(color);
            return new ItemBuilder(mat)
                    .name(Lang.WAYPOINT_COLOR_SELECT_NAME.toString(color, Utils.enumToString(color)))
                    .lore((selected ? Lang.WAYPOINT_COLOR_SELECT_LORE_SELECTED.getMessageList() :
                            Lang.WAYPOINT_COLOR_SELECT_LORE_UNSELECTED.getMessageList())
                            .stream().map(CC::translate).collect(Collectors.toList()))
                    .build();
        }

        @Override
        public int getSlot() {
            return slot;
        }

        @Override
        public void onClick(Player player, int slot, ClickType clickType, InventoryClickEvent event) {
            waypoint.setColor(color);
            team.save();
            team.updateWaypoints();
            team.broadcast(Lang.WAYPOINT_COLOR_SET_BROADCAST.toString(player.getName(), waypoint.getName(), color, Utils.enumToString(color)));
            new EditWaypointMenu(waypoint, team).open(player);
        }
    }
}
