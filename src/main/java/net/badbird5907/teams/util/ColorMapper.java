package net.badbird5907.teams.util;

import lombok.Getter;
import net.badbird5907.blib.util.Logger;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Material;

import java.util.Arrays;
import java.util.List;

public class ColorMapper {
    @Getter
    private static List<Color> colors = Arrays.asList(
            Color.AQUA,
            Color.BLACK,
            Color.BLUE,
            Color.FUCHSIA,
            Color.GRAY,
            Color.GRAY,
            Color.GREEN,
            Color.LIME,
            Color.MAROON,
            Color.NAVY,
            Color.OLIVE,
            Color.ORANGE,
            Color.PURPLE,
            Color.RED,
            Color.SILVER,
            Color.TEAL,
            Color.WHITE,
            Color.YELLOW
    );
    @Getter
    private static List<ChatColor> chatColors = Arrays.asList(
            ChatColor.AQUA,
            ChatColor.BLACK,
            ChatColor.BLUE,
            ChatColor.LIGHT_PURPLE,
            ChatColor.GRAY,
            ChatColor.DARK_GRAY,
            ChatColor.GREEN,
            ChatColor.GREEN,
            ChatColor.DARK_RED,
            ChatColor.DARK_BLUE,
            ChatColor.DARK_GREEN,
            ChatColor.GOLD,
            ChatColor.DARK_PURPLE,
            ChatColor.RED,
            ChatColor.GRAY,
            ChatColor.DARK_AQUA,
            ChatColor.WHITE,
            ChatColor.YELLOW
    );
    public static Color fromChatColor(ChatColor color) {
        return colors.get(chatColors.indexOf(color));
    }
    public static ChatColor fromColor(Color color) {
        return chatColors.get(colors.indexOf(color));
    }

    public static Material dyeFromChatColor(ChatColor color) {
        switch (color) {
            case AQUA:
                return Material.LIGHT_BLUE_DYE;
            case BLACK:
                return Material.BLACK_DYE;
            case BLUE:
            case DARK_BLUE:
                return Material.BLUE_DYE;
            case DARK_AQUA:
                return Material.CYAN_DYE;
            case DARK_GRAY:
            case GRAY:
                return Material.GRAY_DYE;
            case DARK_GREEN:
            case GREEN:
                return Material.GREEN_DYE;
            case DARK_PURPLE:
            case LIGHT_PURPLE:
                return Material.PURPLE_DYE;
            case DARK_RED:
            case RED:
                return Material.RED_DYE;
            case WHITE:
                return Material.WHITE_DYE;
            case YELLOW:
                return Material.YELLOW_DYE;
            case GOLD:
                return Material.ORANGE_DYE;
            default:
                return null;
        }
    }
    public static Material dyeFromColor(Color color) {
        DyeColor dyeColor = DyeColor.getByColor(color);
        if (dyeColor == null) {
            Logger.error("Could not find dye color for " + color);
            return null;
        }
        return Material.getMaterial(dyeColor.name() + "_DYE");
    }
}
