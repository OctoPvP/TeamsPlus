package dev.badbird.teams.manager;

import dev.badbird.teams.TeamsPlus;
import dev.badbird.teams.hooks.impl.CoreProtectHook;
import dev.badbird.teams.hooks.impl.VaultHook;
import dev.badbird.teams.object.ChatChannel;
import dev.badbird.teams.object.Lang;
import dev.badbird.teams.object.PlayerData;
import dev.badbird.teams.object.Team;
import net.badbird5907.blib.util.CC;
import net.badbird5907.blib.utils.StringUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import static dev.badbird.teams.util.ChatUtil.mm;
import static dev.badbird.teams.util.ChatUtil.tr;

public class MessageManager {
    private static final ComponentLogger logger = ComponentLogger.logger("Chat");

    public static void handleAlly(PlayerData data, Player player, Team senderTeam, String message) {
        Team targetTeam = TeamsManager.getInstance().getTeamById(data.getAllyChatTeamId());
        if (targetTeam == null) {
            data.setAllyChatTeamId(null);
            data.setCurrentChannel(ChatChannel.GLOBAL);
            return;
        }
        String displayName = getDisplayName(player);
        Component from = Lang.CHAT_FORMAT_ALLY_FROM.getComponent(tr("name", displayName), tr("from", senderTeam.getName()), tr("to", targetTeam.getName()), tr("message", message));
        targetTeam.broadcast(from);
        senderTeam.broadcast(Lang.CHAT_FORMAT_ALLY_TO.getComponent(tr("name", displayName), tr("from", senderTeam.getName()), tr("to", targetTeam.getName()), tr("message", message)));
        logger.info(from);

        HookManager.getHook(CoreProtectHook.class).ifPresent((h) -> {
            if (h.isEnabled()) {
                h.logChat(player, message, ChatChannel.ALLY);
            }
        });
    }

    public static void handleTeam(Player player, String message, Team team) {
        team.broadcast(Lang.CHAT_FORMAT_TEAM.getComponent(tr("name", MessageManager.getDisplayName(player)), tr("message", message)));
        logger.info(
                Lang.CHAT_FORMAT_TEAM_LOG.getComponent(tr("team_name", team.getName()), tr("player_name", MessageManager.getDisplayName(player)), tr("message", message))
        );
        HookManager.getHook(CoreProtectHook.class).ifPresent((h) -> {
            if (h.isEnabled()) {
                h.logChat(player, message, ChatChannel.TEAM);
            }
        });
    }

    public static boolean handleGlobal(PlayerData data, Player player, String message) {
        boolean octocore = Bukkit.getPluginManager().isPluginEnabled("OctoCore");
        String displayName = getDisplayName(player);
        if (TeamsPlus.getInstance().getConfig().getBoolean("chat.enable")) {
            if (data == null) return false;
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                if (octocore) {
                    net.octopvp.octocore.core.objects.PlayerData data1 = net.octopvp.octocore.core.manager.impl.PlayerManager.getInstance().getData(onlinePlayer);
                    if (data1 != null && !data1.getMessageSettings().isGlobalChat()) {
                        continue;
                    }
                }
                onlinePlayer.sendMessage(MessageManager.formatGlobal(message, player, onlinePlayer, displayName));
            }
        }
        // String teamLog = data.isInTeam() ? "&7[&a" + data.getPlayerTeam().getName() + "&7] " : "";
        logger.info(
                Lang.CHAT_FORMAT_GLOBAL_LOG.getComponent(
                        tr("team", data.isInTeam() ? Component.text("[", NamedTextColor.GRAY).append(Component.text(data.getPlayerTeam().getName(), NamedTextColor.GREEN)).append(Component.text("]", NamedTextColor.GRAY)) : ""),
                        tr("name", displayName),
                        tr("message", message)
                )
        );
        HookManager.getHook(CoreProtectHook.class).ifPresent((h) -> {
            if (h.isEnabled()) {
                h.logChat(player, message, ChatChannel.GLOBAL);
            }
        });
        return true;
    }

    public static Component formatGlobal(String rawMessage, Player player, Player receiver, String displayName) {
        PlayerData senderData = PlayerManager.getData(player), receiverData = PlayerManager.getData(player);
        String format = senderData.isInTeam() ?
                Lang.CHAT_FORMAT_GLOBAL_INTEAM.getMiniMessage() :
                Lang.CHAT_FORMAT_GLOBAL_NOTEAM.getMiniMessage();
        //String format = TeamsPlus.getInstance().getConfig().getString("chat.custom." + (senderData.isInTeam() ? "format-global-inteam" : "format-global-noteam"));
        Component message;
        if (senderData.isInTeam()) {
            String color = "<aqua>";
            if (receiverData.isInTeam() && senderData.getPlayerTeam().getRank(receiverData.getUuid()) != null) {
                color = "<green>";
            }
            if (senderData.isEnemy(receiver)) {
                color = "<red>";
            } else if (senderData.isAlly(receiver))
                color = "<light_purple>";
            // message = StringUtils.replacePlaceholders(format, color, senderData.getPlayerTeam().getName(), displayName, handleMentions(receiver, rawMessage));
            String miniMessage = format.replace("<color>", color);
            message = mm(miniMessage, tr("team", senderData.getPlayerTeam().getName()), tr("name", displayName), tr("message", handleMentions(receiver, rawMessage)), tr("message", handleMentions(receiver, rawMessage)));
        } else {
            // message = StringUtils.replacePlaceholders(format, CC.AQUA, displayName, handleMentions(receiver, rawMessage));
            message = mm(format, tr("name", displayName), tr("message", handleMentions(receiver, rawMessage)));
        }
        return message;
    }

    @SuppressWarnings("Deprecation")
    public static String getDisplayName(Player player) {
        if (Bukkit.getPluginManager().isPluginEnabled("OctoCore")) {
            net.octopvp.octocore.core.objects.PlayerData data = net.octopvp.octocore.core.manager.impl.PlayerManager.getInstance().getData(player);
            if (data != null) return CC.translate(data.getFormattedName(true, player, true));
        }
        return HookManager.getHook(VaultHook.class).map(h -> h.getFormattedName(player)).orElse(player.getDisplayName());
    }

    public static String handleMentions(Player reciever, String message) {
        if (message == null) return null;
        if (reciever == null) return message;
        boolean b = message.contains(reciever.getName());
        if (b && TeamsPlus.getInstance().getConfig().getBoolean("chat.ping-player-on-mention", true)) {
            message = message.replace(reciever.getName(), CC.YELLOW + reciever.getName() + CC.WHITE);
            reciever.playSound(reciever.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
        }
        return message;
    }
}
