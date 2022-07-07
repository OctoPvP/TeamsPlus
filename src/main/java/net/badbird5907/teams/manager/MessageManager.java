package net.badbird5907.teams.manager;

import net.badbird5907.blib.util.CC;
import net.badbird5907.blib.utils.StringUtils;
import net.badbird5907.teams.TeamsPlus;
import net.badbird5907.teams.hooks.Hook;
import net.badbird5907.teams.hooks.impl.CoreProtectHook;
import net.badbird5907.teams.hooks.impl.VaultHook;
import net.badbird5907.teams.object.ChatChannel;
import net.badbird5907.teams.object.Lang;
import net.badbird5907.teams.object.PlayerData;
import net.badbird5907.teams.object.Team;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class MessageManager {

    public static void handleAlly(PlayerData data, Player player, Team senderTeam, String message) {
        Team targetTeam = TeamsManager.getInstance().getTeamById(data.getAllyChatTeamId());
        if (targetTeam == null) {
            data.setAllyChatTeamId(null);
            data.setCurrentChannel(ChatChannel.GLOBAL);
            return;
        }
        targetTeam.broadcast(Lang.CHAT_FORMAT_ALLY.toString(MessageManager.getDisplayName(player), targetTeam.getName(), senderTeam.getName(), message));
        senderTeam.broadcast(Lang.CHAT_FORMAT_ALLY.toString(MessageManager.getDisplayName(player), senderTeam.getName(), targetTeam.getName(), message));

        CoreProtectHook hook = null;
        Hook h = HookManager.getHook(CoreProtectHook.class);
        if (h == null) return;
        hook = (CoreProtectHook) h;
        if (hook.isEnabled()) {
            hook.logChat(player, message, ChatChannel.ALLY);
        }
   }

    public static void handleTeam(Player player, String message, Team team) {
        team.broadcast(Lang.CHAT_FORMAT_TEAM.toString(MessageManager.getDisplayName(player), message));
        CoreProtectHook hook = null;
        Hook h = HookManager.getHook(CoreProtectHook.class);
        if (h == null) return;
        hook = (CoreProtectHook) h;
        if (hook.isEnabled()) {
            hook.logChat(player, message, ChatChannel.TEAM);
        }
    }

    public static boolean handleGlobal(PlayerData data, Player player, String message) {
        if (TeamsPlus.getInstance().getConfig().getBoolean("chat.enable")) {
            if (data == null) return false;
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                onlinePlayer.sendMessage(MessageManager.formatGlobal(message, player, onlinePlayer));
            }//TODO team/ally chat
        }

        CoreProtectHook hook = null;
        Hook h = HookManager.getHook(CoreProtectHook.class);
        if (h == null) return true;
        hook = (CoreProtectHook) h;
        if (hook.isEnabled()) {
            hook.logChat(player, message, ChatChannel.GLOBAL);
        }
        return true;
    }

    public static String formatGlobal(String rawMessage, Player player, Player receiver) {
        PlayerData senderData = PlayerManager.getData(player), receiverData = PlayerManager.getData(player);
        String format = senderData.isInTeam() ?
                Lang.CHAT_FORMAT_GLOBAL_INTEAM.toString() :
                Lang.CHAT_FORMAT_GLOBAL_NOTEAM.toString()
                ;
        //String format = TeamsPlus.getInstance().getConfig().getString("chat.custom." + (senderData.isInTeam() ? "format-global-inteam" : "format-global-noteam"));
        String formattedName = getDisplayName(player);
        String message;
        if (senderData.isInTeam()) {
            String color = CC.AQUA;
            if (player.getUniqueId().equals(receiver.getUniqueId())) {
                color = CC.GREEN;
            } if (senderData.isEnemy(receiver)) {
                color = CC.RED;
            } else if (senderData.isAlly(receiver))
                color = CC.PURPLE;
            message = StringUtils.replacePlaceholders(format, color, senderData.getPlayerTeam().getName(), formattedName, rawMessage);
        } else {
            String color = CC.GREEN;
            if (player.getUniqueId().equals(receiver.getUniqueId()))
                color = CC.GREEN;
            if (senderData.isEnemy(receiver)) {
                color = CC.RED;
            } else if (senderData.isAlly(receiver))
                color = CC.PURPLE;
            message = StringUtils.replacePlaceholders(format, color, formattedName, rawMessage);
        }
        return message;
    }
    public static String getDisplayName(Player player) {
        String formattedName = player.getDisplayName();
        VaultHook vaultHook = (VaultHook) HookManager.getHook(VaultHook.class);
        if (vaultHook != null) {
            formattedName = vaultHook.getFormattedName(player);
        }
        return formattedName;
    }
}
