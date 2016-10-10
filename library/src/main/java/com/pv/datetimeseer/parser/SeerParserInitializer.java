package com.pv.datetimeseer.parser;

import android.content.Context;

import com.pv.datetimeseer.Config;
import com.pv.datetimeseer.SuggestionRow;
import com.pv.datetimeseer.parser.handler.english.DOWSuggestionHandler;
import com.pv.datetimeseer.parser.handler.english.DateSuggestionHandler;
import com.pv.datetimeseer.parser.handler.english.InitialSuggestionHandler;
import com.pv.datetimeseer.parser.handler.english.NumberRelativeTimeSuggestionHandler;
import com.pv.datetimeseer.parser.handler.english.RelativeTimeSuggestionHandler;
import com.pv.datetimeseer.parser.handler.english.TODSuggestionHandler;
import com.pv.datetimeseer.parser.handler.english.TimeSuggestionHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * @author p-v
 */

public class SeerParserInitializer {

    private InitialSuggestionBuilder initialSuggestionBuilder;
    private InitialSuggestionHandler initialSuggestionHandler;
    private Context context;

    public SeerParserInitializer(Context context, Config config) {
        this.context = context;

        // handlers
        initialSuggestionHandler = new InitialSuggestionHandler();
        NumberRelativeTimeSuggestionHandler numberRelativeTimeSuggestionHandler = new NumberRelativeTimeSuggestionHandler();
        RelativeTimeSuggestionHandler relativeTimeSuggestionHandler = new RelativeTimeSuggestionHandler();
        DateSuggestionHandler dateSuggestionHandler = new DateSuggestionHandler();
        DOWSuggestionHandler dowSuggestionHandler = new DOWSuggestionHandler();
        TimeSuggestionHandler timeSuggestionHandler = new TimeSuggestionHandler();
        TODSuggestionHandler todSuggestionHandler = new TODSuggestionHandler();

        // build handler chain
        initialSuggestionHandler.setNextHandler(numberRelativeTimeSuggestionHandler);
        numberRelativeTimeSuggestionHandler.setNextHandler(relativeTimeSuggestionHandler);
        relativeTimeSuggestionHandler.setNextHandler(dateSuggestionHandler);
        dateSuggestionHandler.setNextHandler(dowSuggestionHandler);
        dowSuggestionHandler.setNextHandler(timeSuggestionHandler);
        timeSuggestionHandler.setNextHandler(todSuggestionHandler);

        // builders
        initialSuggestionBuilder = new InitialSuggestionBuilder(config);
        NumberRelativeTimeSuggestionBuilder numberRelativeTimeSuggestionBuilder = new NumberRelativeTimeSuggestionBuilder(config);
        RelativeTimeSuggestionBuilder relativeTimeSuggestionBuilder = new RelativeTimeSuggestionBuilder(config);
        DateSuggestionBuilder dateSuggestionBuilder = new DateSuggestionBuilder(config);
        DOWSuggestionBuilder dowSuggestionBuilder = new DOWSuggestionBuilder(config);
        TimeSuggestionBuilder timeSuggestionBuilder = new TimeSuggestionBuilder(config);
        TODSuggestionBuilder todSuggestionBuilder = new TODSuggestionBuilder(config);

        // build builder chain
        initialSuggestionBuilder.setNextBuilder(timeSuggestionBuilder);
        timeSuggestionBuilder.setNextBuilder(todSuggestionBuilder);
        todSuggestionBuilder.setNextBuilder(numberRelativeTimeSuggestionBuilder);
        numberRelativeTimeSuggestionBuilder.setNextBuilder(relativeTimeSuggestionBuilder);
        relativeTimeSuggestionBuilder.setNextBuilder(dateSuggestionBuilder);
        dateSuggestionBuilder.setNextBuilder(dowSuggestionBuilder);
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
        initialSuggestionBuilder.build(context, suggestionValue, suggestionList);

        return suggestionList;
    }

}
