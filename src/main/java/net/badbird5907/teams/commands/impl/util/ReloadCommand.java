package net.badbird5907.teams.commands.impl.util;

import com.jonahseguin.drink.annotation.Command;
import com.jonahseguin.drink.annotation.Require;
import com.jonahseguin.drink.annotation.Sender;
import net.badbird5907.blib.util.CC;
import net.badbird5907.teams.TeamsPlus;
import net.badbird5907.teams.hooks.Hook;
import net.badbird5907.teams.manager.HookManager;
import net.badbird5907.teams.util.Permission;
import org.bukkit.entity.Player;

public class ReloadCommand {
    @Command(name = "reload", desc = "Reload the configuration files")
    @Require(Permission.RELOAD)
    public void reload(@Sender Player sender) {
        sender.sendMessage(CC.GREEN + "Reloading configuration files...");
        long start = System.currentTimeMillis();
        TeamsPlus.getInstance().reloadConfig();
        TeamsPlus.reloadLang();
        for (Hook hook : HookManager.getHooks()) {
            hook.reload();
        }
        sender.sendMessage(CC.GREEN + "Configuration files reloaded in " + CC.GOLD + (System.currentTimeMillis() - start) + CC.GREEN + "ms");
    }
}
