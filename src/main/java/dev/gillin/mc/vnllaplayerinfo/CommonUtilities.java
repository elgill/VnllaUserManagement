package dev.gillin.mc.vnllaplayerinfo;

public class CommonUtilities {
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
        int dash1 = uuid.indexOf('-', 0);
        int dash2 = uuid.indexOf('-', dash1 + 1);
        int dash3 = uuid.indexOf('-', dash2 + 1);
        int dash4 = uuid.indexOf('-', dash3 + 1);
        int dash5 = uuid.indexOf('-', dash4 + 1);
        if (dash4 < 0 || dash5 >= 0) {
            return false;
        }
        return true;
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

}
