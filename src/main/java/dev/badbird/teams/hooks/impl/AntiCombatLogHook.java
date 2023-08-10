package dev.badbird.teams.hooks.impl;

import lombok.Getter;
import lombok.SneakyThrows;
import net.badbird5907.anticombatlog.api.events.CombatTagEvent;
import dev.badbird.teams.TeamsPlus;
import dev.badbird.teams.hooks.Hook;
import dev.badbird.teams.object.Team;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class AntiCombatLogHook extends Hook implements Listener {
    @Getter
    private static AntiCombatLogHook instance;

    private static YamlConfiguration antiCombatLogConfig;

    public AntiCombatLogHook() {
        super("AntiCombatLog");
    }


    @SneakyThrows
    @Override
    public void init(TeamsPlus plugin) {
        instance = new AntiCombatLogHook();
        reload();

        Bukkit.getPluginManager().registerEvents(this, plugin);

    }

    @SneakyThrows
    @Override
    public void reload() {
        super.reload();
        File file = new File(TeamsPlus.getInstance().getDataFolder() + "/hooks/AntiCombatLog.yml");
        if (!file.getParentFile().exists())
            file.getParentFile().mkdirs();
        if (!file.exists()) {
            Files.copy(TeamsPlus.getInstance().getResource("AntiCombatLog.yml"), file.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
        antiCombatLogConfig = new YamlConfiguration();
        antiCombatLogConfig.load(file);
    }

    @Override
    public void disable(TeamsPlus plugin) {
    }

    @EventHandler
    public void onCombatTag(CombatTagEvent event) {
        if (antiCombatLogConfig.getBoolean("prevent-team-combat-tag", true)) {
            Team victimTeam = TeamsPlus.getInstance().getTeamsManager().getPlayerTeam(event.getVictim().getUniqueId()), attackerTeam = TeamsPlus.getInstance().getTeamsManager().getPlayerTeam(event.getAttacker().getUniqueId());
            if (victimTeam == null || attackerTeam == null)
                return;
            if (victimTeam.getTeamId().toString().equalsIgnoreCase(attackerTeam.getTeamId().toString())) {
                event.setCancelled(true);
            }
        }
    }
}
