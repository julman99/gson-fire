package io.gsonfire.gson;

import java.util.Date;

/**
 * @autor: julio
 */
public class DateUnixtimeMillisTypeAdapter extends DateUnixtimeTypeAdapter {

    public DateUnixtimeMillisTypeAdapter(boolean allowNegativeTimestamp) {
        super(allowNegativeTimestamp);
    }

    @Override
    protected long toTimestamp(Date date) {
        return date.getTime();
    }

    @Override
    protected Date fromTimestamp(long timestamp) {
        return new Date(timestamp);
    }

}
