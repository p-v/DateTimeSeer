package com.pv.datetimeseer.parser;

import android.content.Context;

import com.pv.datetimeseer.Config;
import com.pv.datetimeseer.R;
import com.pv.datetimeseer.SuggestionRow;
import com.pv.datetimeseer.parser.model.RelativeDayNumItem;
import com.pv.datetimeseer.parser.model.TimeItem;

import java.util.Calendar;
import java.util.List;

/**
 * @author p-v
 */
class TODSuggestionBuilder extends SuggestionBuilder {

    TODSuggestionBuilder(Config config) {
        super(config);
    }

    @Override
    public void build(Context context, SuggestionValue suggestionValue, List<SuggestionRow> suggestionList) {
        SuggestionValue.LocalItemItem relItem = suggestionValue.getRelDayItem();
        SuggestionValue.LocalItemItem dowItem = suggestionValue.getDowItem();
        SuggestionValue.LocalItemItem nextDowItem = suggestionValue.getNextDowItem();
        SuggestionValue.LocalItemItem monthItem = suggestionValue.getMonthItem();
        SuggestionValue.LocalItemItem dateItem = suggestionValue.getDateItem();
        TimeItem timeItem = suggestionValue.getTimeItem();
        RelativeDayNumItem
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
