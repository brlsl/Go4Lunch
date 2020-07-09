package com.example.go4lunch.notifications;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Build;
import android.text.TextUtils;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.go4lunch.R;
import com.example.go4lunch.api.UserHelper;
import com.example.go4lunch.controllers.activities.MainActivity;
import com.example.go4lunch.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class NotificationReceiver extends BroadcastReceiver {
    private String mNotificationText;
    private static final String NOTIFICATION_CHANNEL_ID = "NOTIFICATION_CHANNEL_ID";

    @Override
    public void onReceive(Context context, Intent intent) {
        configureNotificationText(context);
    }

    private void sendVisualNotification(Context context, String message) {
        // create intent that will be shown when user clicks on notification
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        // construct detail of our notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_meal_24dp)
                .setContentTitle(context.getString(R.string.notification_title))
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message));

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        notificationManagerCompat.notify(987, builder.build());

        // if API > 26 (android 8)
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "lunch notification";
            String description = "notification received at lunch time";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            assert notificationManager != null;
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void configureNotificationText(Context context){
        UserHelper.getUser(FirebaseAuth.getInstance().getUid())
                .addOnSuccessListener(documentSnapshot ->{
                    User user = documentSnapshot.toObject(User.class);
                    if (user != null) {
                        String userRestaurantChoiceName = user.getRestaurantChoiceName();
                        String userRestaurantChoiceId = user.getRestaurantChoiceId();
                        String userID = user.getUid();

                        if (userRestaurantChoiceId == null || userRestaurantChoiceName == null){
                            mNotificationText = context.getString(R.string.you_have_not_choose_any_restaurant);
                            sendVisualNotification(context, mNotificationText);
                        }
                        else{
                            getWorkmatesJoining(userRestaurantChoiceId, userRestaurantChoiceName, userID, context);
                        }
                    }
                });
    }

    private void getWorkmatesJoining(String restaurantChoiceId, String restaurantChoiceName, String userId, Context context){
        UserHelper.getUsersCollection()
                .whereEqualTo("restaurantChoiceId", restaurantChoiceId)
                .whereEqualTo("restaurantChoiceName", restaurantChoiceName)
                .get()
                .addOnCompleteListener(task ->{
                    List<String> workmatesRestaurantList = new ArrayList<>();
                    for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())){
                        if (!Objects.equals(document.getString("uid"), userId)){
                            workmatesRestaurantList.add(document.getString("username"));
                        }
                    }
                    if (workmatesRestaurantList.isEmpty()){
                       mNotificationText = context.getString(R.string.you_eat_alone, restaurantChoiceName);
                    }
                    else {
                        String workmateSeparatedList = TextUtils.join(", ", workmatesRestaurantList);
                        mNotificationText = context.getString(R.string.you_eat_at_with, restaurantChoiceName,workmateSeparatedList);
                    }
                    sendVisualNotification(context, mNotificationText);
                });
    }
}