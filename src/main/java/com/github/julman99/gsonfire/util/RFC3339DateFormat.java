package com.github.julman99.gsonfire.util;

import java.text.*;
import java.util.Date;
import java.util.TimeZone;

/**
 * @autor: julio
 */
public class RFC3339DateFormat extends DateFormat {

    private final SimpleDateFormat rfc3339Parser = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
    private final SimpleDateFormat rfc3339Formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

    private final String MILLISECONDS_PATTERN_REPLACE = "\\.([0-9]+)";
    private final String MILLISECONDS_PATTERN_MATCH = ".*" + MILLISECONDS_PATTERN_REPLACE + ".*";

    public RFC3339DateFormat(TimeZone serializationTimezone) {
        this.rfc3339Formatter.setTimeZone(serializationTimezone);
    }

    public RFC3339DateFormat() {
        this(TimeZone.getTimeZone("UTC"));
    }

    private String generateTimezone(long time, TimeZone serializationTimezone){
        if(serializationTimezone.getOffset(time) == 0){
            return "Z";
        }

        int offset = (int) (serializationTimezone.getOffset(time) / 1000L);
        int hours = offset / 3600;
        int minutes = Math.abs((offset - hours * 3600) / 60);
        String sign = hours >= 0 ? "+" : "-";

        return sign + String.format("%02d", Math.abs(hours)) + ":" + String.format("%02d", minutes);
    }

    @Override
    public StringBuffer format(Date date, StringBuffer toAppendTo, FieldPosition fieldPosition) {

        StringBuffer formatted = new StringBuffer();

        formatted.append(rfc3339Formatter.format(date).toString());

        //Add milliseconds
        long time = date.getTime();
        if(time % 1000 != 0){
            String fraction = Long.toString((time % 1000L));
            formatted.append("." + fraction);
        }

        //Timezone
        String timezoneStr = generateTimezone(time, this.rfc3339Formatter.getTimeZone());
        formatted.append(timezoneStr);

        return formatted;
    }

    @Override
    public Date parse(String source, ParsePosition pos) {

        //Filter milliseconds
        long millis = 0;
        if(source.contains(".")){
            String millisStr = source.replaceAll(MILLISECONDS_PATTERN_MATCH, "$1");
            millis = Long.parseLong(millisStr);
            source = source.replaceAll(MILLISECONDS_PATTERN_REPLACE,"");
        }

        //Filter ending in Z
        if(source.endsWith("Z")){
            source = source.substring(0, source.length() -1) + "-0000";
        }

        //Filter colon in timezone
        if(source.charAt(source.length() - 3) == ':'){
            source = source.substring(0, source.length() - 3) + source.substring(source.length() - 2);
        }

        try {
            Date res = rfc3339Parser.parse(source);
            if(millis > 0){
                res = new Date(res.getTime() + millis);
            }
            pos.setIndex(source.length());
            return res;
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

}
