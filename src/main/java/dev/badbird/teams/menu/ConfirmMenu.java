package dev.badbird.teams.menu;

import dev.octomc.agile.menu.Menu;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.function.Consumer;

@EqualsAndHashCode(callSuper = true)
@Data
public class ConfirmMenu extends Menu<Gui> {
    private final String action;
    private final Consumer<Boolean> callback;
    private boolean permanent = false;
    private boolean done = false;

    public ConfirmMenu setPermanent(boolean permanent) {
        this.permanent = permanent;
        return this;
    }

    @Override
    public Gui createGui(Player player) {
        return Gui.gui()
                .title(Component.text("Are you sure you want to " + action + "?", NamedTextColor.RED))
                .rows(3)
                .disableAllInteractions()
                .create();
    }

    @Override
    public void populateGui(Gui gui, Player player) {
        gui.getFiller().fill(PLACEHOLDER_ITEM);
        gui.setItem(11, ItemBuilder.from(Material.GREEN_WOOL).name(Component.text("Yes", NamedTextColor.GREEN)).asGuiItem(e -> {
            if (done) return;
            callback.accept(true);
            done = true;
            gui.close(player);
        }));
        gui.setItem(15, ItemBuilder.from(Material.RED_WOOL).name(Component.text("No", NamedTextColor.RED)).asGuiItem(e -> {
            if (done) return;
            callback.accept(false);
            done = true;
            gui.close(player);
        }));
        gui.setItem(13, ItemBuilder.from(Material.PAPER).name(Component.text("Are you sure you want to " + action + "?", NamedTextColor.RED))
                .lore(MiniMessage.miniMessage().deserialize("<gray>Click <green>Yes<gray> to confirm, or <red>No<gray> to cancel."),
                permanent ? Component.text("This action cannot be undone.", NamedTextColor.RED) : null).asGuiItem());
    }
}
