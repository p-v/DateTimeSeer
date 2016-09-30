package com.pv.datetimeseer;

import android.content.Context;
import android.support.annotation.NonNull;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * Abstract Suggestion Handler
 *
 * Has all common methods use for displaying suggestions to the user
 *
 * The module is based on COR design pattern
 *
 * @author p-v
 */
abstract class SuggestionHandler {

    private SuggestionHandler nextHandler;
    private SuggestionHandler nextBuilder;
    Config config;

    static final int EVENING_TIME = 5;
    static final int AFTERNOON_TIME = 14 * 60;
    static final int MORNING_TIME_WEEKDAY = 9 * 60;
    static final int MORNING_TIME_WEEKEND = 10 * 60;
    static final int WEEKEND = Constants.Weekend.SATURDAY_SUNDAY;

    public SuggestionHandler(Config config) {
        if (config == null) {
            config = new Config.ConfigBuilder().build();
        }
        this.config = config;
    }

    /**
     * Sets the next handler after the current handler
     *
     * @param nextHandler next handler after the current handler
     */
    void setNextHandler(SuggestionHandler nextHandler) {
        this.nextHandler = nextHandler;
    }

    /**
     * Sets the next builder after the current builder
     *
     * @param nextBuilder next builder after the current builder
     */
    void setNextBuilder(SuggestionHandler nextBuilder) {
        this.nextBuilder = nextBuilder;
    }

    /**
     * Build the suggestion list based on the suggestion value
     *
     * @param context The context to use.
     * @param suggestionValue The value used to build the suggestions.
     * @param suggestionList The list to add suggestions to. (pass empty the first time)
     */
    public void build(Context context, SuggestionValue suggestionValue, List<SuggestionRow> suggestionList) {
        if (nextBuilder != null) {
            nextBuilder.build(context, suggestionValue, suggestionList);
        }
    }

    /**
     * Interprets the input and converts it to SuggestionValue which can be used to build the
     * suggestion list
     *
     * @param context The context to use.
     * @param input User input.
     * @param lastToken Last token of the user input.
     * @param suggestionValue The value where all the related values are stored based on input (pass
     *                        empty the first time)
     */
    public void handle(Context context, String input, String lastToken, SuggestionValue suggestionValue) {
        if (nextHandler != null) {
            nextHandler.handle(context, input, lastToken, suggestionValue);
        }
    }

    /**
     * Get time Value for the time passed
     *
     * @param context The context to use
     * @param hour Hour
     * @param mins Minutes
     * @param amPm am/pm String
     *
     * @return value which has display value and the real value
     */
    final Value getTimeValue(Context context, int hour, int mins, String amPm, Calendar value) {

        if (value == null) {
            value = Calendar.getInstance();
        }

        if (hour > 12) {
            if (amPm != null) {
                value.set(Calendar.HOUR_OF_DAY, hour);
                value.set(Calendar.MINUTE, mins);
                value.set(Calendar.SECOND, 0);
                value.set(Calendar.MILLISECOND, 0);
                String displayValue = DateTimeUtils.getDisplayTime(context, value, config);
                return new Value(displayValue, value);
            } else {
                value.set(Calendar.HOUR, hour % 12);
                value.set(Calendar.MINUTE, mins);
                value.set(Calendar.SECOND, 0);
                value.set(Calendar.MILLISECOND, 0);
                value.set(Calendar.AM_PM, Calendar.PM);
                String displayValue = DateTimeUtils.getDisplayTime(context, value, config);
                return new Value(displayValue, value);
            }
        } else if (hour < 12) {
            if (amPm != null) {
                value.set(Calendar.HOUR, hour);
                value.set(Calendar.MINUTE, mins);
                value.set(Calendar.SECOND, 0);
                value.set(Calendar.MILLISECOND, 0);
                value.set(Calendar.AM_PM, "pm".equals(amPm) ? Calendar.PM : Calendar.AM);
                String displayValue = DateTimeUtils.getDisplayTime(context, value, config);
                return new Value(displayValue, value);
            } else {
                value.set(Calendar.HOUR, hour % 12);
                value.set(Calendar.MINUTE, mins);
                value.set(Calendar.SECOND, 0);
                value.set(Calendar.MILLISECOND, 0);
                value.set(Calendar.AM_PM, Calendar.AM);
                String displayValue = DateTimeUtils.getDisplayTime(context, value, config);
                return new Value(displayValue, value);
            }
        } else {
            // 12 am/pm case
            if (amPm != null) {
                value.set(Calendar.HOUR_OF_DAY, hour);
                value.set(Calendar.MINUTE, mins);
                value.set(Calendar.SECOND, 0);
                value.set(Calendar.MILLISECOND, 0);
                String displayValue = DateTimeUtils.getDisplayTime(context, value, config);
                return new Value(displayValue, value);
            } else {
                value.set(Calendar.HOUR, hour % 12);
                value.set(Calendar.MINUTE, mins);
                value.set(Calendar.SECOND, 0);
                value.set(Calendar.MILLISECOND, 0);
                value.set(Calendar.AM_PM, Calendar.PM);
                String displayValue = DateTimeUtils.getDisplayTime(context, value, config);
                return new Value(displayValue, value);
            }
        }
    }

    /**
     *
     * Get time Value for the time/tod item passed
     *
     * Time of day item should never be null
     *
     * @param context The context to use
     * @param todItem Time of day item
     * @param timeItem Time item
     * @return return the Value having display and real value
     */
    final Value getTimeValue(@NonNull Context context, @NonNull SuggestionValue.LocalItemItem todItem, TimeSuggestionHandler.TimeItem timeItem) {
        Value value = null;
        switch (todItem.value) {

            // Morning
            case TODSuggestionHandler.TOD_MORNING:
                if (timeItem == null) {
                    Calendar cal = Calendar.getInstance();
                    if (DateTimeUtils.isWeekend(cal.get(Calendar.DAY_OF_WEEK), WEEKEND)) {
                        int morningTime = MORNING_TIME_WEEKEND;
                        int hour = morningTime / 60;
                        int mins = morningTime % 60;
                        value = getTimeValue(context, hour, mins, null, null);
                    } else {
                        int morningTime = MORNING_TIME_WEEKDAY;
                        int hour = morningTime / 60;
                        int mins = morningTime % 60;
                        value = getTimeValue(context, hour, mins, null, null);
                    }
                } else {
                    int hour = timeItem.value / 60;
                    int mins = timeItem.value % 60;

                    if (hour < 12) {
                        value = getTimeValue(context, hour, mins, "am", null);
                    } else {
                        value = getTimeValue(context, hour, mins, "pm", null);
                    }
                }
                break;

            // Afternoon
            case TODSuggestionHandler.TOD_AFTERNOON:
                if (timeItem == null) {
                    int afternoonTime = AFTERNOON_TIME;
                    int hour = afternoonTime / 60;
                    int mins = afternoonTime % 60;
                    value = getTimeValue(context, hour, mins, null, null);
                } else {
                    int hour = timeItem.value / 60;
                    int mins = timeItem.value % 60;
                    value = getTimeValue(context, hour, mins, "pm", null);
                }
                break;

            // Evening
            case TODSuggestionHandler.TOD_EVENING:
                if (timeItem == null) {
                    value = getTimeValue(context, EVENING_TIME, 0, "pm", null);
                } else {
                    int hour = timeItem.value / 60;
                    int mins = timeItem.value % 60;
                    value = getTimeValue(context, hour, mins, "pm", null);
                }
                break;

            // Night
            case TODSuggestionHandler.TOD_NIGHT:
                if (timeItem == null) {
                    int nightTime = 10;
                    value = getTimeValue(context, nightTime, 0, "pm", null);
                } else {
                    int hour = timeItem.value / 60;
                    int mins = timeItem.value % 60;
                    int modHour = hour % 12;
                    if (modHour >=0 && modHour < 4){
                        value = getTimeValue(context, hour, mins, "am", null);
                    } else if (modHour >= 4 && modHour <= 6){
                        value = getTimeValue(context, 9, mins, "pm", null);
                    } else {
                        value = getTimeValue(context, hour, mins, "pm", null);
                    }
                }
                break;

            default:
                break;

        }
        return value;
    }

    /**
     * Get the display string to be shown in the suggestions
     *
     * @param context The context to use
     * @param cal Calendar object to update
     * @param isRelative Is the time relative to the current time
     * @return Display string
     */
    final String getDisplayDate(Context context, Calendar cal,  boolean isRelative) {
        if (isRelative) {
            int days = DateTimeUtils.daysBetween(Calendar.getInstance(), cal);
            if (days == 0) {
                return context.getString(R.string.today) + ", "
                        + DateTimeUtils.getDisplayTime(context, cal, config);
            } else if (days == 1) {
                return context.getString(R.string.tomorrow) + ", "
                        + DateTimeUtils.getDisplayTime(context, cal, config);
            } else {
                return getDisplayDate(cal) + ", " + DateTimeUtils.getDisplayTime(context, cal, config);
            }
        } else {
            return getDisplayDate(cal);
        }
    }

    final String getDisplayDate(Context context, Calendar cal,  String timeString) {
        int days = DateTimeUtils.daysBetween(Calendar.getInstance(), cal);
        if (days == 0) {
            return context.getString(R.string.today) + ", " + timeString;
        } else if (days == 1) {
            return context.getString(R.string.tomorrow) + ", " + timeString;
        } else {
            return getDisplayDate(cal) + ", " + timeString;
        }
    }

    /**
     * Get display date string from calendar in "EEEE, dd MMM" format,
     * <br/>e.g. Mon, 12 Dec <br/> Tue, 20 Jul
     *
     * @param cal Calendar to use
     * @return the display value
     */
    final String getDisplayDate(Calendar cal) {
        if (Calendar.getInstance().get(Calendar.YEAR) == cal.get(Calendar.YEAR)) {
            DateFormat df = new SimpleDateFormat(config.getDateFormatWithoutYear(), Locale.ENGLISH);
            return df.format(cal.getTime());
        } else {
            DateFormat df = new SimpleDateFormat(config.getDateFormatWithYear(), Locale.ENGLISH);
            return df.format(cal.getTime());
        }
    }

    /**
     * Class used internally with all the handlers
     * to build and show suggestions
     */
    protected class Value {

        String displayString;
        public Calendar value;

        public Value(String displayString, Calendar value) {
            this.displayString = displayString;
            this.value = value;
        }

    }

}
