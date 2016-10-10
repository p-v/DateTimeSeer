package com.pv.datetimeseer.parser;

import android.content.Context;

import com.pv.datetimeseer.Config;
import com.pv.datetimeseer.R;
import com.pv.datetimeseer.SuggestionRow;
import com.pv.datetimeseer.parser.helper.DateTimeUtils;

import java.util.Calendar;
import java.util.List;

/**
 * Initial suggestion handler used for suggesting values and ignoring other handlers
 * if its able to handle the input
 *
 * @author p-v
 */
class InitialSuggestionBuilder extends SuggestionBuilder {

    InitialSuggestionBuilder(Config config) {
        super(config);
    }

    @Override
    public void build(Context context, SuggestionValue suggestionValue, List<SuggestionRow> suggestionList) {
        SuggestionValue.LocalItemItem numItem = suggestionValue.getNumberItem();
        SuggestionValue.LocalItemItem otherItem = suggestionValue.getOtherItem();
        if (numItem != null && numItem.value <= 31) {
            int number = numItem.value;

            Value timeValue;
            if (number < 24) {
                Calendar cal = Calendar.getInstance();
                final int currentHourOfDay = cal.get(Calendar.HOUR_OF_DAY);

                // consider it as time other wise date
                // first time from now be it am or pm
                timeValue = getTimeValue(context, number, 0, currentHourOfDay >= 12 ? "pm" : "am", null);


                // first item
                if (currentHourOfDay < timeValue.value.get(Calendar.HOUR_OF_DAY)) {
                    suggestionList.add(new SuggestionRow(context.getString(R.string.today) + ", "
                            + timeValue.displayString, (int)(timeValue.value.getTimeInMillis()/1000)));
                } else {
                    // increment 12 hours
                    timeValue.value.add(Calendar.HOUR_OF_DAY, 12);
                    // if same day show today otherwise tomorrow
                    if (cal.get(Calendar.DAY_OF_YEAR) == timeValue.value.get(Calendar.DAY_OF_YEAR)) {
                        suggestionList.add(new SuggestionRow(context.getString(R.string.today) + ", "
                                + DateTimeUtils.getDisplayTime(context, timeValue.value, config), (int)(timeValue.value.getTimeInMillis()/1000)));
                    } else{
                        suggestionList.add(new SuggestionRow(context.getString(R.string.tomorrow) + ", "
                                + DateTimeUtils.getDisplayTime(context, timeValue.value, config), (int)(timeValue.value.getTimeInMillis()/1000)));
                    }
                }

                // second item
                // increment 12 hours
                timeValue.value.add(Calendar.HOUR_OF_DAY, 12);
                // if same day show today otherwise tomorrow
                if (cal.get(Calendar.DAY_OF_YEAR) == timeValue.value.get(Calendar.DAY_OF_YEAR)) {
                    suggestionList.add(new SuggestionRow(context.getString(R.string.today) + ", "
                            + DateTimeUtils.getDisplayTime(context, timeValue.value, config), (int)(timeValue.value.getTimeInMillis()/1000)));
                } else{
                    suggestionList.add(new SuggestionRow(context.getString(R.string.tomorrow) + ", "
                            + DateTimeUtils.getDisplayTime(context, timeValue.value, config), (int)(timeValue.value.getTimeInMillis()/1000)));
                }
            }

            Calendar cal = Calendar.getInstance();
            int maxDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

            int currentDayOfMonth = cal.get(Calendar.DAY_OF_MONTH);

            if (currentDayOfMonth < number && number <= maxDay) {
                cal.set(Calendar.DAY_OF_MONTH, number);
            } else {
                cal.add(Calendar.MONTH, 1);
                // TODO revisit someday
                maxDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
                cal.set(Calendar.DAY_OF_MONTH, maxDay < number ? maxDay : number);
            }
            suggestionList.add(new SuggestionRow(DateTimeUtils.getDisplayDate(cal, config), SuggestionRow.PARTIAL_VALUE));

        } else if (otherItem != null) {
            suggestionList.add(new SuggestionRow(context.getString(R.string.today), SuggestionRow.PARTIAL_VALUE));
            suggestionList.add(new SuggestionRow(context.getString(R.string.tomorrow), SuggestionRow.PARTIAL_VALUE));
        } else {
            super.build(context, suggestionValue, suggestionList);
        }
    }
}
