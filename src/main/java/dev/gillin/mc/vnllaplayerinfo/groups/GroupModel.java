package dev.gillin.mc.vnllaplayerinfo.groups;

public class GroupModel {
    private String displayName;
    private int votesRequired;
    private long rankLength;
    private String[] earnRankCommands;
    private String[] loseRankCommands;

    public GroupModel() {
    }

    public GroupModel(String displayName, int votesRequired, long rankLength, String[] earnRankCommands, String[] loseRankCommands) {
        this.displayName = displayName;
        this.votesRequired = votesRequired;
        this.rankLength = rankLength;
        this.earnRankCommands = earnRankCommands;
        this.loseRankCommands = loseRankCommands;
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

    public String[] getEarnRankCommands() {
        return earnRankCommands;
    }

    public void setEarnRankCommands(String[] earnRankCommands) {
        this.earnRankCommands = earnRankCommands;
    }

    public String[] getLoseRankCommands() {
        return loseRankCommands;
    }

    public void setLoseRankCommands(String[] loseRankCommands) {
        this.loseRankCommands = loseRankCommands;
    }
}
