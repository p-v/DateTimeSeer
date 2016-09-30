package com.pv.datetimeseer;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author p-v
 */

class Constants {

    @IntDef({Weekend.SATURDAY_SUNDAY, Weekend.FRIDAY_SATURDAY,
            Weekend.THURSDAY_FRIDAY, Weekend.FRIDAY_ONLY,
            Weekend.SATURDAY_ONLY, Weekend.SUNDAY_ONLY})

    @Retention(RetentionPolicy.SOURCE)
    @interface Weekend {
        int SATURDAY_SUNDAY = 0;
        int FRIDAY_SATURDAY = 1;
        int THURSDAY_FRIDAY = 2;
        int FRIDAY_ONLY = 3;
        int SATURDAY_ONLY = 4;
        int SUNDAY_ONLY = 5;
    }

    static final int NEXT_WEEK_THRESHOLD = 4;

    static final String DATE_FORMAT = "EEEE, d MMMM";
    static final String DATE_FORMAT_WITH_YEAR = "EEEE, d MMMM yyyy";
    static final String TIME_FORMAT_24HOUR = "H:mm";
    static final String TIME_FORMAT_12HOUR_WITH_MINS = "h:mma";
    static final String TIME_FORMAT_12HOUR_WITHOUT_MINS = "ha";

    static final String MONTH_FORMAT_SHORT = "MMM";
    static final String MONTH_FORMAT_LONG = "MMMM";


}
