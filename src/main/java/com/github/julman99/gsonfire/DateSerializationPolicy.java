package com.github.julman99.gsonfire;

import com.github.julman99.gsonfire.gson.DateIntegerTypeAdapter;
import com.github.julman99.gsonfire.gson.DateLongTypeAdapter;
import com.github.julman99.gsonfire.gson.DateRFC3339TypeAdapter;
import com.github.julman99.gsonfire.gson.NullableTypeAdapter;
import com.google.gson.TypeAdapter;

import java.util.Date;
import java.util.TimeZone;

/**
 * @autor: julio
 */
public enum DateSerializationPolicy {
    unixTimeMillis {
        @Override
        public TypeAdapter<Date> createTypeAdapter() {
            return new NullableTypeAdapter<Date>(
                new DateLongTypeAdapter()
            );
        }
    },

    unixTimeSeconds {
        @Override
        public TypeAdapter<Date> createTypeAdapter() {
            return new NullableTypeAdapter<Date>(
                new DateIntegerTypeAdapter()
            );
        }
    },

    rfc3339 {
        @Override
        public TypeAdapter<Date> createTypeAdapter() {
            return new NullableTypeAdapter<Date>(
                new DateRFC3339TypeAdapter(TimeZone.getDefault())
            );
        }
    };

    public abstract TypeAdapter<Date> createTypeAdapter();
}
