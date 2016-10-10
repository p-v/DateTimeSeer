package com.pv.datetimeseer.parser;

import android.content.Context;

import com.pv.datetimeseer.Config;
import com.pv.datetimeseer.SuggestionRow;
import com.pv.datetimeseer.parser.handler.SuggestionHandler;
import com.pv.datetimeseer.parser.handler.english.DOWSuggestionHandler_EN;
import com.pv.datetimeseer.parser.handler.english.DateSuggestionHandler_EN;
import com.pv.datetimeseer.parser.handler.english.InitialSuggestionHandler_EN;
import com.pv.datetimeseer.parser.handler.english.NumberRelativeTimeSuggestionHandler_EN;
import com.pv.datetimeseer.parser.handler.english.RelativeTimeSuggestionHandler_EN;
import com.pv.datetimeseer.parser.handler.english.TODSuggestionHandler_EN;
import com.pv.datetimeseer.parser.handler.english.TimeSuggestionHandler_EN;

import java.util.ArrayList;
import java.util.List;

/**
 * @author p-v
 */

public class SeerParserInitializer {

    private SuggestionBuilder initialSuggestionBuilder;
    private SuggestionHandler initialSuggestionHandler;
    private Context context;

    private void initializeHandlers(@Config.Language int language) {
        // handlers
        SuggestionHandler numberRelativeTimeSuggestionHandler;
        SuggestionHandler relativeTimeSuggestionHandler;
        SuggestionHandler dateSuggestionHandler;
        SuggestionHandler dowSuggestionHandler;
        SuggestionHandler timeSuggestionHandler;
        SuggestionHandler todSuggestionHandler;

        switch (language) {

            case Config.Language.ENGLISH:
                initialSuggestionHandler = new InitialSuggestionHandler_EN();
                numberRelativeTimeSuggestionHandler = new NumberRelativeTimeSuggestionHandler_EN();
                relativeTimeSuggestionHandler = new RelativeTimeSuggestionHandler_EN();
                dateSuggestionHandler = new DateSuggestionHandler_EN(language);
                dowSuggestionHandler = new DOWSuggestionHandler_EN();
                timeSuggestionHandler = new TimeSuggestionHandler_EN();
                todSuggestionHandler = new TODSuggestionHandler_EN();
                break;

            // TODO implement other languages here

            default:
                initialSuggestionHandler = new InitialSuggestionHandler_EN();
                numberRelativeTimeSuggestionHandler = new NumberRelativeTimeSuggestionHandler_EN();
                relativeTimeSuggestionHandler = new RelativeTimeSuggestionHandler_EN();
                dateSuggestionHandler = new DateSuggestionHandler_EN(language);
                dowSuggestionHandler = new DOWSuggestionHandler_EN();
                timeSuggestionHandler = new TimeSuggestionHandler_EN();
                todSuggestionHandler = new TODSuggestionHandler_EN();

        }

        // build handler chain
        initialSuggestionHandler.setNextHandler(numberRelativeTimeSuggestionHandler);
        numberRelativeTimeSuggestionHandler.setNextHandler(relativeTimeSuggestionHandler);
        relativeTimeSuggestionHandler.setNextHandler(dateSuggestionHandler);
        dateSuggestionHandler.setNextHandler(dowSuggestionHandler);
        dowSuggestionHandler.setNextHandler(timeSuggestionHandler);
        timeSuggestionHandler.setNextHandler(todSuggestionHandler);
    }

    public SeerParserInitializer(Context context, Config config) {
        this.context = context;

        initializeHandlers(config.getLanguage());

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
