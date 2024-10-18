package dev.badbird.teams.commands.impl.management;

import dev.badbird.teams.TeamsPlus;
import dev.badbird.teams.commands.annotation.Sender;
import dev.badbird.teams.commands.annotation.TeamPermission;
import dev.badbird.teams.hooks.Hook;
import dev.badbird.teams.hooks.impl.VanishHook;
import dev.badbird.teams.manager.HookManager;
import dev.badbird.teams.manager.PlayerManager;
import dev.badbird.teams.manager.TeamsManager;
import dev.badbird.teams.menu.ConfirmMenu;
import dev.badbird.teams.object.Lang;
import dev.badbird.teams.object.PlayerData;
import dev.badbird.teams.object.Team;
import dev.badbird.teams.object.TeamRank;
import dev.badbird.teams.util.Permissions;
import net.badbird5907.blib.util.CC;
import net.badbird5907.blib.util.PlayerUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotation.specifier.Greedy;
import org.incendo.cloud.annotation.specifier.Range;
import org.incendo.cloud.annotations.*;
import org.incendo.cloud.annotations.processing.CommandContainer;
import org.incendo.cloud.annotations.suggestion.Suggestions;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.help.result.CommandEntry;
import org.incendo.cloud.processors.cooldown.annotation.Cooldown;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static dev.badbird.teams.util.ChatUtil.tr;

@CommandContainer
@Command("team|teams")
@CommandDescription("Main TeamsPlus command")
public class TeamsCommand {

    private static final long TEAM_CACHE_TIME = 5 * 1000;
    private static final int MAX_PAGE_SIZE = 15;
    private long lastList = -1;
    private Map<Integer, List<Component>> listCache;

    @Command("help|h|? [query]")
    @CommandDescription("Get help")
    public void help(CommandSender sender, @Argument(value = "query", suggestions = "team_help_queries") @Greedy @Nullable String query) {
        TeamsPlus.getInstance().getTeamsHelp().queryCommands(query == null ? "" : query, sender);
    }

    @Command("")
    public void base(CommandSender sender) {
        help(sender, null);
    }

    @Suggestions("team_help_queries")
    public @NotNull List<@NotNull String> suggestHelpQueries(final @NotNull CommandContext<CommandSender> ctx, final @NotNull String input) {
        return TeamsPlus.getInstance().getTeamsHelp().helpHandler().queryRootIndex(ctx.sender()).entries().stream().map(CommandEntry::syntax).toList();
    }

    public static void sendTeamInfo(CommandSender sender, Team targetTeam) {
        OfflinePlayer owner = Bukkit.getOfflinePlayer(targetTeam.getOwner());
        Component allies;
        if (targetTeam.getSettings().isShowAllies()) {
            Component alliesList = Component.text("");
            for (Map.Entry<UUID, String> uuidStringEntry : targetTeam.getAlliedTeams().entrySet()) {
                alliesList = alliesList.append(Lang.TEAM_INFO_ALLIES_TEAM_ENTRY.getComponent(tr("ally", uuidStringEntry.getValue())).clickEvent(ClickEvent.runCommand("/teams info " + uuidStringEntry.getValue())));
            }
            allies = Lang.TEAM_INFO_ALLIES_LIST.getComponent(tr("total_allies", targetTeam.getAlliedTeams().size()), tr("list", alliesList));
            //StringUtils.replacePlaceholders(.toString((targetTeam.getAlliedTeams().size()), sb.toString()))
        } else allies = Component.text(targetTeam.getAlliedTeams().size()).color(NamedTextColor.GREEN);

        Component enemies;
        if (targetTeam.getSettings().isShowEnemies()) {
            Component enemiesList = Component.text("");
            for (Map.Entry<UUID, String> uuidStringEntry : targetTeam.getEnemiedTeams().entrySet()) {
                enemiesList = enemiesList.append(Lang.TEAM_INFO_ENEMIES_TEAM_ENTRY.getComponent(tr("enemy", uuidStringEntry.getValue())).clickEvent(ClickEvent.runCommand("/teams info " + uuidStringEntry.getValue())));
            }
            enemies = Lang.TEAM_INFO_ENEMIES_LIST.getComponent(tr("total_enemies", targetTeam.getEnemiedTeams().size()), tr("list", enemiesList));
            //                    StringUtils.replacePlaceholders(.toString((targetTeam.getEnemiedTeams().size()), sb.toString()))
        } else {
            enemies = Component.text(targetTeam.getEnemiedTeams().size()).color(NamedTextColor.RED);
        }

        Component members;
        int membersAll = targetTeam.getMembers().size();
        AtomicInteger membersOnline = new AtomicInteger();
        Component component = Component.text("");
        int a = 0;
        for (Map.Entry<UUID, TeamRank> entry : targetTeam.getMembers().entrySet()) {
            a++;
            UUID uuid = entry.getKey();
            TeamRank rank = entry.getValue();
            Player player = Bukkit.getPlayer(uuid);
            if (player != null && !VanishHook.isVanished(player)) { //this is so messy
                membersOnline.getAndIncrement();
                component = component.append((a != 1 ? Lang.TEAM_INFO_MEMBER_ENTRY_SEPARATOR.getComponent() : Component.text())).append(Lang.TEAM_INFO_ONLINE_MEMBER_ENTRY.getComponent(tr("member", PlayerUtil.getPlayerName(uuid)), tr("rank", rank.toString())));
            } else {
                component = component.append((a != 1 ? Lang.TEAM_INFO_MEMBER_ENTRY_SEPARATOR.getComponent() : Component.text())).append(Lang.TEAM_INFO_OFFLINE_MEMBER_ENTRY.getComponent(tr("member", PlayerUtil.getPlayerName(uuid)), tr("rank", rank.toString())));
            }
        }
        members = Lang.TEAM_INFO_MEMBERS_LIST.getComponent(tr("total_members", membersAll), tr("online_members", membersOnline.get()), tr("list", component));
        //.toString(membersOnline, membersAll, sb.toString());
        /*Component message = LegacyComponentSerializer.legacyAmpersand().deserialize(Lang.TEAM_INFO_MESSAGE.toString())
                .replaceText(TextReplacementConfig.builder()
                        .matchLiteral("%1")
                        .replacement(targetTeam.getName()).build())
                .replaceText(TextReplacementConfig.builder()
                        .matchLiteral("%2")
                        .replacement(Objects.requireNonNull(owner.getName())).build())
                .replaceText(TextReplacementConfig.builder()
                        .matchLiteral("%3")
                        .replacement(allies).build())
                .replaceText(TextReplacementConfig.builder()
                        .matchLiteral("%4")
                        .replacement(enemies).build())
                .replaceText(TextReplacementConfig.builder()
                        .matchLiteral("%5")
                        .replacement(members).build());*/
        Component message = Lang.TEAM_INFO_MESSAGE.getComponent(tr("team_name", targetTeam.getName()), tr("owner", owner.getName()), tr("allies", allies), tr("enemies", enemies), tr("members", members), tr("separator", LegacyComponentSerializer.legacySection().deserialize(CC.SEPARATOR)));
        //.toString(targetTeam.getName(), owner.getName(), allies, enemies, members);
        sender.sendMessage(message);
    }

    @Command("reload")
    @CommandDescription("Reload the configuration files")
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

    @Command("plugininfo")
    @CommandDescription("TeamsPlus Info")
    public void plugininfo(@Sender CommandSender sender) {
        sender.sendMessage(CC.GREEN + "TeamsPlus V." + TeamsPlus.getInstance().getDescription().getVersion());
        sender.sendMessage(CC.AQUA + "For help, do /teams help");
    }

    @Command("rename <team> <name>")
    @CommandDescription("Rename your team")
    @Cooldown(duration = 30, timeUnit = ChronoUnit.SECONDS)
    @TeamPermission(TeamRank.ADMIN)
    public void rename(@Sender Player sender, @Sender Team team, String name) {
        int max = TeamsPlus.getInstance().getConfig().getInt("max-name-length", 16);
        if (max < name.length()) {
            sender.sendMessage(Lang.TEAM_NAME_TOO_LONG.getComponent(tr("max", max)));
            return;
        }
        if (TeamsPlus.getInstance().getTeamsManager().getTeamByName(name) != null) {
            sender.sendMessage(Lang.TEAM_ALREADY_EXISTS.getComponent());
            return;
        }
        if (TeamsPlus.getInstance().getConfig().getStringList("team.blocked-names").contains(name.toLowerCase())) {
            sender.sendMessage(Lang.CANNOT_CREATE_TEAM_BLOCKED_NAME.getComponent());
            return;
        }
        team.rename(sender, name);
    }

    @Command("leave")
    @CommandDescription("Leave a team")
    @Cooldown(duration = 5, timeUnit = ChronoUnit.SECONDS)
    public void leaveTeam(@Sender Player sender) {
        PlayerData playerData = PlayerManager.getData(sender);
        if (playerData.getPlayerTeam() == null) {
            sender.sendMessage(Lang.MUST_BE_IN_TEAM.getComponent());
            return;
        }
        playerData.leaveTeam();
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


    @Command("create <name>")
    @CommandDescription("Create a new team")
    @Cooldown(duration = 5, timeUnit = ChronoUnit.SECONDS)
    public void create(@Sender Player sender, String name) {
        PlayerData playerData = PlayerManager.getPlayers().get(sender.getUniqueId());
        if (playerData.getPlayerTeam() != null) {
            sender.sendMessage(Lang.ALREADY_IN_TEAM.getComponent());
            return;
        }
        int max = TeamsPlus.getInstance().getConfig().getInt("max-name-length", 16);
        if (max < name.length()) {
            sender.sendMessage(Lang.TEAM_NAME_TOO_LONG.getComponent(tr("max", max)));
            return;
        }
        if (TeamsPlus.getApi().getTeamsManager().getTeamByName(name) != null) {
            sender.sendMessage(Lang.TEAM_ALREADY_EXISTS.getComponent());
            return;
        }
        if (TeamsPlus.getInstance().getConfig().getStringList("team.blocked-names").contains(name.toLowerCase())) {
            sender.sendMessage(Lang.CANNOT_CREATE_TEAM_BLOCKED_NAME.getComponent());
            return;
        }
        Team team = new Team(name, sender.getUniqueId());
        playerData.setTeamId(team.getTeamId());
        team.save();
        TeamsManager.getInstance().getTeams().put(team.getTeamId(), team);
        playerData.save();
        sender.sendMessage(Lang.CREATED_TEAM.getComponent(tr("team_name", team.getName())));
    }


    @Command("info|who <target>")
    @CommandDescription("Get information about a team, use -p to get information about a player's team")
    public void info(@Sender Player sender, @Argument Team target) {
        sendTeamInfo(sender, target);
    }

    @Command("player <player>")
    @CommandDescription("Get the team of a player")
    public void player(@Sender Player sender, @Argument OfflinePlayer player) {
        Team targetTeam = TeamsPlus.getInstance().getTeamsManager().getPlayerTeam(player.getUniqueId());
        if (targetTeam == null) {
            sender.sendMessage(Lang.PLAYER_NOT_IN_TEAM.getComponent(tr("name", player.getName())));
            return;
        }
        sendTeamInfo(sender, targetTeam);
    }

    @Command("disband|delete")
    @CommandDescription("Disband your team")
    @TeamPermission(TeamRank.OWNER)
    public void disband(@Sender Player senderP, @Sender Team team) {
        new ConfirmMenu("disband your team", (response) -> {
            if (response) {
                team.disband();
            } else senderP.sendMessage(Lang.CANCELED.getComponent());
            senderP.closeInventory();
        }).setPermanent(true).open(senderP);
    }

    @Command("claimdescription <description>")
    @CommandDescription("Set the description of your team's claims")
    @TeamPermission(TeamRank.ADMIN)
    public void setDescription(@Sender Player sender, @Sender Team team, @Greedy String description) {
        int max = TeamsPlus.getInstance().getConfig().getInt("claims.description-max-length", 64);
        if (description.length() > max) {
            sender.sendMessage(Lang.CLAIM_DESCRIPTION_TOO_LONG.getComponent(tr("max", max)));
            return;
        }
        team.setClaimDescription(description);
        team.save();
        sender.sendMessage(Lang.DESCRIPTION_SET.getComponent(tr("description", description)));
    }

    @Command("list [page]")
    @CommandDescription("List all teams")
    public void list(@Sender CommandSender sender, @Default(value = "1") @Range(min = "1") @Argument("page") int page) { //fuck this... :/
        page = page - 1;
        if (listCache != null && lastList + TEAM_CACHE_TIME > System.currentTimeMillis()) {
            if (page >= listCache.size()) {
                sender.sendMessage(Lang.INVALID_ENTRY_NUMBER.getComponent(tr("min", 1), tr("max", listCache.size())));
                return;
            }
            if (page < 0) page = 0;
            for (Component component : listCache.get(page)) {
                sender.sendMessage(component);
            }
            return;
        }
        lastList = System.currentTimeMillis();
        List<Component> components = new ArrayList<>();
        components.add(Lang.LIST_HEADER.getComponent());
        components.add(Lang.LIST_TITLE.getComponent());
        int current = 0;
        Map<Integer, List<Component>> pages = new HashMap<>();
        for (Team team : TeamsManager.getInstance().getTeams().values()) {
            current++;
            // sb.append("\n").append(Lang.LIST_ENTRY.getComponent(tr("name", team.getName())));
            components.add(Lang.LIST_ENTRY.getComponent(tr("name", team.getName())));
            if (current % MAX_PAGE_SIZE == 0) {
                components.add(Lang.LIST_FOOTER.getComponent());
                pages.put(pages.size(), components);
                // sb = new StringBuilder(Lang.LIST_HEADER.toString());
                components = new ArrayList<>();
                components.add(Lang.LIST_HEADER.getComponent());
                components.add(Lang.LIST_TITLE.getComponent());
                current = 0;
            }
        }
        if (current > 0) {
            components.add(Lang.LIST_FOOTER.getComponent());
            pages.put(pages.size(), components);
        }
        int totalPages = pages.size();
        //go through all pages and replace %page% with the page number
        Map<Integer, List<Component>> finalPages = new HashMap<>();


        TextReplacementConfig maxPagesReplacement = TextReplacementConfig.builder().matchLiteral("{max_pages}").replacement(String.valueOf(totalPages)).build();
        pages.forEach((pageNumber, content) -> {
            TextReplacementConfig pageReplacement = TextReplacementConfig.builder().matchLiteral("{page}").replacement(String.valueOf(pageNumber + 1)).build();
            List<Component> finalContent = content.stream().map(entry -> entry.replaceText(pageReplacement).replaceText(maxPagesReplacement)).toList();
            finalPages.put(pageNumber, finalContent);
        });

        listCache = finalPages;
        if (page >= finalPages.size()) {
            sender.sendMessage(Lang.INVALID_ENTRY_NUMBER.getComponent(tr("min", 1), tr("max", finalPages.size())));
            return;
        }
        for (Component component : finalPages.get(page)) {
            sender.sendMessage(component);
        }
    }

}
