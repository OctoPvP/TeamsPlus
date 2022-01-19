package net.badbird5907.teams.api.events;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.badbird5907.teams.manager.StorageManager;
import net.badbird5907.teams.storage.StorageHandler;
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
