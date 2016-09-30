package com.pv.sample;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.pv.datetimeseer.Config;
import com.pv.datetimeseer.SeerFilter;
import com.pv.datetimeseer.SuggestionRow;

import java.util.List;

/**
 * @author p-v
 */

public class AwesomeAdapter extends ArrayAdapter<SuggestionRow> implements Filterable {

    private SeerFilter suggestionFilter;
    private List<SuggestionRow> suggestionRowList;

    public AwesomeAdapter(Context context, int resource) {
        super(context, resource);
    }

    @Override
    public int getCount() {
        return suggestionRowList == null ? 0 : suggestionRowList.size();
    }

    @Nullable
    @Override
    public SuggestionRow getItem(int position) {
        return suggestionRowList.get(position);
    }

    @NonNull
    @Override
    public Filter getFilter() {
        if (suggestionFilter == null) {
            Config config = new Config.ConfigBuilder()
                    .setTimeFormat12HoursWithMins("h:mm a")
                    .setTimeFormat12HoursWithoutMins("h a")
                    .build();

            suggestionFilter =  new SeerFilter(getContext(), config);
            suggestionFilter.setOnSuggestionPublishListener(new SeerFilter.OnSuggestionPublishListener() {
                @Override
                public void onSuggestionPublish(List<SuggestionRow> suggestionList) {
                    AwesomeAdapter.this.suggestionRowList = suggestionList;
                    if (suggestionList != null) {
                        AwesomeAdapter.this.notifyDataSetChanged();
                    } else {
                        AwesomeAdapter.this.notifyDataSetInvalidated();
                    }
                }
            });
        }
        return suggestionFilter;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull  ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.suggestion_row, parent, false);
        }
        TextView textView = (TextView) convertView
                .findViewById(R.id.suggestion_textView);
        SuggestionRow suggestion = getItem(position);
        if (suggestion != null) {
            textView.setText(suggestion.getDisplayValue());
        }
        return convertView;
    }

}
