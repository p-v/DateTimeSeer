package com.pv.datetimeseer.parser.handler.english;

import android.content.Context;

import com.pv.datetimeseer.parser.SuggestionValue;
import com.pv.datetimeseer.parser.handler.SuggestionHandler;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.pv.datetimeseer.parser.helper.Constants.TOD_AFTERNOON;
import static com.pv.datetimeseer.parser.helper.Constants.TOD_EVENING;
import static com.pv.datetimeseer.parser.helper.Constants.TOD_MORNING;
import static com.pv.datetimeseer.parser.helper.Constants.TOD_NIGHT;

/**
 * @author p-v
 */

public class TODSuggestionHandler extends SuggestionHandler {

    private static final String REGEX = "\\b(?:(morn(?:i(?:n(?:g)?)?)?)|(after(?=(?:\\S+|$))(?:n(?:o(?:o(?:n)?)?)?)?)|(even(?:i(?:n(?:g)?)?)?)|(ni(?:g(?:h(?:t)?)?)?))\\b";
    private Pattern pTod;

    public TODSuggestionHandler() {
        pTod = Pattern.compile(REGEX, Pattern.CASE_INSENSITIVE);
    }

    @Override
    public void handle(Context context, String input, SuggestionValue suggestionValue) {
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
        super.handle(context, input, suggestionValue);
    }
    @Override
    public int getType() {
        return 0;
    }
}
