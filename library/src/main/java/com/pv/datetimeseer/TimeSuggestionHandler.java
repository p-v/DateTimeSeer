package com.pv.datetimeseer;

import android.content.Context;

import java.util.Calendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Suggestion handler for handling time information.<br/>
 * Handles strings like 6:30, 6 40, 3pm etc.
 *
 * @author p-v
 */
class TimeSuggestionHandler extends SuggestionHandler {

    private static final String TIME_RGX = "\\b((?:2[0-3])|(?:1\\d)|(?:0?\\d))(?:(?::|\\s)((?:0?\\d)|(?:[0-5][0-9]?)))?\\s{0,2}([ap](?:\\.?m\\.?)?)?\\b";
    private Pattern timePattern;

    /**
     * Time item class.
     */
    class TimeItem extends SuggestionValue.LocalItemItem {

        boolean isAmPmPresent;

        TimeItem(int value, boolean amPmPresent) {
            super(value);
            this.isAmPmPresent = amPmPresent;
        }
    }

    TimeSuggestionHandler(Config config) {
        super(config);
        timePattern = Pattern.compile(TIME_RGX, Pattern.CASE_INSENSITIVE);
    }

    @Override
    public void handle(Context context, String input, String lastToken, SuggestionValue suggestionValue) {

        DateSuggestionHandler.DateItem dateSuggestion =
                (DateSuggestionHandler.DateItem) suggestionValue.get(SuggestionValue.DATE);
        NumberRelativeTimeSuggestionHandler.RelativeDayNumItem numItem =
                (NumberRelativeTimeSuggestionHandler.RelativeDayNumItem) suggestionValue.get(SuggestionValue.RELATIVE_DAY_NUMBER);

        StringBuilder builder = new StringBuilder(input);
        if (numItem != null) {
            builder.replace(numItem.startIdx, numItem.endIdx, "");
        } else if (dateSuggestion != null) {
            builder.replace(dateSuggestion.startIdx, dateSuggestion.endIdx, "");
        }

        Matcher matcher = timePattern.matcher(builder.toString());

        while (matcher.find()) {
            int hourOfDay = Integer.parseInt(matcher.group(1));
            int mins = 0;
            String minsStr = matcher.group(2);
            String amPm = matcher.group(3);

            if (minsStr != null) {
                mins = Integer.parseInt(minsStr);
            }
            if (hourOfDay < 12) {
                if (amPm != null && amPm.matches("(?i)^p.*")) {
                    hourOfDay = hourOfDay + 12;
                }
            } else if (hourOfDay == 12) {
                if (amPm != null && amPm.matches("(?i)^a.*")) {
                    hourOfDay = 0;
                }
            }
            int minsInDay = hourOfDay * 60 + mins;

            suggestionValue.appendSuggestion(SuggestionValue.TIME, new TimeItem(minsInDay, amPm != null));
        }

        super.handle(context, input, lastToken, suggestionValue);
    }

    @Override
    public void build(Context context, SuggestionValue suggestionValue, List<SuggestionRow> suggestionList) {
        SuggestionValue.LocalItemItem relItem = suggestionValue.getRelDayItem();
        SuggestionValue.LocalItemItem dowItem = suggestionValue.getDowItem();
        SuggestionValue.LocalItemItem nextDowItem = suggestionValue.getNextDowItem();
        SuggestionValue.LocalItemItem monthItem = suggestionValue.getMonthItem();
        SuggestionValue.LocalItemItem dateItem = suggestionValue.getDateItem();
        SuggestionValue.LocalItemItem todItem = suggestionValue.getTodItem();
        SuggestionValue.LocalItemItem relItemNum = suggestionValue.getRelativeDayNumItem();

        TimeItem timeItem = suggestionValue.getTimeItem();

        boolean hasOnlyTime = relItem == null && dowItem == null && nextDowItem == null &&
                monthItem == null && dateItem == null && todItem == null && relItemNum == null
                && timeItem != null;

        if (hasOnlyTime) {
            Value timeValue;
            int hour = timeItem.value / 60;
            int mins = timeItem.value % 60;

            Calendar cal = Calendar.getInstance();
            final int currentHourOfDay = cal.get(Calendar.HOUR_OF_DAY);
            final int currentMinsOfHour = cal.get(Calendar.MINUTE);

            if (timeItem.isAmPmPresent) {
                timeValue = getTimeValue(context, hour, mins, null, null);
                if (!(currentHourOfDay > hour || currentHourOfDay == hour && currentMinsOfHour > mins)) {
                    suggestionList.add(new SuggestionRow(context.getString(R.string.today) + ", "
                            + timeValue.displayString, (int)(timeValue.value.getTimeInMillis()/1000)));
                }

                // increment a day for tomorrow
                timeValue.value.add(Calendar.DAY_OF_YEAR, 1);
                suggestionList.add(new SuggestionRow(context.getString(R.string.tomorrow) + ", "
                        + timeValue.displayString, (int)(timeValue.value.getTimeInMillis()/1000)));

                // increment a day for day after tomorrow
                timeValue.value.add(Calendar.DAY_OF_YEAR, 1);
                suggestionList.add(new SuggestionRow(getDisplayDate(timeValue.value)+ ", "
                        + timeValue.displayString, (int)(timeValue.value.getTimeInMillis()/1000)));
            } else {
                // if the current time is more than the entered time
                if (currentHourOfDay > hour || currentHourOfDay == hour && currentMinsOfHour > mins) {

                    timeValue = getTimeValue(context, hour, mins, null, null);

                    int calculatedHourForToday = timeValue.value.get(Calendar.HOUR_OF_DAY);
                    int calculatedMinsForToday = timeValue.value.get(Calendar.MINUTE);
                    // Check if the calculated time for today has past
                    if (calculatedHourForToday > currentHourOfDay ||
                            calculatedHourForToday == currentHourOfDay && calculatedMinsForToday > currentMinsOfHour) {
                        suggestionList.add(new SuggestionRow(context.getString(R.string.today) + ", "
                                + timeValue.displayString, (int)(timeValue.value.getTimeInMillis()/1000)));
                    }
                    // increment a day for tomorrow
                    timeValue.value.add(Calendar.DAY_OF_YEAR, 1);
                    suggestionList.add(new SuggestionRow(context.getString(R.string.tomorrow) + ", "
                            + timeValue.displayString, (int)(timeValue.value.getTimeInMillis()/1000)));

                    // increment a day for day after tomorrow
                    timeValue.value.add(Calendar.DAY_OF_YEAR, 1);
                    suggestionList.add(new SuggestionRow(getDisplayDate(timeValue.value)+ ", "
                            + timeValue.displayString, (int)(timeValue.value.getTimeInMillis()/1000)));
                } else {
                    timeValue = getTimeValue(context, hour, mins, null, null);
                    suggestionList.add(new SuggestionRow(context.getString(R.string.today) + ", "
                            + timeValue.displayString, (int)(timeValue.value.getTimeInMillis()/1000)));

                    // increment a day for tomorrow
                    timeValue.value.add(Calendar.DAY_OF_YEAR, 1);

                    suggestionList.add(new SuggestionRow(context.getString(R.string.tomorrow) + ", "
                            + timeValue.displayString, (int)(timeValue.value.getTimeInMillis()/1000)));

                    // increment a day for day after tomorrow
                    timeValue.value.add(Calendar.DAY_OF_YEAR, 1);
                    suggestionList.add(new SuggestionRow(getDisplayDate(timeValue.value)+ ", "
                            + timeValue.displayString, (int)(timeValue.value.getTimeInMillis()/1000)));
                }
            }
        } else {
            super.build(context, suggestionValue, suggestionList);
        }
    }
}
