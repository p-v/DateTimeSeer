package com.pv.datetimeseer;

import android.content.Context;

import java.util.Calendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Initial suggestion handler used for suggesting values and ignoring other handlers
 * if its able to handle the input
 *
 * @author p-v
 */
class InitialSuggestionHandler extends SuggestionHandler {

    private static final String REGEX = "^\\s*(\\d{1,2})\\s*$";

    private Pattern p;

    InitialSuggestionHandler(Config config) {
        super(config);
        p = Pattern.compile(REGEX);
    }

    @Override
    public void handle(Context context, String input, String lastToken, SuggestionValue suggestionValue) {
        if (input.trim().length() == 2 && input.trim().matches("(?i)^to")) {
            // if only 2 char input is there and that is 't0', do not go further,
            // consider it as today and tomorrow, and show suggestions accordingly
            suggestionValue.appendSuggestion(SuggestionValue.OTHER, 0);
        } else {
            // check if the input has only numbers
            Matcher m = p.matcher(input);
            if (m.find()) {
                int number = Integer.parseInt(m.group(1));
                if (number > 0) {
                    suggestionValue.appendSuggestion(SuggestionValue.NUMBER, number);
                } else {
                    super.handle(context, input, lastToken, suggestionValue);
                }
            } else {
                super.handle(context, input, lastToken, suggestionValue);
            }
        }
    }

    @Override
    public void build(Context context, SuggestionValue suggestionValue, List<SuggestionRow> suggestionList) {
        SuggestionValue.LocalItemItem numItem = suggestionValue.getNumberItem();
        SuggestionValue.LocalItemItem otherItem = suggestionValue.getOtherItem();
        if (numItem != null && numItem.value <= 31) {
            int number = numItem.value;

            Value timeValue;
            if (number < 24) {
                Calendar cal = Calendar.getInstance();
                final int currentHourOfDay = cal.get(Calendar.HOUR_OF_DAY);

                // consider it as time other wise date
                // first time from now be it am or pm
                timeValue = getTimeValue(context, number, 0, currentHourOfDay >= 12 ? "pm" : "am", null);


                // first item
                if (currentHourOfDay < timeValue.value.get(Calendar.HOUR_OF_DAY)) {
                    suggestionList.add(new SuggestionRow(context.getString(R.string.today) + ", "
                            + timeValue.displayString, (int)(timeValue.value.getTimeInMillis()/1000)));
                } else {
                    // increment 12 hours
                    timeValue.value.add(Calendar.HOUR_OF_DAY, 12);
                    // if same day show today otherwise tomorrow
                    if (cal.get(Calendar.DAY_OF_YEAR) == timeValue.value.get(Calendar.DAY_OF_YEAR)) {
                        suggestionList.add(new SuggestionRow(context.getString(R.string.today) + ", "
                                + DateTimeUtils.getDisplayTime(context, timeValue.value, config), (int)(timeValue.value.getTimeInMillis()/1000)));
                    } else{
                        suggestionList.add(new SuggestionRow(context.getString(R.string.tomorrow) + ", "
                                + DateTimeUtils.getDisplayTime(context, timeValue.value, config), (int)(timeValue.value.getTimeInMillis()/1000)));
                    }
                }

                // second item
                // increment 12 hours
                timeValue.value.add(Calendar.HOUR_OF_DAY, 12);
                // if same day show today otherwise tomorrow
                if (cal.get(Calendar.DAY_OF_YEAR) == timeValue.value.get(Calendar.DAY_OF_YEAR)) {
                    suggestionList.add(new SuggestionRow(context.getString(R.string.today) + ", "
                            + DateTimeUtils.getDisplayTime(context, timeValue.value, config), (int)(timeValue.value.getTimeInMillis()/1000)));
                } else{
                    suggestionList.add(new SuggestionRow(context.getString(R.string.tomorrow) + ", "
                            + DateTimeUtils.getDisplayTime(context, timeValue.value, config), (int)(timeValue.value.getTimeInMillis()/1000)));
                }
            }

            Calendar cal = Calendar.getInstance();
            int maxDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

            int currentDayOfMonth = cal.get(Calendar.DAY_OF_MONTH);

            if (currentDayOfMonth < number && number <= maxDay) {
                cal.set(Calendar.DAY_OF_MONTH, number);
            } else {
                cal.add(Calendar.MONTH, 1);
                // TODO revisit someday
                maxDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
                cal.set(Calendar.DAY_OF_MONTH, maxDay < number ? maxDay : number);
            }
            suggestionList.add(new SuggestionRow(DateTimeUtils.getDisplayDate(cal, config), SuggestionRow.PARTIAL_VALUE));

        } else if (otherItem != null) {
            suggestionList.add(new SuggestionRow(context.getString(R.string.today), SuggestionRow.PARTIAL_VALUE));
            suggestionList.add(new SuggestionRow(context.getString(R.string.tomorrow), SuggestionRow.PARTIAL_VALUE));
        } else {
            super.build(context, suggestionValue, suggestionList);
        }
    }
}
