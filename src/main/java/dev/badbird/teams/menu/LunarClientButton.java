package dev.badbird.teams.menu;

import dev.badbird.teams.object.Lang;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.GuiItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class LunarClientButton {
    private static final String MESSAGE = """
            <gold>Use <aqua><bold>Lunar Client</bold></aqua> for the best experience!</gold>
            <yellow><click:open_url:"https://lunarclient.com/"><hover:show_text:"<gold>Click to open url!">Click here to download it!</yellow>
            """;
    public static GuiItem LUNAR_CLIENT_BUTTON = ItemBuilder.from(Material.REDSTONE_TORCH)
            .name(Lang.WAYPOINT_INFO_NAME.getComponent())
            .lore(Lang.WAYPOINT_INFO_LORE.getComponentList())
            .asGuiItem(e -> {
                Player player = (Player) e.getWhoClicked();
                player.sendRichMessage(MESSAGE);
            });
}
