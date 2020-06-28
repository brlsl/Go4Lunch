package com.example.go4lunch.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class DateUtils {
    public static String getTodayDateToStr(){
        Date today = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE",Locale.ENGLISH);
        return sdf.format(today).toLowerCase();
    }

    public static Integer getTodayDateToInteger(String day){
        HashMap<String, Integer> map = new HashMap<>();
        map.put("monday",0);
        map.put("tuesday",1);
        map.put("wednesday",2);
        map.put("thursday",3);
        map.put("friday",4);
        map.put("saturday",5);
        map.put("sunday",6);
        return map.get(day);
    }

    // ok
    public static String getCurrentHourToStr(){
        Date today = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm aa",Locale.ENGLISH);
        return sdf.format(today);
    }


    public static int currentHourCompareToRestaurantHour(String inputHour) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm aa",Locale.ENGLISH);
        Date currentHour = sdf.parse(getCurrentHourToStr());

        Date restaurantHour = sdf.parse(inputHour);

        return currentHour.compareTo(restaurantHour);
    }

    public static int compareOpeningHoursToCloseHours(String openHour, String closeHour) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm aa",Locale.ENGLISH);

        Date open = sdf.parse(openHour);
        Date close = sdf.parse(closeHour);

        return openHour.compareTo(closeHour);
    }

    public static boolean isClosingSoon(String closeHour) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm aa", Locale.ENGLISH);
        Date restaurantClosingHour = sdf.parse(closeHour);
        Date currentHour = sdf.parse(getCurrentHourToStr());

        assert restaurantClosingHour != null;
        assert currentHour != null;
        long duration = restaurantClosingHour.getTime() - currentHour.getTime();
        long diffInMinutes = TimeUnit.MILLISECONDS.toMinutes(duration);

        if (diffInMinutes >= 0 && diffInMinutes <= 45) {
            System.out.println("Closing soon");
            return true;
        }
        return false;
    }

}
