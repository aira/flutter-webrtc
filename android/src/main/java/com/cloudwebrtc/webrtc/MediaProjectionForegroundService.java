package com.cloudwebrtc.webrtc;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ServiceInfo;
import android.os.Build;
import android.os.IBinder;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

public class MediaProjectionForegroundService extends Service {

    // Notification constants
    private static final int FOREGROUND_NOTIFICATION_ID = 1;
    private static final String NOTIFICATION_CHANNEL_ID = "media_projection_channel";
    private static final String NOTIFICATION_CHANNEL_NAME = "Media Projection Service";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        System.out.println(">>>>> MPFS onStartCommand() called.");

        // Extract notification message from intent
        String notificationTitle = intent.getStringExtra("notificationTitle");
        String notificationText = intent.getStringExtra("notificationText");

        // Create notification channel (Android Oreo and above)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel();
        }

        // Build notification content
        Intent launchIntent = getPackageManager().getLaunchIntentForPackage(getPackageName());
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                0,
                launchIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
//                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent);

        if (notificationTitle != null) {
            notificationBuilder.setContentTitle(notificationTitle);
        }

        if (notificationText != null) {
            notificationBuilder.setContentText(notificationText);
        }

        System.out.println(">>>>> MPFS onStartCommand() starting foreground service.");
        // Start service in foreground with notification
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(FOREGROUND_NOTIFICATION_ID, notificationBuilder.build(), ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PROJECTION);
        } else {
            startForeground(FOREGROUND_NOTIFICATION_ID, notificationBuilder.build());
        }
        System.out.println(">>>>> MPFS onStartCommand() started foreground service.");

        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private void createNotificationChannel() {
        NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.createNotificationChannel(channel);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // Stop foreground service and remove notification
        stopForeground(true);
        stopSelf();
    }
}