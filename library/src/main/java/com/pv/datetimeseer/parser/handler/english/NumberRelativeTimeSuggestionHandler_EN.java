package com.pv.datetimeseer.parser.handler.english;

import android.content.Context;

import com.pv.datetimeseer.parser.SuggestionValue;
import com.pv.datetimeseer.parser.handler.SuggestionHandler;
import com.pv.datetimeseer.parser.model.RelativeDayNumItem;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author p-v
 */

public class NumberRelativeTimeSuggestionHandler_EN extends SuggestionHandler {

    private static final String REGEX = "\\b(?:(?:(?:(?:after\\s{1,2})?(\\d\\d?)\\s{0,2})|next\\s{1,2})(?:(month?)|(m(?:i(?:n(?:u(?:te?)?)?)?)?)|(we(?:ek?))|(d(?:ay?))|(hr|h(?:o(?:ur?)?)?))s?)\\b";
    private Pattern pRel;

    public NumberRelativeTimeSuggestionHandler_EN(){
        pRel = Pattern.compile(REGEX, Pattern.CASE_INSENSITIVE);
    }

    @Override
    public void handle(Context context, String input, SuggestionValue suggestionValue) {
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
        super.handle(context, input, suggestionValue);
    }

}
