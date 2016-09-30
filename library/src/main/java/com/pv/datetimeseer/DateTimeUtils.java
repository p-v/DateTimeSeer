package com.pv.datetimeseer;

import android.content.Context;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * @author p-v
 */
@SuppressWarnings("WrongConstant")
class DateTimeUtils {

    static String getDisplayDate(Calendar cal, Config config) {
        DateFormat df = new SimpleDateFormat(config.getDateFormatWithoutYear(), Locale.ENGLISH);
        return df.format(cal.getTime());
    }

    static String getDisplayTime(Context context, Calendar cal, Config config) {

        String format;
        if (android.text.format.DateFormat.is24HourFormat(context)) {
            format = config.getTimeFormat24Hours();
        } else {
            if (cal.get(Calendar.MINUTE) == 0) {
                format = config.getTimeFormat12HoursWithoutMins();
            } else {
                format = config.getTimeFormat12HoursWithMins();
            }
        }
        DateFormat df = new SimpleDateFormat(format, Locale.ENGLISH);
        return df.format(cal.getTime());
    }

    static int daysBetween(Calendar day1, Calendar day2) {
        /**
         * Saved some effort using the solution described here,
         * http://stackoverflow.com/a/28865648/1587370
         */
        Calendar dayOne = (Calendar) day1.clone(),
                dayTwo = (Calendar) day2.clone();

        if (dayOne.get(Calendar.YEAR) == dayTwo.get(Calendar.YEAR)) {
            return Math.abs(dayOne.get(Calendar.DAY_OF_YEAR) - dayTwo.get(Calendar.DAY_OF_YEAR));
        } else {
            if (dayTwo.get(Calendar.YEAR) > dayOne.get(Calendar.YEAR)) {
                //swap them
                Calendar temp = dayOne;
                dayOne = dayTwo;
                dayTwo = temp;
            }
            int extraDays = 0;

            int dayOneOriginalYearDays = dayOne.get(Calendar.DAY_OF_YEAR);

            while (dayOne.get(Calendar.YEAR) > dayTwo.get(Calendar.YEAR)) {
                dayOne.add(Calendar.YEAR, -1);
                // getActualMaximum() important for leap years
                extraDays += dayOne.getActualMaximum(Calendar.DAY_OF_YEAR);
            }

            return extraDays - dayTwo.get(Calendar.DAY_OF_YEAR) + dayOneOriginalYearDays;
        }
    }

    /**
     * Determines if the passed day of week is weekend
     *
     * @param dayOfWeek day to determine
     * @param weekendValue user's weekend value
     * @return true if weekend, false otherwise
     */
    static boolean isWeekend(int dayOfWeek,
                                    @Constants.Weekend int weekendValue) {
        switch (weekendValue) {
            case Constants.Weekend.SATURDAY_SUNDAY:
                return Calendar.SATURDAY == dayOfWeek || Calendar.SUNDAY == dayOfWeek;
            case Constants.Weekend.FRIDAY_SATURDAY:
                return Calendar.FRIDAY == dayOfWeek || Calendar.SATURDAY == dayOfWeek;
            case Constants.Weekend.THURSDAY_FRIDAY:
                return Calendar.THURSDAY == dayOfWeek || Calendar.FRIDAY == dayOfWeek;
            case Constants.Weekend.FRIDAY_ONLY:
                return Calendar.FRIDAY == dayOfWeek;
            case Constants.Weekend.SATURDAY_ONLY:
                return Calendar.SATURDAY == dayOfWeek;
            case Constants.Weekend.SUNDAY_ONLY:
                return Calendar.SUNDAY == dayOfWeek;
            default:
                return false;
        }
    }
}
