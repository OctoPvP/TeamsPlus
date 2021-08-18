package net.badbird5907.teams.runnable;

import net.badbird5907.teams.TeamsPlus;
import net.badbird5907.teams.manager.PlayerManager;
import net.badbird5907.teams.object.Team;
import org.bukkit.scheduler.BukkitRunnable;

public class DataUpdateRunnable extends BukkitRunnable {
    @Override
    public void run() {
        PlayerManager.getPlayers().forEach((uuid, playerData) -> playerData.update());
        for (Team team : TeamsPlus.getInstance().getTeamsManager().getTeams()) {
            team.update();
        }
    }
}
