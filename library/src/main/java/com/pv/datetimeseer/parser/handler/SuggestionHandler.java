package com.pv.datetimeseer.parser.handler;

import android.content.Context;
import android.support.annotation.IntDef;

import com.pv.datetimeseer.parser.SuggestionValue;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author p-v
 */

public abstract class SuggestionHandler {


    @IntDef({Type.DATE_SUGGESTION, Type.DOW_SUGGESTION, Type.INITIAL_SUGGESTION,
            Type.NUMBER_RELATIVE_SUGGESTION, Type.RELATIVE_TIME_SUGGESTIONS,
            Type.TIME_SUGGESTION, Type.TOD_SUGGESTION})

    @Retention(RetentionPolicy.SOURCE)
    public @interface Type {
        int DATE_SUGGESTION = 0;
        int DOW_SUGGESTION = 1;
        int INITIAL_SUGGESTION = 2;
        int NUMBER_RELATIVE_SUGGESTION = 3;
        int RELATIVE_TIME_SUGGESTIONS = 4;
        int TIME_SUGGESTION = 5;
        int TOD_SUGGESTION = 6;
    }
    private SuggestionHandler nextHandler;

    public SuggestionHandler() {
    }

    /**
     * Sets the next handler after the current handler
     *
     * @param nextHandler next handler after the current handler
     */
    public void setNextHandler(SuggestionHandler nextHandler) {
        this.nextHandler = nextHandler;
    }

    /**
     * Interprets the input and converts it to SuggestionValue which can be used to build the
     * suggestion list
     *
     * @param context The context to use.
     * @param input User input.
     * @param suggestionValue The value where all the related values are stored based on input (pass
     *                        empty the first time)
     */
    public void handle(Context context, String input, SuggestionValue suggestionValue) {
        if (nextHandler != null) {
            nextHandler.handle(context, input, suggestionValue);
        }
    }

    public abstract @Type int getType();
}
