package com.pv.datetimeseer.parser.handler.english;

import android.content.Context;

import com.pv.datetimeseer.parser.SuggestionValue;
import com.pv.datetimeseer.parser.handler.SuggestionHandler;
import com.pv.datetimeseer.parser.model.RelativeDayItem;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author p-v
 */

public class RelativeTimeSuggestionHandler_EN extends SuggestionHandler {

    private static final String REGEX = "\\b(?:(tod(?:a(?:y)?)?)|(tom(?:o(?:r(?:r(?:o(?:w)?)?)?)?)?)" +
            "|(day after tomorrow)|((?:ton(?:i(?:g(?:(?:h)?t)?)?)?)|(?:ton(?:i(?:t(?:e)?)?)?)))\\b";
    private Pattern pRel;

    public RelativeTimeSuggestionHandler_EN() {
        pRel = Pattern.compile(REGEX, Pattern.CASE_INSENSITIVE);
    }

    @Override
    public void handle(Context context, String input, SuggestionValue suggestionValue) {
        Matcher m = pRel.matcher(input);
        String text;
        if (m.find()) {
            if ((text = m.group(1)) != null) {
                suggestionValue.appendSuggestion(SuggestionValue.RELATIVE_DAY,
                        new RelativeDayItem(0, !"today".equalsIgnoreCase(text.trim())));
            } else if ((text = m.group(2)) != null) {
                suggestionValue.appendSuggestion(SuggestionValue.RELATIVE_DAY,
                        new RelativeDayItem(1, !"tomorrow".equalsIgnoreCase(text.trim())));
            } else if (m.group(3) != null){
                suggestionValue.appendSuggestion(SuggestionValue.RELATIVE_DAY, new RelativeDayItem(2, false));
            } else {
                suggestionValue.appendSuggestion(SuggestionValue.RELATIVE_DAY, new RelativeDayItem(10, false));
            }
        }
        super.handle(context, input, suggestionValue);
    }

}
