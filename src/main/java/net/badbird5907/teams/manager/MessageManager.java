package net.badbird5907.teams.manager;

import net.badbird5907.anticombatlog.utils.CC;
import net.badbird5907.blib.utils.StringUtils;
import net.badbird5907.teams.TeamsPlus;
import net.badbird5907.teams.object.player.PlayerData;
import org.bukkit.entity.Player;

public class MessageManager {
    public static String formatGlobal(String rawMessage, Player player, Player receiver) {
        PlayerData senderData = PlayerManager.getData(player), receiverData = PlayerManager.getData(player);
        String format = TeamsPlus.getInstance().getConfig().getString("chat.custom." + (senderData.isInTeam() ? "format-global-inteam" : "format-global-noteam"));
        String message;
        if (senderData.isInTeam()) {
            String color = CC.AQUA;
            if (player == receiver)
                color = CC.GREEN;
            if (senderData.isEnemy(receiver)) {
                color = CC.RED;
            } else if (senderData.isAlly(receiver))
                color = CC.PURPLE;
            message = StringUtils.replacePlaceholders(format, color, senderData.getPlayerTeam().getName(), senderData, player.getName(), rawMessage);
        } else {
            String color = CC.GREEN;
            if (player == receiver)
                color = CC.GREEN;
            if (senderData.isEnemy(receiver)) {
                color = CC.RED;
            } else if (senderData.isAlly(receiver))
                color = CC.PURPLE;
            message = StringUtils.replacePlaceholders(format, color, senderData.getName(), rawMessage);
        }
        return message;
    }
}
