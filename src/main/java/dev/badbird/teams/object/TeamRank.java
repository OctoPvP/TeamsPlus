package dev.badbird.teams.object;

public enum TeamRank {
    OWNER(5),
    ADMIN(4),
    MODERATOR(3),
    TRUSTED(2),
    MEMBER(1),
    RECRUIT(0);
    private final int permissionLevel;

    TeamRank(int level) {
        this.permissionLevel = level;
    }

    public static TeamRank getRank(int level) {
        return switch (level) {
            case 5 -> OWNER;
            case 4 -> ADMIN;
            case 3 -> MODERATOR;
            case 2 -> TRUSTED;
            case 1 -> MEMBER;
            default -> RECRUIT;
        };
    }

    public int getPermissionLevel() {
        return this.permissionLevel;
    }
}
