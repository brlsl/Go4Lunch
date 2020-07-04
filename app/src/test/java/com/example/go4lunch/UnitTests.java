package com.example.go4lunch;

import com.example.go4lunch.utils.DateUtils;

import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class UnitTests {

    @Test
    public void getCurrentDay_isCorrect(){
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE", Locale.ENGLISH);
        String today = sdf.format(cal.getTime()).toLowerCase();
        assertEquals(today, DateUtils.getTodayDateToStr());
    }

    @Test
    public void getCurrentTime_isCorrect() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm aa", Locale.ENGLISH);
        String now = sdf.format(cal.getTime());
        assertEquals(now, DateUtils.getCurrentHourToStr());
    }

    @Test
    public void getTodayToInteger_isCorrect() {
        String today = DateUtils.getTodayDateToStr();
        int dayInteger = 99;
        if (today.equals("monday"))
            dayInteger = 0;
        if(today.equals("tuesday"))
            dayInteger = 1;
        if(today.equals("wednesday"))
            dayInteger = 2;
        if(today.equals("thursday"))
            dayInteger = 3;
        if(today.equals("friday"))
            dayInteger = 4;
        if(today.equals("saturday"))
            dayInteger = 5;
        if(today.equals("sunday"))
            dayInteger = 6;
        assertEquals(dayInteger, (int) DateUtils.getTodayToInteger(today));

    }

    @Test
    public void currentHourCompareToRestaurantHours_isCorrect() throws ParseException {
        String currentHour = "12:00 am"; // midnight
        String restaurantHour = "12:00 pm"; // midday

        assertTrue(DateUtils.hour1CompareToHour2(currentHour, restaurantHour)<0);
        assertFalse(DateUtils.hour1CompareToHour2(currentHour, restaurantHour)>0);
        assertNotEquals(0, DateUtils.hour1CompareToHour2(currentHour, restaurantHour));
        //assertEquals(0, DateUtils.hour1CompareToHour2(currentHour, restaurantHour));

    }
    @Test
    public void compareOpeningHoursToClosingHours_isCorrect() throws ParseException {
        String openingHour = "11:30 am";
        String closingHour = "12:00 pm";

        assertTrue(DateUtils.openingHoursCompareToCloseHours(openingHour,closingHour)< 0);

    }

    @Test
    public void closingSoon_isCorrect(){

    }

}