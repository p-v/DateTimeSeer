package com.pv.datetimeseer.parser;

import android.content.Context;

import com.pv.datetimeseer.Config;
import com.pv.datetimeseer.R;
import com.pv.datetimeseer.SuggestionRow;
import com.pv.datetimeseer.parser.model.TimeItem;

import java.util.Calendar;
import java.util.List;

/**
 * Suggestion handler for handling time information.<br/>
 * Handles strings like 6:30, 6 40, 3pm etc.
 *
 * @author p-v
 */
class TimeSuggestionBuilder extends SuggestionBuilder {

    TimeSuggestionBuilder(Config config) {
        super(config);
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
