package com.pv.datetimeseer.parser.handler.english;

import android.content.Context;

import com.pv.datetimeseer.parser.SuggestionValue;
import com.pv.datetimeseer.parser.handler.SuggestionHandler;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author p-v
 */

public class InitialSuggestionHandler_EN extends SuggestionHandler {

    private static final String REGEX = "^\\s*(\\d{1,2})\\s*$";

    private Pattern p;

    public InitialSuggestionHandler_EN() {
        p = Pattern.compile(REGEX);
    }

    @Override
    public void handle(Context context, String input, SuggestionValue suggestionValue) {
        if (input.trim().length() == 2 && input.trim().matches("(?i)^to")) {
            // if only 2 char input is there and that is 't0', do not go further,
            // consider it as today and tomorrow, and show suggestions accordingly
            suggestionValue.appendSuggestion(SuggestionValue.OTHER, 0);
        } else {
            // check if the input has only numbers
            Matcher m = p.matcher(input);
            if (m.find()) {
                int number = Integer.parseInt(m.group(1));
                if (number > 0) {
                    suggestionValue.appendSuggestion(SuggestionValue.NUMBER, number);
                } else {
                    super.handle(context, input, suggestionValue);
                }
            } else {
                super.handle(context, input, suggestionValue);
            }
        }
    }

}
