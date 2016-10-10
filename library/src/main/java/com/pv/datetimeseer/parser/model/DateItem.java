package com.pv.datetimeseer.parser.model;

import com.pv.datetimeseer.parser.SuggestionValue;

/**
 * @author p-v
 */

public class DateItem extends SuggestionValue.LocalItemItem {

    public int startIdx;
    public int endIdx;

    public DateItem(int value, int startIdx, int endIdx) {
        super(value);
        this.startIdx = startIdx;
        this.endIdx = endIdx;
    }
}
