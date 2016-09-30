package com.pv.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import com.pv.datetimeseer.SuggestionRow;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private AwesomeAdapter awesomeAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final AutoCompleteTextView autoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.awesome_view);
        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (awesomeAdapter != null) {
                    SuggestionRow suggestionRow = awesomeAdapter.getItem(position);
                    if (suggestionRow != null) {
                        String displayText;
                        if (suggestionRow.getValue() == SuggestionRow.PARTIAL_VALUE) {
                            displayText = suggestionRow.getDisplayValue() + " ";
                            // show dropdown for partial values
                            autoCompleteTextView.post(new Runnable() {
                                @Override
                                public void run() {
                                    autoCompleteTextView.showDropDown();
                                }
                            });
                        } else {
                            displayText = suggestionRow.getDisplayValue();
                            long selectedTime = suggestionRow.getValue() * 1000L;
                            DateFormat df = new SimpleDateFormat("EEEE, d MMMM yyyy h:mma", Locale.ENGLISH);
                            String timeOnScreen = df.format(new Date(selectedTime));
                            Toast.makeText(MainActivity.this, String.format(getString(R.string.awesome_time),
                                    timeOnScreen), Toast.LENGTH_SHORT).show();
                        }
                        autoCompleteTextView.setText(displayText);
                        autoCompleteTextView.setSelection(displayText.length());
                    }
                }
            }
        });
        awesomeAdapter = new AwesomeAdapter(this, android.R.layout.simple_dropdown_item_1line);
        autoCompleteTextView.setAdapter(awesomeAdapter);
    }
}
