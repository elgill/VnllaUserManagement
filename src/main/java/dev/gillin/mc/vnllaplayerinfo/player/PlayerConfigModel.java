package dev.gillin.mc.vnllaplayerinfo.player;

import dev.gillin.mc.vnllaplayerinfo.VnllaPlayerInfo;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static dev.gillin.mc.vnllaplayerinfo.VnllaPlayerInfo.GROUP;

public class PlayerConfigModel {
    private String playerId;
    private int totalVotes;
    private int vip1Votes;
    private int vip2Votes;
    private String rank;
    private long vip1Expire;
    private long vip2Expire;
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
    private FileConfiguration config;

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

    public int getVip1Votes() {
        return vip1Votes;
    }

    public void setVip1Votes(int vip1Votes) {
        this.vip1Votes = vip1Votes;
    }

    public int getVip2Votes() {
        return vip2Votes;
    }

    public void setVip2Votes(int vip2Votes) {
        this.vip2Votes = vip2Votes;
    }

    public int getVotesOwed() {
        return votesOwed;
    }

    public void setVotesOwed(int votesOwed) {
        this.votesOwed = votesOwed;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public long getVip1Expire() {
        return vip1Expire;
    }

    public void setVip1Expire(long vip1Expire) {
        this.vip1Expire = vip1Expire;
    }

    public long getVip2Expire() {
        return vip2Expire;
    }

    public void setVip2Expire(long vip2Expire) {
        this.vip2Expire = vip2Expire;
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
        return ipAddresses;
    }

    public void setIpAddresses(List<String> ipAddresses) {
        this.ipAddresses = ipAddresses;
    }

    public FileConfiguration getConfig() {
        return config;
    }

    public void setConfig(FileConfiguration config) {
        this.config = config;
    }

    //returns file from playerdata folder in plugin folder
    public static File getPlayerFile(VnllaPlayerInfo plugin, String uuid) {
        File f = new File(plugin.getDataFolder() + File.separator + "playerdata" + File.separator + uuid + ".yml");
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

    public static FileConfiguration getPlayerConfig(VnllaPlayerInfo plugin, String uuid) {
        FileConfiguration config = YamlConfiguration.loadConfiguration(getPlayerFile(plugin, uuid));
        if (!config.isSet("uuid")) {
            config.set("uuid", uuid);
        }
        if (!config.isSet(GROUP)) {
            config.set(GROUP, "default");
        }
        return config;
    }

    //save changes in config file
    public boolean saveConfig(VnllaPlayerInfo plugin) {
        try {
            config.set("votes.totalVotes", getTotalVotes());
            config.set("votes.vip1Votes", getVip1Votes());
            config.set("votes.vip2Votes", getVip2Votes());
            config.set("votes.rank", getRank());
            config.set("votes.vip1expire", getVip1Expire());
            config.set("votes.vip2expire", getVip2Expire());
            config.set("votes.owed",getVotesOwed());
            config.set("groups", getGroups());

            config.set("playtime.lastLogin", getLastLogin());
            config.set("playtime.lastLogout", getLastLogout());
            config.set("playtime.totalAllTime", getTotalPlaytime());
            config.set("lastlocation.x", getLastLocationX());
            config.set("lastlocation.y", getLastLocationY());
            config.set("lastlocation.z", getLastLocationZ());
            config.set("lastlocation.world", getLastLocationWorld());
            config.set("lastPlayerName", getLastPlayerName());
            config.set("ips", getIpAddresses());

            config.save(new File( plugin.getDataFolder() + File.separator + "playerdata" + File.separator + playerId + ".yml"));
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Me failed to write changes to the player file, me sorry :(", e);
        }
        logger.log(Level.INFO, "Saved player information in {0}.yml", playerId);
        return true;
    }

    // Deserialize a FileConfiguration object into a PlayerConfigDataModel object
    public static PlayerConfigModel fromUUID(VnllaPlayerInfo plugin, String uuid) {
        FileConfiguration config = getPlayerConfig(plugin, uuid);
        PlayerConfigModel data = new PlayerConfigModel();

        data.setPlayerId(uuid);
        data.setTotalVotes(config.getInt("votes.totalVotes"));
        data.setVip1Votes(config.getInt("votes.vip1Votes"));
        data.setVip2Votes(config.getInt("votes.vip2Votes"));
        data.setRank(config.getString("votes.rank"));
        data.setVip1Expire(config.getLong("votes.vip1expire"));
        data.setVip2Expire(config.getLong("votes.vip2expire"));
        data.setVotesOwed(config.getInt("votes.owed"));

        data.setGroups(config.getStringList("groups"));
        data.setLastLogin(config.getLong("playtime.lastLogin"));
        data.setLastLogout(config.getLong("playtime.lastLogout"));
        data.setTotalPlaytime(config.getLong("playtime.totalAllTime"));
        data.setLastLocationX(config.getDouble("lastlocation.x"));
        data.setLastLocationY(config.getDouble("lastlocation.Y"));
        data.setLastLocationZ(config.getDouble("lastlocation.z"));
        data.setLastLocationWorld(config.getString("lastlocation.world"));
        data.setLastPlayerName(config.getString("lastPlayerName"));
        data.setIpAddresses(config.getStringList("ips"));

        data.setConfig(config);

        return data;
    }
}
