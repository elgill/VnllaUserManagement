package dev.gillin.mc.vnllausermanagement.datamodels;

import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;

public class ServerConfigModel {

    public static final String SQLITE_FILENAME = "sqlite.filename";
    public static final String STATUS_OBJECTIVES = "status_objectives";
    public static final String STATS_OBJECTIVES = "stats_objectives";
    public static final String VOTE_AWARD_COMMANDS = "vote_award_commands";
    private String sqliteFileName;

    private List<String> voteAwardCommands;

    private List<String> statusObjectives;
    private List<String> statsObjectives;

    public String getSqliteFileName() {
        return sqliteFileName;
    }

    public void setSqliteFileName(String sqliteFileName) {
        this.sqliteFileName = sqliteFileName;
    }

    public List<String> getVoteAwardCommands() {
        return voteAwardCommands;
    }

    public void setVoteAwardCommands(List<String> voteAwardCommands) {
        this.voteAwardCommands = voteAwardCommands;
    }

    public List<String> getStatusObjectives() {
        return statusObjectives;
    }

    public void setStatusObjectives(List<String> statusObjectives) {
        this.statusObjectives = statusObjectives;
    }

    public List<String> getStatsObjectives() {
        return statsObjectives;
    }

    public void setStatsObjectives(List<String> statsObjectives) {
        this.statsObjectives = statsObjectives;
    }

    public static ServerConfigModel fromConfigFile(FileConfiguration config){
        ServerConfigModel serverConfigModel = new ServerConfigModel();
        serverConfigModel.setSqliteFileName(config.getString(SQLITE_FILENAME));
        serverConfigModel.setStatusObjectives(config.getStringList(STATUS_OBJECTIVES));
        serverConfigModel.setStatsObjectives(config.getStringList(STATS_OBJECTIVES));
        serverConfigModel.setVoteAwardCommands(config.getStringList(VOTE_AWARD_COMMANDS));
        return  serverConfigModel;
    }

    @Override
    public String toString() {
        return "ServerConfigModel{" +
                "sqliteFileName='" + sqliteFileName + '\'' +
                ", voteAwardCommands=" + voteAwardCommands +
                ", statusObjectives=" + statusObjectives +
                ", statsObjectives=" + statsObjectives +
                '}';
    }
}
