package dev.gillin.mc.vnllaplayerinfo.Database;

import dev.gillin.mc.vnllaplayerinfo.Errors;
import dev.gillin.mc.vnllaplayerinfo.VnllaPlayerInfo;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;


public class SQLite extends Database {
    private String dbname;
    public SQLite(VnllaPlayerInfo instance){
        super(instance);
        dbname = plugin.getConfig().getString("SQLite.Filename", "defaultname");
    }
    public static final String SQLiteCreateTokensTable = "CREATE TABLE IF NOT EXISTS players (" +
            "uuid VARCHAR(32) NOT NULL," +
            "ip VARCHAR(15) NOT NULL" +
            ");";

    // SQL creation stuff, You can leave the blow stuff untouched.
    public Connection getSQLConnection() {
        File dataFolder = new File(plugin.getDataFolder(), dbname+".db");
        if (!dataFolder.exists()){
            try {
                dataFolder.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().log(Level.SEVERE, "File write error: {0}.db", dbname);
            }
        }
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + dataFolder);
            return connection;
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE,"SQLite exception on initialize", ex);
        } catch (ClassNotFoundException ex) {
            plugin.getLogger().log(Level.SEVERE, "You need the SQLite JDBC library. Google it. Put it in /lib folder.", ex);
        }
        catch(Exception ex) {
        	plugin.getLogger().log(Level.SEVERE, "IDK U figure it out.", ex);
        }
        return null;
    }

    public void load() {
        connection = getSQLConnection();
        try {
            Statement s = connection.createStatement();
            s.executeUpdate(SQLiteCreateTokensTable);
            s.close(); 
           
        } catch (SQLException e) {
            e.printStackTrace();
            plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), e);
        }
        initialize();
    }
}
