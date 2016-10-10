package com.pv.datetimeseer.parser.model;

import com.pv.datetimeseer.parser.SuggestionValue;

/**
 * @author p-v
 */

public class RelativeDayItem extends SuggestionValue.LocalItemItem {

    public boolean isPartial;

    public RelativeDayItem(int value, boolean isPartial) {
        super(value);
        this.isPartial = isPartial;
    }
}
