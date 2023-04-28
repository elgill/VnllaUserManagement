package dev.gillin.mc.vnllausermanagement.player;

import dev.gillin.mc.vnllausermanagement.VnllaUserManagement;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PlayerConfigModel {
    public static final String TOTAL_VOTES = "votes.totalVotes";
    public static final String VOTES_OWED = "votes.owed";
    public static final String GROUPS_SECTION = "groups";
    public static final String LAST_LOGIN = "playtime.lastLogin";
    public static final String LAST_LOGOUT = "playtime.lastLogout";
    public static final String TOTAL_ALL_TIME = "playtime.totalAllTime";
    public static final String LASTLOCATION_X = "lastlocation.x";
    public static final String LASTLOCATION_Y = "lastlocation.y";
    public static final String LASTLOCATION_Z = "lastlocation.z";
    public static final String LASTLOCATION_WORLD = "lastlocation.world";
    public static final String LAST_PLAYER_NAME = "lastPlayerName";
    public static final String IPS = "ips";
    public static final String EARNED_GROUPS = "earnedGroups";
    public static final String ACTIVE = "active";
    public static final String CURRENT_VOTES = "currentVotes";
    public static final String EXPIRATION = "expiration";
    public static final String PLAYERDATA_FOLDER = "playerdata";
    public static final String PENDING_EARN_GROUP="pendingEarnGroup";
    public static final String PENDING_LOSE_GROUP="pendingLoseGroup";
    private String playerId;
    private int totalVotes;
    private int votesOwed; //votes.owed
    private long lastLogin; //playtime.lastLogin
    private long lastLogout; //playtime.lastLogout
    private long totalPlaytime; //playtime.totalAllTime
    private double lastLocationX; //lastlocation.x
    private double lastLocationY; //lastlocation.y
    private double lastLocationZ; //lastlocation.z
    private String lastLocationWorld; //lastlocation.world
    private String lastPlayerName; //lastPlayerName
    private List<String> ipAddresses;
    private List<String> groups;
    private List<String> pendingEarnedGroups;
    private List<String> pendingLostGroups;
    private FileConfiguration config;
    private Map<String, GroupInfo> groupInfos;
    static final Logger logger = Bukkit.getLogger();

    public String getPlayerId() {
        return playerId;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }

    public int getTotalVotes() {
        return totalVotes;
    }

    public void setTotalVotes(int totalVotes) {
        this.totalVotes = totalVotes;
    }

    public int getVotesOwed() {
        return votesOwed;
    }

    public void setVotesOwed(int votesOwed) {
        this.votesOwed = votesOwed;
    }


    public long getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(long lastLogin) {
        this.lastLogin = lastLogin;
    }

    public long getLastLogout() {
        return lastLogout;
    }

    public void setLastLogout(long lastLogout) {
        this.lastLogout = lastLogout;
    }

    public long getTotalPlaytime() {
        return totalPlaytime;
    }

    public void setTotalPlaytime(long totalPlaytime) {
        this.totalPlaytime = totalPlaytime;
    }

    public double getLastLocationX() {
        return lastLocationX;
    }

    public void setLastLocationX(double lastLocationX) {
        this.lastLocationX = lastLocationX;
    }

    public double getLastLocationY() {
        return lastLocationY;
    }

    public void setLastLocationY(double lastLocationY) {
        this.lastLocationY = lastLocationY;
    }

    public double getLastLocationZ() {
        return lastLocationZ;
    }

    public void setLastLocationZ(double lastLocationZ) {
        this.lastLocationZ = lastLocationZ;
    }

    public String getLastLocationWorld() {
        return lastLocationWorld;
    }

    public void setLastLocationWorld(String lastLocationWorld) {
        this.lastLocationWorld = lastLocationWorld;
    }

    public String getLastPlayerName() {
        return lastPlayerName;
    }

    public void setLastPlayerName(String lastPlayerName) {
        this.lastPlayerName = lastPlayerName;
    }

    public List<String> getGroups() {
        return groups;
    }

    public void setGroups(List<String> groups) {
        this.groups = groups;
    }

    public List<String> getIpAddresses() {
        if(ipAddresses == null){
            ipAddresses = new ArrayList<>();
        }
        return ipAddresses;
    }

    public void setIpAddresses(List<String> ipAddresses) {
        this.ipAddresses = ipAddresses;
    }

    public Map<String, GroupInfo> getGroupInfos() {
        return groupInfos;
    }

    public void setGroupInfos(Map<String, GroupInfo> groupInfos) {
        this.groupInfos = groupInfos;
    }

    public FileConfiguration getConfig() {
        return config;
    }

    public void setConfig(FileConfiguration config) {
        this.config = config;
    }

    public List<String> getPendingEarnedGroups() {
        if(pendingEarnedGroups == null){
            pendingEarnedGroups = new ArrayList<>();
        }
        return pendingEarnedGroups;
    }

    public void setPendingEarnedGroups(List<String> pendingEarnedGroups) {
        this.pendingEarnedGroups = pendingEarnedGroups;
    }

    public List<String> getPendingLostGroups() {
        if(pendingLostGroups == null){
            pendingLostGroups = new ArrayList<>();
        }
        return pendingLostGroups;
    }

    public void setPendingLostGroups(List<String> pendingLostGroups) {
        this.pendingLostGroups = pendingLostGroups;
    }

    //returns file from playerdata folder in plugin folder
    public static File getPlayerFile(VnllaUserManagement plugin, String uuid) {
        File f = new File(plugin.getDataFolder() + File.separator + PLAYERDATA_FOLDER + File.separator + uuid + ".yml");
        if (!f.exists()) {
            try {
                if(f.createNewFile())
                    logger.log(Level.INFO, "File created: {0}.yml", uuid);
                else
                    logger.log(Level.SEVERE,"Failed to create file: {0}.yml", uuid);

            } catch (IOException e) {
                logger.log(Level.SEVERE, "Me failed to make the new player file, me sorry :(", e);
            }
        }
        return f;
    }

    public static FileConfiguration getPlayerConfig(VnllaUserManagement plugin, String uuid) {
        FileConfiguration config = YamlConfiguration.loadConfiguration(getPlayerFile(plugin, uuid));
        if (!config.isSet("uuid")) {
            config.set("uuid", uuid);
        }
        return config;
    }

    //save changes in config file
    public void saveConfig(VnllaUserManagement plugin) {
        Bukkit.getLogger().log(Level.INFO, "Saving PlayerConfigModel[{0}] into file", this);
        try {
            config.set(TOTAL_VOTES, getTotalVotes());
            config.set(VOTES_OWED,getVotesOwed());
            config.set(GROUPS_SECTION, getGroups());

            config.set(LAST_LOGIN, getLastLogin());
            config.set(LAST_LOGOUT, getLastLogout());
            config.set(TOTAL_ALL_TIME, getTotalPlaytime());
            config.set(LASTLOCATION_X, getLastLocationX());
            config.set(LASTLOCATION_Y, getLastLocationY());
            config.set(LASTLOCATION_Z,     getLastLocationZ());
            config.set(LASTLOCATION_WORLD, getLastLocationWorld());
            config.set(LAST_PLAYER_NAME, getLastPlayerName());
            config.set(IPS, getIpAddresses());
            config.set(PENDING_EARN_GROUP, getPendingEarnedGroups());
            config.set(PENDING_LOSE_GROUP, getPendingLostGroups());

            if (config.isConfigurationSection(EARNED_GROUPS)) {
                config.set(EARNED_GROUPS, null);
            }

            ConfigurationSection groupsSection = config.createSection(EARNED_GROUPS);
            for (Map.Entry<String, GroupInfo> entry : getGroupInfos().entrySet()) {
                GroupInfo groupInfo = entry.getValue();
                ConfigurationSection groupSection = groupsSection.createSection(entry.getKey());
                groupSection.set(ACTIVE, groupInfo.isActive());
                groupSection.set(CURRENT_VOTES, groupInfo.getCurrentVotes());
                groupSection.set(EXPIRATION, groupInfo.getExpiration());
            }

            config.save(new File( plugin.getDataFolder() + File.separator + PLAYERDATA_FOLDER + File.separator + playerId + ".yml"));
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to write changes to the player file :(", e);
        }
        logger.log(Level.INFO, "Saved player information in {0}.yml", playerId);
    }

    // Deserialize a FileConfiguration object into a PlayerConfigDataModel object
    public static PlayerConfigModel fromUUID(VnllaUserManagement plugin, String uuid) {
        FileConfiguration config = getPlayerConfig(plugin, uuid);
        PlayerConfigModel data = new PlayerConfigModel();

        data.setPlayerId(uuid);
        data.setTotalVotes(config.getInt(TOTAL_VOTES));
        data.setVotesOwed(config.getInt(VOTES_OWED));

        data.setGroups(config.getStringList(GROUPS_SECTION));
        data.setLastLogin(config.getLong(LAST_LOGIN));
        data.setLastLogout(config.getLong(LAST_LOGOUT));
        data.setTotalPlaytime(config.getLong(TOTAL_ALL_TIME));
        data.setLastLocationX(config.getDouble(LASTLOCATION_X));
        data.setLastLocationY(config.getDouble(LASTLOCATION_Y));
        data.setLastLocationZ(config.getDouble(LASTLOCATION_Z));
        data.setLastLocationWorld(config.getString(LASTLOCATION_WORLD));
        data.setLastPlayerName(config.getString(LAST_PLAYER_NAME));
        data.setIpAddresses(config.getStringList(IPS));
        data.setPendingEarnedGroups(config.getStringList(PENDING_EARN_GROUP));
        data.setPendingLostGroups(config.getStringList(PENDING_LOSE_GROUP));

        ConfigurationSection groupsSection = config.getConfigurationSection(EARNED_GROUPS);
        HashMap<String, GroupInfo> groupInfos = new HashMap<>();
        if (groupsSection != null) {
            for (String groupName : groupsSection.getKeys(false)) {
                ConfigurationSection groupSection = groupsSection.getConfigurationSection(groupName);
                if(groupSection == null){
                    Bukkit.getLogger().log(Level.SEVERE, "Unable to retrieve data for {0} group from {1}",
                            new String[]{groupName, uuid});
                    continue;
                }
                GroupInfo groupInfo = new GroupInfo(groupName);

                groupInfo.setGroupName(groupName);
                groupInfo.setCurrentVotes(groupSection.getInt(CURRENT_VOTES));
                groupInfo.setActive(groupSection.getBoolean(ACTIVE));
                groupInfo.setExpiration(groupSection.getLong(EXPIRATION));

                groupInfos.put(groupName, groupInfo);
            }
        }
        data.setGroupInfos(groupInfos);

        data.setConfig(config);

        return data;
    }

    @Override
    public String toString() {
        return "PlayerConfigModel{" +
                "playerId='" + playerId + '\'' +
                ", totalVotes=" + totalVotes +
                ", votesOwed=" + votesOwed +
                ", lastLogin=" + lastLogin +
                ", lastLogout=" + lastLogout +
                ", totalPlaytime=" + totalPlaytime +
                ", lastLocationX=" + lastLocationX +
                ", lastLocationY=" + lastLocationY +
                ", lastLocationZ=" + lastLocationZ +
                ", lastLocationWorld='" + lastLocationWorld + '\'' +
                ", lastPlayerName='" + lastPlayerName + '\'' +
                ", ipAddresses=" + ipAddresses +
                ", groups=" + groups +
                ", pendingEarnedGroups=" + pendingEarnedGroups +
                ", pendingLostGroups=" + pendingLostGroups +
                ", config=" + config +
                ", groupInfos=" + groupInfos +
                '}';
    }
}
