package com.pv.datetimeseer;

import android.support.annotation.IntDef;

import com.pv.datetimeseer.parser.helper.Constants;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author p-v
 */

public class Config {

    @IntDef({Language.ENGLISH})

    @Retention(RetentionPolicy.SOURCE)
    public @interface Language {
        int ENGLISH = 0;
    }

    private final String dateFormatWithYear;
    private final String dateFormatWithoutYear;
    private final String timeFormat24Hours;
    private final String timeFormat12HoursWithMins;
    private final String timeFormat12HoursWithoutMins;
    private final @Language int language;

    private Config(ConfigBuilder builder) {
        this.dateFormatWithYear = builder.dateFormatWithYear;
        this.dateFormatWithoutYear = builder.dateFormatWithoutYear;
        this.timeFormat24Hours = builder.timeFormat24Hours;
        this.timeFormat12HoursWithMins = builder.timeFormat12HoursWithMins;
        this.timeFormat12HoursWithoutMins = builder.timeFormat12HoursWithoutMins;
        this.language = builder.language;
    }

    public String getDateFormatWithYear() {
        return dateFormatWithYear == null ? Constants.DATE_FORMAT_WITH_YEAR : dateFormatWithYear;
    }

    public String getDateFormatWithoutYear() {
        return dateFormatWithoutYear == null ? Constants.DATE_FORMAT : dateFormatWithoutYear;
    }

    public String getTimeFormat24Hours() {
        return timeFormat24Hours == null ? Constants.TIME_FORMAT_24HOUR : timeFormat24Hours;
    }

    public String getTimeFormat12HoursWithMins() {
        return timeFormat12HoursWithMins == null ?
                Constants.TIME_FORMAT_12HOUR_WITH_MINS : timeFormat12HoursWithMins;
    }

    public String getTimeFormat12HoursWithoutMins() {
        return timeFormat12HoursWithoutMins == null ?
                Constants.TIME_FORMAT_12HOUR_WITHOUT_MINS : timeFormat12HoursWithoutMins;
    }

    public @Language int getLanguage() {
        return language;
    }


    public static class ConfigBuilder {

        private String dateFormatWithYear;
        private String dateFormatWithoutYear;
        private String timeFormat24Hours;
        private String timeFormat12HoursWithMins;
        private String timeFormat12HoursWithoutMins;
        private @Language int language;

        public ConfigBuilder setDateFormatWithYear(String dateFormatWithYear) {
            this.dateFormatWithYear = dateFormatWithYear;
            return this;
        }

        public ConfigBuilder setDateFormatWithoutYear(String dateFormatWithoutYear) {
            this.dateFormatWithoutYear = dateFormatWithoutYear;
            return this;
        }

        public ConfigBuilder setTimeFormat24Hours(String timeFormat24Hours) {
            this.timeFormat24Hours = timeFormat24Hours;
            return this;
        }

        public ConfigBuilder setTimeFormat12HoursWithMins(String timeFormat12HoursWithMins) {
            this.timeFormat12HoursWithMins = timeFormat12HoursWithMins;
            return this;
        }

        public ConfigBuilder setTimeFormat12HoursWithoutMins(String timeFormat12HoursWithoutMins) {
            this.timeFormat12HoursWithoutMins = timeFormat12HoursWithoutMins;
            return this;
        }

        public ConfigBuilder setLanguage(@Language int language) {
            this.language = language;
            return this;
        }

        public Config build() {
            return new Config(this);
        }

    }

}
