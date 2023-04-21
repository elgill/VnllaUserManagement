package dev.gillin.mc.vnllaplayerinfo.database;

import org.bukkit.Bukkit;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class PlayerData {
    private final SQLiteConnection connection;

    public PlayerData(SQLiteConnection connection) {
        this.connection = connection;
        createTable();
    }

    private void createTable() {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS players (" +
                "uuid VARCHAR(32) NOT NULL," +
                "ip VARCHAR(15) NOT NULL" +
                ");" +
                "CREATE INDEX IF NOT EXISTS 'ip_index' ON 'players' ('ip');";

        try (Connection conn = connection.getConnection();
             PreparedStatement statement = conn.prepareStatement(createTableSQL)) {
            statement.execute();
        } catch (SQLException e) {
            Bukkit.getLogger().log(Level.SEVERE, "Error creating table", e);
        }
    }

    // Insert player IP associated with a UUID
    public void insertPlayerIP(String uuid, String ip) {
        String insertSQL = "INSERT INTO players (uuid, ip) VALUES (?, ?);";

        try (Connection conn = connection.getConnection();
             PreparedStatement statement = conn.prepareStatement(insertSQL)) {
            statement.setString(1, uuid);
            statement.setString(2, ip);
            statement.executeUpdate();
        } catch (SQLException e) {
            Bukkit.getLogger().log(Level.SEVERE, "Error inserting player IP", e);
        }
    }

    // Get a list of IPs associated with a UUID
    public List<String> getIPsByUUID(String uuid) {
        List<String> ips = new ArrayList<>();
        String selectSQL = "SELECT ip FROM players WHERE uuid = ?;";

        try (Connection conn = connection.getConnection();
             PreparedStatement statement = conn.prepareStatement(selectSQL)) {
            statement.setString(1, uuid);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                ips.add(rs.getString("ip"));
            }
        } catch (SQLException e) {
            Bukkit.getLogger().log(Level.SEVERE, "Error getting IPs by UUID", e);
        }

        return ips;
    }

    // Get a list of UUIDs associated with an IP
    public List<String> getUUIDsByIP(String ip) {
        List<String> uuids = new ArrayList<>();
        String selectSQL = "SELECT uuid FROM players WHERE ip = ?;";

        try (Connection conn = connection.getConnection();
             PreparedStatement statement = conn.prepareStatement(selectSQL)) {
            statement.setString(1, ip);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                uuids.add(rs.getString("uuid"));
            }
        } catch (SQLException e) {
            Bukkit.getLogger().log(Level.SEVERE, "Error getting UUIDs by IP", e);
        }

        return uuids;
    }

    // Delete all IP entries for a specific UUID
    public void deleteIPsByUUID(String uuid) {
        String deleteSQL = "DELETE FROM players WHERE uuid = ?;";

        try (Connection conn = connection.getConnection();
             PreparedStatement statement = conn.prepareStatement(deleteSQL)) {
            statement.setString(1, uuid);
            statement.executeUpdate();
        } catch (SQLException e) {
            Bukkit.getLogger().log(Level.SEVERE, "Error deleting IPs by UUID", e);
        }
    }
}
