package net.badbird5907.teams.manager;

import net.badbird5907.blib.util.Logger;
import net.badbird5907.teams.TeamsPlus;
import net.badbird5907.teams.hooks.Hook;
import net.badbird5907.teams.hooks.impl.AntiCombatLogHook;
import net.badbird5907.teams.hooks.impl.VanishHook;
import net.badbird5907.teams.hooks.impl.VaultHook;
import org.bukkit.Bukkit;

import java.util.HashSet;
import java.util.Set;

public class HookManager {
    private static final Hook[] hooks = new Hook[]{
            new AntiCombatLogHook(),
            new VanishHook(),
            new VaultHook()
    };
    private static final Set<Hook> set = new HashSet<>();

    public static void init() {
        for (Hook hook : hooks) {
            if (!hook.getPlugin().isEmpty()) {
                if (Bukkit.getPluginManager().isPluginEnabled(hook.getPlugin())) {
                    Logger.info("Hooking into " + hook.getPlugin());
                    hook.init(TeamsPlus.getInstance());
                    set.add(hook); //disable using the set so we dont cause any NPEs/errors
                }
            }
        }
        Logger.info("Hooked into %1 plugins.", set.size());
    }

    public static void disable() {
        set.forEach(hook -> hook.disable(TeamsPlus.getInstance()));
    }

    public static Set<Hook> getHooks() {
        return set;
    }

    public static Hook getHook(Class<? extends Hook> clazz) {
        for (Hook hook : set) {
            if (hook.getClass().equals(clazz)) {
                return hook;
            }
        }
        return null;
    }
}
