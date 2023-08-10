package dev.badbird.teams.api.events;

import dev.badbird.teams.manager.StorageManager;
import dev.badbird.teams.storage.StorageHandler;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor
@Getter
public class StorageManagerLoadEvent extends Event {
    private final StorageManager storageManager;
    @Getter
    private final StorageHandler storageHandler;

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return new HandlerList();
    }
}
