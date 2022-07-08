package net.badbird5907.teams.menu;

import net.badbird5907.blib.menu.buttons.Button;
import net.badbird5907.blib.menu.menu.Menu;
import net.badbird5907.blib.util.ItemBuilder;
import net.badbird5907.blib.util.Logger;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

public class TestMenu extends Menu {
    @Override
    public List<Button> getButtons(Player player) {
        return Arrays.asList(new Button() {
            @Override
            public ItemStack getItem(Player player) {
                return new ItemBuilder(Material.LEVER)
                        .name("Test")
                        .build();
            }

            @Override
            public int getSlot() {
                return 0;
            }

            @Override
            public void onClick(Player player, int slot, ClickType clickType, InventoryClickEvent event) {
                Logger.debug("h");
                player.sendMessage("Click");
            }
        });
    }

    @Override
    public String getName(Player player) {
        return "Test";
    }
}
