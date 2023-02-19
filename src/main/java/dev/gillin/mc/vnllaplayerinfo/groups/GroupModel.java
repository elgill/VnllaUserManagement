package dev.gillin.mc.vnllaplayerinfo.groups;

public class GroupModel {
    private String displayName;
    private int votesRequired;
    private long rankLength;
    private String earnRankCommand;
    private String loseRankCommand;

    public GroupModel() {
    }

    public GroupModel(String displayName, int votesRequired, long rankLength, String earnRankCommand, String loseRankCommand) {
        this.displayName = displayName;
        this.votesRequired = votesRequired;
        this.rankLength = rankLength;
        this.earnRankCommand = earnRankCommand;
        this.loseRankCommand = loseRankCommand;
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

    public String getEarnRankCommand() {
        return earnRankCommand;
    }

    public void setEarnRankCommand(String earnRankCommand) {
        this.earnRankCommand = earnRankCommand;
    }

    public String getLoseRankCommand() {
        return loseRankCommand;
    }

    public void setLoseRankCommand(String loseRankCommand) {
        this.loseRankCommand = loseRankCommand;
    }

}
