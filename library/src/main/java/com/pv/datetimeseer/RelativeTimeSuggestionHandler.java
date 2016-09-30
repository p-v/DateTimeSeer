package com.pv.datetimeseer;

import android.content.Context;

import java.util.Calendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author p-v
 */
class RelativeTimeSuggestionHandler extends SuggestionHandler {

    private static final String REGEX = "\\b(?:(tod(?:a(?:y)?)?)|(tom(?:o(?:r(?:r(?:o(?:w)?)?)?)?)?)" +
            "|(day after tomorrow)|((?:ton(?:i(?:g(?:(?:h)?t)?)?)?)|(?:ton(?:i(?:t(?:e)?)?)?)))\\b";
    private Pattern pRel;

    RelativeTimeSuggestionHandler(Config config) {
        super(config);
        pRel = Pattern.compile(REGEX, Pattern.CASE_INSENSITIVE);
    }
    class RelativeDayItem extends SuggestionValue.LocalItemItem {

        boolean isPartial;

        RelativeDayItem(int value, boolean isPartial) {
            super(value);
            this.isPartial = isPartial;
        }
    }

    @Override
    public void handle(Context context, String input, String lastToken, SuggestionValue suggestionValue) {
        Matcher m = pRel.matcher(input);
        String text;
        if (m.find()) {
            if ((text = m.group(1)) != null) {
                suggestionValue.appendSuggestion(SuggestionValue.RELATIVE_DAY,
                        new RelativeDayItem(0, !"today".equalsIgnoreCase(text.trim())));
            } else if ((text = m.group(2)) != null) {
                suggestionValue.appendSuggestion(SuggestionValue.RELATIVE_DAY,
                        new RelativeDayItem(1, !"tomorrow".equalsIgnoreCase(text.trim())));
            } else if (m.group(3) != null){
                suggestionValue.appendSuggestion(SuggestionValue.RELATIVE_DAY, new RelativeDayItem(2, false));
            } else {
                suggestionValue.appendSuggestion(SuggestionValue.RELATIVE_DAY, new RelativeDayItem(10, false));
            }
        }
        super.handle(context, input, lastToken, suggestionValue);
    }

    @Override
    public void build(Context context, SuggestionValue suggestionValue, List<SuggestionRow> suggestionList) {
        RelativeDayItem relItem = suggestionValue.getRelDayItem();
        if (relItem != null) {
            SuggestionValue.LocalItemItem todItem = suggestionValue.getTodItem();
            TimeSuggestionHandler.TimeItem timeItem = suggestionValue.getTimeItem();
            Value timeValue = null;
            if (todItem != null || timeItem != null) {
                if (todItem == null) {
                    // timeItem is not null here
                    int hour = timeItem.value / 60;
                    int mins = timeItem.value % 60;
                    timeValue = getTimeValue(context, hour, mins, null, null);
                } else {
                    timeValue = getTimeValue(context, todItem, timeItem);
                }
            }
            // handle relative value
            if (relItem.value == 0) {
                // For today
                if (timeValue == null) {
                    if (!relItem.isPartial) {

                        int afternoonTime = AFTERNOON_TIME;

                        // time is not specified
                        Calendar cal = Calendar.getInstance();
                        int currentHourOfDay = cal.get(Calendar.HOUR_OF_DAY);
                        int afternoonHour = afternoonTime / 60;
                        int afternoonMins = afternoonTime % 60;
                        int eveningHour = 12 + EVENING_TIME;

                        if (currentHourOfDay < 23) {
                            timeValue = getTimeValue(context, currentHourOfDay + 1, 0, null, null);
                            // Current time is less than 11PM
                            suggestionList.add(new SuggestionRow(context.getString(R.string.today) + ", "
                                    + timeValue.displayString, (int)(timeValue.value.getTimeInMillis()/1000)));
                        }

                        if (currentHourOfDay < afternoonHour && currentHourOfDay + 1 != afternoonHour) {
                            timeValue = getTimeValue(context, afternoonHour, afternoonMins, null, null);
                            suggestionList.add(new SuggestionRow(context.getString(R.string.today) + ", "
                                    + timeValue.displayString, (int)(timeValue.value.getTimeInMillis()/1000)));
                        }

                        if (currentHourOfDay < eveningHour && currentHourOfDay +1 != eveningHour) {
                            timeValue = getTimeValue(context, eveningHour, 0, "pm", null);
                            suggestionList.add(new SuggestionRow(context.getString(R.string.today) + ", "
                                    + timeValue.displayString, (int)(timeValue.value.getTimeInMillis()/1000)));
                        }
                    } else {
                        suggestionList.add(new SuggestionRow(context.getString(R.string.today), SuggestionRow.PARTIAL_VALUE));
                    }
                } else {
                    Calendar cal = Calendar.getInstance();
                    if (timeValue.value.before(cal)) {
                        // time past increment time by 12 hours if AM/PM is not specified otherwise by 24
                        if (timeItem.isAmPmPresent) {
                            timeValue.value.add(Calendar.HOUR_OF_DAY, 24);
                            suggestionList.add(new SuggestionRow(context.getString(R.string.tomorrow) + ", "
                                    + DateTimeUtils.getDisplayTime(context, timeValue.value, config), (int)(timeValue.value.getTimeInMillis()/1000)));
                        } else {
                            timeValue.value.add(Calendar.HOUR_OF_DAY, 12);
                            if (timeValue.value.before(cal)) {
                                timeValue.value.add(Calendar.HOUR_OF_DAY, 12);
                                suggestionList.add(new SuggestionRow(context.getString(R.string.tomorrow) + ", "
                                        + DateTimeUtils.getDisplayTime(context, timeValue.value, config), (int)(timeValue.value.getTimeInMillis()/1000)));
                            } else {
                                if (cal.get(Calendar.DAY_OF_YEAR) == timeValue.value.get(Calendar.DAY_OF_YEAR)) {
                                    suggestionList.add(new SuggestionRow(context.getString(R.string.today) + ", "
                                            + DateTimeUtils.getDisplayTime(context, timeValue.value, config), (int)(timeValue.value.getTimeInMillis()/1000)));
                                } else {
                                    suggestionList.add(new SuggestionRow(context.getString(R.string.tomorrow) + ", "
                                            + DateTimeUtils.getDisplayTime(context, timeValue.value, config), (int)(timeValue.value.getTimeInMillis()/1000)));
                                }
                            }
                        }
                    } else {
                        suggestionList.add(new SuggestionRow(context.getString(R.string.today) + ", "
                                + timeValue.displayString, (int)(timeValue.value.getTimeInMillis()/1000)));
                    }
                }
            } else if (relItem.value == 1) {
                if (timeValue == null) {
                    if (!relItem.isPartial) {
                        int morningTime = MORNING_TIME_WEEKDAY;

                        timeValue = getTimeValue(context, morningTime / 60, morningTime % 60, null, null);

                        // increment a day for tomorrow
                        timeValue.value.add(Calendar.DAY_OF_YEAR, 1);
                        suggestionList.add(new SuggestionRow(context.getString(R.string.tomorrow) + ", "
                                + timeValue.displayString, (int)(timeValue.value.getTimeInMillis()/1000)));

                        int afternoonTime = AFTERNOON_TIME;
                        timeValue = getTimeValue(context, afternoonTime / 60,  afternoonTime % 60, null, null);
                        // increment a day for tomorrow
                        timeValue.value.add(Calendar.DAY_OF_YEAR, 1);
                        suggestionList.add(new SuggestionRow(context.getString(R.string.tomorrow) + ", "
                                + timeValue.displayString, (int)(timeValue.value.getTimeInMillis()/1000)));

                        int eveningHour = 12 + EVENING_TIME;
                        timeValue = getTimeValue(context, eveningHour, 0, "pm", null);
                        // increment a day for tomorrow
                        timeValue.value.add(Calendar.DAY_OF_YEAR, 1);
                        suggestionList.add(new SuggestionRow(context.getString(R.string.tomorrow) + ", "
                                + timeValue.displayString, (int)(timeValue.value.getTimeInMillis()/1000)));
                    } else {
                        suggestionList.add(new SuggestionRow(context.getString(R.string.tomorrow), SuggestionRow.PARTIAL_VALUE));
                    }

                } else {
                    // increment a day for tomorrow
                    timeValue.value.add(Calendar.DAY_OF_YEAR, 1);
                    suggestionList.add(new SuggestionRow(context.getString(R.string.tomorrow) + ", "
                            + timeValue.displayString, (int)(timeValue.value.getTimeInMillis()/1000)));
                }
            } else if (relItem.value == 2) {
                if (timeValue == null) {
                    int morningTime = MORNING_TIME_WEEKDAY;

                    timeValue = getTimeValue(context, morningTime / 60, morningTime % 60, null, null);
                    // increment two days for day after tomorrow
                    timeValue.value.add(Calendar.DAY_OF_YEAR, 2);
                    suggestionList.add(new SuggestionRow(getDisplayDate(timeValue.value) + ", "
                            + timeValue.displayString, (int)(timeValue.value.getTimeInMillis()/1000)));

                    int afternoonTime = AFTERNOON_TIME;
                    timeValue = getTimeValue(context, afternoonTime / 60, afternoonTime % 60, null, null);
                    // increment two days for day after tomorrow
                    timeValue.value.add(Calendar.DAY_OF_YEAR, 2);
                    suggestionList.add(new SuggestionRow(getDisplayDate(timeValue.value) + ", "
                            + timeValue.displayString, (int)(timeValue.value.getTimeInMillis()/1000)));

                    int eveningHour = 12 + EVENING_TIME;
                    timeValue = getTimeValue(context, eveningHour, 0, "pm", null);
                    // increment two days for day after tomorrow
                    timeValue.value.add(Calendar.DAY_OF_YEAR, 2);
                    suggestionList.add(new SuggestionRow(getDisplayDate(timeValue.value) + ", "
                            + timeValue.displayString, (int)(timeValue.value.getTimeInMillis()/1000)));
                } else {
                    // increment two days for day after tomorrow
                    timeValue.value.add(Calendar.DAY_OF_YEAR, 2);
                    suggestionList.add(new SuggestionRow(getDisplayDate(timeValue.value) + ", "
                            + timeValue.displayString, (int)(timeValue.value.getTimeInMillis()/1000)));

                }
            } else if (relItem.value == 10) {
                if (timeValue == null) {
                    timeValue = getTimeValue(context, 22, 0, "pm", null);
                    suggestionList.add(new SuggestionRow(context.getString(R.string.today) + ", "
                            + timeValue.displayString, (int)(timeValue.value.getTimeInMillis()/1000)));
                } else {
                    suggestionList.add(new SuggestionRow(context.getString(R.string.today) + ", "
                            + timeValue.displayString, (int)(timeValue.value.getTimeInMillis()/1000)));
                }
            }
        } else {
            super.build(context, suggestionValue, suggestionList);
        }
    }

}
