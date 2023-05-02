package dev.gillin.mc.vnllausermanagement.player;

public class GroupInfo {
    private String groupName;
    private boolean active;
    private int currentVotes;
    private long expiration;

    public GroupInfo(String groupName) {
        this.groupName = groupName;
        this.active = false;
        this.currentVotes = 0;
        this.expiration = 0L;
    }

    public GroupInfo(String groupName, boolean active, int currentVotes, long expiration) {
        this.groupName = groupName;
        this.active = active;
        this.currentVotes = currentVotes;
        this.expiration = expiration;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public int getCurrentVotes() {
        return currentVotes;
    }

    public void setCurrentVotes(int currentVotes) {
        this.currentVotes = currentVotes;
    }

    public long getExpiration() {
        return expiration;
    }

    public void setExpiration(long expiration) {
        this.expiration = expiration;
    }

    // getters and setters for each property
}
