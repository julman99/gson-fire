package io.gsonfire.gson;

import io.gsonfire.util.RFC3339DateFormat;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.TimeZone;

/**
 * @autor: julio
 */
public final class DateRFC3339TypeAdapter extends TypeAdapter<Date> {

    private final boolean serializeTime;
    private final TimeZone serializationTimezone;
    private final ThreadLocal<DateFormat> dateFormatThreadLocal = new ThreadLocal<DateFormat>();

    /**
     * Creates a RFC3339 type adapter that will serialize dates in UTC
     */
    public DateRFC3339TypeAdapter(boolean serializeTime) {
        this(TimeZone.getDefault(), serializeTime);
    }

    /**
     * Creates a RFC3339 type adapter that will serialize dates using the provided timezone
     */
    public DateRFC3339TypeAdapter(TimeZone serializationTimezone, boolean serializeTime) {
        this.serializationTimezone = serializationTimezone;
        this.serializeTime = serializeTime;
    }

    private DateFormat getDateFormat() {
        final DateFormat existingDateFormat = dateFormatThreadLocal.get();
        if(existingDateFormat == null) {
            final DateFormat newDateFormat = new RFC3339DateFormat(serializationTimezone, serializeTime);
            this.dateFormatThreadLocal.set(newDateFormat);
            return newDateFormat;
        } else {
            return existingDateFormat;
        }
    }

    @Override
    public void write(JsonWriter out, Date value) throws IOException {
        out.value(getDateFormat().format(value));
    }

    @Override
    public Date read(JsonReader in) throws IOException {
        final String dateStr = in.nextString();
        try {
            return getDateFormat().parse(dateStr);
        } catch (ParseException e) {
            throw new IOException("Could not parse date " + dateStr, e);
        }
    }
}
