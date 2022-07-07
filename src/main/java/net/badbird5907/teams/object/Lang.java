package net.badbird5907.teams.object;

import lombok.Getter;
import net.badbird5907.blib.util.CC;
import net.badbird5907.blib.utils.StringUtils;
import net.badbird5907.teams.TeamsPlus;

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


    NO_PERMISSION("no-permission", "%prefix% &cYou do not have permission to do that!"),

    CANNOT_INVITE_SELF("team.cannot-invite-self", "&cYou cannot invite yourself!"),

    CANNOT_CREATE_TEAM_BLOCKED_NAME("cannot-create-blocked-name", "%prefix% &cYou cannot create a team with that name!"),

    CHAT_FORMAT_TEAM("chat.format-team", "&7[&aTeam&7] &a%1%r: %2"),

    CHAT_FORMAT_ALLY("chat.format-ally", "&7[&dAlly&7] &a%1%r: %2"),

    CHAT_FORMAT_GLOBAL_INTEAM("chat.format-global-inteam", "%1[%2] %3&r&7:&f %4"),

    CHAT_FORMAT_GLOBAL_NOTEAM("chat.format-global-noteam", "%1%2&7:&f %3"),

    TEAM_INFO_MESSAGE("team-info.message", new String[]{
            "%separator%",
            "&3 - &b&l%1 &r &3",
            "&9 * &bOwner: %2",
            "&9 * &bAllies: %3",
            "&9 * &bEnemies: %4",
            "&9 * &bMembers: &8[&a%5&7/&a%6&8] %9",
            "%separator%"
    }),

    TEAM_INFO_ONLINE_MEMBER_ENTRY("team-info.online-member-entry", "&a%1"),

    TEAM_INFO_OFFLINE_MEMBER_ENTRY("team-info.offline-member-entry", "&c%1"),

    TEAM_INFO_MEMBER_ENTRY_SEPARATOR("team-info.member-entry-separator", "&7, "),

    TEAM_INFO_ALLIES_TEAM_ENTRY("team-info.allies-team-entry", "&b%1"),

    TEAM_INFO_ENEMIES_TEAM_ENTRY("team-info.enemies-team-entry", "&c%1 "),

    TEAM_INFO_ENEMIES_LIST("team-info.enemies-list", "&8[&a%1&8] %2"),

    TEAM_INFO_ALLIES_LIST("team-info.allies-list", "&8[&a%1&8] %2"),

    TEAM_INFO_MEMBERS_LIST("team-info.members-list", "&8[&f%1&7/&f%2&8] %3"),

    TEAM_RENAME("team.rename", "&3%1&b has renamed the team to &3%2"),

    INVITE("invite.player", "&6%1&b has invited you to join the team &6%2&b do &a/team join %2&b to join!"),

    INVITE_TEAM_MESSAGE("invite.team", "&aYour invite to &6%1&a has expired."),

    INVITE_EXPIRED("invite.expired", "&aYour invite to &6%1&a has expired."),

    INVITE_ALREADY_SENT("invite.already-sent", "&cYou already have sent %1 a invite!"),

    NO_INVITE("invite.no-invite", "&cYou have no invite to %1!"),

    TEAM_JOINED("invite.joined", "&a%1 has joined the team!"),

    PLAYER_LEAVE_TEAM("team.player-leave", "&6%1&c has left the team!"),

    LEFT_TEAM("team.leave", "&aYou have left the team!"),

    CANNOT_LEAVE_OWN_TEAM("team.cannot-leave-own-team", "&cYou cannot leave your own team!"),

    TEAM_DISBANDED("team.disband", "&cYour team '%1' has been disbanded!"),

    TEMP_PVP_ENABLED("team.temp-pvp-enable", "&6%1&a has enabled temp-pvp for &b%2&a seconds!"),

    TEAM_NOT_ALLIED_WITH_TEAM("team.not-allied-with-team", "&cYou are not allied with that team!"),

    TEAM_ENEMY_TEAM("enemy.team-enemy-team", "&cYou are now enemies with the team &6%1&c!"),

    TEAM_NEUTRAL_TEAM("neutral.team-neutral-team", "&aYou are now neutral with the team &6%1"),

    TEAM_ALREADY_NEUTRAL("neutral.already-neutral", "&cYou are already neutral with &6%1&c!"),

    TEAM_PVP_DISALLOW("pvp.team-disallow", "&cYou can't damage &6%1&c because you're in the same team as them!"),

    ALLY_PVP_DISALLOW("pvp.ally-disallow", "&cYou can't damage &6%1&c because you're allied with them!"),

    ALLY_SUCCESS("ally.ally-success", "&aYou are now allied with &6%1"),

    TEAM_ALLY_TEAM_ASK("ally.team-ally-team-ask.message", "&6%1&b would like to ally with your team. Do &a/teams ally %1&b to accept!"),

    TEAM_ALLY_TEAM_ASK_HOVER("ally.team-ally-team-ask.hover", "&aClick to accept!"),

    ALLY_REQUEST_DENY_TIMEOUT("ally.ally-request-deny-timeout", "&c%1 took too long to answer your ally request!"),

    CANNOT_ALLY_SELF("ally.cannot-ally-self", "&cYou cannot ally your own team!"),
    ALREADY_SENT_ALLY_REQUEST("ally.already-sent-request", "&cYou already sent %1 an ally request!"),

    ALLY_SENT_REQUEST("ally.sent-request", "&aSent a request to &6%1&a to ally with your team!"),

    ALREADY_ALLIES("ally.already-allies", "&cYou are already allied with &6%1&c!"),

    STAFF_DISBAND_TEAM("staff.disband-team", "&aSuccessfully disbanded %1."),

    PLAYER_NOT_IN_TEAM("team.player-not-in-team", "&cYou are not in a team!"),

    CANCELED("canceled", "%prefix% &cCanceled!"),

    LIST_HEADER("team.list.header", "&7&m-------------------------------------"),

    LIST_TITLE("team.list.title", "&aAll Teams (%max_pages% total):"),

    LIST_FOOTER("team.list.footer", "&7&m----------&r&7(&6Page &7%page%/%max_pages%)&7&m-----------"),

    LIST_ENTRY("team.list.entry", "&7 - &a%1"),

    INVALID_ENTRY_NUMBER("team.list.invalid", "&cInvalid entry number! min: %1 max: %2"),

    CHAT_SWITCH_TO_TEAM("chat.switch.team", "&aSwitched to team chat!"),

    CHAT_SWITCH_TO_GLOBAL("chat.switch.global", "&aSwitched to global chat!"),

    CHAT_SWITCH_TO_ALLY("chat.switch.ally", "&aSwitched to ally chat with &6%1&a!"),

    CANNOT_ALLY_CHAT_SELF("chat.cannot-ally-chat-self", "&aYou cannot ally chat with your own team!");
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
}
