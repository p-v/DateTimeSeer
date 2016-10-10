package com.pv.datetimeseer.parser.handler.english;

import android.content.Context;

import com.pv.datetimeseer.parser.SuggestionValue;
import com.pv.datetimeseer.parser.handler.SuggestionHandler;
import com.pv.datetimeseer.parser.helper.Constants;
import com.pv.datetimeseer.parser.model.DateItem;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author p-v
 */

public class DateSuggestionHandler extends SuggestionHandler {

    private Pattern pDate;
    private static final String DATE_RGX = "\\b(?:(0?[1-9]|[12][0-9]|3[01])(?:st|nd|rd|th)?\\s+\\b(jan|feb|mar|apr|may|jun|jul|aug|sep|oct|nov|dec|january|february|march|april|may|june|july|august|september|october|november|december)\\b(?:(?:,?\\s*)(?:(?:20)?(\\d\\d)(?!:)))?|(jan|feb|mar|apr|may|jun|jul|aug|sep|oct|nov|dec|january|february|march|april|may|june|july|august|september|october|november|december)\\s+(0?[1-9]|[12][0-9]|3[01])(?:st|nd|rd|th)?\\b(?:(?:,?\\s*)(?:\\b(?:20)?(\\d\\d)(?!:))\\b)?)\\b";

    public DateSuggestionHandler() {
        pDate = Pattern.compile(DATE_RGX, Pattern.CASE_INSENSITIVE);
    }

    @Override
    public void handle(Context context, String input, SuggestionValue suggestionValue) {
        Matcher m = pDate.matcher(input);

        if (m.find()) {
            int dayOfMonth;
            int year = 0;
            String month;
            String yearStr;
            if (m.group(1) != null) {
                dayOfMonth = Integer.parseInt(m.group(1));
                month = m.group(2);
                yearStr = m.group(3);
            } else {
                dayOfMonth = Integer.parseInt(m.group(5));
                month = m.group(4);
                yearStr = m.group(6);
            }
            if (yearStr != null) {
                year = 2000 + Integer.parseInt(yearStr);
            }
            DateFormat fmt;
            if(month.length() == 3){
                fmt = new SimpleDateFormat(Constants.MONTH_FORMAT_SHORT, Locale.ENGLISH);
            }else{
                fmt = new SimpleDateFormat(Constants.MONTH_FORMAT_LONG, Locale.ENGLISH);
            }
            Date date = null;
            try {
                date = fmt.parse(month);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Calendar cal = Calendar.getInstance();

            int currentYear = cal.get(Calendar.YEAR);
            if (year > 0 && year >= currentYear) {
                cal.set(Calendar.YEAR, year);
            }
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            if (date != null) {
                Calendar ctemp = Calendar.getInstance();
                ctemp.setTime(date);
                cal.set(Calendar.MONTH, ctemp.get(Calendar.MONTH));
            }
            suggestionValue.appendSuggestion(SuggestionValue.DATE,
                    new DateItem((int)(cal.getTimeInMillis()/1000), m.start(), m.end()));
        }

        super.handle(context, input, suggestionValue);
    }

    @Override
    public int getType() {
        return Type.DATE_SUGGESTION;
    }
}
