package dev.badbird.teams.claims;

import lombok.Data;

@Data
public class ClaimResult {
    private final boolean success;
    private final String message;
}
