package net.badbird5907.teams.util;

import org.apache.commons.lang3.StringUtils;

public class Utils {
    public static String enumToString(Enum<?> e) {
        return StringUtils.capitalize(e.name().toLowerCase().replace("_", " "));
    }

    public static String formatDouble(double d) {
        return String.format("%.2f", d);
    }
}
