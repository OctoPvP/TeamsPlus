package dev.badbird.teams.listeners;

import dev.badbird.teams.claims.ChunkWrapper;
import dev.badbird.teams.claims.ClaimHandler;
import dev.badbird.teams.claims.ClaimInfo;
import dev.badbird.teams.manager.PlayerManager;
import dev.badbird.teams.manager.TeamsManager;
import dev.badbird.teams.object.Lang;
import dev.badbird.teams.object.Team;
import net.badbird5907.blib.util.Cooldown;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.List;

import static dev.badbird.teams.util.ChatUtil.tr;

public class ClaimListener implements Listener {
    // TODO: staff mode = allow staff to break blocks
    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        handleEvent(event, event.getPlayer(), new ChunkWrapper(event.getBlock().getChunk()));
    }

    private static final List<Material> ALLOWED_INTERACT_BLOCKS = List.of(
            Material.ENDER_CHEST,
            Material.CRAFTING_TABLE,
            Material.SMITHING_TABLE,
            Material.CARTOGRAPHY_TABLE,
            Material.FLETCHING_TABLE,
            Material.STONECUTTER,
            Material.GRINDSTONE
    );

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getInteractionPoint() == null) return;
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getInteractionPoint() != null && event.getInteractionPoint().getBlock().getType() == Material.ENDER_CHEST) {
            return;
        }

        handleEvent(event, event.getPlayer(), new ChunkWrapper(event.getInteractionPoint()));
    }

    public void handleEvent(Cancellable event, Player player, ChunkWrapper wrapper) {
        Team team = PlayerManager.getData(player).getPlayerTeam();
        ClaimInfo claim = ClaimHandler.getInstance().getClaim(wrapper.getHash());
        if (claim != null && !claim.canPlayerModify(player.getUniqueId(), team)) {
            event.setCancelled(true);
            if (Cooldown.isOnCooldown("claim-msg", player.getUniqueId())) {
                return;
            }
            player.sendMessage(Lang.CLAIM_CANNOT_MODIFY.getComponent(
                    tr("claimer", TeamsManager.getInstance().getTeamName(claim.getOwningTeam()))
            ));
            Cooldown.addCooldown("claim-msg", player.getUniqueId(), 3);
        }
    }
}
