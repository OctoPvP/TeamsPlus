package net.badbird5907.teams.api;

import lombok.Getter;
import lombok.Setter;
import net.badbird5907.teams.TeamsPlus;
import net.badbird5907.teams.manager.StorageManager;
import net.badbird5907.teams.manager.TeamsManager;
import net.badbird5907.teams.storage.StorageHandler;

@Getter
@Setter
public class TeamsPlusAPI {
    private static TeamsPlusAPI instance;
    public TeamsPlusAPI(){
        if (instance != null)
            throw new IllegalStateException("Cannot create more than one TeamsPlusAPI instance.");
        instance = this;

    }
    public void setStorageHandler(StorageHandler storageHandler){
        StorageHandler handler = StorageManager.getStorageHandler();
        StorageManager.getInstance().setStorageHandler(storageHandler);
        reload(handler);
    }
    public void reload(StorageHandler prevHandler){
        if (!getStorageManager().isCurrentlyInit())
            prevHandler.disable();
        StorageManager.getStorageHandler().init();
    }
    public TeamsManager getTeamsManager(){
        return TeamsPlus.getInstance().getTeamsManager();
    }
    public StorageManager getStorageManager(){
        return TeamsPlus.getInstance().getStorageManager();
    }
}
