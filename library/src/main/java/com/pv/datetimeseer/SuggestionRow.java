package com.pv.datetimeseer;

/**
 * @author p-v
 */

public class SuggestionRow {

    public static final int PARTIAL_VALUE = -999;

    private String displayValue;
    /**
     * Time in
     */
    private int value;

    public SuggestionRow(String displayValue, int value) {
        this.displayValue = displayValue;
        this.value = value;
    }

    /**
     * Use this to get more results when the value is set to PARTIAL_VALUE.
     *
     * @return the display value. <br>
     *
     */
    public String getDisplayValue() {
        return displayValue;
    }

    /**
     *
     * @return the time in seconds
     */
    public int getValue() {
        return value;
    }
}
