package dev.gillin.mc.vnllaplayerinfo.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.Bukkit;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;

public class SQLiteConnection {
    private HikariDataSource dataSource;

    public SQLiteConnection(String dbName, File dataFolder) {
        setupDataSource(dbName, dataFolder);
    }

    private void setupDataSource(String dbName, File dataFolder) {
        File dbFile = Paths.get(dataFolder.getAbsolutePath(), dbName + ".db").toFile();

        if (!dbFile.exists()) {
            try {
                dbFile.createNewFile();
            } catch (IOException e) {
                // Log the error (replace "plugin" with your plugin instance)
                Bukkit.getLogger().log(Level.SEVERE, "File write error: " + dbName + ".db");
            }
        }

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:sqlite:" + dbFile);
        config.setDriverClassName("org.sqlite.JDBC");
        config.setMaximumPoolSize(10); // Adjust the pool size based on your needs
        config.setConnectionTestQuery("SELECT 1");
        config.setPoolName("SQLitePool");

        dataSource = new HikariDataSource(config);
    }

    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public void close() {
        if (dataSource != null) {
            dataSource.close();
        }
    }
}
