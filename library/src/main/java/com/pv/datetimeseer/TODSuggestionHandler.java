package com.pv.datetimeseer;

import android.content.Context;

import java.util.Calendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author p-v
 */
class TODSuggestionHandler extends SuggestionHandler {

    static final int TOD_MORNING = 1;
    static final int TOD_AFTERNOON = 2;
    static final int TOD_EVENING = 3;
    static final int TOD_NIGHT = 4;

    private static final String REGEX = "\\b(?:(morn(?:i(?:n(?:g)?)?)?)|(after(?=(?:\\S+|$))(?:n(?:o(?:o(?:n)?)?)?)?)|(even(?:i(?:n(?:g)?)?)?)|(ni(?:g(?:h(?:t)?)?)?))\\b";
    private Pattern pTod;

    TODSuggestionHandler(Config config) {
        super(config);
        pTod = Pattern.compile(REGEX, Pattern.CASE_INSENSITIVE);
    }

    @Override
    public void handle(Context context, String input, String lastToken, SuggestionValue suggestionValue) {
        Matcher matcher = pTod.matcher(input);
        if (matcher.find()) {
            int value;
            if (matcher.group(1) != null) {
                value = TOD_MORNING;
            } else if (matcher.group(2) != null) {
                value = TOD_AFTERNOON;
            } else if(matcher.group(3) != null) {
                value = TOD_EVENING;
            } else {
                value = TOD_NIGHT;
            }
            suggestionValue.appendSuggestion(SuggestionValue.TIME_OF_DAY, value);
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
        TimeSuggestionHandler.TimeItem timeItem = suggestionValue.getTimeItem();
        NumberRelativeTimeSuggestionHandler.RelativeDayNumItem
                relNumItem = suggestionValue.getRelativeDayNumItem();
        SuggestionValue.LocalItemItem todItem = suggestionValue.getTodItem();

        // Ignoring time timeItem. Using it later in the method
        boolean hasOnlyTime = relItem == null && dowItem == null && nextDowItem == null &&
                monthItem == null && dateItem == null && relNumItem == null && todItem != null;

        if (hasOnlyTime) {
            Value timeValue = getTimeValue(context, todItem, timeItem);
            if (timeValue != null) {
                if (timeItem == null || !timeItem.isAmPmPresent) {
                    suggestionList.add(new SuggestionRow(context.getString(R.string.today) + ", "
                            + timeValue.displayString, (int)(timeValue.value.getTimeInMillis()/1000)));

                    // increment a day for tomorrow
                    timeValue.value.add(Calendar.DAY_OF_YEAR, 1);
                    suggestionList.add(new SuggestionRow(context.getString(R.string.tomorrow) + ", "
                            + timeValue.displayString, (int)(timeValue.value.getTimeInMillis()/1000)));

                    // increment a day for day after tomorrow
                    timeValue.value.add(Calendar.DAY_OF_YEAR, 1);
                    suggestionList.add(new SuggestionRow(getDisplayDate(timeValue.value) + ", "
                            + timeValue.displayString, (int)(timeValue.value.getTimeInMillis()/1000)));
                } else {
                    // Time present with AM/PM
                    int hour = timeItem.value / 60;
                    int mins = timeItem.value % 60;
                    timeValue = getTimeValue(context, hour, mins, null, null);

                    suggestionList.add(new SuggestionRow(context.getString(R.string.today) + ", "
                            + timeValue.displayString, (int)(timeValue.value.getTimeInMillis()/1000)));

                    // increment a day for tomorrow
                    timeValue.value.add(Calendar.DAY_OF_YEAR, 1);
                    suggestionList.add(new SuggestionRow(context.getString(R.string.tomorrow) + ", "
                            + timeValue.displayString, (int)(timeValue.value.getTimeInMillis()/1000)));

                    // increment a day for day after tomorrow
                    timeValue.value.add(Calendar.DAY_OF_YEAR, 1);
                    suggestionList.add(new SuggestionRow(getDisplayDate(timeValue.value) + ", "
                            + timeValue.displayString, (int)(timeValue.value.getTimeInMillis()/1000)));
                }
            }
        } else {
            super.build(context, suggestionValue, suggestionList);
        }

    }
}
