package com.pv.datetimeseer.parser;

import android.content.Context;

import com.pv.datetimeseer.Config;
import com.pv.datetimeseer.SuggestionRow;
import com.pv.datetimeseer.parser.helper.DateTimeUtils;
import com.pv.datetimeseer.parser.model.DateItem;
import com.pv.datetimeseer.parser.model.TimeItem;

import java.util.Calendar;
import java.util.List;

/**
 * @author p-v
 */
class DateSuggestionBuilder extends SuggestionBuilder {

    DateSuggestionBuilder(Config config) {
        super(config);
    }

    @Override
    public void build(Context context, SuggestionValue suggestionValue, List<SuggestionRow> suggestionList) {
        DateItem dateItem = suggestionValue.getDateItem();
        if (dateItem != null) {
            SuggestionValue.LocalItemItem todItem = suggestionValue.getTodItem();
            TimeItem timeItem = suggestionValue.getTimeItem();

            Value timeValue = null;
            int secondsOfDay = 0;
            if (todItem != null || timeItem != null) {
                if (todItem == null) {
                    // timeItem is not null here
                    int hour = timeItem.value / 60;
                    int mins = timeItem.value % 60;
                    timeValue = getTimeValue(context, hour, mins, null, null);
                    secondsOfDay = timeItem.value * 60;
                } else {
                    timeValue = getTimeValue(context, todItem, timeItem);
                    secondsOfDay = 60 * (timeValue.value.get(Calendar.MINUTE)
                            + timeValue.value.get(Calendar.HOUR_OF_DAY) * 60);
                }

            }

            Calendar cal = Calendar.getInstance();
            boolean incrementYear = false;
            if (cal.getTimeInMillis() / 1000 > (dateItem.value + secondsOfDay)) {
                // current time is more than the specified date
                incrementYear = true;
            }
            cal.setTimeInMillis(dateItem.value * 1000L);
            if (incrementYear) {
                cal.add(Calendar.YEAR, 1);
            }

            if (timeValue == null) {
                if (DateTimeUtils.isWeekend(cal.get(Calendar.DAY_OF_WEEK), WEEKEND)) {
                    int morningTime = MORNING_TIME_WEEKEND;
                    int hour = morningTime / 60;
                    int mins = morningTime % 60;
                    timeValue = getTimeValue(context, hour, mins, null, null);
                } else {
                    int morningTime = MORNING_TIME_WEEKDAY;
                    int hour = morningTime / 60;
                    int mins = morningTime % 60;
                    timeValue = getTimeValue(context, hour, mins, null, null);
                }

                // show multiple suggestions
                // update time value
                timeValue.value.set(Calendar.DAY_OF_YEAR, cal.get(Calendar.DAY_OF_YEAR));
                timeValue.value.set(Calendar.YEAR, cal.get(Calendar.YEAR));

                String displayDate = getDisplayDate(timeValue.value);

                // first item
                suggestionList.add(new SuggestionRow(displayDate + ", "
                        + timeValue.displayString, (int)(timeValue.value.getTimeInMillis()/1000)));

                //second item
                int afternoonTime = AFTERNOON_TIME;
                int hour = afternoonTime / 60;
                int mins = afternoonTime % 60;
                timeValue.value.set(Calendar.HOUR_OF_DAY, hour);
                timeValue.value.set(Calendar.MINUTE, mins);
                suggestionList.add(new SuggestionRow(displayDate + ", "
                        + DateTimeUtils.getDisplayTime(context, timeValue.value, config), (int)(timeValue.value.getTimeInMillis()/1000)));

                //third item
                timeValue.value.set(Calendar.HOUR_OF_DAY, 12 + EVENING_TIME);
                timeValue.value.set(Calendar.MINUTE, 0);
                suggestionList.add(new SuggestionRow(displayDate + ", "
                        + DateTimeUtils.getDisplayTime(context, timeValue.value, config), (int)(timeValue.value.getTimeInMillis()/1000)));
            } else {
                // just show one suggestion as the time is specified with date
                // update time value
                timeValue.value.set(Calendar.DAY_OF_YEAR, cal.get(Calendar.DAY_OF_YEAR));
                timeValue.value.set(Calendar.YEAR, cal.get(Calendar.YEAR));

                suggestionList.add(new SuggestionRow(getDisplayDate(timeValue.value) + ", "
                        + timeValue.displayString, (int)(timeValue.value.getTimeInMillis()/1000)));
            }


        } else {
            super.build(context, suggestionValue, suggestionList);
        }
    }
}
