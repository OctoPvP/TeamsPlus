package dev.badbird.teams.commands.impl.managment;

import dev.badbird.teams.TeamsPlus;
import dev.badbird.teams.commands.annotation.TeamPermission;
import dev.badbird.teams.object.Lang;
import dev.badbird.teams.object.PlayerData;
import dev.badbird.teams.object.Team;
import dev.badbird.teams.object.TeamRank;
import dev.badbird.teams.util.UUIDUtil;
import net.octopvp.commander.annotation.*;
import net.octopvp.commander.bukkit.annotation.PlayerOnly;
import org.bukkit.entity.Player;

@Command(name = "teams", aliases = {"teamsplus", "team"})
@SecondaryParent
public class TeamRelationsCommand {

    @Command(name = "enemy", description = "Enemy another team.")
    @Cooldown(10)
    @TeamPermission(TeamRank.ADMIN)
    public void enemyTeam(@Sender Player sender, @Sender @Required Team senderTeam, @Required Team target) {
        if (senderTeam.getTeamId().equals(target.getTeamId())) {
            sender.sendMessage(Lang.CANNOT_ENEMY_SELF.toString());
            return;
        }
        senderTeam.enemyTeam(target, true);
    }

    @Command(name = "neutral", description = "Become neutral with another team.")
    @Cooldown(10)
    @TeamPermission(TeamRank.ADMIN)
    public void neutralTeam(@Sender Player sender, @Sender @Required Team senderTeam, @Required Team target) {
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

    @Command(name = "ally", description = "Ally a team")
    @PlayerOnly
    @Cooldown(10)
    @TeamPermission(TeamRank.ADMIN)
    public void ally(@Sender PlayerData sender, Team team) {
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
        if (UUIDUtil.contains(team.getAllyRequests(), selfTeam.getTeamId())) {
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
