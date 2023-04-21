package dev.gillin.mc.vnllausermanagement;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.UUID;

public class CommonUtilities {
    private CommonUtilities() {
        throw new IllegalStateException("Utility class");
    }

    /**
     *
     * @param uuid - UUID to validate
     * @return true/false
     */
    public static boolean isValidUUID(String uuid){
        int len = uuid.length();
        if (len > 36) {
            return false;
        }
        int dash1 = uuid.indexOf('-');
        int dash2 = uuid.indexOf('-', dash1 + 1);
        int dash3 = uuid.indexOf('-', dash2 + 1);
        int dash4 = uuid.indexOf('-', dash3 + 1);
        int dash5 = uuid.indexOf('-', dash4 + 1);
        return !(dash4 < 0 || dash5 >= 0);
    }

    /**
     * @param time - Time in milliseconds
     * @param stopAtHours - Boolean to determine if number of days should be displayed
     * @return - Readable String
     */
    public static String makeTimeReadable(long time, boolean stopAtHours) {
        long t = time / 1000;
        //seconds
        if (t < 60) {
            return t + " second(s)";
        }
        //minutes
        else if (t < 3600) {
            return (t / 60) + " minute(s)";
        }
        //hours
        else if (t < 216000 || stopAtHours) {
            return (t / 3600) + " hour(s)";
        }
        //days
        else {
            return (t / (3600 * 24)) + " day(s)";
        }
    }

    @SuppressWarnings("deprecation")
    public static OfflinePlayer getOfflinePlayerByString(String playerInputted){
        OfflinePlayer player;
        if(CommonUtilities.isValidUUID(playerInputted)){
            player = Bukkit.getOfflinePlayer(UUID.fromString(playerInputted));
        } else {
            // Deprecated, but no good replacement as of now
            player = Bukkit.getOfflinePlayer(playerInputted);
        }
        return player;
    }

}
