package dev.badbird.teams.manager;

import dev.badbird.teams.hooks.impl.*;
import net.badbird5907.blib.util.Logger;
import dev.badbird.teams.TeamsPlus;
import dev.badbird.teams.hooks.Hook;
import org.bukkit.Bukkit;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class HookManager {
    private static final Hook[] hooks = new Hook[]{
            new AntiCombatLogHook(),
            new VanishHook(),
            new VaultHook(),
            new CoreProtectHook(),
            new LunarClientHook()
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

    public static <T extends Hook> Optional<T> getHook(Class<T> clazz) {
        for (Hook hook : set) {
            if (hook.getClass().equals(clazz)) {
                return (Optional<T>) Optional.of(hook);
            }
        }
        return Optional.empty();
    }
}
