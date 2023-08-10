package dev.badbird.teams.listeners;

import dev.badbird.teams.manager.PlayerManager;
import dev.badbird.teams.object.Lang;
import dev.badbird.teams.object.PlayerData;
import dev.badbird.teams.object.PvPCheckResult;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;

public class CombatListener implements Listener {
    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
            Player victim = (Player) event.getEntity(), attacker = (Player) event.getDamager();
            PlayerData attackerData = PlayerManager.getData(attacker.getUniqueId());
            if (attackerData == null || PlayerManager.getData(victim.getUniqueId()) == null) {
                return;
            }
            PvPCheckResult checkResult = attackerData.canDamage(victim);
            if (checkResult != PvPCheckResult.ALLOWED) {
                switch (checkResult) {
                    case DISALLOW_ALLY -> {
                        event.setCancelled(true);
                        attacker.sendMessage(Lang.ALLY_PVP_DISALLOW.toString(victim.getName()));
                    }
                    case DISALLOW_TEAM -> {
                        event.setCancelled(true);
                        attacker.sendMessage(Lang.TEAM_PVP_DISALLOW.toString(victim.getName()));
                    }
                    case DISALLOW_OTHER -> {
                        event.setCancelled(true);
                    }
                    default -> {
                    }
                }
            }
        }
    }

    @EventHandler
    public void onDamage(ProjectileHitEvent event) {
        Projectile projectile = event.getEntity();
        if (event.getHitEntity() instanceof Player && projectile.getShooter() instanceof Player) {
            Player victim = (Player) event.getHitEntity(), attacker = (Player) projectile.getShooter();
            PlayerData attackerData = PlayerManager.getData(attacker.getUniqueId());
            if (attackerData == null || PlayerManager.getData(victim.getUniqueId()) == null) {
                return;
            }
            PvPCheckResult checkResult = attackerData.canDamage(victim);
            if (checkResult != PvPCheckResult.ALLOWED) {
                switch (checkResult) {
                    case DISALLOW_ALLY -> {
                        event.setCancelled(true);
                        attacker.sendMessage(Lang.ALLY_PVP_DISALLOW.toString(victim.getName()));
                    }
                    case DISALLOW_TEAM -> {
                        event.setCancelled(true);
                        attacker.sendMessage(Lang.TEAM_PVP_DISALLOW.toString(victim.getName()));
                    }
                    case DISALLOW_OTHER -> {
                        event.setCancelled(true);
                    }
                    default -> {
                    }
                }
            }
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        if (event.getEntity().getKiller() != null) {
            PlayerData attackerData = PlayerManager.getData(event.getEntity().getKiller().getUniqueId());
            if (attackerData != null)
                attackerData.onKill(event.getEntity());
        }
    }
}
