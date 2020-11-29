package com.example.go4lunch.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import com.example.go4lunch.models.apiGooglePlace.placeDetails.ResultDetails;
import com.example.go4lunch.notifications.NotificationReceiver;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class Utils {
    private static final String PREFERENCES_NOTIFICATION_KEY ="notification_preferences_key";

    public static String getTodayDateToStr(){
        Date today = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE",Locale.ENGLISH);
        return sdf.format(today).toLowerCase();
    }

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

    public static String hoursWithoutDay(String day){
        String todayString = Utils.getTodayDateToStr();
        String[] hours = day.toLowerCase().split(todayString+": ");
        return hours[1].trim();
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

    public static void sortRestaurantByNameAZ(List<ResultDetails> resultDetailsList){
        Collections.sort(resultDetailsList, (o1, o2) ->
                o1.getName().toLowerCase().compareTo(o2.getName().toLowerCase()));
    }

    public static void sortHighRatingFirst(List<ResultDetails> resultDetailsList){
        Collections.sort(resultDetailsList,
                (o1, o2) -> Double.compare(o2.getRating(), o1.getRating()));
    }

    public static void scheduleNotification(Context context){
        SharedPreferences preferences =  PreferenceManager.getDefaultSharedPreferences(context);
        boolean isNotificationEnabled = preferences.getBoolean(PREFERENCES_NOTIFICATION_KEY,true); // notification settings

        Intent intent = new Intent(context, NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Calendar notificationTime = Calendar.getInstance();
        notificationTime.set(Calendar.HOUR_OF_DAY, 12);
        notificationTime.set(Calendar.MINUTE, 0);
        notificationTime.set(Calendar.SECOND, 0);
        notificationTime.set(Calendar.MILLISECOND, 0);

        if (alarmManager !=null){
            if (isNotificationEnabled){ // notifications are enabled in settings
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, notificationTime.getTimeInMillis(),1000*60*60*24, pendingIntent);
            } else { // notifications are disabled in settings
                alarmManager.cancel(pendingIntent); // cancel next scheduled notification
            }
        }
    }
}
