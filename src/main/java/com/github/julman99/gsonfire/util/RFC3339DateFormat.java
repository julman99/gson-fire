package com.github.julman99.gsonfire.util;

import java.text.*;
import java.util.Date;

/**
 * @autor: julio
 */
public class RFC3339DateFormat extends DateFormat {

    private final SimpleDateFormat rfc3339 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
    private final String MILLISECONDS_PATTERN_REPLACE = "\\.([0-9]+)";
    private final String MILLISECONDS_PATTERN_MATCH = ".*" + MILLISECONDS_PATTERN_REPLACE + ".*";


    @Override
    public StringBuffer format(Date date, StringBuffer toAppendTo, FieldPosition fieldPosition) {
        String formatted = rfc3339.format(date).toString();
        String formattedWithColon = formatted.substring(0, formatted.length() -2) + ":" + formatted.substring(formatted.length() -2 );
        toAppendTo.append(formattedWithColon);
        return toAppendTo;
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
            Date res = rfc3339.parse(source);
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
