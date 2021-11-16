package com.example.pushnotificationdemobau;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

public class MyForegroundService extends Service {
    private NotificationCompat.Builder notificationBuilder;

    @Override
    public void onCreate() {
        super.onCreate();

        createNotificationChannel();
        PendingIntent contentIntentClose = createCloseIntent();
        notificationBuilder = new NotificationCompat.Builder(this, "channelId")
                .setContentTitle("My Awesome notification")
                .setContentText("Don't forget to drink water")
                .setSmallIcon(R.drawable.ic_emoji)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .addAction(R.drawable.ic_emoji, "CANCEL", contentIntentClose);


    }

    @SuppressLint("UnspecifiedImmutableFlag")
    private PendingIntent createCloseIntent() {
        Intent intentClose = new Intent(this, MyForegroundService.class);
        intentClose.setAction("stop");

        return PendingIntent.getService(this, 0, intentClose, 0);
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "My channel";
            String description = "Description of my channel";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("channelId", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if ("start".equals(intent.getAction())) {
            startForeground(77, notificationBuilder.build());
        }
        else if ("stop".equals(intent.getAction())) {
            stopForeground(true);
        }
        return START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


}
