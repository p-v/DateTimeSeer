package com.pv.datetimeseer.parser.handler.english;

import android.content.Context;

import com.pv.datetimeseer.parser.SuggestionValue;
import com.pv.datetimeseer.parser.handler.SuggestionHandler;
import com.pv.datetimeseer.parser.model.DateItem;
import com.pv.datetimeseer.parser.model.RelativeDayNumItem;
import com.pv.datetimeseer.parser.model.TimeItem;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author p-v
 */

public class TimeSuggestionHandler_EN extends SuggestionHandler {

    private static final String TIME_RGX = "\\b((?:2[0-3])|(?:1\\d)|(?:0?\\d))(?:(?::|\\s)((?:0?\\d)|(?:[0-5][0-9]?)))?\\s{0,2}([ap](?:\\.?m\\.?)?)?\\b";
    private Pattern timePattern;

    public TimeSuggestionHandler_EN() {
        timePattern = Pattern.compile(TIME_RGX, Pattern.CASE_INSENSITIVE);
    }

    @Override
    public void handle(Context context, String input, SuggestionValue suggestionValue) {

        DateItem dateSuggestion =
                (DateItem) suggestionValue.get(SuggestionValue.DATE);
        RelativeDayNumItem numItem =
                (RelativeDayNumItem) suggestionValue.get(SuggestionValue.RELATIVE_DAY_NUMBER);

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

        super.handle(context, input, suggestionValue);
    }

}
