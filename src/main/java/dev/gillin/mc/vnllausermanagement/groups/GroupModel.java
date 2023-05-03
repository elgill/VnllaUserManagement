package dev.gillin.mc.vnllausermanagement.groups;

import java.util.List;

public class GroupModel {
    private String groupKey;
    private String displayName;
    private int votesRequired;
    private long rankLength;
    private List<String> earnRankCommands;
    private List<String> loseRankCommands;
    private boolean voteAchievable;
    private String luckPermsGroupName;

    public GroupModel(String groupKey){
        this.groupKey = groupKey;
    }

    public String getGroupKey() {
        return groupKey;
    }

    public void setGroupKey(String groupKey) {
        this.groupKey = groupKey;
    }

    public void setLuckPermsGroupName(String luckPermsGroupName) {
        this.luckPermsGroupName = luckPermsGroupName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public int getVotesRequired() {
        return votesRequired;
    }

    public void setVotesRequired(int votesRequired) {
        this.votesRequired = votesRequired;
    }

    public long getRankLength() {
        return rankLength;
    }

    public void setRankLength(long rankLength) {
        this.rankLength = rankLength;
    }

    public List<String> getEarnRankCommands() {
        return earnRankCommands;
    }

    public void setEarnRankCommands(List<String> earnRankCommands) {
        this.earnRankCommands = earnRankCommands;
    }

    public List<String> getLoseRankCommands() {
        return loseRankCommands;
    }

    public void setLoseRankCommands(List<String> loseRankCommands) {
        this.loseRankCommands = loseRankCommands;
    }

    public boolean isVoteAchievable() {
        return voteAchievable;
    }

    public void setVoteAchievable(boolean voteAchievable) {
        this.voteAchievable = voteAchievable;
    }

    public String getLuckPermsGroupName() {
        return luckPermsGroupName;
    }

    @Override
    public String toString() {
        return "GroupModel{" +
                "groupKey='" + groupKey + '\'' +
                ", displayName='" + displayName + '\'' +
                ", votesRequired=" + votesRequired +
                ", rankLength=" + rankLength +
                ", earnRankCommands=" + earnRankCommands +
                ", loseRankCommands=" + loseRankCommands +
                ", voteAchievable=" + voteAchievable +
                ", luckPermsGroupName='" + luckPermsGroupName + '\'' +
                '}';
    }
}
