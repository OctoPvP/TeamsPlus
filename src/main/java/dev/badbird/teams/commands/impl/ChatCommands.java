package dev.badbird.teams.commands.impl;

import dev.badbird.teams.TeamsPlus;
import dev.badbird.teams.commands.annotation.Sender;
import dev.badbird.teams.manager.MessageManager;
import dev.badbird.teams.object.ChatChannel;
import dev.badbird.teams.object.Lang;
import dev.badbird.teams.object.PlayerData;
import dev.badbird.teams.object.Team;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotation.specifier.Greedy;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.CommandDescription;
import org.incendo.cloud.annotations.processing.CommandContainer;
import org.incendo.cloud.annotations.suggestion.Suggestions;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.help.result.CommandEntry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static dev.badbird.teams.util.ChatUtil.tr;

@CommandContainer
@Command("chat|ch")
@CommandDescription("Chat with your team/allies")
public class ChatCommands {
    @Command("help|h|? [query]")
    @CommandDescription("Get help")
    public void help(CommandSender sender, @Argument(value = "query", suggestions = "chat_help_queries") @Greedy @Nullable String query) {
        TeamsPlus.getInstance().getChatHelp() .queryCommands(query == null ? "" : query, sender);
    }
    @Command("")
    public void base(CommandSender sender) {
        help(sender, null);
    }
    @Suggestions("chat_help_queries")
    public @NotNull List<@NotNull String> suggestHelpQueries(
            final @NotNull CommandContext<CommandSender> ctx,
            final @NotNull String input
    ) {
        return TeamsPlus.getInstance().getChatHelp().helpHandler()
                .queryRootIndex(ctx.sender())
                .entries()
                .stream()
                .map(CommandEntry::syntax)
                .toList();
    }
    @Command("team|t <message>")
    @CommandDescription("Chat with your team")
    public void teamChat(@Sender PlayerData sender, @Sender Player senderP, @Sender Team team, @Nullable @Greedy String message) {
        if (message != null && !message.isEmpty()) {
            MessageManager.handleTeam(senderP, message, team);
            return;
        }
        sender.setCurrentChannel(ChatChannel.TEAM);
        sender.sendMessage(Lang.CHAT_SWITCH_TO_TEAM.getComponent());
    }

    @Command("ally|al <team> <message>")
    @CommandDescription("Chat with your allies")
    public void allyChat(@Sender PlayerData sender, @Sender Team senderTeam, @Sender Player senderP, @Argument Team team, @Greedy @Nullable String message) {
        if (!senderTeam.isAlly(team)) {
            sender.sendMessage(Lang.TEAM_NOT_ALLIED_WITH_TEAM.getComponent());
            return;
        }
        if (senderTeam.getTeamId().equals(team.getTeamId())) {
            sender.sendMessage(Lang.CANNOT_ALLY_CHAT_SELF.getComponent());
            return;
        }
        if (message != null && !message.isEmpty()) {
            MessageManager.handleAlly(sender, senderP, senderTeam, message);
            return;
        }
        sender.setCurrentChannel(ChatChannel.ALLY);
        sender.setAllyChatTeamId(team.getTeamId());
        sender.sendMessage(Lang.CHAT_SWITCH_TO_ALLY.getComponent(tr("team", team.getName())));
    }

    @Command("all|global|g|a <message>")
    @CommandDescription("Chat with everyone")
    public void globalChat(@Sender PlayerData sender, @Sender Player senderP, @Nullable @Greedy String message) {
        if (message != null && !message.isEmpty()) {
            MessageManager.handleGlobal(sender, senderP, message);
            return;
        }
        sender.setCurrentChannel(ChatChannel.GLOBAL);
        sender.sendMessage(Lang.CHAT_SWITCH_TO_GLOBAL.getComponent());
    }
}
