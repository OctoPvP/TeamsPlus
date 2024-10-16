package dev.badbird.teams.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.Nullable;

public class ChatUtil {
    public static Component mm(String miniMessage, TagResolver... resolvers) {
        return MiniMessage.miniMessage().deserialize(miniMessage, resolvers);
    }

    public static TagResolver tr(String tag, @Nullable Object result) {
        return Placeholder.component(tag,
                result instanceof Component ? (Component) result : Component.text(result == null ? "null" : result.toString())
        );
    }

    public static NamedTextColor chatColorToNamedTextColor(ChatColor chatColor) {
        return NamedTextColor.NAMES.value(chatColor.name());
    }
}
