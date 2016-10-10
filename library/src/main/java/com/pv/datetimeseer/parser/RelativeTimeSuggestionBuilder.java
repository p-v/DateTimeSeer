package com.pv.datetimeseer.parser;

import android.content.Context;

import com.pv.datetimeseer.Config;
import com.pv.datetimeseer.R;
import com.pv.datetimeseer.SuggestionRow;
import com.pv.datetimeseer.parser.helper.DateTimeUtils;
import com.pv.datetimeseer.parser.model.RelativeDayItem;
import com.pv.datetimeseer.parser.model.TimeItem;

import java.util.Calendar;
import java.util.List;

/**
 * @author p-v
 */
class RelativeTimeSuggestionBuilder extends SuggestionBuilder {

    RelativeTimeSuggestionBuilder(Config config) {
        super(config);
    }

    @Override
    public void build(Context context, SuggestionValue suggestionValue, List<SuggestionRow> suggestionList) {
        RelativeDayItem relItem = suggestionValue.getRelDayItem();
        if (relItem != null) {
            SuggestionValue.LocalItemItem todItem = suggestionValue.getTodItem();
            TimeItem timeItem = suggestionValue.getTimeItem();
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
