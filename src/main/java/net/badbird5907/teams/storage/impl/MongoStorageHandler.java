package net.badbird5907.teams.storage.impl;

import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import lombok.Getter;
import net.badbird5907.blib.util.Logger;
import net.badbird5907.blib.util.PlayerUtil;
import net.badbird5907.teams.TeamsPlus;
import net.badbird5907.teams.object.PlayerData;
import net.badbird5907.teams.object.Team;
import net.badbird5907.teams.storage.StorageHandler;
import org.bson.Document;
import org.bson.json.JsonWriterSettings;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class MongoStorageHandler implements StorageHandler {
    @Getter
    private static final JsonWriterSettings jsonWriterSettings = JsonWriterSettings.builder()
            .int64Converter((value, writer) -> writer.writeNumber(value.toString()))
            .build();
    private MongoDatabase mongoDatabase = null;
    private MongoClient mongoClient;
    private MongoCollection<Document> teamsCollection, usersCollection;

    @Override
    public void init() {
        Logger.info("Using MongoDB as the storage handler.");
        TeamsPlus plugin = TeamsPlus.getInstance();
        MongoCredential credentials;
        String base = "mongo.auth.";
        if (plugin.getConfig().getBoolean(base + "enabled")) {
            credentials = MongoCredential.createCredential(plugin.getConfig().getString(base + "username"), plugin.getConfig().getString(base + "db"), plugin.getConfig().getString(base + "password").toCharArray());
            mongoClient = MongoClients.create(
                    MongoClientSettings.builder()
                            .applyToClusterSettings(builder ->
                                    builder.hosts(Arrays.asList(new ServerAddress(plugin.getConfig().getString("mongo.host"), plugin.getConfig().getInt("mongo.port")))))
                            .credential(credentials)
                            .build());
        } else {
            mongoClient = MongoClients.create(
                    MongoClientSettings.builder()
                            .applyToClusterSettings(builder ->
                                    builder.hosts(Arrays.asList(new ServerAddress(plugin.getConfig().getString("mongo.host"), plugin.getConfig().getInt("mongo.port")))))
                            .build());
        }
        mongoDatabase = mongoClient.getDatabase(plugin.getConfig().getString("mongo.db"));
        teamsCollection = mongoDatabase.getCollection("teams");
        usersCollection = mongoDatabase.getCollection("users");
    }

    @Override
    public void disable() {
        mongoClient.close();
    }

    @Override
    public @NotNull Set<Team> getTeams() {
        Set<Team> set = new HashSet<>();
        for (Document document : teamsCollection.find()) {
            set.add(TeamsPlus.getGson().fromJson(document.toJson(jsonWriterSettings), Team.class));
        }
        return set;
    }

    @Override
    public PlayerData getData(UUID player) {
        if (!doesDataExist(player))
            return new PlayerData(player).onLoad();
        return TeamsPlus.getGson().fromJson(usersCollection.find(Filters.eq("uuid", player.toString())).first().toJson(getJsonWriterSettings()), PlayerData.class).onLoad();
    }

    @Override
    public PlayerData getData(String name) {
        if (usersCollection.find(Filters.eq("name", name)).first() != null)
            return TeamsPlus.getGson().fromJson(usersCollection.find(Filters.eq("name", name)).first().toJson(getJsonWriterSettings()), PlayerData.class).onLoad();
        else
            return new PlayerData(PlayerUtil.getPlayerUUID(name)).onLoad();
    }

    @Override
    public void saveData(PlayerData playerData) {
        if (doesDataExist(playerData.getUuid())) {
            usersCollection.replaceOne(getProfileDocument(playerData.getUuid()), Document.parse(TeamsPlus.getGson().toJson(playerData)), new ReplaceOptions().upsert(true));
        } else usersCollection.insertOne(Document.parse(TeamsPlus.getGson().toJson(playerData)));
    }

    public Document getProfileDocument(UUID uuid) {
        String a = uuid.toString();
        return usersCollection.find(Filters.eq("uuid", a)).first();
    }

    public boolean doesDataExist(UUID uuid) {
        return usersCollection.find(Filters.eq("uuid", uuid.toString())).first() != null;
    }

    @Override
    public void saveTeam(Team team) {
        String json = TeamsPlus.getGson().toJson(team);
        if (doesTeamDocumentExist(team.getTeamId()))
            teamsCollection.replaceOne(Filters.eq("teamId", team.getTeamId().toString()), Document.parse(json), new ReplaceOptions().upsert(true));
        else teamsCollection.insertOne(Document.parse(json));
    }

    public boolean doesTeamDocumentExist(UUID teamId) {
        return teamsCollection.find(Filters.eq("teamId", teamId.toString())).first() != null;
    }
}
