package dev.badbird.teams.util;

import com.google.gson.*;
import dev.badbird.teams.TeamsPlus;
import dev.badbird.teams.claims.ChunkWrapper;
import dev.badbird.teams.claims.ClaimInfo;
import dev.badbird.teams.object.Team;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class TeamGsonAdapter implements JsonSerializer<Team>, JsonDeserializer<Team> {
    @Override
    public Team deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject obj = jsonElement.getAsJsonObject();
        JsonObject copyWithoutClaims = new JsonObject();
        for (Map.Entry<String, JsonElement> entry : obj.entrySet()) { // TODO: optimize this shit
            if (!entry.getKey().equals("__claims")) {
                copyWithoutClaims.add(entry.getKey(), entry.getValue());
            }
        }
        Map<Long, ClaimInfo> claimMap = new HashMap<>();
        Team team = TeamsPlus.getCleanGson().fromJson(copyWithoutClaims, Team.class);
        if (obj.has("__claims")) {
            JsonArray claimsObj = obj.getAsJsonArray("__claims");
            for (JsonElement entry : claimsObj) {
                claimMap.put(Long.parseLong(entry.getAsString()), new ClaimInfo(new ChunkWrapper(entry.getAsLong()), team.getTeamId()));
            }
        }
        team.setClaims(claimMap);
        return team;
    }

    @Override
    public JsonElement serialize(Team team, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject obj = TeamsPlus.getCleanGson().toJsonTree(team).getAsJsonObject();
        JsonArray claimsObj = new JsonArray();
        for (Map.Entry<Long, ClaimInfo> entry : team.getClaims().entrySet()) {
            // claimsObj.addProperty(entry.getKey().toString(), entry.getValue().getChunkHash());
            claimsObj.add(entry.getValue().getChunkHash());
        }
        obj.add("__claims", claimsObj);
        return obj;
    }
}

