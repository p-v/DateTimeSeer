package com.pv.datetimeseer.parser;

import android.content.Context;

import com.pv.datetimeseer.Config;
import com.pv.datetimeseer.SuggestionRow;
import com.pv.datetimeseer.parser.helper.DateTimeUtils;
import com.pv.datetimeseer.parser.model.RelativeDayNumItem;
import com.pv.datetimeseer.parser.model.TimeItem;

import java.util.Calendar;
import java.util.List;

/**
 *
 * Relative time suggestion handler<br/><br/>
 *
 * e.g. <br/>after 6 hours<br/>
 * 6mins<br/>
 * 10 days<br/>
 * after 2 months<br/>
 *
 * @author p-v
 */
class NumberRelativeTimeSuggestionBuilder extends SuggestionBuilder {

    NumberRelativeTimeSuggestionBuilder(Config config){
        super(config);
    }

    @Override
    public void build(Context context, SuggestionValue suggestionValue, List<SuggestionRow> suggestionList) {
        RelativeDayNumItem relNumItem = suggestionValue.getRelativeDayNumItem();
        if (relNumItem != null) {
            Calendar calendar = Calendar.getInstance();

            boolean isPartial = false;
            switch (relNumItem.type) {
                case RelativeDayNumItem.DAY:
                    isPartial = true;
                    calendar.add(Calendar.DAY_OF_YEAR, relNumItem.value);
                    break;
                case RelativeDayNumItem.HOUR:
                    calendar.add(Calendar.HOUR_OF_DAY, relNumItem.value);
                    break;
                case RelativeDayNumItem.MIN:
                    calendar.add(Calendar.MINUTE, relNumItem.value);
                    break;
                case RelativeDayNumItem.WEEK:
                    isPartial = true;
                    calendar.add(Calendar.WEEK_OF_YEAR, relNumItem.value);
                    break;
                case RelativeDayNumItem.MONTH:
                    isPartial = true;
                    calendar.add(Calendar.MONTH, relNumItem.value);
                    break;
            }

            if (relNumItem.type != RelativeDayNumItem.HOUR && relNumItem.type != RelativeDayNumItem.MIN) {
                TimeItem timeItem = suggestionValue.getTimeItem();

                if (timeItem != null) {
                    int hour = timeItem.value / 60;
                    int mins = timeItem.value % 60;

                    calendar.set(Calendar.HOUR_OF_DAY, hour);
                    calendar.set(Calendar.MINUTE, mins);
                }
            }

            if (isPartial) {
                suggestionList.add(new SuggestionRow(DateTimeUtils.getDisplayDate(calendar, config),
                        SuggestionRow.PARTIAL_VALUE));
            } else {
                suggestionList.add(new SuggestionRow(getDisplayDate(context, calendar, true),
                        (int) (calendar.getTimeInMillis()/1000)));
            }


        } else {
            super.build(context, suggestionValue, suggestionList);
        }
    }
}
