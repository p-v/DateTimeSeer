package com.pv.datetimeseer.parser;

import android.content.Context;

import com.pv.datetimeseer.Config;
import com.pv.datetimeseer.SuggestionRow;
import com.pv.datetimeseer.parser.helper.Constants;
import com.pv.datetimeseer.parser.helper.DateTimeUtils;
import com.pv.datetimeseer.parser.model.TimeItem;

import java.util.Calendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Suggestion handler for Day of Week.<br/>
 * Handles strings such as, next friday, fri, tuesday<br/>
 * Also handles partial strings such as tues, thus
 *
 * @author p-v
 */
class DOWSuggestionBuilder extends SuggestionBuilder {

    DOWSuggestionBuilder(Config config) {
        super(config);
    }

    @Override
    public void build(Context context, SuggestionValue suggestionValue, List<SuggestionRow> suggestionList) {
        SuggestionValue.LocalItemItem dowItem = suggestionValue.getDowItem();
        SuggestionValue.LocalItemItem nextDowItem = suggestionValue.getNextDowItem();

        // Check whether to handle or not
        if (dowItem != null || nextDowItem != null) {

            // Time related items
            SuggestionValue.LocalItemItem todItem = suggestionValue.getTodItem();
            TimeItem timeItem = suggestionValue.getTimeItem();

            // Initialize user value and current day value
            Calendar cal = Calendar.getInstance();
            final int currentDayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
            final int actualUserValue = dowItem != null ? dowItem.value : nextDowItem.value;

            int daysToAdd;
            if (currentDayOfWeek < actualUserValue) {
                // day of week user value is less than the current day value
                // example user value Saturday = 7 and current day is Friday 6
                daysToAdd = actualUserValue - currentDayOfWeek;
            } else {
                // day of week user value is more than the current day value
                // example user value Monday = 1 and current day is Saturday 7
                daysToAdd = 7 - (currentDayOfWeek - actualUserValue);
            }

            // User inputs the day same as today, then consider the next day of week
            if (dowItem != null && daysToAdd == 0) {
                daysToAdd += 7;
            }

            // For next day of the week item add 7 days if the day is within the threshold value
            if (nextDowItem != null && daysToAdd < Constants.NEXT_WEEK_THRESHOLD) {
                daysToAdd += 7;
            }

            // Compute display and real value if time items are  not null
            Value timeValue;
            if (todItem != null || timeItem != null) {
                if (todItem == null) {
                    // timeItem is not null here
                    int hour = timeItem.value / 60;
                    int mins = timeItem.value % 60;
                    timeValue = getTimeValue(context, hour, mins, null, null);
                } else {
                    timeValue = getTimeValue(context, todItem, timeItem);
                }

                timeValue.value.add(Calendar.DAY_OF_WEEK, daysToAdd);

                // add to suggestion list
                suggestionList.add(new SuggestionRow(getDisplayDate(context,
                        timeValue.value, timeValue.displayString),
                        (int)(timeValue.value.getTimeInMillis()/1000)));

                timeValue.value.add(Calendar.DAY_OF_WEEK, 7);
                suggestionList.add(new SuggestionRow(getDisplayDate(context,
                        timeValue.value, timeValue.displayString),
                        (int)(timeValue.value.getTimeInMillis()/1000)));

                timeValue.value.add(Calendar.DAY_OF_WEEK, 7);
                suggestionList.add(new SuggestionRow(getDisplayDate(context,
                        timeValue.value, timeValue.displayString),
                        (int)(timeValue.value.getTimeInMillis()/1000)));
            } else {
                // Create 3 partial date suggestions
                cal = Calendar.getInstance();
                cal.add(Calendar.DAY_OF_WEEK , daysToAdd);
                // first suggestion
                suggestionList.add(new SuggestionRow(DateTimeUtils.getDisplayDate(cal, config),
                        SuggestionRow.PARTIAL_VALUE));

                // second suggestion
                cal.add(Calendar.DAY_OF_WEEK , 7);
                suggestionList.add(new SuggestionRow(DateTimeUtils.getDisplayDate(cal, config),
                        SuggestionRow.PARTIAL_VALUE));

                // third suggestion
                cal.add(Calendar.DAY_OF_WEEK , 7);
                suggestionList.add(new SuggestionRow(DateTimeUtils.getDisplayDate(cal, config),
                        SuggestionRow.PARTIAL_VALUE));
            }


        } else {
            super.build(context, suggestionValue, suggestionList);
        }
    }
}
