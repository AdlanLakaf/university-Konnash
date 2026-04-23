package com.konnash.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateUtils {

    private static final SimpleDateFormat TIME_FORMAT =
            new SimpleDateFormat("HH:mm", new Locale("ar"));

    /**
     * Returns a human-readable label for a timestamp.
     * Shows "اليوم ساعة HH:mm"
     */
    public static String formatTimestamp(long timestamp) {
        String time = TIME_FORMAT.format(new Date(timestamp));
        return "اليوم ساعة " + time;
    }

    public static String formatAmount(double amount) {
        return String.format(Locale.getDefault(), "%.1f د.ج.", amount);
    }
}
