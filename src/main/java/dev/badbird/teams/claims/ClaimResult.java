package dev.badbird.teams.claims;

import lombok.Data;
import net.kyori.adventure.text.Component;

@Data
public class ClaimResult {
    private final boolean success;
    private final Component message;
}
