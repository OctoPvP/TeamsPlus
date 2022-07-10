package net.badbird5907.teams.menu.waypoint;

import lombok.RequiredArgsConstructor;
import net.badbird5907.blib.menu.buttons.Button;
import net.badbird5907.blib.menu.menu.Menu;
import net.badbird5907.blib.objects.TypeCallback;
import net.badbird5907.blib.util.CC;
import net.badbird5907.blib.util.ItemBuilder;
import net.badbird5907.blib.util.QuestionConversation;
import net.badbird5907.teams.TeamsPlus;
import net.badbird5907.teams.hooks.impl.LunarClientHook;
import net.badbird5907.teams.object.Lang;
import net.badbird5907.teams.object.Team;
import net.badbird5907.teams.object.Waypoint;
import net.badbird5907.teams.util.ColorMapper;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.conversations.Prompt;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@RequiredArgsConstructor
public class EditWaypointMenu extends Menu {
    private final Waypoint waypoint;
    private final Team team;

    @Override
    public List<Button> getButtons(Player player) {
        List<Button> buttons = new ArrayList<>();
        buttons.add(new NameButton());
        buttons.add(new IconButton());
        buttons.add(new DeleteButton());
        buttons.add(new ColorButton(waypoint.getColor()));
        if (LunarClientHook.isOnLunarClient(player)) buttons.add(new ToggleLunarButton());
        buttons.add(new PlaceholderButton());
        return buttons;
    }

    @Override
    public String getName(Player player) {
        return "Edit Waypoint";
    }

    private class IconButton extends Button {

        @Override
        public ItemStack getItem(Player player) {
            return new ItemBuilder(waypoint.getIcon())
                    .name(Lang.WAYPOINT_EDIT_ICON_NAME.toString(StringUtils.capitalize(waypoint.getIcon().name().toLowerCase().replace("_", " "))))
                    .lore(Lang.WAYPOINT_EDIT_ICON_LORE.getMessageList().stream().map(CC::translate).collect(Collectors.toList()))
                    .build();
        }

        @Override
        public int getSlot() {
            return 11;
        }

        @Override
        public void onClick(Player player, int slot, ClickType clickType, InventoryClickEvent event) {
            new EditIconMenu(waypoint, team).open(player);
        }
    }

    private class NameButton extends Button {

        @Override
        public ItemStack getItem(Player player) {
            return new ItemBuilder(Material.NAME_TAG)
                    .name(Lang.WAYPOINT_EDIT_NAME.toString())
                    .lore(Lang.WAYPOINT_EDIT_LORE.getMessageList().stream().map(CC::translate).collect(Collectors.toList()))
                    .build();
        }

        @Override
        public int getSlot() {
            return 13;
        }

        @Override
        public void onClick(Player player, int slot, ClickType clickType, InventoryClickEvent event) {
            player.closeInventory();
            TeamsPlus.getInstance().getConversationFactory().withFirstPrompt(
                            new QuestionConversation(Lang.WAYPOINT_EDIT_NAME_MESSAGE.toString(), (TypeCallback<Prompt, String>) s -> {
                                TeamsPlus.getInstance().getWaypointManager().removeWaypoint(player, waypoint);
                                waypoint.setName(s);
                                team.save();
                                open(player);
                                return Prompt.END_OF_CONVERSATION;
                            }))
                    .withLocalEcho(false)
                    .buildConversation(player)
                    .begin();
        }
    }

    @RequiredArgsConstructor
    private class ColorButton extends Button {
        private final ChatColor color;

        @Override
        public ItemStack getItem(Player player) {
            return new ItemBuilder(ColorMapper.dyeFromChatColor(color))
                    .name(Lang.WAYPOINT_COLOR_NAME.toString())
                    .lore(Lang.WAYPOINT_COLOR_LORE.getMessageList().stream().map(CC::translate).collect(Collectors.toList()))
                    .build();
        }

        @Override
        public int getSlot() {
            return 15;
        }

        @Override
        public void onClick(Player player, int slot, ClickType clickType, InventoryClickEvent event) {
            new SelectColorMenu(waypoint, player.getUniqueId(), team).open(player);
        }
    }

    private class DeleteButton extends Button {

        @Override
        public ItemStack getItem(Player player) {
            return new ItemBuilder(Material.REDSTONE_BLOCK)
                    .name(Lang.WAYPOINT_DELETE_BUTTON_NAME.toString())
                    .lore(Lang.WAYPOINT_DELETE_BUTTON_LORE.getMessageList().stream().map(CC::translate).collect(Collectors.toList()))
                    .build();
        }

        @Override
        public int getSlot() {
            return 26;
        }

        @Override
        public void onClick(Player player, int slot, ClickType clickType, InventoryClickEvent event) {
            //team.getWaypoints().remove(waypoint);
            //team.save();
            team.removeWaypoint(waypoint);
            player.sendMessage(Lang.WAYPOINT_DELETED.toString());
            new ListWaypointsMenu(team, player.getUniqueId()).open(player);
        }
    }

    @RequiredArgsConstructor
    private class ToggleLunarButton extends Button {
        private final boolean toggled = false;

        @Override
        public ItemStack getItem(Player player) {
            return new ItemBuilder(Material.LEVER)
                    .name(Lang.TOGGLE_LUNAR_NAME.toString())
                    .lore((toggled ? Lang.TOGGLE_LUNAR_LORE_ENABLED.getMessageList() : Lang.TOGGLE_LUNAR_LORE_DISABLED.getMessageList()))
                    .build();
        }

        @Override
        public int getSlot() {
            return 0;
        }

        @Override
        public void onClick(Player player, int slot, ClickType clickType, InventoryClickEvent event) {
            if (toggled) waypoint.getDisabledPlayers().remove(player.getUniqueId());
            else waypoint.getDisabledPlayers().add(player.getUniqueId());
        }
    }

    private class PlaceholderButton extends net.badbird5907.blib.menu.buttons.PlaceholderButton {
        @Override
        public int[] getSlots() {
            return genPlaceholderSpots(IntStream.range(0, 26), 26, 15, 13, 11);
        }
    }
}
