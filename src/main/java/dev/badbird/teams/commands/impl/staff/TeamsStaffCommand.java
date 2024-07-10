package dev.badbird.teams.commands.impl.staff;

import dev.badbird.teams.commands.annotation.AllowOffline;
import dev.badbird.teams.menu.ConfirmMenu;
import dev.badbird.teams.object.Lang;
import dev.badbird.teams.object.PlayerData;
import dev.badbird.teams.object.Team;
import dev.badbird.teams.object.TeamRank;
import dev.badbird.teams.util.Permissions;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.CommandDescription;
import org.incendo.cloud.annotations.Permission;
import org.incendo.cloud.annotations.processing.CommandContainer;

//@Command(name = "teamsstaff", description = "Manage teams")
//@Permission(Permissions.STAFF)
@CommandContainer
@Command("teamsstaff")
@CommandDescription("Manage teams")
@Permission(Permissions.STAFF)
public class TeamsStaffCommand {
    //@Command(name = "disband", aliases = "delete", description = "Disband a team")
    @Command("disband|delete <target>")
    @CommandDescription("Disband a team")
    public void disband(CommandSender sender, Team target) {
        if (sender instanceof Player player) {
            if (target != null && player.hasPermission("teamsplus.staff.disband")) {
                new ConfirmMenu("disband this team", (bool) -> {
                    player.closeInventory();
                    if (bool) {
                        target.disband();
                        player.sendMessage(Lang.STAFF_DISBAND_TEAM.toString(target.getName()));
                    } else {
                        player.sendMessage(Lang.CANCELED.toString());
                    }
                }).open(player);
            }
        } else {
            target.disband();
            sender.sendMessage(Lang.STAFF_DISBAND_TEAM.toString(target.getName()));
        }
    }

    //@Command(name = "forcejoin", description = "Force a player to join a team")
    @Command("forcejoin <target> <player>")
    @CommandDescription("Force a player to join a team")
    @Permission("teamsplus.staff.forcejoin")
    public void forcejoin(CommandSender sender, @Argument Team target, @AllowOffline @Argument PlayerData player) {
        target.join(player);
        sender.sendMessage(Lang.STAFF_FORCE_JOIN.toString(player.getName(), target.getName()));
    }

    //@Command(name = "forceleave", description = "Force a player to leave a team")
    @Command("forceleave <player>")
    @CommandDescription("Force a player to leave a team")
    @Permission("teamsplus.staff.forceleave")
    public void forceleave(CommandSender sender, @Argument @AllowOffline PlayerData player) {
        Team target = player.getPlayerTeam();
        if (target == null) {
            sender.sendMessage(Lang.TARGET_NOT_IN_TEAM.toString(player.getName()));
            return;
        }
        if (target.getOwner().equals(player.getUuid())) {
            sender.sendMessage(Lang.STAFF_FORCE_LEAVE_OWNER.toString());
            return;
        }
        target.leave(player);
        sender.sendMessage(Lang.STAFF_FORCE_LEAVE.toString(player.getName(), target.getName()));
    }

    @Command("forcerank <target> <player> <rank>")
    @CommandDescription("Force a player to a rank")
    @Permission("teamsplus.staff.forcerank")
    public void forcerank(CommandSender sender, @Argument Team target, @Argument @AllowOffline PlayerData player, @Argument TeamRank rank) {
        target.setRank(player.getUuid(), rank);
        sender.sendMessage(Lang.STAFF_FORCE_RANK.toString(player.getName(), rank.name(), target.getName()));
    }

    @Command("transfer <target> <team>")
    @CommandDescription("Transfer team ownership to another player")
    @Permission("teamsplus.staff.transfer")
    public void transfer(CommandSender sender, @Argument @AllowOffline PlayerData target, @Argument Team team) {
        // check team valid
        Team playerTeam = target.getPlayerTeam();
        if (playerTeam == null || !playerTeam.getTeamId().equals(team.getTeamId())) {
            sender.sendMessage(Lang.STAFF_FORCE_TRANSFER_TARGET_NOT_IN_TEAM.toString(target.getName()));
            return;
        }
        if (playerTeam.getOwner().equals(target.getUuid())) {
            sender.sendMessage(Lang.STAFF_FORCE_TRANSFER_TARGET_IS_ALREADY_OWNER.toString());
            return;
        }
        playerTeam.transferOwnership(target, (sender instanceof ConsoleCommandSender) ? "Console" : sender.getName());
    }

    @Command("rename <target> <newName>")
    @CommandDescription("Rename a team")
    @Permission("teamsplus.staff.rename")

    public void rename(CommandSender sender, @Argument Team target, @Argument String newName) {
        String oldName = target.getName();
        target.setName(newName);
        sender.sendMessage(Lang.STAFF_FORCE_RENAME.toString(oldName, newName));
    }
}
