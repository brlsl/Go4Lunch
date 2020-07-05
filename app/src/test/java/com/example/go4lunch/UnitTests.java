package com.example.go4lunch;

import com.example.go4lunch.models.apiGooglePlace.placeDetails.ResultDetails;
import com.example.go4lunch.utils.DateUtils;
import com.example.go4lunch.utils.SortUtils;

import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class UnitTests {
    private List<ResultDetails> mResultDetailsList = Arrays.asList(
            new ResultDetails("C", 2.0),
            new ResultDetails("A", 4.0),
            new ResultDetails("B", 3.0)
    );

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

    }

    @Test
    public void closingSoon_isCorrect() throws ParseException {
        String closingHour = "1:30 pm";
        String doNotPrint = "12:44 pm";
        String doNotPrint2 = "01:31 pm";
        String printMin = "12:45 pm";
        String printMax = "01:30 pm";

        assertFalse(DateUtils.isClosingSoon(doNotPrint, closingHour));
        assertFalse(DateUtils.isClosingSoon(doNotPrint2, closingHour));
        assertTrue(DateUtils.isClosingSoon(printMin,closingHour));
        assertTrue(DateUtils.isClosingSoon(printMax,closingHour));
    }

    @Test
    public void sortByName_isCorrect(){
        assertEquals(mResultDetailsList.get(0).getName(),"C");
        assertEquals(mResultDetailsList.get(1).getName(),"A");
        assertEquals(mResultDetailsList.get(2).getName(),"B");
        SortUtils.sortRestaurantByNameAZ(mResultDetailsList);
        assertEquals(mResultDetailsList.get(0).getName(),"A");
        assertEquals(mResultDetailsList.get(1).getName(),"B");
        assertEquals(mResultDetailsList.get(2).getName(),"C");
    }

    @Test
    public void sortByRating_isCorrect(){
        assertEquals(mResultDetailsList.get(0).getRating(),2.0, 0);
        assertEquals(mResultDetailsList.get(1).getRating(),4.0, 0);
        assertEquals(mResultDetailsList.get(2).getRating(),3.0, 0);
        SortUtils.sortHighRatingFirst(mResultDetailsList);
        assertEquals(mResultDetailsList.get(0).getRating(),4.0, 0);
        assertEquals(mResultDetailsList.get(1).getRating(),3.0, 0);
        assertEquals(mResultDetailsList.get(2).getRating(),2.0, 0);
    }
}