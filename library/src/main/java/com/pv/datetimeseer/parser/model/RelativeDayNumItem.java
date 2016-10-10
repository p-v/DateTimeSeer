package com.pv.datetimeseer.parser.model;

import com.pv.datetimeseer.parser.SuggestionValue;

/**
 * @author p-v
 */

public class RelativeDayNumItem extends SuggestionValue.LocalItemItem {

    public static final int DAY = 1;
    public static final int HOUR = 2;
    public static final int MIN = 3;
    public static final int WEEK = 4;
    public static final int MONTH = 5;

    public int type;
    public int startIdx;
    public int endIdx;

    public RelativeDayNumItem(int value, int type, int startIdx, int endIdx) {
        super(value);
        this.type = type;
        this.startIdx = startIdx;
        this.endIdx = endIdx;
    }
}
