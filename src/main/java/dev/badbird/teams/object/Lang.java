package dev.badbird.teams.object;

import lombok.Getter;
import net.badbird5907.blib.util.CC;
import net.badbird5907.blib.utils.StringUtils;
import dev.badbird.teams.TeamsPlus;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum Lang {
    PREFIX("prefix", "&7[&bTeams+&7]&r"),

    CREATED_TEAM("created-team", "%prefix% &aSuccessfully created team &6%1"),

    TEAM_ALREADY_EXISTS("team-already-exists", "%prefix% &cThat team already exists!"),

    TEAM_DOES_NOT_EXIST("team-does-not-exist", "%prefix% &cThat team does not exist!"),

    MUST_BE_IN_TEAM("must-be-in-team", "%prefix% &cYou must be in a team to do that!"),

    ALREADY_IN_TEAM("already-in-team", "%prefix% &cYou are already in a team!"),

    PLAYER_NOT_FOUND("player-not-found", "%prefix% &cCould not find that player!"),


    NO_PERMISSION("no-permission", "%prefix% &cYou do not have permission to do that (%2)! Ask a team admin/leader to promote you! (/team promote %1)"),

    CANNOT_INVITE_SELF("team.cannot-invite-self", "&cYou cannot invite yourself!"),

    CANNOT_CREATE_TEAM_BLOCKED_NAME("cannot-create-blocked-name", "%prefix% &cYou cannot create a team with that name!"),

    CHAT_FORMAT_TEAM("chat.format-team", "&7[&aTeam&7] &a%1 &r: %2"),
    CHAT_FORMAT_TEAM_LOG("chat.format-team-log", "&7[&aTeam&7] &7[&a%1&7] &a%2&r: %3"),
    CHAT_FORMAT_ALLY("chat.format-ally", "&7[&dAlly&7] &a%1 &7(%2 -> %3) &r: %4"),
    CHAT_FORMAT_GLOBAL_INTEAM("chat.format-global.inteam", "%1[%2] %3&r&7:&f %4"),
    CHAT_FORMAT_GLOBAL_NOTEAM("chat.format-global.noteam", "%1%2&7:&f %3"),
    CHAT_FORMAT_GLOBAL_LOG("chat.format-global.log", "%1%2&r: %3"),

    TEAM_INFO_MESSAGE("team-info.message", new String[]{
            "%separator%",
            "&b - &b&l%1 &r &3",
            "&9 * &bOwner: %2",
            "&9 * &bAllies: %3",
            "&9 * &bEnemies: %4",
            "&9 * &bMembers: &8[&a%5&7/&a%6&8] %9",
            "%separator%"
    }),

    TEAM_INFO_ONLINE_MEMBER_ENTRY("team-info.online-member-entry", "&a%1"),

    TEAM_INFO_OFFLINE_MEMBER_ENTRY("team-info.offline-member-entry", "&c%1"),

    TEAM_INFO_MEMBER_ENTRY_SEPARATOR("team-info.member-entry-separator", "&7, "),

    TEAM_INFO_ALLIES_TEAM_ENTRY("team-info.allies-team-entry", "&b%1 "),

    TEAM_INFO_ENEMIES_TEAM_ENTRY("team-info.enemies-team-entry", "&c%1 "),

    TEAM_INFO_ENEMIES_LIST("team-info.enemies-list", "&8[&a%1&8] %2"),

    TEAM_INFO_ALLIES_LIST("team-info.allies-list", "&8[&a%1&8] %2"),

    TEAM_INFO_MEMBERS_LIST("team-info.members-list", "&8[&f%1&7/&f%2&8] %3"),

    TEAM_RENAME("team.rename", "&6%1&a has renamed the team to &6%2"),

    INVITE("invite.player", "&6%1&b has invited you to join the team &6%2&b do &a/team join %2&b to join!"),

    INVITE_HOVER("invite.hover", "&aClick to join &6%1"),

    INVITE_TEAM_MESSAGE("invite.team", "&aYour invite to &6%1&a has expired."),

    INVITE_EXPIRED("invite.expired", "&aYour invite to &6%1&a has expired."),

    INVITE_ALREADY_SENT("invite.already-sent", "&cYou already have sent %1 a invite!"),

    NO_INVITE("invite.no-invite", "&cYou have no invite to %1!"),

    TEAM_JOINED("invite.joined", "&a%1 has joined the team!"),

    TEAM_MAX_SENDER("invite.team-max.sender", "&cYour team is full! (%1/%2)"),

    TEAM_MAX_RECEIVER("invite.team-max.receiver", "&cThat team is full!"),

    PLAYER_LEAVE_TEAM("team.player-leave", "&6%1&c has left the team!"),

    LEFT_TEAM("team.leave", "&aYou have left the team!"),

    TEAM_NAME_TOO_LONG("team.name-too-long", "&cTeam name too long! Max:&6 %1"),

    CANNOT_LEAVE_OWN_TEAM("team.cannot-leave-own-team", "&cYou cannot leave your own team!"),

    TEAM_DISBANDED("team.disband", "&cYour team '%1' has been disbanded!"),

    TEMP_PVP_ENABLED("team.temp-pvp-enable", "&6%1&a has enabled temp-pvp for &b%2&a seconds!"),

    TEAM_NOT_ALLIED_WITH_TEAM("team.not-allied-with-team", "&cYou are not allied with that team!"),

    TEAM_ENEMY_TEAM("enemy.team-enemy-team", "&cYou are now enemies with the team &6%1&c!"),

    CANNOT_ENEMY_SELF("enemy.cannot-enemy-self", "&cYou cannot enemy yourself!"),

    TEAM_NEUTRAL_TEAM("neutral.team-neutral-team", "&aYou are now neutral with the team &6%1"),

    TEAM_ALREADY_NEUTRAL("neutral.already-neutral", "&cYou are already neutral with &6%1&c!"),

    CANNOT_NEUTRAL_SELF("neutral.cannot-neutral-self", "&cYou cannot neutral yourself!"),

    TEAM_PVP_DISALLOW("pvp.team-disallow", "&cYou can't damage &6%1&c because you're in the same team as them!"),

    ALLY_PVP_DISALLOW("pvp.ally-disallow", "&cYou can't damage &6%1&c because you're allied with them!"),

    ALLY_SUCCESS("ally.ally-success", "&aYou are now allied with &6%1"),

    MAX_ALLIES_REACHED("ally.max", "&cYou can't ally with &6%1&c because you've reached the max allies limit! (%2/%3)"),

    MAX_ALLIES_REACHED_TARGET("ally.max-target", "&c%1 has reached the max allies limit!"),

    TEAM_ALLY_TEAM_ASK("ally.team-ally-team-ask.message", "&6%1&b would like to ally with your team. Do &a/teams ally %1&b to accept!"),

    TEAM_ALLY_TEAM_ASK_HOVER("ally.team-ally-team-ask.hover", "&aClick to accept!"),

    ALLY_REQUEST_DENY_TIMEOUT("ally.ally-request-deny-timeout", "&c%1 took too long to answer your ally request!"),

    CANNOT_ALLY_SELF("ally.cannot-ally-self", "&cYou cannot ally your own team!"),

    ALREADY_SENT_ALLY_REQUEST("ally.already-sent-request", "&cYou already sent %1 an ally request!"),

    ALLY_SENT_REQUEST("ally.sent-request", "&aSent a request to &6%1&a to ally with your team!"),

    ALREADY_ALLIES("ally.already-allies", "&cYou are already allied with &6%1&c!"),

    STAFF_DISBAND_TEAM("staff.disband-team", "&aSuccessfully disbanded %1."),

    STAFF_FORCE_JOIN("staff.force-join", "&aSuccessfully force joined %1 to %2."),

    STAFF_FORCE_LEAVE("staff.force-leave", "&aSuccessfully removed %1 from %2."),
    STAFF_FORCE_LEAVE_OWNER("staff.force-leave-owner", "&cYou cannot remove the owner of a team!"),

    STAFF_FORCE_RANK("staff.force-rank", "&aSuccessfully set the rank of %1 to %2."),

    STAFF_FORCE_RENAME("staff.force-rename", "&aSuccessfully renamed %1 to %2."),
    STAFF_FORCE_TRANSFER_SUCCESS("staff.force-transfer.success", "&aSuccessfully transferred ownership of %1 to %2."),
    STAFF_FORCE_TRANSFER_TARGET_NOT_IN_TEAM("staff.force-transfer.target-not-in-team", "&c%1 is not in that team!"),
    STAFF_FORCE_TRANSFER_TARGET_IS_ALREADY_OWNER("staff.force-transfer.target-is-already-owner", "&c%1 is already the owner of that team!"),
    PLAYER_NOT_IN_TEAM("team.player-not-in-team", "&cYou are not in a team!"),
    TARGET_NOT_IN_TEAM("team.target-not-in-team", "&c%1 is not in a team!"),

    CANCELED("canceled", "%prefix% &cCanceled!"),

    LIST_HEADER("team.list.header", "&7&m-------------------------------------"),

    LIST_TITLE("team.list.title", "&aAll Teams (%max_pages% total):"),

    LIST_FOOTER("team.list.footer", "&7&m----------&r&7(&6Page &7%page%/%max_pages%)&7&m-----------"),

    LIST_ENTRY("team.list.entry", "&7 - &a%1"),

    INVALID_ENTRY_NUMBER("team.list.invalid", "&cInvalid entry number! min: %1 max: %2"),

    CHAT_SWITCH_TO_TEAM("chat.switch.team", "&aSwitched to team chat!"),

    CHAT_SWITCH_TO_GLOBAL("chat.switch.global", "&aSwitched to global chat!"),

    CHAT_SWITCH_TO_ALLY("chat.switch.ally", "&aSwitched to ally chat with &6%1&a!"),

    CANNOT_ALLY_CHAT_SELF("chat.cannot-ally-chat-self", "&aYou cannot ally chat with your own team!"),

    CANNOT_KICK_SAME_RANK_OR_HIGHER("team.cannot-kick-same-rank-or-higher", "&cYou cannot kick a player of the same or higher rank of you!"),

    KICKED_FROM_TEAM("team.kicked-from-team", "&cYou have been kicked from the team for &6%1&c!"),

    PLAYER_KICKED("team.player-kicked", "&c%1 has been kicked from the team for &6%3&c by &6%2&c!"),

    WAYPOINT_LORE("waypoint.item-lore", new String[]{
            "&7X: %1 &7Y: %2 &7Z: %3",
            "",
            "&Click to edit!",
            "&eShift-click to remove!"
    }),

    WAYPOINT_FILTER_NAME("waypoint.filter.name", "&aFilter"),

    WAYPOINT_FILTER_LORE("waypoint.filter.lore", new String[]{
            "",
            "&eClick to filter entries",
    }),

    WAYPOINT_INFO_NAME("waypoint.info.name", "&aInfo"),

    WAYPOINT_INFO_LORE("waypoint.info.lore", new String[]{
            "",
            "&eIt is recommended to use Lunar Client",
            "&eto view waypoints in-game!",
            "&aDownload @ lunarclient.com"
    }),

    TOGGLE_LUNAR_NAME("waypoint.toggle-lunar.name", "&aToggle Lunar Waypoint Permanently"),

    TOGGLE_LUNAR_LORE_ENABLED("waypoint.toggle-lunar.lore.enabled", new String[]{
            "",
            "&eClick to disable lunar integration for this waypoint.",
            "&7This will only affect you."
    }),

    TOGGLE_LUNAR_LORE_DISABLED("waypoint.toggle-lunar.lore.disabled", new String[]{
            "",
            "&eClick to enable lunar integration for this waypoint!",
            "&7This will only affect you."
    }),

    TOGGLE_LUNAR_ON("waypoint.toggle-lunar.message.on", "&aToggled lunar integration on for this waypoint!"),

    TOGGLE_LUNAR_OFF("waypoint.toggle-lunar.message.off", "&aToggled lunar integration &coff&a for this waypoint!"),

    WAYPOINT_CREATED("waypoint.created", "&6%1&a created a waypoint &6'%2'&a! View it with &e/teamwaypoint list&a!"),

    WAYPOINT_SEARCH_MESSAGE("waypoint.search-message", "&aPlease enter a search term!"),

    WAYPOINT_EDIT_NAME("waypoint.edit.edit-name.name", "&aEdit Name"),

    WAYPOINT_EDIT_LORE("waypoint.edit.edit-name.lore", new String[]{
            "",
            "&eClick to edit!"
    }),

    WAYPOINT_EDIT_NAME_MESSAGE("waypoint.edit.edit-name.message", "&aPlease enter a new name for the waypoint!"),

    WAYPOINT_EDIT_ICON_NAME("waypoint.edit.icon.name", "&aIcon: &e%1"),

    WAYPOINT_EDIT_ICON_LORE("waypoint.edit.icon.lore", new String[]{
            "",
            "&eClick to edit!"
    }),

    WAYPOINT_SELECT_ICON_LORE("waypoint.edit.icon-select.lore", new String[]{
            "",
            "&eClick to select!"
    }),

    WAYPOINT_SELECT_ICON_BROADCAST("waypoint.edit.icon-select.broadcast", "&6%1&a set the icon for the waypoint '&6%2&a' to &6%3&a!"),

    WAYPOINT_DELETE_BUTTON_NAME("waypoint.edit.delete-button.name", "&cDelete"),

    WAYPOINT_DELETE_BUTTON_LORE("waypoint.edit.delete-button.lore", new String[]{
            "",
            "&eClick to delete."
    }),

    WAYPOINT_DELETED_BROADCAST("waypoint.edit.delete-button.broadcast", "&6%1&a deleted the waypoint '&6%2&a'."),

    WAYPOINT_EXISTS("waypoint.exists", "&cWaypoint already exists!"),

    WAYPOINT_COLOR_NAME("waypoint.edit.color.name", "&aColor"),

    WAYPOINT_COLOR_LORE("waypoint.edit.color.lore", new String[]{
            "",
            "&eClick to select color!"
    }),

    WAYPOINT_COLOR_SELECT_NAME("waypoint.color-select.name", "%1%2"),

    WAYPOINT_COLOR_SELECT_LORE_SELECTED("waypoint.color-select.lore.selected", new String[]{
            "",
            "&aAlready selected!"
    }),

    WAYPOINT_COLOR_SELECT_LORE_UNSELECTED("waypoint.color-select.lore.unselected", new String[]{
            "",
            "&eClick to select!"
    }),

    WAYPOINT_COLOR_SET_BROADCAST("waypoint.edit.color-select.broadcast", "&6%1&a set the color for the waypoint '&6%2&a' to %3%4&a!"),

    WAYPOINT_NAME_EDITED("waypoint.edit-name.broadcast", "&6%1&a renamed the waypoint '&6%2&a' to '&6%3&a'."),

    TEAM_TRANSFER_FAILED_TARGET_NOT_IN_TEAM("team.transfer.failed.not-in-team", "&c%1 isn''t in your team!"),

    TEAM_TRANSFER_FAILED_CANNOT_TRANSFER_TO_SELF("team.transfer.failed.cannot-transfer-to-self", "&cYou cannot transfer ownership to yourself!"),

    TEAM_TRANSFER_BROADCAST("team.transfer.broadcast", "&6%1&a has transferred ownership of this team to &6%2&a!"),

    TEAM_PROMOTE_FAILED_CANNOT_PROMOTE_SELF("team.promote.failed.cannot-promote-self", "&cYou cannot promote yourself!"),

    TEAM_PROMOTE_FAILED_NOT_IN_SAME_TEAM("team.promote.failed.not-in-team", "&c%1 isn't in your team!"),

    TEAM_PROMOTE_FAILED_CANNOT_PROMOTE_HIGHER("team.promote.failed.cannot-promote-higher", "&cYou cannot promote a player to a rank equivalent or higher than you!"),

    TEAM_PROMOTE_BROADCAST("team.promote.broadcast", "&6%1&a has promoted &6%2&a to %3!"),

    TEAM_DEMOTE_FAILED_CANNOT_DEMOTE_SELF("team.demote.failed.cannot-demote-self", "&cYou cannot demote yourself!"),

    TEAM_DEMOTE_FAILED_NOT_IN_SAME_TEAM("team.demote.failed.not-in-team", "&c%1 isn't in your team!"),

    TEAM_DEMOTE_FAILED_CANNOT_DEMOTE_HIGHER("team.demote.failed.cannot-demote-higher", "&cYou cannot demote a player to a rank equivalent or higher than you!"),

    TEAM_DEMOTE_BROADCAST("team.demote.broadcast", "&6%1&a has demoted &6%2&a to %3."),

    TEAM_CANNOT_DEMOTE_LOWER("team.demote.failed.cannot-demote-lower", "&cYou cannot demote that player any lower than recruit!"),

    PLAYER_INFO("player-info.message", new String[]{
            "&7&m-------------------------------------",
            "&bName: %1",
            "&bTeam: %2",
            "&bTeam Rank: %3",
            "&bKills: &e%4",
            "&7&m-------------------------------------"
    }),

    PLAYER_INFO_NOT_IN_TEAM("player-info.not-in-team", "&cNone"),

    KILL_SPAM_PREVENTION("kill-spam-prevention.message", "&cYou killed &6%1&c twice in the last &6%2&c minutes, the kill will not count towards your stats."),

    CLAIM_SUCCESS("claim.success", "&aSuccessfully claimed this chunk!"),
    CLAIM_ALREADY_CLAIMED("claim.already-claimed", "&cThis chunk is already claimed!"),
    CLAIM_NOT_ENOUGH_MONEY("claim.not-enough-money", "&cYou don't have enough money to claim this chunk! &e(%1)"),
    CLAIM_CANNOT_MODIFY("claim.cannot-modify", "&cThis chunk is claimed by &6%1&c and you cannot modify it!"),
    ;
    @Getter
    private final String configPath;
    private final String finalMessage;
    @Getter
    private String def;

    private List<String> messageList;

    Lang(String configPath, String def) {
        this.configPath = configPath;
        this.def = def;
        finalMessage = TeamsPlus.getLangFile().getString(configPath, def);
    }

    Lang(String configPath, String[] def) { //TODO reload
        this.configPath = configPath;
        messageList = TeamsPlus.getLangFile().getStringList(configPath);
        if (messageList.isEmpty())
            messageList = Arrays.asList(def);
        StringBuilder sb = new StringBuilder();
        for (String s : messageList) {
            sb.append(s).append("\n");
        }
        finalMessage = sb.toString();
    }

    public List<String> getMessageList(Object... placeholders) {
        List<String> list = new ArrayList<>();
        for (String s : messageList) {
            list.add(StringUtils.replacePlaceholders(CC.translate(s).replace("%prefix%", PREFIX.getRaw()).replace("%separator%", CC.SEPARATOR), placeholders));
        }
        return list;
    }

    public String toString(Object... placeholders) {
        return CC.translate(StringUtils.replacePlaceholders(finalMessage.replace("%prefix%", PREFIX.getRaw()).replace("%separator%", CC.SEPARATOR), placeholders));
    }

    public String getRaw() {
        return finalMessage;
    }

    @Override
    public String toString() {
        return CC.translate(finalMessage.replace("%prefix%", PREFIX.getRaw() //stack overflow error
        ).replace("%separator%", CC.SEPARATOR));
    }

    public String toStringReplace() {
        return finalMessage.replace("%prefix%", PREFIX.getRaw()
        ).replace("%separator%", CC.SEPARATOR);
    }

    public Component getComponent(Object... placeholders) {
        Component component = LegacyComponentSerializer.legacyAmpersand().deserialize(toStringReplace());
        for (int i = 0; i < placeholders.length; i++) {
            TextReplacementConfig.Builder b =  TextReplacementConfig.builder()
                    .matchLiteral(("%" + (i + 1)));
            Object o = placeholders[i];
            if (o instanceof String str) {
                b.replacement(LegacyComponentSerializer.legacyAmpersand().deserialize(str));
            }
            if (o instanceof ComponentLike cl) {
                b.replacement(cl);
            }
            if (o instanceof Number n) {
                b.replacement(n.toString());
            }
            component = component.replaceText(
                    b.build()
            );
        }
        return component;
    }

    public List<Component> getComponentList(Object... placeholders) {
        List<Component> list = new ArrayList<>();
        for (String s : messageList) {
            Component component = LegacyComponentSerializer.legacyAmpersand().deserialize(StringUtils.replacePlaceholders(CC.translate(s).replace("%prefix%", PREFIX.getRaw()).replace("%separator%", CC.SEPARATOR), placeholders));
            list.add(component);
        }
        return list;
    }
}
