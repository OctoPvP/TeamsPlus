package dev.badbird.teams.commands.impl;

import dev.badbird.teams.claims.ClaimHandler;
import dev.badbird.teams.claims.ClaimResult;
import dev.badbird.teams.commands.annotation.TeamPermission;
import dev.badbird.teams.object.Team;
import dev.badbird.teams.object.TeamRank;
import net.octopvp.commander.annotation.Command;
import net.octopvp.commander.annotation.Sender;
import org.bukkit.entity.Player;

@Command(name = "claim", description = "Claim a chunk for your team")
public class ClaimCommand {
    @TeamPermission(TeamRank.TRUSTED)
    @Command(name = "", description = "Claim a chunk for your team")
    public void claim(@Sender Player sender, @Sender Team senderTeam) {
        long chunkHash = ClaimHandler.getInstance().hashChunk(sender.getChunk());
        if (ClaimHandler.getInstance().isClaimed(chunkHash)) {
            sender.sendMessage("This chunk is already claimed.");
            return;
        }
        ClaimResult result = senderTeam.claim(sender, sender.getLocation());
    }
}
