package dev.badbird.teams.claims;

// tanks, https://discord.com/channels/289587909051416579/1079267380326314125/1261157798129963129

import dev.badbird.teams.manager.StorageManager;
import dev.badbird.teams.manager.TeamsManager;
import dev.badbird.teams.object.Team;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.UUID;

public class WorldClaimTracker {
    private Long2ObjectMap<UUID> chunkTeamMap; // chunk -> team
    private Object2ObjectMap<UUID, LongSet> teamChunks; // team -> chunks

    private UUID world;

    public WorldClaimTracker(World world) {
        this.world = world.getUID();
        chunkTeamMap = new Long2ObjectOpenHashMap<>();
        teamChunks = new Object2ObjectOpenHashMap<>();
    }

    public void loadClaims() {
        StorageManager.getStorageHandler().getClaimedChunksInWorld(world).forEach((team, chunks) -> {
            teamChunks.put(team, chunks);
            chunks.forEach(chunk -> chunkTeamMap.put(chunk, team));
        });
    }

    public void addClaim(UUID team, long chunk) {
        chunkTeamMap.put(chunk, team);
        LongSet ls = teamChunks.computeIfAbsent(team, k -> new LongOpenHashSet());
        ls.add(chunk);

        StorageManager.getStorageHandler().saveClaimedChunks(team, world, ls);
    }

    public void removeClaim(UUID team, long chunk) {
        chunkTeamMap.remove(chunk);
        LongSet ls = teamChunks.get(team);
        if (ls != null) {
            ls.remove(chunk);
            StorageManager.getStorageHandler().saveClaimedChunks(team, world, ls);
        }
    }

    public boolean isClaimed(long chunk) {
        return chunkTeamMap.containsKey(chunk);
    }

    public boolean isAdminClaim(long chunk) {
        UUID uuid = chunkTeamMap.get(chunk);
        if (uuid == null) return false;
        return isAdminTeam(uuid);
    }

    public boolean isAdminTeam(UUID uuid) {
        return uuid.getLeastSignificantBits() == 0 && uuid.getMostSignificantBits() == 0;
    }

    public UUID getClaimingTeam(long chunk) {
        return chunkTeamMap.get(chunk);
    }

    public boolean canPlayerModify(long chunk, UUID uuid, Team... cachedTeam) {
        Player player = Bukkit.getPlayer(uuid);

        UUID claimingTeam = getClaimingTeam(chunk);
        if (claimingTeam == null) return true; // not claimed
        if (isAdminTeam(claimingTeam)) {
            return player != null && player.hasPermission("teams.claims.admin");
        }
        // Team team = cachedTeam != null ? cachedTeam : TeamsManager.getInstance().getTeamById(uuid);
        Team team = cachedTeam != null && cachedTeam.length > 0 ? cachedTeam[0] : TeamsManager.getInstance().getTeamById(uuid);
        if (team == null) return false;
        return team.getMembers().containsKey(uuid); // TODO: allow teams to configure if their allies can also modify
    }

    public int getClaimCount() {
        return chunkTeamMap.size();
    }
}
