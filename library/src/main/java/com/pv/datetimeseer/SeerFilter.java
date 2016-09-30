package com.pv.datetimeseer;

import android.content.Context;
import android.text.TextUtils;
import android.widget.Filter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author p-v
 */

public class SeerFilter extends Filter {

    public interface OnSuggestionPublishListener {
        void onSuggestionPublish(List<SuggestionRow> suggestionList);
    }

    private InitialSuggestionHandler initialSuggestionHandler;


    private Context context;
    private OnSuggestionPublishListener onSuggestionPublishListener;

    public SeerFilter(Context context, Config config) {

        this.context = context;

        // initialize all handlers
        initialSuggestionHandler =
                new InitialSuggestionHandler(config);
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

    public SeerFilter(Context context) {
        this(context, null);
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        final FilterResults results = new FilterResults();
        if (TextUtils.isEmpty(constraint)) {
            // Return empty results.
            return results;
        }

        String input = constraint.toString();
        String[] splitString = input.split("\\s+");

        // Stores information about the user input
        SuggestionValue suggestionValue = new SuggestionValue();

        // Interpret user input and store values in suggestion value
        initialSuggestionHandler.handle(context, input, splitString[splitString.length - 1],
                suggestionValue);

        List<SuggestionRow> suggestionList = new ArrayList<>(3);

        // Save values in instance so `SparseArrayCompat#get` method is not
        // called again and again in the builders
        suggestionValue.init();

        // Build suggestion list base on the user input (i.e. the suggestion value)
        initialSuggestionHandler.build(context, suggestionValue, suggestionList);

        // update result
        results.values = suggestionList;
        results.count = suggestionList.size();
        return results;

    }

    @Override
    @SuppressWarnings("unchecked")
    protected void publishResults(CharSequence constraint, FilterResults results) {
        List<SuggestionRow> suggestionList = null;
        if(results != null && results.count > 0) {
            suggestionList = (List<SuggestionRow>) results.values;
        }
        if (onSuggestionPublishListener != null) {
            onSuggestionPublishListener.onSuggestionPublish(suggestionList);
        }
    }

    public void setOnSuggestionPublishListener(OnSuggestionPublishListener onSuggestionPublishListener) {
        this.onSuggestionPublishListener = onSuggestionPublishListener;
    }
}
