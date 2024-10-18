package dev.badbird.teams.listeners;

import dev.badbird.teams.claims.ChunkWrapper;
import dev.badbird.teams.claims.ClaimHandler;
import dev.badbird.teams.claims.WorldClaimTracker;
import dev.badbird.teams.manager.PlayerManager;
import dev.badbird.teams.manager.TeamsManager;
import dev.badbird.teams.object.Lang;
import dev.badbird.teams.object.Team;
import net.badbird5907.blib.util.Cooldown;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static dev.badbird.teams.util.ChatUtil.tr;

public class ClaimListener implements Listener {
    // TODO: staff mode = allow staff to break blocks
    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        handleEvent(event, event.getPlayer(), new ChunkWrapper(event.getBlock().getChunk()));
    }


    private static Map<UUID, UUID> playerToClaimMap = new HashMap<>();

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        onMove(event.getPlayer(), event.getFrom(), event.getTo());
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        playerToClaimMap.remove(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        playerToClaimMap.put(e.getPlayer().getUniqueId(), ClaimHandler.getInstance().getTracker(e.getPlayer().getWorld()).getClaimingTeam(new ChunkWrapper(e.getPlayer().getLocation().getChunk()).getHash()));
    }

    private void onMove(Player player, Location from, Location to) {
        // World fromWorld = from.getWorld();
        World toWorld = to.getWorld();

        // boolean switchedDimensions = !fromWorld.getName().equals(toWorld.getName());
        UUID cachedClaim = playerToClaimMap.get(player.getUniqueId());
        UUID movingTo = ClaimHandler.getInstance().getTracker(toWorld).getClaimingTeam(new ChunkWrapper(to.getChunk()).getHash());
        if (cachedClaim == null && movingTo != null) { //Entering a claim
            // System.out.println("x: " + to.getBlockX() + " y: " + to.getBlockY() + " z: " + to.getBlockZ() + " | " + movingTo + " vs " + cachedClaim);

            // System.out.println("Entering a claim");
            Team toTeam = TeamsManager.getInstance().getTeamById(movingTo);
            if (toTeam == null) {
                return;
            }
            Component enter = Lang.CLAIM_ENTER_TITLE.getComponent(
                    tr("team", toTeam.getName())
            );
            Component sub = Lang.CLAIM_ENTER_SUBTITLE.getComponent(
                    tr("message", toTeam.getClaimDescription())
            );

            playerToClaimMap.put(player.getUniqueId(), movingTo);
            Title title = Title.title(enter, sub);
            player.showTitle(title);
        } else if (cachedClaim != null && movingTo == null) { //Leaving a claim
            // System.out.println("x: " + to.getBlockX() + " y: " + to.getBlockY() + " z: " + to.getBlockZ() + " | " + movingTo + " vs " + cachedClaim);
            // System.out.println("Leaving a claim");
            Team fromTeam = TeamsManager.getInstance().getTeamById(cachedClaim);
            if (fromTeam == null) {
                return;
            }
            Component leave = Lang.CLAIM_LEAVE_TITLE.getComponent(
                    tr("team", fromTeam.getName())
            );
            Component sub = Lang.CLAIM_LEAVE_SUBTITLE.getComponent(
                    tr("message", fromTeam.getClaimDescription())
            );
            playerToClaimMap.remove(player.getUniqueId());
            Title title = Title.title(leave, sub);
            player.showTitle(title);
        }
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
        Location ip = event.getInteractionPoint();
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && ip != null && event.getClickedBlock() != null && ALLOWED_INTERACT_BLOCKS.contains(event.getClickedBlock().getType())) {
            return;
        }
        if (ip == null) {
            return;
        }
        handleEvent(event, event.getPlayer(), new ChunkWrapper(ip));
    }

    public void handleEvent(Cancellable event, Player player, ChunkWrapper wrapper) {
        Team team = PlayerManager.getData(player).getPlayerTeam();
        WorldClaimTracker tracker = ClaimHandler.getInstance().getTracker(wrapper.getWorld());
        if (!tracker.canPlayerModify(wrapper.getHash(), player.getUniqueId(), team)) {
            event.setCancelled(true);
            if (Cooldown.isOnCooldown("claim-msg", player.getUniqueId())) {
                return;
            }
            player.sendMessage(Lang.CLAIM_CANNOT_MODIFY.getComponent(
                    tr("claimer", TeamsManager.getInstance().getTeamName(tracker.getClaimingTeam(wrapper.getHash())))
            ));
            Cooldown.addCooldown("claim-msg", player.getUniqueId(), 3);
        }
    }
}
