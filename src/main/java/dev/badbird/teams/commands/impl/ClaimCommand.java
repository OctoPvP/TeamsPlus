package dev.badbird.teams.commands.impl;

import dev.badbird.teams.claims.ClaimHandler;
import dev.badbird.teams.claims.ClaimResult;
import dev.badbird.teams.commands.annotation.Sender;
import dev.badbird.teams.commands.annotation.TeamPermission;
import dev.badbird.teams.object.Team;
import dev.badbird.teams.object.TeamRank;
import net.octopvp.octocore.core.utils.SoundUtil;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.CommandDescription;
import org.incendo.cloud.annotations.processing.CommandContainer;

@CommandContainer
public class ClaimCommand {
    @TeamPermission(TeamRank.TRUSTED)
    @Command("claim")
    @CommandDescription("Claim a chunk for your team")
    public void claim(@Sender Player sender, @Sender Team senderTeam) {
        long chunkHash = ClaimHandler.getInstance().hashChunk(sender.getChunk());
        if (ClaimHandler.getInstance().isClaimed(chunkHash)) {
            sender.sendRichMessage("<red>This chunk is already claimed.");
            return;
        }
        ClaimResult result = senderTeam.claim(sender, sender.getLocation());
        if (!result.isSuccess()){
            SoundUtil.playError(sender);
        }
        sender.sendMessage(result.getMessage());
    }
}
