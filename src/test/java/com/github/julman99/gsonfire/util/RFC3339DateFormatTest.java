package com.github.julman99.gsonfire.util;

import org.junit.Test;

import java.text.ParseException;
import java.util.Date;

import static org.junit.Assert.assertEquals;

/**
 * @autor: julio
 */
public class RFC3339DateFormatTest {

    @Test
    public void testParseSimple() throws Exception {
        RFC3339DateFormat format = new RFC3339DateFormat();
        Date date = format.parse("2014-01-06T12:45:01-05:00");
        assertEquals(1389030301000L, date.getTime()); //Unix timestamp created with http://www.unixtimestamp.com/index.php
    }

    @Test
    public void testParseTimezoneShiftDays() throws Exception {
        RFC3339DateFormat format = new RFC3339DateFormat();
        Date date = format.parse("2013-02-06T21:29:08-05:00");
        assertEquals(1360204148000L, date.getTime()); //Unix timestamp created with http://www.unixtimestamp.com/index.php
    }

    @Test
    public void testParseWithMillis() throws ParseException {
        RFC3339DateFormat format = new RFC3339DateFormat();
        Date date = format.parse("2013-02-07T02:29:08.123Z");
        assertEquals(1360204148123L, date.getTime()); //Unix timestamp created with http://www.unixtimestamp.com/index.php
    }

}
