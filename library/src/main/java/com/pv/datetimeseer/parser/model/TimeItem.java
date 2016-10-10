package com.pv.datetimeseer.parser.model;

import com.pv.datetimeseer.parser.SuggestionValue;

/**
 * @author p-v
 * Time item class.
 */
public class TimeItem extends SuggestionValue.LocalItemItem {

    public boolean isAmPmPresent;

    public TimeItem(int value, boolean amPmPresent) {
        super(value);
        this.isAmPmPresent = amPmPresent;
    }
}
