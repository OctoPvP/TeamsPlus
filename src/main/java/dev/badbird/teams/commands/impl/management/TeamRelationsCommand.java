package dev.badbird.teams.commands.impl.management;

import dev.badbird.teams.TeamsPlus;
import dev.badbird.teams.commands.annotation.Sender;
import dev.badbird.teams.commands.annotation.TeamPermission;
import dev.badbird.teams.object.Lang;
import dev.badbird.teams.object.PlayerData;
import dev.badbird.teams.object.Team;
import dev.badbird.teams.object.TeamRank;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.CommandDescription;
import org.incendo.cloud.annotations.processing.CommandContainer;
import org.incendo.cloud.processors.cooldown.annotation.Cooldown;

import java.time.temporal.ChronoUnit;

@CommandContainer
@Command("teams|team")
public class TeamRelationsCommand {

    @Command("enemy <target>")
    @CommandDescription("Enemy another team.")
    @Cooldown(duration = 10, timeUnit = ChronoUnit.SECONDS)
    @TeamPermission(TeamRank.ADMIN)
    public void enemyTeam(@Sender Player sender, @Sender Team senderTeam, @Argument Team target) {
        if (senderTeam.getTeamId().equals(target.getTeamId())) {
            sender.sendMessage(Lang.CANNOT_ENEMY_SELF.toString());
            return;
        }
        senderTeam.enemyTeam(target, true);
    }

    @Command("neutral <target>")
    @CommandDescription("Become neutral with another team.")
    @Cooldown(duration = 10, timeUnit = ChronoUnit.SECONDS)
    @TeamPermission(TeamRank.ADMIN)
    public void neutralTeam(@Sender Player sender, @Sender Team senderTeam, @Argument Team target) {
        if (senderTeam.getTeamId().equals(target.getTeamId())) {
            sender.sendMessage(Lang.CANNOT_NEUTRAL_SELF.toString());
            return;
        }
        if (!senderTeam.isEnemy(target) && !senderTeam.isAlly(target)) {
            sender.sendMessage(Lang.TEAM_ALREADY_NEUTRAL.toString(target.getName()));
            return;
        }
        senderTeam.neutralTeam(target, true);
    }

    @Command("ally <team>")
    @CommandDescription("Ally a team")
    @Cooldown(duration = 10, timeUnit = ChronoUnit.SECONDS)
    @TeamPermission(TeamRank.ADMIN)
    public void ally(@Sender PlayerData sender, @Argument Team team) {
        Team selfTeam = sender.getPlayerTeam();
        if (selfTeam == null) {
            sender.sendMessage(Lang.MUST_BE_IN_TEAM.toString());
            return;
        }
        if (team == null) {
            sender.sendMessage(Lang.TEAM_DOES_NOT_EXIST.toString());
            return;
        }
        if (selfTeam.getTeamId().equals(team.getTeamId())) {
            sender.sendMessage(Lang.CANNOT_ALLY_SELF.toString());
            return;
        }
        if (team.getAllyRequests().containsKey(selfTeam.getTeamId())) {
            sender.sendMessage(Lang.ALREADY_SENT_ALLY_REQUEST.toString(team.getName()));
            return;
        }
        if (selfTeam.isAlly(team)) {
            sender.sendMessage(Lang.ALREADY_ALLIES.toString(team.getName()));
            return;
        }
        int maxAllies = TeamsPlus.getInstance().getConfig().getInt("team.max-allies", -1);
        if (maxAllies > 0) {
            int s = selfTeam.getAlliedTeams().size();
            if (s >= maxAllies) {
                sender.sendMessage(Lang.MAX_ALLIES_REACHED.toString(team.getName(), s, maxAllies));
                return;
            }
            int a = team.getAlliedTeams().size();
            if (a >= maxAllies) {
                sender.sendMessage(Lang.MAX_ALLIES_REACHED_TARGET.toString(team.getName(), a, maxAllies));
                return;
            }
        }
        selfTeam.requestToAllyAnotherTeam(team);
    }
}
