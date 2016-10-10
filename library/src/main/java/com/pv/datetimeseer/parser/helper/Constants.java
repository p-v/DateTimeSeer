package com.pv.datetimeseer.parser.helper;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author p-v
 */

public class Constants {

    @IntDef({Weekend.SATURDAY_SUNDAY, Weekend.FRIDAY_SATURDAY,
            Weekend.THURSDAY_FRIDAY, Weekend.FRIDAY_ONLY,
            Weekend.SATURDAY_ONLY, Weekend.SUNDAY_ONLY})

    @Retention(RetentionPolicy.SOURCE)
    public @interface Weekend {
        int SATURDAY_SUNDAY = 0;
        int FRIDAY_SATURDAY = 1;
        int THURSDAY_FRIDAY = 2;
        int FRIDAY_ONLY = 3;
        int SATURDAY_ONLY = 4;
        int SUNDAY_ONLY = 5;
    }

    public static final int NEXT_WEEK_THRESHOLD = 4;

    public static final String DATE_FORMAT = "EEEE, d MMMM";
    public static final String DATE_FORMAT_WITH_YEAR = "EEEE, d MMMM yyyy";
    public static final String TIME_FORMAT_24HOUR = "H:mm";
    public static final String TIME_FORMAT_12HOUR_WITH_MINS = "h:mma";
    public static final String TIME_FORMAT_12HOUR_WITHOUT_MINS = "ha";

    public static final String MONTH_FORMAT_SHORT = "MMM";
    public static final String MONTH_FORMAT_LONG = "MMMM";

    public static final int TOD_MORNING = 1;
    public static final int TOD_AFTERNOON = 2;
    public static final int TOD_EVENING = 3;
    public static final int TOD_NIGHT = 4;


}
