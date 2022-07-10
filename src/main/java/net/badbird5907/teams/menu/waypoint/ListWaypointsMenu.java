package net.badbird5907.teams.menu.waypoint;

import lombok.RequiredArgsConstructor;
import net.badbird5907.blib.menu.buttons.Button;
import net.badbird5907.blib.menu.menu.PaginatedMenu;
import net.badbird5907.blib.objects.TypeCallback;
import net.badbird5907.blib.util.CC;
import net.badbird5907.blib.util.ItemBuilder;
import net.badbird5907.blib.util.QuestionConversation;
import net.badbird5907.teams.TeamsPlus;
import net.badbird5907.teams.hooks.impl.LunarClientHook;
import net.badbird5907.teams.object.Lang;
import net.badbird5907.teams.object.Team;
import net.badbird5907.teams.object.Waypoint;
import org.bukkit.Material;
import org.bukkit.conversations.Prompt;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class ListWaypointsMenu extends PaginatedMenu {
    private final Team team;
    private final UUID uuid;

    private String searchTerm = "";

    @Override
    public String getPagesTitle(Player player) {
        return "Waypoints";
    }

    @Override
    public List<Button> getPaginatedButtons(Player player) {
        List<Button> waypoints = new ArrayList<>();
        for (Waypoint waypoint : team.getWaypoints()) {
            if (searchTerm != null && !searchTerm.isEmpty()) {
                if (waypoint.getName().toLowerCase().contains(searchTerm.toLowerCase())) {
                    waypoints.add(new WaypointButton(waypoint));
                }
            } else waypoints.add(new WaypointButton(waypoint));
        }
        return waypoints;
    }

    @Override
    public Button getFilterButton() {
        return new Button() {
            @Override
            public ItemStack getItem(Player player) {
                return new ItemBuilder(Material.HOPPER)
                        .name(Lang.WAYPOINT_FILTER_NAME.toString())
                        .lore(Lang.WAYPOINT_FILTER_LORE.getMessageList().stream().map(CC::translate).collect(Collectors.toList()))
                        .build();
            }

            @Override
            public int getSlot() {
                return 43;
            }

            @Override
            public void onClick(Player player, int slot, ClickType clickType, InventoryClickEvent event) {
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
            }
        };
    }

    @Override
    public List<Button> getToolbarButtons() {
        if (LunarClientHook.isOnLunarClient(uuid)) return null;
        return Arrays.asList(new LunarClientButton());
    }

    public static class LunarClientButton extends Button {
        @Override
        public ItemStack getItem(Player player) {
            return new ItemBuilder(Material.REDSTONE_TORCH)
                    .name(Lang.WAYPOINT_INFO_NAME.toString())
                    .lore(Lang.WAYPOINT_INFO_LORE.getMessageList().stream().map(CC::translate).collect(Collectors.toList()))
                    .build();
        }

        @Override
        public int getSlot() {
            return 0;
        }
    }
    @RequiredArgsConstructor
    private class WaypointButton extends Button {
        private final Waypoint waypoint;

        @Override
        public ItemStack getItem(Player player) {
            return new ItemBuilder(waypoint.getIcon())
                    .setName(CC.WHITE + waypoint.getName())
                    .lore(Lang.WAYPOINT_LORE.getMessageList(waypoint.getLocation().getX(), waypoint.getLocation().getY(), waypoint.getLocation().getZ()).stream().map(CC::translate).collect(Collectors.toList()))
                    .build();
        }

        @Override
        public int getSlot() {
            return 0;
        }

        @Override
        public void onClick(Player player, int slot, ClickType clickType, InventoryClickEvent event) {
            if (clickType.isShiftClick()) {
                /*
                team.getWaypoints().remove(waypoint);
                team.save();
                player.sendMessage(Lang.WAYPOINT_DELETED.toString());
                update(player);
                 */
                team.removeWaypoint(waypoint);
                player.sendMessage(Lang.WAYPOINT_DELETED.toString());
                update(player);
            } else new EditWaypointMenu(waypoint, team).open(player);
        }
    }
}
