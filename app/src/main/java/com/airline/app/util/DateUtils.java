package com.airline.app.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateUtils {
    private static final String ISO_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
    private static final String DATE_SEARCH_FORMAT = "yyyy-MM-dd";
    private static final String DISPLAY_DATE_FORMAT = "EEE, dd MMM yyyy";
    private static final String DISPLAY_TIME_FORMAT = "hh:mm a";
    private static final String FULL_DISPLAY_FORMAT = "dd MMM yyyy, hh:mm a";

    public static String getTodaySearchDate() {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_SEARCH_FORMAT, Locale.US);
        return sdf.format(new Date());
    }

    public static String formatDateForSearch(Calendar calendar) {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_SEARCH_FORMAT, Locale.US);
        return sdf.format(calendar.getTime());
    }

    public static String formatDateForDisplay(Calendar calendar) {
        SimpleDateFormat sdf = new SimpleDateFormat(DISPLAY_DATE_FORMAT, Locale.US);
        return sdf.format(calendar.getTime());
    }

    public static String formatIsoToDisplayDate(String isoString) {
        if (isoString == null || isoString.isEmpty()) return "";
        try {
            SimpleDateFormat isoFormat = new SimpleDateFormat(isoString.contains("T") ? ISO_FORMAT : DATE_SEARCH_FORMAT, Locale.US);
            Date date = isoFormat.parse(isoString);
            if (date != null) {
                SimpleDateFormat displayFormat = new SimpleDateFormat(DISPLAY_DATE_FORMAT, Locale.US);
                return displayFormat.format(date);
            }
        } catch (ParseException e) {
            // fallback if already formatted or different structure
            return isoString;
        }
        return isoString;
    }

    public static String formatIsoToDisplayTime(String isoString) {
        if (isoString == null || isoString.isEmpty()) return "";
        try {
            SimpleDateFormat isoFormat = new SimpleDateFormat(ISO_FORMAT, Locale.US);
            Date date = isoFormat.parse(isoString);
            if (date != null) {
                SimpleDateFormat displayFormat = new SimpleDateFormat(DISPLAY_TIME_FORMAT, Locale.US);
                return displayFormat.format(date);
            }
        } catch (ParseException e) {
            if (isoString.length() >= 5 && isoString.contains(":")) {
                return isoString;
            }
        }
        return isoString;
    }

    public static String formatIsoToFullDateTime(String isoString) {
        if (isoString == null || isoString.isEmpty()) return "";
        try {
            SimpleDateFormat isoFormat = new SimpleDateFormat(isoString.contains("T") ? ISO_FORMAT : DATE_SEARCH_FORMAT, Locale.US);
            Date date = isoFormat.parse(isoString);
            if (date != null) {
                SimpleDateFormat displayFormat = new SimpleDateFormat(FULL_DISPLAY_FORMAT, Locale.US);
                return displayFormat.format(date);
            }
        } catch (ParseException e) {
            return isoString;
        }
        return isoString;
    }
}
