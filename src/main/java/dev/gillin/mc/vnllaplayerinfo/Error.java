package dev.gillin.mc.vnllaplayerinfo;

import java.util.logging.Level;

public class Error {
    public static void execute(VnllaPlayerInfo plugin, Exception ex){
        plugin.getLogger().log(Level.SEVERE, "Couldn't execute MySQL statement: ", ex);
    }
    public static void close(VnllaPlayerInfo plugin, Exception ex){
        plugin.getLogger().log(Level.SEVERE, "Failed to close MySQL connection: ", ex);
    }
}