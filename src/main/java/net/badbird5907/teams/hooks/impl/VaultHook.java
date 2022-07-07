package net.badbird5907.teams.hooks.impl;

import net.badbird5907.blib.util.CC;
import net.badbird5907.teams.TeamsPlus;
import net.badbird5907.teams.hooks.Hook;
import net.milkbowl.vault.chat.Chat;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServiceRegisterEvent;
import org.bukkit.event.server.ServiceUnregisterEvent;

public class VaultHook extends Hook implements Listener {
    private Chat vaultChat;

    public VaultHook() {
        super("Vault");
    }

    private void refreshVault() {
        Chat vaultChat = Bukkit.getServer().getServicesManager().load(Chat.class);
        if (vaultChat != this.vaultChat) {
            TeamsPlus.getInstance().getLogger().info("New Vault Chat implementation registered: " + (vaultChat == null ? "null" : vaultChat.getName()));
        }
        this.vaultChat = vaultChat;
    }

    @Override
    public void init(TeamsPlus plugin) {
        refreshVault();
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onServiceChange(ServiceRegisterEvent e) {
        if (e.getProvider().getService() == Chat.class) {
            refreshVault();
        }
    }

    @EventHandler
    public void onServiceChange(ServiceUnregisterEvent e) {
        if (e.getProvider().getService() == Chat.class) {
            refreshVault();
        }
    }

    public String getFormattedName(Player player) {
        return CC.translate(vaultChat.getPlayerPrefix(player) + " " + player.getDisplayName());
    }

    @Override
    public void disable(TeamsPlus plugin) {

    }
}
