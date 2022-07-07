package net.badbird5907.teams.manager;

import net.badbird5907.blib.util.CC;
import net.badbird5907.blib.utils.StringUtils;
import net.badbird5907.teams.TeamsPlus;
import net.badbird5907.teams.hooks.impl.VaultHook;
import net.badbird5907.teams.object.Lang;
import net.badbird5907.teams.object.PlayerData;
import org.bukkit.entity.Player;

public class MessageManager {
    public static String formatGlobal(String rawMessage, Player player, Player receiver) {
        PlayerData senderData = PlayerManager.getData(player), receiverData = PlayerManager.getData(player);
        String format = senderData.isInTeam() ?
                Lang.CHAT_FORMAT_GLOBAL_INTEAM.toString() :
                Lang.CHAT_FORMAT_GLOBAL_NOTEAM.toString()
                ;
        //String format = TeamsPlus.getInstance().getConfig().getString("chat.custom." + (senderData.isInTeam() ? "format-global-inteam" : "format-global-noteam"));
        String formattedName = player.getDisplayName();
        VaultHook vaultHook = (VaultHook) HookManager.getHook(VaultHook.class);
        if (vaultHook != null) {
            formattedName = vaultHook.getFormattedName(player);
        }
        String message;
        if (senderData.isInTeam()) {
            String color = CC.AQUA;
            if (player == receiver)
                color = CC.GREEN;
            if (senderData.isEnemy(receiver)) {
                color = CC.RED;
            } else if (senderData.isAlly(receiver))
                color = CC.PURPLE;
            message = StringUtils.replacePlaceholders(format, color, senderData.getPlayerTeam().getName(), formattedName, rawMessage);
        } else {
            String color = CC.GREEN;
            if (player == receiver)
                color = CC.GREEN;
            if (senderData.isEnemy(receiver)) {
                color = CC.RED;
            } else if (senderData.isAlly(receiver))
                color = CC.PURPLE;
            message = StringUtils.replacePlaceholders(format, color, formattedName, rawMessage);
        }
        return message;
    }
}
