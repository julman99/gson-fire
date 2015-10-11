package io.gsonfire;

import io.gsonfire.gson.DateUnixtimeSecondsTypeAdapter;
import io.gsonfire.gson.DateUnixtimeMillisTypeAdapter;
import io.gsonfire.gson.DateRFC3339TypeAdapter;
import io.gsonfire.gson.NullableTypeAdapter;
import com.google.gson.TypeAdapter;

import java.util.Date;
import java.util.TimeZone;

/**
 * @autor: julio
 */
public enum DateSerializationPolicy {

    /**
     * Serializes to/from unix timestamps in milliseconds and allows negative numbers
     */
    unixTimeMillis {
        @Override
        TypeAdapter<Date> createTypeAdapter(TimeZone serializeTimezone) {
            return new NullableTypeAdapter<Date>(
                new DateUnixtimeMillisTypeAdapter(true)
            );
        }
    },

    /**
     * Serializes to/from unix timestamps in seconds and allows negative numbers
     */
    unixTimeSeconds {
        @Override
        TypeAdapter<Date> createTypeAdapter(TimeZone serializeTimezone) {
            return new NullableTypeAdapter<Date>(
                new DateUnixtimeSecondsTypeAdapter(true)
            );
        }
    },

    /**
     * Serializes to/from unix timestamps in milliseconds and only allows positive numbers. If a negative unix timestamp is passed, it
     * will be serialized as null
     */
    unixTimePositiveMillis {
        @Override
        TypeAdapter<Date> createTypeAdapter(TimeZone serializeTimezone) {
            return new NullableTypeAdapter<Date>(
                new DateUnixtimeMillisTypeAdapter(false)
            );
        }
    },

    /**
     * Serializes to/from unix timestamps and only allows positive numbers. If a negative unix timestamp is passed, it
     * will be serialized as null
     */
    unixTimePositiveSeconds {
        @Override
        TypeAdapter<Date> createTypeAdapter(TimeZone serializeTimezone) {
            return new NullableTypeAdapter<Date>(
                new DateUnixtimeSecondsTypeAdapter(false)
            );
        }
    },

    /**
     * Serializes dates in RFC3339 including the date and time. For example: 1985-04-12T23:20:50.52Z
     */
    rfc3339 {
        @Override
        TypeAdapter<Date> createTypeAdapter(TimeZone serializeTimezone) {
            return new NullableTypeAdapter<Date>(
                new DateRFC3339TypeAdapter(serializeTimezone, true)
            );
        }
    },

    /**
     * Serializes dates in RFC3339 including only date date. For example: 1985-04-12
     */
    rfc3339Date {
        @Override
        TypeAdapter<Date> createTypeAdapter(TimeZone serializeTimezone) {
            return new NullableTypeAdapter<Date>(
                new DateRFC3339TypeAdapter(serializeTimezone, false)
            );
        }
    };

    abstract TypeAdapter<Date> createTypeAdapter(TimeZone serializeTimezone);
}
