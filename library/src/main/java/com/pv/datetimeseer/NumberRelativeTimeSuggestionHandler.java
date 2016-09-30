package com.pv.datetimeseer;

import android.content.Context;

import java.util.Calendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
class NumberRelativeTimeSuggestionHandler extends SuggestionHandler {

    private static final String REGEX = "\\b(?:(?:(?:(?:after\\s{1,2})?(\\d\\d?)\\s{0,2})|next\\s{1,2})(?:(month?)|(m(?:i(?:n(?:u(?:te?)?)?)?)?)|(we(?:ek?))|(d(?:ay?))|(hr|h(?:o(?:ur?)?)?))s?)\\b";
    private Pattern pRel;

    class RelativeDayNumItem extends SuggestionValue.LocalItemItem {

        static final int DAY = 1;
        static final int HOUR = 2;
        static final int MIN = 3;
        static final int WEEK = 4;
        static final int MONTH = 5;

        int type;
        int startIdx;
        int endIdx;

        RelativeDayNumItem(int value, int type, int startIdx, int endIdx) {
            super(value);
            this.type = type;
            this.startIdx = startIdx;
            this.endIdx = endIdx;
        }
    }

    NumberRelativeTimeSuggestionHandler(Config config){
        super(config);
        pRel = Pattern.compile(REGEX, Pattern.CASE_INSENSITIVE);
    }

    @Override
    public void handle(Context context, String input, String lastToken, SuggestionValue suggestionValue) {
        Matcher m = pRel.matcher(input);
        if (m.find()) {

            int digit;
            if (m.group(1) != null) {
                digit = Integer.parseInt(m.group(1));
            } else {
                // group 2 i.e. "next" isn't null
                digit = 1;
            }

            int type = -1;
            if (m.group(2) != null) {
                type = RelativeDayNumItem.MONTH;
            } else if (m.group(3) != null) {
                type = RelativeDayNumItem.MIN;
            } else if (m.group(4) != null) {
                type = RelativeDayNumItem.WEEK;
            } else if (m.group(5) != null) {
                type = RelativeDayNumItem.DAY;
            } else if (m.group(6) != null) {
                type = RelativeDayNumItem.HOUR;
            }

            if (type != -1) {
                suggestionValue.appendSuggestion(SuggestionValue.RELATIVE_DAY_NUMBER,
                        new RelativeDayNumItem(digit, type, m.start(), m.end()));

                if (type == RelativeDayNumItem.HOUR || type == RelativeDayNumItem.MIN) {
                    // ignore rest of the handlers if hour/min found
                    return;
                }
            }
        }
        super.handle(context, input, lastToken, suggestionValue);
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
                TimeSuggestionHandler.TimeItem timeItem = suggestionValue.getTimeItem();

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
