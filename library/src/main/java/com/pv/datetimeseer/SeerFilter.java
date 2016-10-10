package com.pv.datetimeseer;

import android.content.Context;
import android.text.TextUtils;
import android.widget.Filter;

import com.pv.datetimeseer.parser.SeerParserInitializer;

import java.util.List;

/**
 * @author p-v
 */

public class SeerFilter extends Filter {

    public interface OnSuggestionPublishListener {
        void onSuggestionPublish(List<SuggestionRow> suggestionList);
    }

    private OnSuggestionPublishListener onSuggestionPublishListener;
    private SeerParserInitializer parserInitializer;

    public SeerFilter(Context context, Config config) {
        parserInitializer = new SeerParserInitializer(context, config);
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

        // get suggestions after parsing the input
        List<SuggestionRow> suggestionList = parserInitializer.buildSuggestions(input);

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
