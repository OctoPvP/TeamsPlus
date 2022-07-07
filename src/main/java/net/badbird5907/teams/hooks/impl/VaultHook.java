package net.badbird5907.teams.hooks.impl;

import net.badbird5907.teams.TeamsPlus;
import net.badbird5907.teams.hooks.Hook;
import net.milkbowl.vault.chat.Chat;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

public class VaultHook extends Hook {
    public VaultHook() {
        super("Vault");
    }

    private static Chat chat;

    @Override
    public void init(TeamsPlus plugin) {
        RegisteredServiceProvider<Chat> rsp = plugin.getServer().getServicesManager().getRegistration(Chat.class);
        chat = rsp.getProvider();
    }

    public String getFormattedName(Player player) {
        return chat.getPlayerPrefix(player);
    }

    @Override
    public void disable(TeamsPlus plugin) {

    }
}
