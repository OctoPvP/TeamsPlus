package dev.badbird.teams.manager;

import lombok.Getter;
import lombok.Setter;
import net.badbird5907.blib.util.Logger;
import dev.badbird.teams.TeamsPlus;
import dev.badbird.teams.api.events.StorageManagerLoadEvent;
import dev.badbird.teams.object.StorageType;
import dev.badbird.teams.storage.StorageHandler;
import dev.badbird.teams.storage.impl.FlatFileStorageHandler;
import dev.badbird.teams.storage.impl.MongoStorageHandler;
import org.bukkit.Bukkit;

public class StorageManager {
    @Getter
    private static StorageManager instance;
    @Setter
    private StorageHandler storageHandler = new FlatFileStorageHandler();
    @Getter
    private boolean currentlyInit = false;

    public StorageManager() {
        instance = this;
        StorageType type = StorageType.valueOf(TeamsPlus.getInstance().getConfig().getString("data-storage", "FLATFILE"));
        Logger.info("Starting storage handler %1", type.name());
        if (type == StorageType.MONGO)
            storageHandler = new MongoStorageHandler();
        if (type == StorageType.SQL)
            storageHandler = new SQLStorageHandler();
        //dont need to check flatfile since thats default
        currentlyInit = true;
        StorageManagerLoadEvent event = new StorageManagerLoadEvent(this, storageHandler);
        Bukkit.getPluginManager().callEvent(event);
        storageHandler = event.getStorageHandler();
        currentlyInit = false;
        storageHandler.init();
    }

    public static StorageHandler getStorageHandler() {
        return getInstance().storageHandler;
    }
}
