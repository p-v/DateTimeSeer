package com.pv.datetimeseer;

import android.support.v4.util.SparseArrayCompat;

/**
 * Values regarding the user input
 *
 * @author p-v
 */
class SuggestionValue extends SparseArrayCompat<SuggestionValue.LocalItemItem> {

    static final int RELATIVE_DAY = 0x01;
    static final int DAY_OF_WEEK = 0x02;
    static final int TIME_OF_DAY = 0x04;
    static final int MONTH = 0x08;
    static final int NUMBER = 0x10;
    static final int TIME = 0x20;
    static final int DATE = 0x40;
    static final int DAY_OF_WEEK_NEXT = 0x80;
    static final int RELATIVE_DAY_NUMBER = 0x0100;
    static final int OTHER = 0x0200;

    private TimeSuggestionHandler.TimeItem timeItem;
    private LocalItemItem todItem;
    private RelativeTimeSuggestionHandler.RelativeDayItem relDayItem;
    private NumberRelativeTimeSuggestionHandler.RelativeDayNumItem relativeDayNumItem;
    private LocalItemItem dowItem;
    private LocalItemItem nextDowItem;
    private LocalItemItem monthItem;
    private DateSuggestionHandler.DateItem dateItem;
    private LocalItemItem numberItem;
    private LocalItemItem otherItem;

    void init() {
        relDayItem = (RelativeTimeSuggestionHandler.RelativeDayItem) this.get(RELATIVE_DAY);
        relativeDayNumItem = (NumberRelativeTimeSuggestionHandler.RelativeDayNumItem) this.get(RELATIVE_DAY_NUMBER);
        dowItem = this.get(DAY_OF_WEEK);
        nextDowItem = this.get(DAY_OF_WEEK_NEXT);
        monthItem = this.get(MONTH);
        dateItem = (DateSuggestionHandler.DateItem) this.get(DATE);
        todItem = this.get(TIME_OF_DAY);
        timeItem = (TimeSuggestionHandler.TimeItem) this.get(TIME);
        numberItem = this.get(NUMBER);
        otherItem = this.get(OTHER);
    }

    public void appendSuggestion(int flag, int value) {
        super.append(flag, new LocalItemItem(value));
    }

    public void appendSuggestion(int flag, LocalItemItem item) {
        super.append(flag, item);
    }

    public static class LocalItemItem {

        public int value;

        public LocalItemItem(int value) {
            this.value = value;
        }

    }

    public TimeSuggestionHandler.TimeItem getTimeItem() {
        return timeItem;
    }

    public LocalItemItem getTodItem() {
        return todItem;
    }

    public RelativeTimeSuggestionHandler.RelativeDayItem getRelDayItem() {
        return relDayItem;
    }

    public LocalItemItem getDowItem() {
        return dowItem;
    }

    public LocalItemItem getNextDowItem() {
        return nextDowItem;
    }

    public LocalItemItem getMonthItem() {
        return monthItem;
    }

    public DateSuggestionHandler.DateItem getDateItem() {
        return dateItem;
    }

    public NumberRelativeTimeSuggestionHandler.RelativeDayNumItem getRelativeDayNumItem() {
        return relativeDayNumItem;
    }

    public LocalItemItem getNumberItem() {
        return numberItem;
    }

    public LocalItemItem getOtherItem() {
        return otherItem;
    }


}
