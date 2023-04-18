package dev.gillin.mc.vnllaplayerinfo.database;

import dev.gillin.mc.vnllaplayerinfo.Errors;
import dev.gillin.mc.vnllaplayerinfo.Error;
import dev.gillin.mc.vnllaplayerinfo.VnllaPlayerInfo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;


public abstract class Database {
    VnllaPlayerInfo plugin;
    Connection connection;
    // The name of the table we created back in SQLite class.
    public String table = "players";
    public Database(VnllaPlayerInfo instance){
        plugin = instance;
    }

    public abstract Connection getSQLConnection();

    public abstract void load();

    public void initialize(){
        connection = getSQLConnection();
        try{
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM players WHERE uuid = ?");
            ps.setString(1,"def7c9be-a0f9-46be-8c94-583d6e3e14b9");
            ResultSet rs = ps.executeQuery();
            close(ps,rs);
   
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, "Unable to retrieve connection", ex);
        }
    }

    // These are the methods you can use to get things out of your database. You of course can make new ones to return different things in the database.
    // This returns the number of people the player killed.
    public ArrayList<String> getIPsByUUID(String string) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs;
        ArrayList<String> ips=new ArrayList<>();
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("SELECT * FROM " + table + " WHERE uuid = '"+string+"';");
   
            rs = ps.executeQuery();
            while(rs.next()){
                ips.add(rs.getString("ip"));
            }
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
            }
        }
        return ips;
    }
    
    public ArrayList<String> getUUIDsByIP(String string) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs;
        ArrayList<String> uuids=new ArrayList<>();
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("SELECT * FROM " + table + " WHERE ip = '"+string+"';");
   
            rs = ps.executeQuery();
            while(rs.next()){
                uuids.add(rs.getString("uuid"));
            }
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
            }
        }
        return uuids;
    }

    // Now we need methods to save things to the database
    public void addIp(String uuid, String ip) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("SELECT * FROM " + table + " WHERE ip = ? AND uuid= ?;");
            ps.setString(1, ip);
            ps.setString(2, uuid);
            if(ps.executeQuery().next()) return;
            ps.close();
            ps = conn.prepareStatement("INSERT INTO " + table + " (uuid, ip) VALUES(?,?);");
            ps.setString(1, uuid);
            ps.setString(2, ip);
            ps.executeUpdate();
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
            }
        }
    }
    
    public void wipeIP(String uuid) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("DELETE FROM "+table+" WHERE uuid = ? ;");
            ps.setString(1, uuid);
            ps.executeUpdate();
            return;
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
            }
        }
    }


    public void close(PreparedStatement ps,ResultSet rs){
        try {
            if (ps != null)
                ps.close();
            if (rs != null)
                rs.close();
        } catch (SQLException ex) {
            Error.close(plugin, ex);
        }
    }
}