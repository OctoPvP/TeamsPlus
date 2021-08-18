package net.badbird5907.teams.manager;

import lombok.Getter;
import lombok.Setter;
import net.badbird5907.teams.TeamsPlus;
import net.badbird5907.teams.api.events.StorageManagerLoadEvent;
import net.badbird5907.teams.object.StorageType;
import net.badbird5907.teams.storage.StorageHandler;
import net.badbird5907.teams.storage.impl.FlatFileStorageHandler;
import net.badbird5907.teams.storage.impl.MongoStorageHandler;
import net.badbird5907.teams.storage.impl.SQLStorageHandler;
import org.bukkit.Bukkit;

public class StorageManager {
    @Setter
    private StorageHandler storageHandler = new FlatFileStorageHandler();
    @Getter
    private static StorageManager instance;
    @Getter
    private boolean currentlyInit = false;
    public StorageManager(){
        instance = this;
        StorageType type = StorageType.valueOf(TeamsPlus.getInstance().getConfig().getString("data-storage"));
        if (type == StorageType.MONGO)
            storageHandler = new MongoStorageHandler();
        if (type == StorageType.SQL)
            storageHandler = new SQLStorageHandler();
        //dont need to check flatfile since thats default
        currentlyInit = true;
        StorageManagerLoadEvent event = new StorageManagerLoadEvent(this,storageHandler);
        Bukkit.getPluginManager().callEvent(event);
        storageHandler = event.getStorageHandler();
        currentlyInit = false;
        storageHandler.init();
    }
    public static StorageHandler getStorageHandler(){
        return getInstance().storageHandler;
    }
}
