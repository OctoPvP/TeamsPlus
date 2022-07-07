package net.badbird5907.teams.commands.impl.managment;

import com.google.common.collect.Lists;
import net.badbird5907.blib.util.CC;
import net.badbird5907.blib.util.PlayerUtil;
import net.badbird5907.blib.util.Tasks;
import net.badbird5907.blib.utils.StringUtils;
import net.badbird5907.teams.TeamsPlus;
import net.badbird5907.teams.commands.CommandManager;
import net.badbird5907.teams.commands.annotation.TeamPermission;
import net.badbird5907.teams.hooks.Hook;
import net.badbird5907.teams.hooks.impl.VanishHook;
import net.badbird5907.teams.manager.HookManager;
import net.badbird5907.teams.manager.PlayerManager;
import net.badbird5907.teams.manager.TeamsManager;
import net.badbird5907.teams.menu.ConfirmMenu;
import net.badbird5907.teams.object.Lang;
import net.badbird5907.teams.object.PlayerData;
import net.badbird5907.teams.object.Team;
import net.badbird5907.teams.object.TeamRank;
import net.badbird5907.teams.util.Permissions;
import net.octopvp.commander.annotation.Optional;
import net.octopvp.commander.annotation.*;
import net.octopvp.commander.bukkit.BukkitCommandSender;
import net.octopvp.commander.bukkit.annotation.PlayerOnly;
import net.octopvp.commander.sender.CoreCommandSender;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Command(name = "teams", aliases = {"teamsplus", "team"}, description = "Main TeamsPlus command")
public class TeamsCommand {

    private static final long TEAM_CACHE_TIME = 5 * 1000;
    private static final int MAX_PAGE_SIZE = 15;
    private long lastList = -1;
    private Map<Integer, String> listCache;

    public static void sendTeamInfo(CommandSender sender, Team targetTeam) {
        OfflinePlayer owner = Bukkit.getOfflinePlayer(targetTeam.getOwner());
        String enemies;
        if (targetTeam.getSettings().isShowEnemies()) {
            StringBuilder sb = new StringBuilder();
            targetTeam.getEnemiedTeams().forEach((uuid, name) -> sb.append(Lang.TEAM_INFO_ENEMIES_TEAM_ENTRY.toString(name)));
            enemies = StringUtils.replacePlaceholders(Lang.TEAM_INFO_ENEMIES_LIST.toString((targetTeam.getEnemiedTeams().size()), sb.toString()));
        } else {
            enemies = CC.GREEN + targetTeam.getEnemiedTeams().size();
        }
        String allies;
        if (targetTeam.getSettings().isShowAllies()) {
            StringBuilder sb = new StringBuilder();
            targetTeam.getAlliedTeams().forEach((uuid, name) -> sb.append(Lang.TEAM_INFO_ALLIES_TEAM_ENTRY.toString(name)));
            allies = StringUtils.replacePlaceholders(Lang.TEAM_INFO_ALLIES_LIST.toString((targetTeam.getAlliedTeams().size()), sb.toString()));
        } else allies = CC.GREEN + (targetTeam.getAlliedTeams().size());
        String members;
        int membersAll = targetTeam.getMembers().size();
        AtomicInteger membersOnline = new AtomicInteger();
        StringBuilder sb = new StringBuilder();
        int a = 0;
        for (Map.Entry<UUID, TeamRank> entry : targetTeam.getMembers().entrySet()) {
            a++;
            UUID uuid = entry.getKey();
            TeamRank rank = entry.getValue();
            Player player = Bukkit.getPlayer(uuid);
            if (player != null && !VanishHook.isVanished(player)) {
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

    @Command(name = "reload", description = "Reload the configuration files")
    @Permission(Permissions.RELOAD)
    public void reload(@Sender Player sender) {
        sender.sendMessage(CC.GREEN + "Reloading configuration files...");
        long start = System.currentTimeMillis();
        TeamsPlus.getInstance().reloadConfig();
        TeamsPlus.reloadLang();
        for (Hook hook : HookManager.getHooks()) {
            hook.reload();
        }
        sender.sendMessage(CC.GREEN + "Configuration files reloaded in " + CC.GOLD + (System.currentTimeMillis() - start) + CC.GREEN + "ms");
    }

    @Command(name = "plugininfo", description = "TeamsPlus Info")
    public void plugininfo(@Sender CommandSender sender) {
        sender.sendMessage(CC.GREEN + "TeamsPlus V." + TeamsPlus.getInstance().getDescription().getVersion());
        sender.sendMessage(CC.AQUA + "For help, do /teamsplus help");

    }

    @Command(name = "rename", description = "Rename your team")
    @PlayerOnly
    @Cooldown(30)
    @TeamPermission(TeamRank.ADMIN)
    public void rename(@Sender Player sender, @Sender Team team, String name) {
        int max = TeamsPlus.getInstance().getConfig().getInt("max-name-length", 16);
        if (max < name.length()) {
            sender.sendMessage(Lang.TEAM_NAME_TOO_LONG.toString(max));
            return;
        }
        if (TeamsPlus.getInstance().getTeamsManager().getTeamByName(name) != null) {
            sender.sendMessage(Lang.TEAM_ALREADY_EXISTS.toString());
            return;
        }
        if (TeamsPlus.getInstance().getConfig().getStringList("team.blocked-names").contains(name.toLowerCase())) {
            sender.sendMessage(Lang.CANNOT_CREATE_TEAM_BLOCKED_NAME.toString());
            return;
        }
        team.rename(sender, name);
    }

    @Command(name = "leave", description = "Leave a team")
    @Cooldown(5)
    public void leaveTeam(@Sender Player sender) {
        PlayerData playerData = PlayerManager.getData(sender);
        if (playerData.getPlayerTeam() == null) {
            sender.sendMessage(Lang.MUST_BE_IN_TEAM.toString());
            return;
        }
        playerData.leaveTeam();
    }

    @Command(name = "join", aliases = {"accept", "jointeam"}, description = "Join a team")
    public void join(@Sender Player sender, Team targetTeam) {
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
    }

    /*
    @Completer(name = "join", index = 0)
    public List<String> completer(@Sender CoreCommandSender s, String input, String lastArg) {
        BukkitCommandSender sender = (BukkitCommandSender) s;
        if (sender.isPlayer()) {
            PlayerData data = PlayerManager.getData(sender.getPlayer());
            if (data != null) {
                List<String> toReturn = new ArrayList<>();
                data.getPendingInvites().forEach((k, v) -> {
                    Team team = TeamsManager.getInstance().getTeamById(k);
                    if (team != null) {
                        toReturn.add(team.getName());
                    }
                });
                return toReturn;
            }
        }
        return Lists.newArrayList();
        //return CommandManager.getCommander().getArgumentProviders().get(Team.class).provideSuggestions(input, lastArg, s);
    }
     */


    @Command(name = "create", description = "Create a new team")
    public void create(@Sender Player sender, String name) {
        PlayerData playerData = PlayerManager.getPlayers().get(sender.getUniqueId());
        if (playerData.getPlayerTeam() != null) {
            sender.sendMessage(Lang.ALREADY_IN_TEAM.toString());
            return;
        }
        int max = TeamsPlus.getInstance().getConfig().getInt("max-name-length", 16);
        if (max < name.length()) {
            sender.sendMessage(Lang.TEAM_NAME_TOO_LONG.toString(max));
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
        Team team = new Team(name, sender.getUniqueId());
        playerData.setTeamId(team.getTeamId());
        Tasks.runAsync(() -> {
            team.save();
            TeamsManager.getInstance().getTeams().add(team);
            playerData.save();
        });
        sender.sendMessage(Lang.CREATED_TEAM.toString(team.getName()));
        return;
    }


    @Command(name = "info", aliases = "who", description = "Get information about a team, use -p to get information about a player's team")
    public void info(@Sender Player sender, @Optional String target, @Switch(value = "p", aliases = "player") boolean player) {
        if (target == null || target.isEmpty()) {
            Team team = TeamsPlus.getInstance().getTeamsManager().getPlayerTeam(sender.getUniqueId());
            if (team == null) {
                sender.sendMessage(Lang.MUST_BE_IN_TEAM.toString());
                return;
            }
            sendTeamInfo(sender, team);
            return;
        }
        Team targetTeam;
        if (player) {
            OfflinePlayer targetPlayer = Bukkit.getOfflinePlayer(target);
            targetTeam = TeamsPlus.getInstance().getTeamsManager().getPlayerTeam(targetPlayer.getUniqueId());
            if (targetTeam == null) {
                sender.sendMessage(Lang.PLAYER_NOT_IN_TEAM.toString(targetPlayer.getName()));
                return;
            }
        } else targetTeam = TeamsPlus.getInstance().getTeamsManager().getTeamByName(target);
        if (targetTeam == null) {
            sender.sendMessage(Lang.TEAM_DOES_NOT_EXIST.toString());
            return;
        }
        sendTeamInfo(sender, targetTeam);
    }

    @Command(name = "disband", aliases = "delete", description = "Disband a team")
    @TeamPermission(TeamRank.OWNER)
    public void disband(@Sender Player player, @Sender Team team) {
        new ConfirmMenu("disband your team", (response) -> {
            if (response) {
                team.disband();
            } else player.sendMessage(Lang.CANCELED.toString());
            player.closeInventory();
        }).open(player);
    }

    @Command(name = "list", description = "List all teams")
    public void list(@Sender CommandSender sender, @Optional @DefaultNumber(1) int page) { //fuck this... :/
        page = page - 1;
        if (listCache != null && lastList + TEAM_CACHE_TIME > System.currentTimeMillis()) {
            if (page >= listCache.size()) {
                sender.sendMessage(Lang.INVALID_ENTRY_NUMBER.toString(1, listCache.size()));
                return;
            }
            if (page < 0) page = 0;
            sender.sendMessage(listCache.get(page));
            return;
        }
        lastList = System.currentTimeMillis();
        StringBuilder sb = new StringBuilder();
        sb.append(Lang.LIST_HEADER)
                .append("\n")
                .append(Lang.LIST_TITLE);
        int current = 0;
        Map<Integer, String> pages = new HashMap<>();
        for (Team team : TeamsManager.getInstance().getTeams()) {
            current++;
            sb.append("\n").append(Lang.LIST_ENTRY.toString(team.getName()));
            if (current % MAX_PAGE_SIZE == 0) {
                sb.append("\n").append(Lang.LIST_FOOTER);
                pages.put(pages.size(), sb.toString());
                sb = new StringBuilder(Lang.LIST_HEADER.toString());
                sb.append("\n")
                        .append(Lang.LIST_TITLE);
                current = 0;
            }
        }
        if (current > 0) {
            sb.append("\n").append(Lang.LIST_FOOTER);
            pages.put(pages.size(), sb.toString());
        }
        int totalPages = pages.size();
        //go through all pages and replace %page% with the page number
        Map<Integer, String> finalPages = new HashMap<>();
        pages.forEach((pageNumber, content) -> {
            String finalContent = content.replace("%page%", String.valueOf(pageNumber + 1))
                    .replace("%max_pages%", String.valueOf(totalPages));
            finalPages.put(pageNumber, finalContent);
        });

        listCache = finalPages;
        if (page >= finalPages.size()) {
            sender.sendMessage(Lang.INVALID_ENTRY_NUMBER.toString(1, listCache.size()));
            return;
        }
        sender.sendMessage(finalPages.get(page));
    }
}
