package net.badbird5907.teams.manager;

import net.badbird5907.blib.util.Logger;
import net.badbird5907.teams.TeamsPlus;
import net.badbird5907.teams.hooks.Hook;
import net.badbird5907.teams.hooks.impl.AntiCombatLogHook;
import org.bukkit.Bukkit;

import java.util.HashSet;
import java.util.Set;

public class HookManager {
    private static Hook[] hooks = new Hook[]{
            new AntiCombatLogHook()
    };
    private static Set<Hook> set = new HashSet<>();
    public static void init(){
        for (Hook hook : hooks) {
            if (Bukkit.getPluginManager().isPluginEnabled(hook.getPlugin())){
                hook.init(TeamsPlus.getInstance());
                set.add(hook); //disable using the set so we dont cause any NPEs/errors
            }
        }
        Logger.info("Hooked into %s plugins.",set.size());
    }
    public static void disable(){
        set.forEach(hook -> hook.disable(TeamsPlus.getInstance())); //should have used a method refrence but i'm too lazy
    }
}
