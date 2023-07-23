package com.mta.greenguardianapplication;

import android.app.Notification;
import android.content.pm.PackageManager;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.util.Log;


public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @RequiresApi(Build.VERSION_CODES.O)
    @Override
    public void onCreate() {
        super.onCreate();

        createNotificationChannel();
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private void createNotificationChannel() {
        String channelId = "myFirebaseChannel";
        String channelName = "My Firebase Channel";
        int importance = NotificationManager.IMPORTANCE_HIGH;

        NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
        channel.setDescription("This is the notification channel for My Firebase Messaging Service.");

        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.d("Notification!!", remoteMessage.getMessageId());
        String title = remoteMessage.getNotification().getTitle();
        String body = remoteMessage.getNotification().getBody();
        final String CHANNEL_ID = "HEADS_UP_NOTIFICATION";
        NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID, "myNotification", NotificationManager.IMPORTANCE_HIGH);
        getSystemService(NotificationManager.class).createNotificationChannel(channel);
        Notification.Builder notification = new Notification.Builder(this, CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(R.drawable.baseline_notifications_active_24)
                .setAutoCancel(true);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        NotificationManagerCompat.from(this).notify(1, notification.build());
        //getFirebaseMessage(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody());
    }

    public void getFirebaseMessage(String title, String msg) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "myFirebaseChannel")
                .setSmallIcon(R.drawable.baseline_notifications_active_24)
                .setContentTitle(title)
                .setContentText(msg)
                .setAutoCancel(true);

        NotificationManagerCompat manager = NotificationManagerCompat.from(getApplicationContext());
        //manager.notify(101, builder.build());
    }


    public MyFirebaseMessagingService() {
        super();
    }

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        Log.d("FCM Token", token);
    }


}
