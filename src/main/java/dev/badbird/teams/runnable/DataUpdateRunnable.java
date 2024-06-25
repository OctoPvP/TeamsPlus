package dev.badbird.teams.runnable;

import dev.badbird.teams.TeamsPlus;
import dev.badbird.teams.manager.PlayerManager;
import dev.badbird.teams.manager.StorageManager;
import dev.badbird.teams.manager.TeamsManager;
import dev.badbird.teams.object.Team;
import org.bukkit.scheduler.BukkitRunnable;

public class DataUpdateRunnable extends BukkitRunnable {
    // private static final long SAVE_INTERVAL = 60000; // 1 minute
    // private static long lastSave = System.currentTimeMillis();

    @Override
    public void run() {
        PlayerManager.getPlayers().forEach((uuid, playerData) -> playerData.update());
        TeamsManager.getInstance().getTeams().forEach((_unused, team) -> team.update());
        /*
        if (System.currentTimeMillis() - lastSave > SAVE_INTERVAL) {
            lastSave = System.currentTimeMillis();
            TeamsManager.getInstance().saveTeams(StorageManager.getStorageHandler());
        }
         */
    }
}
