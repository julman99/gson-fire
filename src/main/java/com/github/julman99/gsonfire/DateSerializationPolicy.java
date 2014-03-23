package com.github.julman99.gsonfire;

import com.github.julman99.gsonfire.gson.DateIntegerTypeAdapter;
import com.github.julman99.gsonfire.gson.DateLongTypeAdapter;
import com.github.julman99.gsonfire.gson.DateRFC3339TypeAdapter;
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
            return new DateLongTypeAdapter();
        }
    },

    unixTimeSeconds {
        @Override
        public TypeAdapter<Date> createTypeAdapter() {
            return new DateIntegerTypeAdapter();
        }
    },

    rfc3999 {
        @Override
        public TypeAdapter<Date> createTypeAdapter() {
            return new DateRFC3339TypeAdapter(TimeZone.getDefault());
        }
    };

    public abstract TypeAdapter<Date> createTypeAdapter();
}
