package org.n3r.sshe.util;

import org.apache.commons.lang3.time.DateFormatUtils;

import java.util.concurrent.TimeUnit;

public class Util {
    public static String currentTime() {
        return DateFormatUtils.format(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss");
    }

    public static String humanReadableByteCount(long bytes, boolean si) {
        int unit = si ? 1000 : 1024;
        if (bytes < unit) return bytes + " B";

        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp - 1) + (si ? "" : "i");
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }

    public static String humanReadableDuration(long millis) {
        long days = TimeUnit.MILLISECONDS.toDays(millis);
        millis -= TimeUnit.DAYS.toMillis(days);
        long hours = TimeUnit.MILLISECONDS.toHours(millis);
        millis -= TimeUnit.HOURS.toMillis(hours);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
        millis -= TimeUnit.MINUTES.toMillis(minutes);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis);

        StringBuilder sb = new StringBuilder(64);
        if (days > 0) sb.append(days).append(" Days ");
        if (hours > 0 ) sb.append(hours).append(" Hours ");
        if (minutes > 0 ) sb.append(minutes).append(" Minutes ");
        if (seconds > 0 ) sb.append(seconds).append(" Seconds");

        return sb.toString().trim();
    }

    public static boolean anyOf(String cmd, String... collection) {
        for (String element : collection) {
            if (cmd.equals(element)) return true;
        }

        return false;
    }

    public static void sleepMillis(long sleepMillis) {
        try {
            Thread.sleep(sleepMillis);
        } catch (InterruptedException e) {
            // Ignore
        }
    }
}
