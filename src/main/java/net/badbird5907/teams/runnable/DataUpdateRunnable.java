package net.badbird5907.teams.runnable;

import net.badbird5907.teams.TeamsPlus;
import net.badbird5907.teams.manager.PlayerManager;
import net.badbird5907.teams.manager.StorageManager;
import net.badbird5907.teams.manager.TeamsManager;
import net.badbird5907.teams.object.Team;
import org.bukkit.scheduler.BukkitRunnable;

public class DataUpdateRunnable extends BukkitRunnable {
    private static long lastSave = System.currentTimeMillis();
    private static final long SAVE_INTERVAL = 60000; // 1 minute
    @Override
    public void run() {
        PlayerManager.getPlayers().forEach((uuid, playerData) -> playerData.update());
        for (Team team : TeamsPlus.getInstance().getTeamsManager().getTeams()) {
            team.update();
        }

        if (System.currentTimeMillis() - lastSave > SAVE_INTERVAL) {
            lastSave = System.currentTimeMillis();
            TeamsManager.getInstance().saveTeams(StorageManager.getStorageHandler());
        }
    }
}
