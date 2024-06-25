package dev.badbird.teams.storage.impl;

import com.google.gson.Gson;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.*;
import dev.badbird.teams.TeamsPlus;
import dev.badbird.teams.object.PlayerData;
import dev.badbird.teams.object.Team;
import dev.badbird.teams.storage.StorageHandler;
import lombok.Getter;
import net.badbird5907.blib.util.Logger;
import net.badbird5907.blib.util.PlayerUtil;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.json.JsonWriterSettings;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class MongoStorageHandler implements StorageHandler {
    @Getter
    private static final JsonWriterSettings jsonWriterSettings = JsonWriterSettings.builder()
            .int64Converter((value, writer) -> writer.writeNumber(value.toString()))
            .build();
    private MongoDatabase mongoDatabase = null;
    private MongoClient mongoClient;
    private MongoCollection<Document> teamsCollection, usersCollection;
    private Gson gson = TeamsPlus.getGson();

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
            set.add(gson.fromJson(document.toJson(jsonWriterSettings), Team.class));
        }
        return set;
    }

    @Override
    public PlayerData getData(UUID player) {
        Document doc = usersCollection.find(Filters.eq("uuid", player.toString())).first();
        if (doc == null)
            return new PlayerData(player).onLoad();
        return gson.fromJson(doc.toJson(getJsonWriterSettings()), PlayerData.class).onLoad();
    }

    @Override
    public PlayerData getData(String name) {
        UUID uuid = PlayerUtil.getPlayerUUID(name);
        Document doc = usersCollection.find(Filters.eq("uuid", uuid.toString())).first();
        if (doc != null)
            return gson.fromJson(doc.toJson(getJsonWriterSettings()), PlayerData.class).onLoad();
        else
            return new PlayerData(uuid).onLoad();
    }

    @Override
    public void saveData(PlayerData playerData) {
        UpdateOptions updateOptions = new UpdateOptions().upsert(true);
        String uuid = playerData.getUuid().toString();
        Bson filter = Filters.eq("uuid", uuid);
        Document doc = Document.parse(gson.toJson(playerData));
        Bson update = new Document("$set", doc);
        usersCollection.updateOne(
                filter,
                update,
                updateOptions
        );
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
        String json = gson.toJson(team);
        // upsert the team
        UpdateOptions updateOptions = new UpdateOptions().upsert(true);
        teamsCollection.updateOne(
                Filters.eq("teamId", team.getTeamId().toString()),
                new Document("$set", Document.parse(json)),
                updateOptions
        );
    }

    @Override
    public void saveTeams(Collection<Team> teams) {
        List<WriteModel<Document>> operations = new ArrayList<>();
        UpdateOptions ups = new UpdateOptions().upsert(true);

        for (Team team : teams) {
            String json = gson.toJson(team);
            Document teamDocument = Document.parse(json);

            UpdateOneModel<Document> upsert = new UpdateOneModel<>(
                    Filters.eq("teamId", team.getTeamId().toString()),
                    new Document("$set", teamDocument),
                    ups
            );

            operations.add(upsert);
        }

        teamsCollection.bulkWrite(operations);
    }

    @Override
    public void removeTeam(Team team) {
        teamsCollection.deleteOne(Filters.eq("teamId", team.getTeamId().toString()));
    }

    public boolean doesTeamDocumentExist(UUID teamId) {
        return teamsCollection.find(Filters.eq("teamId", teamId.toString())).first() != null;
    }
}
