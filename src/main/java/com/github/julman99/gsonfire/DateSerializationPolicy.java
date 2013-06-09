package com.github.julman99.gsonfire;

import com.github.julman99.gsonfire.gson.DateIntegerTypeAdapter;
import com.github.julman99.gsonfire.gson.DateLongTypeAdapter;
import com.google.gson.TypeAdapter;

import java.util.Date;

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
    };

    public abstract TypeAdapter<Date> createTypeAdapter();
}
