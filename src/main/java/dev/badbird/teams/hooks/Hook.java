package dev.badbird.teams.hooks;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import dev.badbird.teams.TeamsPlus;

@RequiredArgsConstructor
@Getter
public abstract class Hook {
    private final String plugin;

    public abstract void init(TeamsPlus plugin);

    public abstract void disable(TeamsPlus plugin);

    public void reload() {
    }
}
