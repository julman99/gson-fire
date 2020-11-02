package io.gsonfire.util;

import org.junit.Test;

import java.text.ParseException;
import java.util.Date;
import java.util.TimeZone;

import static org.junit.Assert.assertEquals;

/**
 * @author julio
 */
public class RFC3339DateFormatTest {

    @Test
    public void testParseSimple() throws Exception {
        RFC3339DateFormat format = new RFC3339DateFormat();
        Date date = format.parse("2014-01-06T12:45:01-05:00");
        assertEquals(1389030301000L, date.getTime()); //Unix timestamp created with http://www.unixtimestamp.com/index.php
    }

    @Test
    public void testParseNoTZ() throws Exception {
        RFC3339DateFormat format = new RFC3339DateFormat();
        Date date = format.parse("2014-01-06T17:45:01");
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

    @Test
    public void testParseLowerCase() throws ParseException {
        RFC3339DateFormat format = new RFC3339DateFormat();
        Date date = format.parse("2013-02-07t02:29:08.123z");
        assertEquals(1360204148123L, date.getTime()); //Unix timestamp created with http://www.unixtimestamp.com/index.php
    }

    @Test
    public void testParseDate_NoTime() throws ParseException {
        RFC3339DateFormat format = new RFC3339DateFormat(false);
        Date date = format.parse("2013-02-07");
        assertEquals(1360195200000L, date.getTime()); //Unix timestamp created with http://www.unixtimestamp.com/index.php
    }

    @Test
    public void testParseDateTime_NoTime() throws ParseException {
        RFC3339DateFormat format = new RFC3339DateFormat(false);
        Date date = format.parse("2013-02-07T02:29:08.123Z");
        assertEquals(1360204148123L, date.getTime()); //Unix timestamp created with http://www.unixtimestamp.com/index.php
    }

    @Test
    public void testParseDate_Time() throws ParseException {
        RFC3339DateFormat format = new RFC3339DateFormat();
        Date date = format.parse("2013-02-07");
        assertEquals(1360195200000L, date.getTime()); //Unix timestamp created with http://www.unixtimestamp.com/index.php
    }

    @Test
    public void testFormatWithoutMillis() throws ParseException {
        RFC3339DateFormat format = new RFC3339DateFormat();
        String formatted = format.format(new Date(1360204148000L));
        assertEquals("2013-02-07T02:29:08Z", formatted);
    }

    @Test
    public void testFormatWithMillis() throws ParseException {
        RFC3339DateFormat format = new RFC3339DateFormat();
        String formatted = format.format(new Date(1360204148123L));
        assertEquals("2013-02-07T02:29:08.123Z", formatted);
    }

    @Test
    public void testFormatWithMillis_NY() throws ParseException {
        RFC3339DateFormat format = new RFC3339DateFormat(TimeZone.getTimeZone("America/New_York"));
        String formatted = format.format(new Date(1360204148123L));
        assertEquals("2013-02-06T21:29:08.123-05:00", formatted);
    }

    @Test
    public void testFormatWithMillis_CCS() throws ParseException {
        RFC3339DateFormat format = new RFC3339DateFormat(TimeZone.getTimeZone("America/Caracas"));
        String formatted = format.format(new Date(1360204148123L));
        assertEquals("2013-02-06T21:59:08.123-04:30", formatted);
    }

    @Test
    public void testFormatNoTime() throws ParseException {
        RFC3339DateFormat format = new RFC3339DateFormat(false);
        String formatted = format.format(new Date(1360204148123L));
        assertEquals("2013-02-07", formatted);
    }
    
    @Test
    public void testParseWithMillisAndLeadingZero() throws ParseException {
        RFC3339DateFormat format = new RFC3339DateFormat();
        Date date = format.parse("2020-10-02T09:25:29.073Z");
        assertEquals(1601630729073L, date.getTime());
    }
    
    @Test
    public void testFormatWithMillisWithLeadingZero(){
        RFC3339DateFormat format = new RFC3339DateFormat();
        String formatted = format.format(new Date(1601630729073L));
        assertEquals("2020-10-02T09:25:29.073Z", formatted);
    }
    
    @Test
    public void testParseWithMillisAndTailingZero() throws ParseException {
        RFC3339DateFormat format = new RFC3339DateFormat();
        Date date = format.parse("2020-10-02T09:25:29.730Z");
        assertEquals(1601630729730L, date.getTime());
    }
    
    @Test
    public void testParseWithMillisAndWithoutTailingZero() throws ParseException {
        RFC3339DateFormat format = new RFC3339DateFormat();
        Date date = format.parse("2020-10-02T09:25:29.73Z");
        assertEquals(1601630729730L, date.getTime());
    }
    
    @Test
    public void testFormatWithMillisWithInvisibleTailingZero(){
        RFC3339DateFormat format = new RFC3339DateFormat();
        String formatted = format.format(new Date(1601630729730L));
        assertEquals("2020-10-02T09:25:29.73Z", formatted);
    }
    
    @Test
    public void testFormatWithMillisWithVisibleTailingZero(){
        RFC3339DateFormat format = new RFC3339DateFormat(true, true);
        String formatted = format.format(new Date(1601630729730L));
        assertEquals("2020-10-02T09:25:29.730Z", formatted);
    }
    
    @Test
    public void testFormatWithZeroMillisVisible(){
        RFC3339DateFormat format = new RFC3339DateFormat(true, true);
        String formatted = format.format(new Date(1601630729000L));
        assertEquals("2020-10-02T09:25:29.000Z", formatted);
    }
    
    @Test
    public void testFormatWithZeroMillisNotVisible(){
        RFC3339DateFormat format = new RFC3339DateFormat(true, false);
        String formatted = format.format(new Date(1601630729000L));
        assertEquals("2020-10-02T09:25:29Z", formatted);
    }
}
