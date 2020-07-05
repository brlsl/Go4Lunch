package com.example.go4lunch.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
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

    // ok
    public static String getCurrentHourToStr(){
        Date now = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm aa",Locale.ENGLISH);
        return sdf.format(now);
    }

    public static Integer getTodayToInteger(String day){
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

    public static long hour1CompareToHour2(String inputHour1, String inputHour2) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm aa",Locale.ENGLISH);
        Date hour1 = sdf.parse(inputHour1);
        Date hour2 = sdf.parse(inputHour2);

        assert hour1 != null;
        assert hour2 != null;
        return hour1.getTime() - hour2.getTime();
    }

    public static boolean isClosingSoon(String inputHour, String closeHour) throws ParseException {
        long duration = hour1CompareToHour2(inputHour,closeHour);
        long diffInMinutes = TimeUnit.MILLISECONDS.toMinutes(duration);

        return diffInMinutes >= -45 && diffInMinutes <= 0;
    }

}
