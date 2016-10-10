package com.pv.datetimeseer.parser;

import android.support.v4.util.SparseArrayCompat;

import com.pv.datetimeseer.parser.model.DateItem;
import com.pv.datetimeseer.parser.model.RelativeDayItem;
import com.pv.datetimeseer.parser.model.RelativeDayNumItem;
import com.pv.datetimeseer.parser.model.TimeItem;

/**
 * Values regarding the user input
 *
 * @author p-v
 */
public class SuggestionValue extends SparseArrayCompat<SuggestionValue.LocalItemItem> {

    public static final int RELATIVE_DAY = 0x01;
    public static final int DAY_OF_WEEK = 0x02;
    public static final int TIME_OF_DAY = 0x04;
    public static final int MONTH = 0x08;
    public static final int NUMBER = 0x10;
    public static final int TIME = 0x20;
    public static final int DATE = 0x40;
    public static final int DAY_OF_WEEK_NEXT = 0x80;
    public static final int RELATIVE_DAY_NUMBER = 0x0100;
    public static final int OTHER = 0x0200;

    private TimeItem timeItem;
    private LocalItemItem todItem;
    private RelativeDayItem relDayItem;
    private RelativeDayNumItem relativeDayNumItem;
    private LocalItemItem dowItem;
    private LocalItemItem nextDowItem;
    private LocalItemItem monthItem;
    private DateItem dateItem;
    private LocalItemItem numberItem;
    private LocalItemItem otherItem;

    void init() {
        relDayItem = (RelativeDayItem) this.get(RELATIVE_DAY);
        relativeDayNumItem = (RelativeDayNumItem) this.get(RELATIVE_DAY_NUMBER);
        dowItem = this.get(DAY_OF_WEEK);
        nextDowItem = this.get(DAY_OF_WEEK_NEXT);
        monthItem = this.get(MONTH);
        dateItem = (DateItem) this.get(DATE);
        todItem = this.get(TIME_OF_DAY);
        timeItem = (TimeItem) this.get(TIME);
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

    public TimeItem getTimeItem() {
        return timeItem;
    }

    public LocalItemItem getTodItem() {
        return todItem;
    }

    public RelativeDayItem getRelDayItem() {
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

    public DateItem getDateItem() {
        return dateItem;
    }

    public RelativeDayNumItem getRelativeDayNumItem() {
        return relativeDayNumItem;
    }

    public LocalItemItem getNumberItem() {
        return numberItem;
    }

    public LocalItemItem getOtherItem() {
        return otherItem;
    }


}
