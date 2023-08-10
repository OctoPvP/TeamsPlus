package dev.badbird.teams.api;

import dev.badbird.teams.TeamsPlus;
import dev.badbird.teams.manager.StorageManager;
import dev.badbird.teams.manager.TeamsManager;
import dev.badbird.teams.storage.StorageHandler;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TeamsPlusAPI {
    private static TeamsPlusAPI instance;

    public TeamsPlusAPI() {
        if (instance != null)
            throw new IllegalStateException("Cannot create more than one TeamsPlusAPI instance.");
        instance = this;

    }

    public void setStorageHandler(StorageHandler storageHandler) {
        StorageHandler handler = StorageManager.getStorageHandler();
        StorageManager.getInstance().setStorageHandler(storageHandler);
        reload(handler);
    }

    public void reload(StorageHandler prevHandler) {
        if (!getStorageManager().isCurrentlyInit())
            prevHandler.disable();
        StorageManager.getStorageHandler().init();
    }

    public TeamsManager getTeamsManager() {
        return TeamsPlus.getInstance().getTeamsManager();
    }

    public StorageManager getStorageManager() {
        return TeamsPlus.getInstance().getStorageManager();
    }
}
