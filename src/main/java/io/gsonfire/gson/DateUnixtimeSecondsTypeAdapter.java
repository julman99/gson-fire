package io.gsonfire.gson;

import java.util.Date;

/**
 * @autor: julio
 */
public final class DateUnixtimeSecondsTypeAdapter extends DateUnixtimeTypeAdapter {

    public DateUnixtimeSecondsTypeAdapter(boolean allowNegativeTimestamp) {
        super(allowNegativeTimestamp);
    }

    @Override
    protected long toTimestamp(Date date) {
        return date.getTime() / 1000L;
    }

    @Override
    protected Date fromTimestamp(long timestamp) {
        return new Date(timestamp * 1000L);
    }

}
