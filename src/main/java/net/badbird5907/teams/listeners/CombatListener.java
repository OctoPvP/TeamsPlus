package net.badbird5907.teams.listeners;

import net.badbird5907.teams.manager.PlayerManager;
import net.badbird5907.teams.object.Lang;
import net.badbird5907.teams.object.PlayerData;
import net.badbird5907.teams.object.PvPCheckResult;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

public class CombatListener implements Listener {
    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
            Player victim = (Player) event.getEntity(), attacker = (Player) event.getDamager();
            PlayerData attackerData = PlayerManager.getData(attacker.getUniqueId());
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
