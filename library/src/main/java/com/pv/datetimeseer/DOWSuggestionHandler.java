package com.pv.datetimeseer;

import android.content.Context;

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
class DOWSuggestionHandler extends SuggestionHandler {

    private static final String DAY_OF_WEEK = "(next\\s{0,2})?(?:\\b(?:(?:(mon)|(fri)|(sun))(?:d(?:ay?)?)?)|\\b(tue(?:s(?:d(?:ay?)?)?)?)|\\b(wed(?:n(?:e(?:s(?:d(?:ay?)?)?)?)?)?)|\\b(thu(?:r(?:s(?:d(?:ay?)?)?)?)?)|\\b(sat(?:u(?:r(?:d(?:ay?)?)?)?)?))\\b";
    private Pattern pDow;

    DOWSuggestionHandler(Config config) {
        super(config);
        pDow = Pattern.compile(DAY_OF_WEEK, Pattern.CASE_INSENSITIVE);
    }

    @Override
    public void handle(Context context, String input, String lastToken, SuggestionValue suggestionValue) {
        Matcher matcher = pDow.matcher(input);
        if (matcher.find()) {
            int value = -1;
            if (matcher.group(2) != null) {
                // Monday
                value = Calendar.MONDAY;
            } else if (matcher.group(3) != null) {
                // Friday
                value = Calendar.FRIDAY;
            } else if (matcher.group(4) != null) {
                // Sunday
                value = Calendar.SUNDAY;
            } else if (matcher.group(5) != null) {
                // Tuesday
                value = Calendar.TUESDAY;
            } else if (matcher.group(6) != null) {
                // Wednesday
                value = Calendar.WEDNESDAY;
            } else if (matcher.group(7) != null) {
                // Thursday
                value = Calendar.THURSDAY;
            } else if (matcher.group(8) != null) {
                // Saturday
                value = Calendar.SATURDAY;
            }
            if (value != -1) {
                if (matcher.group(1) != null) {
                    suggestionValue.appendSuggestion(SuggestionValue.DAY_OF_WEEK_NEXT, value);
                } else {
                    suggestionValue.appendSuggestion(SuggestionValue.DAY_OF_WEEK, value);
                }
            }
        }

        super.handle(context, input, lastToken, suggestionValue);
    }

    @Override
    public void build(Context context, SuggestionValue suggestionValue, List<SuggestionRow> suggestionList) {
        SuggestionValue.LocalItemItem dowItem = suggestionValue.getDowItem();
        SuggestionValue.LocalItemItem nextDowItem = suggestionValue.getNextDowItem();

        // Check whether to handle or not
        if (dowItem != null || nextDowItem != null) {

            // Time related items
            SuggestionValue.LocalItemItem todItem = suggestionValue.getTodItem();
            TimeSuggestionHandler.TimeItem timeItem = suggestionValue.getTimeItem();

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
