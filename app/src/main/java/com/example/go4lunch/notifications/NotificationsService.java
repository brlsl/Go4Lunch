package com.example.go4lunch.notifications;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.Build;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.example.go4lunch.R;
import com.example.go4lunch.api.UserHelper;
import com.example.go4lunch.controllers.activities.MainActivity;
import com.example.go4lunch.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@SuppressLint("MissingFirebaseInstanceTokenRefresh")
public class NotificationsService extends FirebaseMessagingService {
    private static final int NOTIFICATION_ID  = 123456789 ;
    private static final String NOTIFICATION_TAG  = "GO4LUNCH";

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        // 1 - Get message sent by Firebase
        //String message = Objects.requireNonNull(remoteMessage.getNotification()).getBody();
        configureMessage();
    }

    private void sendVisualNotification(String messageBody) {
        // 1 - Create an Intent that will be shown when user will click on the Notification
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        // 2 - Create a Style for the Notification
        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        inboxStyle.setBigContentTitle("Go4Lunch: your lunch");
        inboxStyle.addLine(messageBody);

        // 3 - Create a Channel (Android 8)
        String channelId = getString(R.string.default_notification_channel_id);

        // 4 - Build a Notification object
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.ic_meal_24dp)
                        .setContentTitle(getString(R.string.app_name))
                        .setAutoCancel(true)
                        .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                        .setContentIntent(pendingIntent)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(messageBody));

        // 5 - Add the Notification to the Notification Manager and show it.
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // 6 - Support Version >= Android 8
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(channelId, messageBody, importance);
            assert notificationManager != null;
            notificationManager.createNotificationChannel(mChannel);
        }

        // 7 - Show notification
        assert notificationManager != null;
        notificationManager.notify(NOTIFICATION_TAG, NOTIFICATION_ID, notificationBuilder.build());
    }

    private void configureMessage(){
        UserHelper.getUser(FirebaseAuth.getInstance().getUid())
                .addOnSuccessListener(documentSnapshot ->{
                    User user = documentSnapshot.toObject(User.class);
                    if (user != null) {
                        String userRestaurantChoiceName = user.getRestaurantChoiceName();
                        String userRestaurantChoiceId = user.getRestaurantChoiceId();
                        String userID = user.getUid();

                        if (userRestaurantChoiceId == null || userRestaurantChoiceName == null){
                            sendVisualNotification(getString(R.string.you_have_not_choose_any_restaurant));
                        }
                        else{
                            getWorkmatesJoining(userRestaurantChoiceId, userRestaurantChoiceName, userID);
                        }
                    }
                });
    }

    private void getWorkmatesJoining(String restaurantChoiceId, String restaurantChoiceName, String userId){
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
                    if (workmatesRestaurantList.isEmpty())
                        sendVisualNotification(getString(R.string.you_eat_alone, restaurantChoiceName));
                    else {
                        String workmateSeparatedList = TextUtils.join(", ", workmatesRestaurantList);
                        sendVisualNotification(getString(R.string.you_eat_at_with, restaurantChoiceName,workmateSeparatedList));
                    }
                });

    }
}
