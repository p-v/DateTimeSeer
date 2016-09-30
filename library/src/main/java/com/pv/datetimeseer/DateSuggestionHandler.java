package com.pv.datetimeseer;

import android.content.Context;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author p-v
 */
class DateSuggestionHandler extends SuggestionHandler {

    private static final String DATE_RGX = "\\b(?:(0?[1-9]|[12][0-9]|3[01])(?:st|nd|rd|th)?\\s+\\b(jan|feb|mar|apr|may|jun|jul|aug|sep|oct|nov|dec|january|february|march|april|may|june|july|august|september|october|november|december)\\b(?:(?:,?\\s*)(?:(?:20)?(\\d\\d)(?!:)))?|(jan|feb|mar|apr|may|jun|jul|aug|sep|oct|nov|dec|january|february|march|april|may|june|july|august|september|october|november|december)\\s+(0?[1-9]|[12][0-9]|3[01])(?:st|nd|rd|th)?\\b(?:(?:,?\\s*)(?:\\b(?:20)?(\\d\\d)(?!:))\\b)?)\\b";
    private Pattern pDate;

    static class DateItem extends SuggestionValue.LocalItemItem {

        int startIdx;
        int endIdx;

        DateItem(int value, int startIdx, int endIdx) {
            super(value);
            this.startIdx = startIdx;
            this.endIdx = endIdx;
        }
    }

    DateSuggestionHandler(Config config) {
        super(config);
        pDate = Pattern.compile(DATE_RGX, Pattern.CASE_INSENSITIVE);
    }

    @Override
    public void handle(Context context, String input, String lastToken, SuggestionValue suggestionValue) {
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

        super.handle(context, input, lastToken, suggestionValue);
    }

    @Override
    public void build(Context context, SuggestionValue suggestionValue, List<SuggestionRow> suggestionList) {
        DateItem dateItem = suggestionValue.getDateItem();
        if (dateItem != null) {
            SuggestionValue.LocalItemItem todItem = suggestionValue.getTodItem();
            TimeSuggestionHandler.TimeItem timeItem = suggestionValue.getTimeItem();

            Value timeValue = null;
            int secondsOfDay = 0;
            if (todItem != null || timeItem != null) {
                if (todItem == null) {
                    // timeItem is not null here
                    int hour = timeItem.value / 60;
                    int mins = timeItem.value % 60;
                    timeValue = getTimeValue(context, hour, mins, null, null);
                    secondsOfDay = timeItem.value * 60;
                } else {
                    timeValue = getTimeValue(context, todItem, timeItem);
                    secondsOfDay = 60 * (timeValue.value.get(Calendar.MINUTE)
                            + timeValue.value.get(Calendar.HOUR_OF_DAY) * 60);
                }

            }

            Calendar cal = Calendar.getInstance();
            boolean incrementYear = false;
            if (cal.getTimeInMillis() / 1000 > (dateItem.value + secondsOfDay)) {
                // current time is more than the specified date
                incrementYear = true;
            }
            cal.setTimeInMillis(dateItem.value * 1000L);
            if (incrementYear) {
                cal.add(Calendar.YEAR, 1);
            }

            if (timeValue == null) {
                if (DateTimeUtils.isWeekend(cal.get(Calendar.DAY_OF_WEEK), WEEKEND)) {
                    int morningTime = MORNING_TIME_WEEKEND;
                    int hour = morningTime / 60;
                    int mins = morningTime % 60;
                    timeValue = getTimeValue(context, hour, mins, null, null);
                } else {
                    int morningTime = MORNING_TIME_WEEKDAY;
                    int hour = morningTime / 60;
                    int mins = morningTime % 60;
                    timeValue = getTimeValue(context, hour, mins, null, null);
                }

                // show multiple suggestions
                // update time value
                timeValue.value.set(Calendar.DAY_OF_YEAR, cal.get(Calendar.DAY_OF_YEAR));
                timeValue.value.set(Calendar.YEAR, cal.get(Calendar.YEAR));

                String displayDate = getDisplayDate(timeValue.value);

                // first item
                suggestionList.add(new SuggestionRow(displayDate + ", "
                        + timeValue.displayString, (int)(timeValue.value.getTimeInMillis()/1000)));

                //second item
                int afternoonTime = AFTERNOON_TIME;
                int hour = afternoonTime / 60;
                int mins = afternoonTime % 60;
                timeValue.value.set(Calendar.HOUR_OF_DAY, hour);
                timeValue.value.set(Calendar.MINUTE, mins);
                suggestionList.add(new SuggestionRow(displayDate + ", "
                        + DateTimeUtils.getDisplayTime(context, timeValue.value, config), (int)(timeValue.value.getTimeInMillis()/1000)));

                //third item
                timeValue.value.set(Calendar.HOUR_OF_DAY, 12 + EVENING_TIME);
                timeValue.value.set(Calendar.MINUTE, 0);
                suggestionList.add(new SuggestionRow(displayDate + ", "
                        + DateTimeUtils.getDisplayTime(context, timeValue.value, config), (int)(timeValue.value.getTimeInMillis()/1000)));
            } else {
                // just show one suggestion as the time is specified with date
                // update time value
                timeValue.value.set(Calendar.DAY_OF_YEAR, cal.get(Calendar.DAY_OF_YEAR));
                timeValue.value.set(Calendar.YEAR, cal.get(Calendar.YEAR));

                suggestionList.add(new SuggestionRow(getDisplayDate(timeValue.value) + ", "
                        + timeValue.displayString, (int)(timeValue.value.getTimeInMillis()/1000)));
            }


        } else {
            super.build(context, suggestionValue, suggestionList);
        }
    }
}
