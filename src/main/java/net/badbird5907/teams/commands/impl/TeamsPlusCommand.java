package net.badbird5907.teams.commands.impl;


import net.badbird5907.blib.util.CC;
import net.badbird5907.blib.util.PlayerUtil;
import net.badbird5907.blib.util.Tasks;
import net.badbird5907.blib.utils.StringUtils;
import net.badbird5907.teams.TeamsPlus;
import net.badbird5907.teams.commands.annotation.Sender;
import net.badbird5907.teams.hooks.Hook;
import net.badbird5907.teams.manager.HookManager;
import net.badbird5907.teams.manager.PlayerManager;
import net.badbird5907.teams.manager.TeamsManager;
import net.badbird5907.teams.object.Lang;
import net.badbird5907.teams.object.PlayerData;
import net.badbird5907.teams.object.Team;
import net.badbird5907.teams.object.TeamRank;
import net.badbird5907.teams.util.Permission;
import net.badbird5907.teams.util.UUIDUtil;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import revxrsal.commands.annotation.*;
import revxrsal.commands.bukkit.annotation.CommandPermission;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

@Command({ "teamsplus", "teams+", "team", "teams"})
public class TeamsPlusCommand {
    @Subcommand("")
    public void execute(CommandSender sender) {
        sender.sendMessage(CC.GREEN + "TeamsPlus V." + TeamsPlus.getInstance().getDescription().getVersion());
        sender.sendMessage(CC.AQUA + "For help, do /teamsplus help");
    }
    @Subcommand("create")
    @Description("Create a new team")
    @Usage("<name>")
    public void create(Player sender, String name) {
        PlayerData playerData = PlayerManager.getPlayers().get(sender.getUniqueId());
        if (playerData.getPlayerTeam() != null) {
            sender.sendMessage(Lang.ALREADY_IN_TEAM.toString());
            return;
        }
        if (TeamsPlus.getApi().getTeamsManager().getTeamByName(name) != null) {
            sender.sendMessage(Lang.TEAM_ALREADY_EXISTS.toString());
            return;
        }
        if (TeamsPlus.getInstance().getConfig().getStringList("team.blocked-names").contains(name.toLowerCase())) {
            sender.sendMessage(Lang.CANNOT_CREATE_TEAM_BLOCKED_NAME.toString());
            return;
        }
        Team team = new Team(name, sender.getPlayer().getUniqueId());
        playerData.setTeamId(team.getTeamId());
        Tasks.runAsync(() -> {
            team.save();
            TeamsManager.getInstance().getTeams().add(team);
            playerData.save();
        });
        sender.sendMessage(Lang.CREATED_TEAM.toString(team.getName()));
        return;
    }
    @Subcommand("ally")
    @Description("Aly a player or team")
    @Usage("<target>")
    public void ally(PlayerData sender, Team targetTeam) {
        /*
        List<String> args = Arrays.asList(arg1);
        boolean self = args.contains("self"), player = args.contains("player");
        String target = args.get(args.size() - 1);
        if (self && player) {
            sender.sendMessage(Lang.CANNOT_ALLY_PLAYER.toString());
            return;
        }
        if (self) {
            Team targetTeam = TeamsManager.getInstance().getTeamByName(target);
            if (targetTeam == null) {
                sender.sendMessage(Lang.TEAM_DOES_NOT_EXIST.toString());
                return;
            }
            sender.askAlly(targetTeam);
        } else {
            if (player) {
                if (!sender.isInTeam()) {
                    sender.sendMessage(Lang.MUST_BE_IN_TEAM.toString());
                    return;
                }
                PlayerData targetData = PlayerManager.getData(target);
                if (targetData == null) {
                    sender.sendMessage(Lang.PLAYER_NOT_FOUND.toString());
                    return;
                }
                sender.getPlayerTeam().requestToAllyPlayer(targetData);
                return;
            }

            //moved from here

        }
         */
        Team selfTeam = sender.getPlayerTeam();
        if (selfTeam == null) {
            sender.sendMessage(Lang.MUST_BE_IN_TEAM.toString());
            return;
        }
        if (selfTeam == targetTeam) {
            sender.sendMessage(Lang.CANNOT_ALLY_SELF.toString());
            return;
        }
        if (targetTeam == null) {
            sender.sendMessage(Lang.TEAM_DOES_NOT_EXIST.toString());
            return;
        }
        if (UUIDUtil.contains(targetTeam.getAllyRequests(), selfTeam.getTeamId())) {
            sender.sendMessage(Lang.ALREADY_SENT_ALLY_REQUEST.toString(targetTeam.getName()));
            return;
        }
        selfTeam.requestToAllyAnotherTeam(targetTeam);
    }
    @Subcommand({ "invite", "inv" })
    @Description("Invite a player to your team")
    @Usage("<player>")
    public void invite(Player sender, PlayerData targetData) {
        Team senderTeam = TeamsPlus.getInstance().getTeamsManager().getPlayerTeam(sender.getPlayer().getUniqueId());
        if (senderTeam == null) {
            sender.sendMessage(Lang.MUST_BE_IN_TEAM.toString());
            return;
        }
        if (targetData == null) {
            sender.sendMessage(Lang.PLAYER_NOT_FOUND.toString());
            return;
        }
        if (targetData.getPendingInvites().containsKey(senderTeam.getTeamId())) {
            sender.sendMessage(Lang.INVITE_ALREADY_SENT.toString(targetData.getName()));
        } else {
            targetData.invite(senderTeam, sender.getName());
        }
        return;
    }
    @Subcommand({"join","accept","jointeam"})
    @Usage("<team>")
    @Description("Join a team")
    public void join(Player sender, Team targetTeam) {
        PlayerData data = PlayerManager.getData(sender);
        if (data.isInTeam()) {
            sender.sendMessage(Lang.ALREADY_IN_TEAM.toString());
            return;
        }
        if (targetTeam == null) {
            sender.sendMessage(Lang.TEAM_DOES_NOT_EXIST.toString());
            return;
        }
        if (data.getPendingInvites().get(targetTeam.getTeamId()) != null) {
            data.getPendingInvites().remove(targetTeam.getTeamId());
            data.joinTeam(targetTeam);
        } else {
            sender.sendMessage(Lang.NO_INVITE.toString(targetTeam.getName()));
        }
        return;
    }
    @Subcommand("leave")
    @Description("Leave a team")
    public void leaveTeam(Player sender) {
        PlayerData playerData = PlayerManager.getData(sender);
        if (playerData.getPlayerTeam() == null) {
            sender.sendMessage(Lang.MUST_BE_IN_TEAM.toString());
            return;
        }
        playerData.leaveTeam();
    }
    @Subcommand("rename")
    @Description("Rename your team")
    @Usage("<name>")
    public void rename(Player sender, Team team, String name) {
        if (TeamsPlus.getInstance().getConfig().getStringList("team.blocked-names").contains(name.toLowerCase())) {
            sender.sendMessage(Lang.CANNOT_CREATE_TEAM_BLOCKED_NAME.toString());
            return;
        }
        team.rename(sender, name);
    }  @Subcommand("reload")
    @CommandPermission(Permission.RELOAD)
    @Description("Reload config files")
    public void reload(Player sender) {
        sender.sendMessage(CC.GREEN + "Reloading configuration files...");
        long start = System.currentTimeMillis();
        TeamsPlus.getInstance().reloadConfig();
        TeamsPlus.reloadLang();
        for (Hook hook : HookManager.getHooks()) {
            hook.reload();
        }
        sender.sendMessage(CC.GREEN + "Configuration files reloaded in " + CC.GOLD + (System.currentTimeMillis() - start) + CC.GREEN + "ms");
    }
    @Subcommand({"info","whois"})
    @Description("Get information about a team")
    @Usage("[team]")
    public void info(Player sender, @Optional String target) {
        if (target == null || target.isEmpty()) {
            Team team = TeamsPlus.getInstance().getTeamsManager().getPlayerTeam(sender.getPlayer().getUniqueId());
            if (team == null) {
                sender.sendMessage(Lang.MUST_BE_IN_TEAM.toString());
                return;
            }
            sendTeamInfo(sender, team);
            return;
        }
        Team targetTeam = TeamsPlus.getInstance().getTeamsManager().getTeamByName(target);
        if (targetTeam == null) {
            sender.sendMessage(Lang.TEAM_DOES_NOT_EXIST.toString());
            return;
        }
        sendTeamInfo(sender, targetTeam);
    }
    public static void sendTeamInfo(CommandSender sender, Team targetTeam) {
        OfflinePlayer owner = Bukkit.getOfflinePlayer(targetTeam.getOwner());
        String enemies;
        if (targetTeam.getSettings().isShowEnemies()) {
            StringBuilder sb = new StringBuilder();
            targetTeam.getEnemiedTeams().forEach((uuid, level, name) -> sb.append(Lang.TEAM_INFO_ENEMIED_TEAM_ENTRY.toString(name)));
            targetTeam.getEnemiedPlayers().forEach(((uuid, enemyLevel) -> sb.append(Lang.TEAM_INFO_ENEMIED_PLAYER_ENTRY.toString(PlayerUtil.getPlayerName(uuid)))));
            enemies = StringUtils.replacePlaceholders(Lang.TEAM_INFO_ENEMIES_LIST.toString((targetTeam.getEnemiedTeams().size() + targetTeam.getEnemiedPlayers().size()), sb.toString()));
        } else {
            enemies = CC.GREEN + targetTeam.getEnemiedTeams().size() + targetTeam.getEnemiedPlayers().size();
        }
        String allies;
        if (targetTeam.getSettings().isShowAllies()) {
            StringBuilder sb = new StringBuilder();
            targetTeam.getAlliedTeams().forEach((uuid, name) -> sb.append(Lang.TEAM_INFO_ALLIES_TEAM_ENTRY.toString(name)));
            targetTeam.getAlliedPlayers().forEach((uuid, name) -> sb.append(Lang.TEAM_INFO_ALLIES_PLAYER_ENTRY.toString(name)));
            allies = StringUtils.replacePlaceholders(Lang.TEAM_INFO_ALLIES_LIST.toString((targetTeam.getAlliedTeams().size() + targetTeam.getAlliedPlayers().size()), sb.toString()));
        } else allies = CC.GREEN + (targetTeam.getAlliedTeams().size() + targetTeam.getAlliedPlayers().size());
        String members;
        int membersAll = targetTeam.getMembers().size();
        AtomicInteger membersOnline = new AtomicInteger();
        StringBuilder sb = new StringBuilder();
        int a = 0;
        for (Map.Entry<UUID, TeamRank> entry : targetTeam.getMembers().entrySet()) {
            a++;
            UUID uuid = entry.getKey();
            TeamRank rank = entry.getValue();
            if (Bukkit.getPlayer(uuid) != null) {
                membersOnline.getAndIncrement();
                sb.append((a != 1 ? Lang.TEAM_INFO_MEMBER_ENTRY_SEPARATOR : "")).append(Lang.TEAM_INFO_ONLINE_MEMBER_ENTRY.toString(PlayerUtil.getPlayerName(uuid)));
            } else {
                sb.append((a != 1 ? Lang.TEAM_INFO_MEMBER_ENTRY_SEPARATOR : "")).append(Lang.TEAM_INFO_OFFLINE_MEMBER_ENTRY.toString(PlayerUtil.getPlayerName(uuid)));
            }
        }
        members = Lang.TEAM_INFO_MEMBERS_LIST.toString(membersOnline, membersAll, sb.toString());
        String message = Lang.TEAM_INFO_MESSAGE.toString(targetTeam.getName(), owner.getName(), allies, enemies, members);
        sender.sendMessage(CC.translate(message));
    }
}
