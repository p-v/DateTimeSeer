package com.pv.datetimeseer.parser;

import android.content.Context;

import com.pv.datetimeseer.Config;
import com.pv.datetimeseer.SuggestionRow;

import java.util.ArrayList;
import java.util.List;

/**
 * @author p-v
 */

public class SeerParserInitializer {

    private InitialSuggestionHandler initialSuggestionHandler;
    private Context context;

    public SeerParserInitializer(Context context, Config config) {
        this.context = context;

        initialSuggestionHandler = new InitialSuggestionHandler(config);
        NumberRelativeTimeSuggestionHandler numberRelativeTimeSuggestionHandler = new NumberRelativeTimeSuggestionHandler(config);
        RelativeTimeSuggestionHandler relativeTimeSuggestionHandler = new RelativeTimeSuggestionHandler(config);
        DateSuggestionHandler dateSuggestionHandler = new DateSuggestionHandler(config);
        DOWSuggestionHandler dowSuggestionHandler = new DOWSuggestionHandler(config);
        TimeSuggestionHandler timeSuggestionHandler = new TimeSuggestionHandler(config);
        TODSuggestionHandler todSuggestionHandler = new TODSuggestionHandler(config);

        // build handler chain
        initialSuggestionHandler.setNextHandler(numberRelativeTimeSuggestionHandler);
        numberRelativeTimeSuggestionHandler.setNextHandler(relativeTimeSuggestionHandler);
        relativeTimeSuggestionHandler.setNextHandler(dateSuggestionHandler);
        dateSuggestionHandler.setNextHandler(dowSuggestionHandler);
        dowSuggestionHandler.setNextHandler(timeSuggestionHandler);
        timeSuggestionHandler.setNextHandler(todSuggestionHandler);

        // build builder chain
        initialSuggestionHandler.setNextBuilder(timeSuggestionHandler);
        timeSuggestionHandler.setNextBuilder(todSuggestionHandler);
        todSuggestionHandler.setNextBuilder(numberRelativeTimeSuggestionHandler);
        numberRelativeTimeSuggestionHandler.setNextBuilder(relativeTimeSuggestionHandler);
        relativeTimeSuggestionHandler.setNextBuilder(dateSuggestionHandler);
        dateSuggestionHandler.setNextBuilder(dowSuggestionHandler);
    }

    public List<SuggestionRow> buildSuggestions(String input) {

        // Stores information about the user input
        SuggestionValue suggestionValue = new SuggestionValue();

        // Interpret user input and store values in suggestion value
        initialSuggestionHandler.handle(context, input, suggestionValue);

        List<SuggestionRow> suggestionList = new ArrayList<>(3);

        // Save values in instance so `SparseArrayCompat#get` method is not
        // called again and again in the builders
        suggestionValue.init();

        // Build suggestion list base on the user input (i.e. the suggestion value)
        initialSuggestionHandler.build(context, suggestionValue, suggestionList);

        return suggestionList;
    }

}
