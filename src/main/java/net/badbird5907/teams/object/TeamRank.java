package net.badbird5907.teams.object;

public enum TeamRank {
    OWNER(5),
    ADMIN(4),
    MODERATOR(3),
    TRUSTED(2),
    MEMBER(1),
    RECRUIT(0);
    private int permissionLevel;

    TeamRank(int level) {
        this.permissionLevel = level;
    }

    public int getPermissionLevel() {
        return this.permissionLevel;
    }
}
