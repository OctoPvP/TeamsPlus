package dev.badbird.teams.menu.waypoint;

import dev.badbird.teams.TeamsPlus;
import dev.badbird.teams.hooks.impl.LunarClientHook;
import dev.badbird.teams.manager.HookManager;
import dev.badbird.teams.object.Lang;
import dev.badbird.teams.object.Team;
import dev.badbird.teams.object.TeamWaypoint;
import dev.badbird.teams.util.ColorMapper;
import dev.octomc.agile.menu.Menu;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import lombok.RequiredArgsConstructor;
import net.badbird5907.blib.objects.TypeCallback;
import net.badbird5907.blib.util.QuestionConversation;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.conversations.Prompt;
import org.bukkit.entity.Player;

import static dev.badbird.teams.util.ChatUtil.tr;

@SuppressWarnings("deprecation")
@RequiredArgsConstructor
public class EditWaypointMenu extends Menu<Gui> {
    private final TeamWaypoint waypoint;
    private final Team team;

    @Override
    public Gui createGui(Player player) {
        return Gui.gui()
                .title("Edit Waypoint")
                .rows(3)
                .create();
    }

    @Override
    public void populateGui(Gui gui, Player player) {
        final ChatColor color = waypoint.getColor();
        gui.setItem(11, ItemBuilder.from(waypoint.getIcon())
                .name(Lang.WAYPOINT_EDIT_ICON_NAME.getComponent(tr("name", waypoint.getName())))
                .lore(Lang.WAYPOINT_EDIT_ICON_LORE.getComponentList())
                .asGuiItem(e -> new EditIconMenu(waypoint, team).open(player))
        );
        gui.setItem(13, ItemBuilder.from(Material.NAME_TAG)
                .name(Lang.WAYPOINT_EDIT_NAME.getComponent())
                .lore(Lang.WAYPOINT_EDIT_LORE.getComponentList())
                .asGuiItem(e -> {
                    player.closeInventory();
                    TeamsPlus.getInstance().getConversationFactory().withFirstPrompt(
                                    new QuestionConversation(LegacyComponentSerializer.legacySection().serialize(Lang.WAYPOINT_EDIT_NAME_MESSAGE.getComponent()),
                                            (TypeCallback<Prompt, String>) s -> {
                                                TeamsPlus.getInstance().getWaypointManager().removeWaypoint(waypoint);
                                                String prevName = waypoint.getName();
                                                waypoint.setName(s);
                                                team.save();
                                                team.broadcast(Lang.WAYPOINT_NAME_EDITED.getComponent(
                                                        tr("player", player.getName()),
                                                        tr("prev_name", prevName),
                                                        tr("new_name", s)
                                                ));
                                                open(player);
                                                return Prompt.END_OF_CONVERSATION;
                                            }))
                            .withLocalEcho(false)
                            .buildConversation(player)
                            .begin();
                })
        );
        gui.setItem(15, ItemBuilder.from(ColorMapper.dyeFromChatColor(color))
                .name(Lang.WAYPOINT_COLOR_NAME.getComponent())
                .lore(Lang.WAYPOINT_COLOR_LORE.getComponentList())
                .asGuiItem(e -> new SelectColorMenu(waypoint, team, player.getUniqueId()).open(player))
        );
        gui.setItem(26, ItemBuilder.from(Material.REDSTONE_BLOCK)
                .name(Lang.WAYPOINT_DELETE_BUTTON_NAME.getComponent())
                .lore(Lang.WAYPOINT_DELETE_BUTTON_LORE.getComponentList())
                .asGuiItem(e -> {
                    team.removeWaypoint(waypoint);
                    team.broadcast(Lang.WAYPOINT_DELETED_BROADCAST.getComponent(
                            tr("sender", player.getName()),
                            tr("waypoint", waypoint.getName())
                    ));
                    new ListWaypointsMenu(team, player.getUniqueId()).open(player);
                })
        );
        boolean lunar = HookManager.getHook(LunarClientHook.class).map(LunarClientHook::isEnabled).orElse(false);
        boolean toggled = !waypoint.getDisabledPlayers().contains(player.getUniqueId());
        if (lunar) {
            gui.setItem(0, ItemBuilder.from(Material.LEVER)
                    .name(Lang.TOGGLE_LUNAR_NAME.getComponent())
                    .lore(toggled ? Lang.TOGGLE_LUNAR_LORE_ENABLED.getComponentList() : Lang.TOGGLE_LUNAR_LORE_DISABLED.getComponentList())
                    .asGuiItem(e -> {
                        if (!toggled) {
                            player.sendMessage(Lang.TOGGLE_LUNAR_ON.getComponent());
                            waypoint.getDisabledPlayers().remove(player.getUniqueId());
                            TeamsPlus.getInstance().getWaypointManager().updatePlayerWaypoints(player);
                        } else {
                            player.sendMessage(Lang.TOGGLE_LUNAR_OFF.getComponent());
                            waypoint.getDisabledPlayers().add(player.getUniqueId());
                            TeamsPlus.getInstance().getWaypointManager().hideWaypointFromPlayer(player, waypoint);
                        }
                        team.save();
                        update(player);
                    })
            );
        }
        gui.getFiller().fill(PLACEHOLDER_ITEM);
    }
}
