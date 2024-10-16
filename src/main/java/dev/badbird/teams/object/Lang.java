package dev.badbird.teams.object;

import dev.badbird.teams.TeamsPlus;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;

import java.util.Arrays;
import java.util.List;

import static dev.badbird.teams.util.ChatUtil.mm;

public enum Lang {
    CREATED_TEAM("created-team", "<green>Successfully created team <gold><team_name>"),

    TEAM_ALREADY_EXISTS("team-already-exists", "<red>That team already exists!"),

    TEAM_DOES_NOT_EXIST("team-does-not-exist", "<red>That team does not exist!"),

    MUST_BE_IN_TEAM("must-be-in-team", "<red>You must be in a team to do that!"),

    ALREADY_IN_TEAM("already-in-team", "<red>You are already in a team!"),

    PLAYER_NOT_FOUND("player-not-found", "<red>Could not find that player!"),


    NO_PERMISSION("no-permission", "<red>You do not have permission to do that (<rank>)! Ask a team admin/leader to promote you! (/team promote <name>)"),

    CANNOT_INVITE_SELF("team.cannot-invite-self", "<red>You cannot invite yourself!"),

    CANNOT_CREATE_TEAM_BLOCKED_NAME("cannot-create-blocked-name", "<red>You cannot create a team with that name!"),

    CHAT_FORMAT_TEAM("chat.format-team", "<gray>[<green>Team<gray>] <green><name> <reset>: <message>"),
    CHAT_FORMAT_TEAM_LOG("chat.format-team-log", "<gray>[<green>Team<gray>] <gray>[<green><team_name><gray>] <green><player_name><reset>: <message>"),
    CHAT_FORMAT_ALLY_FROM("chat.format-ally.from", "<gray>[<light_purple>Ally<gray>] <green><name> <gray>(<from> -> <to>) <reset>: <message>"),
    CHAT_FORMAT_ALLY_TO("chat.format-ally.to", "<gray>[<light_purple>Ally<gray>] <green><name> <gray>(<to> <- <from>) <reset>: <message>"),
    CHAT_FORMAT_GLOBAL_INTEAM("chat.format-global.inteam", "<target_color>[<team>] <name><reset><gray>:<white> <message>"),
    CHAT_FORMAT_GLOBAL_NOTEAM("chat.format-global.noteam", "<color><name><gray>:<white> <message>"),
    CHAT_FORMAT_GLOBAL_LOG("chat.format-global.log", "<team><name><gray>:<white> <message>"),

    TEAM_INFO_MESSAGE("team-info.message", new String[]{
            "<separator>",
            "<aqua> - <aqua><bold><team_name> <reset> <dark_aqua>",
            "<blue> * <aqua>Owner: <owner>",
            "<blue> * <aqua>Allies: <allies>",
            "<blue> * <aqua>Enemies: <enemies>",
            "<blue> * <aqua>Members: <members>",
            "<separator>"
    }),

    TEAM_INFO_ONLINE_MEMBER_ENTRY("team-info.online-member-entry", "<green><member>"),

    TEAM_INFO_OFFLINE_MEMBER_ENTRY("team-info.offline-member-entry", "<red><member>"),

    TEAM_INFO_MEMBER_ENTRY_SEPARATOR("team-info.member-entry-separator", "<gray>, "),

    TEAM_INFO_ALLIES_TEAM_ENTRY("team-info.allies-team-entry", "<aqua><ally> "),

    TEAM_INFO_ENEMIES_TEAM_ENTRY("team-info.enemies-team-entry", "<red><enemy> "),

    TEAM_INFO_ENEMIES_LIST("team-info.enemies-list", "<dark_gray>[<green><total_enemies><dark_gray>] <list>"),

    TEAM_INFO_ALLIES_LIST("team-info.allies-list", "<dark_gray>[<green><total_allies><dark_gray>] <list>"),

    TEAM_INFO_MEMBERS_LIST("team-info.members-list", "<dark_gray>[<green><online_members><gray> / <green><total_members><dark_gray>] <list>"),

    TEAM_RENAME("team.rename", "<gold><name><green> has renamed the team to <gold><new_name>"),

    INVITE("invite.player", "<gold><sender<<aqua> has invited you to join the team <gold><team><aqua> do <green>/team join <team><aqua> to join!"),

    INVITE_HOVER("invite.hover", "<green>Click to join <gold><team>"),

    INVITE_TEAM_MESSAGE("invite.team", "<gold><sender><green> has invited <gold><target><green> to join the team!"),

    INVITE_EXPIRED("invite.expired", "<green>Your invite to <gold><team></gold> has expired."),

    INVITE_ALREADY_SENT("invite.already-sent", "<red>You already have sent <target> a invite!"),

    NO_INVITE("invite.no-invite", "<red>You have no invite to <team>!"),

    TEAM_JOINED("invite.joined", "<green><name> has joined the team!"),

    TEAM_MAX_SENDER("invite.team-max.sender", "<red>Your team is full! <gray>(<current>/<max>)"),

    TEAM_MAX_RECEIVER("invite.team-max.receiver", "<red>That team is full! <gray>(<current>/<max>)"),

    PLAYER_LEAVE_TEAM("team.player-leave", "<gold><name><red> has left the team!"),

    LEFT_TEAM("team.leave", "<green>You have left the team!"),

    TEAM_NAME_TOO_LONG("team.name-too-long", "<red>Team name too long! Max:<gold> <max>"),

    CANNOT_LEAVE_OWN_TEAM("team.cannot-leave-own-team", "<red>You cannot leave your own team!"),

    TEAM_DISBANDED("team.disband", "<red>Your team <gold>'<team>'</gold> has been disbanded!"),

    TEMP_PVP_ENABLED("team.temp-pvp-enable", "<gold><name><green> has enabled temp-pvp for <aqua><time><green> seconds!"),

    TEAM_NOT_ALLIED_WITH_TEAM("team.not-allied-with-team", "<red>You are not allied with that team!"),

    TEAM_ENEMY_TEAM("enemy.team-enemy-team", "<red>You are now enemies with <gold><team><red>!"),

    CANNOT_ENEMY_SELF("enemy.cannot-enemy-self", "<red>You cannot enemy yourself!"),

    TEAM_NEUTRAL_TEAM("neutral.team-neutral-team", "<green>You are now neutral with the team <gold><team>"),

    TEAM_ALREADY_NEUTRAL("neutral.already-neutral", "<red>You are already neutral with <gold><target><red>!"),

    CANNOT_NEUTRAL_SELF("neutral.cannot-neutral-self", "<red>You cannot neutral yourself!"),

    TEAM_PVP_DISALLOW("pvp.team-disallow", "<red>You can't damage <gold><target></gold> because you're in the same team as them!"),

    ALLY_PVP_DISALLOW("pvp.ally-disallow", "<red>You can't damage <gold><target></gold> because you're allied with them!"),

    ALLY_SUCCESS("ally.ally-success", "<green>You are now allied with <gold><team>"),

    MAX_ALLIES_REACHED("ally.max", "<red>You can't ally with <gold><target></gold> because you've reached the max allies limit! (<current>/<max>)"),

    MAX_ALLIES_REACHED_TARGET("ally.max-target", "<red><target> can't ally with you because they've reached the max allies limit!"),

    TEAM_ALLY_TEAM_ASK("ally.team-ally-team-ask.message", "<gold><name><aqua> would like to ally with your team. Do <green>/teams ally <team><aqua> to accept!"),

    TEAM_ALLY_TEAM_ASK_HOVER("ally.team-ally-team-ask.hover", "<green>Click to accept!"),

    ALLY_REQUEST_DENY_TIMEOUT("ally.ally-request-deny-timeout", "<red><name> took too long to answer your ally request!"),

    CANNOT_ALLY_SELF("ally.cannot-ally-self", "<red>You cannot ally your own team!"),

    ALREADY_SENT_ALLY_REQUEST("ally.already-sent-request", "<red>You already sent <target> an ally request!"),

    ALLY_SENT_REQUEST("ally.sent-request", "<green>Sent a request to <gold><target><green> to ally with your team!"),

    ALREADY_ALLIES("ally.already-allies", "<red>You are already allied with <gold><target><red>!"),

    STAFF_DISBAND_TEAM("staff.disband", "<green>Successfully disbanded <target>."),

    STAFF_FORCE_JOIN("staff.force-join", "<green>Successfully force joined <player> to <team>."),

    STAFF_FORCE_LEAVE("staff.force-leave", "<green>Successfully removed <player> from <team>."),
    STAFF_FORCE_LEAVE_OWNER("staff.force-leave-owner", "<red>You cannot remove the owner of a team!"),

    STAFF_FORCE_RANK("staff.force-rank", "<green>Successfully set the rank of <player> to <rank>."),

    STAFF_FORCE_RENAME("staff.force-rename", "<green>Successfully renamed <old> to <new>."),
    STAFF_FORCE_TRANSFER_SUCCESS("staff.force-transfer.success", "<green>Successfully transferred ownership of <team> to <target>."),
    STAFF_FORCE_TRANSFER_TARGET_NOT_IN_TEAM("staff.force-transfer.target-not-in-team", "<red><target> is not in that team!"),
    STAFF_FORCE_TRANSFER_TARGET_IS_ALREADY_OWNER("staff.force-transfer.target-is-already-owner", "<red>%1 is already the owner of that team!"),
    PLAYER_NOT_IN_TEAM("team.player-not-in-team", "<red>You are not in a team!"),
    TARGET_NOT_IN_TEAM("team.target-not-in-team", "<red><target> is not in a team!"),

    CANCELED("canceled", "<red>Canceled!"),

    LIST_HEADER("team.list.header", "<gray><strikethrough>-------------------------------------"),

    LIST_TITLE("team.list.title", "<green>All Teams ({max_pages} total):"),

    LIST_FOOTER("team.list.footer", "<gray><strikethrough>----------<reset><gray>(<gold>Page <gray>{page}/{max_pages})<gray><strikethrough>-----------"),

    LIST_ENTRY("team.list.entry", "<gray> - <green><name>"),

    INVALID_ENTRY_NUMBER("team.list.invalid", "<red>Invalid entry number! min: <min> max: <max>"),

    CHAT_SWITCH_TO_TEAM("chat.switch.team", "<green>Switched to team chat!"),

    CHAT_SWITCH_TO_GLOBAL("chat.switch.global", "<green>Switched to global chat!"),

    CHAT_SWITCH_TO_ALLY("chat.switch.ally", "<green>Switched to ally chat with <gold><team><green>!"),

    CANNOT_ALLY_CHAT_SELF("chat.cannot-ally-chat-self", "<green>You cannot ally chat with your own team!"),

    CANNOT_KICK_SAME_RANK_OR_HIGHER("team.cannot-kick-same-rank-or-higher", "<red>You cannot kick a player of the same or higher rank of you!"),

    KICKED_FROM_TEAM("team.kicked-from-team", "<red>You have been kicked from your team for <gold><reason><red>!"),

    PLAYER_KICKED("team.player-kicked", "<red><target> has been kicked from the team for <gold><reason><red> by <gold><sender><red>!"),

    WAYPOINT_LORE("waypoint.item-lore", new String[]{
            "<gray>X: <x> <gray>Y: <y> <gray>Z: <z>",
            "",
            "<red>lick to edit!",
            "<yellow>Shift-click to remove!"
    }),

    WAYPOINT_FILTER_NAME("waypoint.filter.name", "<green>Filter"),

    WAYPOINT_FILTER_LORE("waypoint.filter.lore", new String[]{
            "",
            "<yellow>Click to filter entries",
    }),

    WAYPOINT_INFO_NAME("waypoint.info.name", "<green>Info"),

    WAYPOINT_INFO_LORE("waypoint.info.lore", new String[]{
            "",
            "<yellow>It is recommended to use Lunar Client",
            "<yellow>to view waypoints in-game!",
            "<green>Download @ lunarclient.com"
    }),

    TOGGLE_LUNAR_NAME("waypoint.toggle-lunar.name", "<green>Toggle Lunar Waypoint Permanently"),

    TOGGLE_LUNAR_LORE_ENABLED("waypoint.toggle-lunar.lore.enabled", new String[]{
            "",
            "<yellow>Click to disable lunar integration for this waypoint.",
            "<gray>This will only affect you."
    }),

    TOGGLE_LUNAR_LORE_DISABLED("waypoint.toggle-lunar.lore.disabled", new String[]{
            "",
            "<yellow>Click to enable lunar integration for this waypoint!",
            "<gray>This will only affect you."
    }),

    TOGGLE_LUNAR_ON("waypoint.toggle-lunar.message.on", "<green>Toggled lunar integration on for this waypoint!"),

    TOGGLE_LUNAR_OFF("waypoint.toggle-lunar.message.off", "<green>Toggled lunar integration <red>off<green> for this waypoint!"),

    WAYPOINT_CREATED("waypoint.created", "<gold><player><green> created a waypoint <gold>'<waypoint>'<green>! View it with <yellow>/teamwaypoint list<green>!"),

    WAYPOINT_SEARCH_MESSAGE("waypoint.search-message", "<green>Please enter a search term!"),

    WAYPOINT_EDIT_NAME("waypoint.edit.edit-name.name", "<green>Edit Name"),

    WAYPOINT_EDIT_LORE("waypoint.edit.edit-name.lore", new String[]{
            "",
            "<yellow>Click to edit!"
    }),

    WAYPOINT_EDIT_NAME_MESSAGE("waypoint.edit.edit-name.message", "<green>Please enter a new name for the waypoint!"),

    WAYPOINT_EDIT_ICON_NAME("waypoint.edit.icon.name", "<green>Icon: <yellow><name>"),

    WAYPOINT_EDIT_ICON_LORE("waypoint.edit.icon.lore", new String[]{
            "",
            "<yellow>Click to edit!"
    }),

    WAYPOINT_SELECT_ICON_LORE("waypoint.edit.icon-select.lore", new String[]{
            "",
            "<yellow>Click to select!"
    }),

    WAYPOINT_SELECT_ICON_BROADCAST("waypoint.edit.icon-select.broadcast", "<gold><player><green> set the icon for the waypoint '<gold><waypoint><green>' to <gold><icon><green>!"),

    WAYPOINT_DELETE_BUTTON_NAME("waypoint.edit.delete-button.name", "<red>Delete"),

    WAYPOINT_DELETE_BUTTON_LORE("waypoint.edit.delete-button.lore", new String[]{
            "",
            "<yellow>Click to delete."
    }),

    WAYPOINT_DELETED_BROADCAST("waypoint.edit.delete-button.broadcast", "<gold><sender><green> deleted the waypoint '<gold><waypoint><green>'."),

    WAYPOINT_EXISTS("waypoint.exists", "<red>Waypoint already exists!"),

    WAYPOINT_COLOR_NAME("waypoint.edit.color.name", "<green>Color"),

    WAYPOINT_COLOR_LORE("waypoint.edit.color.lore", new String[]{
            "",
            "<yellow>Click to select color!"
    }),

    WAYPOINT_COLOR_SELECT_NAME("waypoint.color-select.name", "<name>"),

    WAYPOINT_COLOR_SELECT_LORE_SELECTED("waypoint.color-select.lore.selected", new String[]{
            "",
            "<green>Already selected!"
    }),

    WAYPOINT_COLOR_SELECT_LORE_UNSELECTED("waypoint.color-select.lore.unselected", new String[]{
            "",
            "<yellow>Click to select!"
    }),

    WAYPOINT_COLOR_SET_BROADCAST("waypoint.edit.color-select.broadcast", "<gold><player><green> set the color for the waypoint '<gold><waypoint><green>' to <color><green>!"),

    WAYPOINT_NAME_EDITED("waypoint.edit-name.broadcast", "<gold><player><green> renamed the waypoint '<gold><prev_name><green>' to '<gold><new_name><green>'."),

    TEAM_TRANSFER_FAILED_TARGET_NOT_IN_TEAM("team.transfer.failed.not-in-team", "<red><target> isn't in your team!"),

    TEAM_TRANSFER_FAILED_CANNOT_TRANSFER_TO_SELF("team.transfer.failed.cannot-transfer-to-self", "<red>You cannot transfer ownership to yourself!"),

    TEAM_TRANSFER_BROADCAST("team.transfer.broadcast", "<gold><sender><green> has transferred ownership of this team to <gold><target><green>!"),

    TEAM_PROMOTE_FAILED_CANNOT_PROMOTE_SELF("team.promote.failed.cannot-promote-self", "<red>You cannot promote yourself!"),

    TEAM_PROMOTE_FAILED_NOT_IN_SAME_TEAM("team.promote.failed.not-in-team", "<red><target> isn't in your team!"),

    TEAM_PROMOTE_FAILED_CANNOT_PROMOTE_HIGHER("team.promote.failed.cannot-promote-higher", "<red>You cannot promote a player to a rank equivalent or higher than you!"),

    TEAM_PROMOTE_BROADCAST("team.promote.broadcast", "<gold><sender><green> has promoted <gold><target></gold> to <rank>!"),

    TEAM_DEMOTE_FAILED_CANNOT_DEMOTE_SELF("team.demote.failed.cannot-demote-self", "<red>You cannot demote yourself!"),

    TEAM_DEMOTE_FAILED_NOT_IN_SAME_TEAM("team.demote.failed.not-in-team", "<red><target> isn't in your team!"),

    TEAM_DEMOTE_FAILED_CANNOT_DEMOTE_HIGHER("team.demote.failed.cannot-demote-higher", "<red>You cannot demote a player to a rank equivalent or higher than you!"),

    TEAM_DEMOTE_BROADCAST("team.demote.broadcast", "<gold><sender><green> has demoted <gold><target></gold> to <rank>."),

    TEAM_CANNOT_DEMOTE_LOWER("team.demote.failed.cannot-demote-lower", "<red>You cannot demote that player any lower than recruit!"),

    PLAYER_INFO("player-info.message", new String[]{
            "<separator>",
            "<aqua>Name: <name>",
            "<aqua>Team: <team>",
            "<aqua>Team Rank: <team_rank>",
            "<aqua>Kills: <yellow><kills>",
            "<separator>"
    }),

    PLAYER_INFO_NOT_IN_TEAM("player-info.not-in-team", "<red>None"),

    KILL_SPAM_PREVENTION("kill-spam-prevention.message", "<red>You killed <gold><player><red> twice in the last <gold><minutes><red> minutes, the kill will not count towards your stats."),

    CLAIM_SUCCESS("claim.success", "<green>Successfully claimed this chunk!"),
    CLAIM_ALREADY_CLAIMED("claim.already-claimed", "<red>This chunk is already claimed!"),
    CLAIM_NOT_ENOUGH_MONEY("claim.not-enough-money", "<red>You don't have enough money to claim this chunk! <yellow>(<cost>)"),
    CLAIM_CANNOT_MODIFY("claim.cannot-modify", "<red>This chunk is claimed by <gold><claimer><red> and you cannot modify it!"),
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
        finalMessage = def;// TeamsPlus.getLangFile().getString(configPath, def);
    }

    Lang(String configPath, String[] def) { //TODO reload
        this.configPath = configPath;
        messageList = TeamsPlus.getLangFile().getStringList(configPath);
        if (true || messageList.isEmpty())
            messageList = Arrays.asList(def);
        StringBuilder sb = new StringBuilder();
        for (String s : messageList) {
            sb.append(s).append("\n");
        }
        finalMessage = sb.toString();
    }

    public Component getComponent(TagResolver... placeholders) {
        return mm(finalMessage, placeholders);
    }

    public List<Component> getComponentList(TagResolver... placeholders) {
        return messageList.stream().map(s -> mm(s, placeholders)).toList();
    }

    public String getMiniMessage() {
        return finalMessage;
    }

    @Deprecated(forRemoval = true)
    @Override
    public String toString() {
        return null;
    }
}
