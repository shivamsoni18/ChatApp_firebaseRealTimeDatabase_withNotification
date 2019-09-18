package com.example.firebasechat.Utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.provider.Settings;
import androidx.core.app.NotificationCompat;

import com.example.firebasechat.Activities.ChatActivity;
import com.example.firebasechat.R;

public class MyNotificationManager {

    public static final int ID_BIG_NOTIFICATION = 234;
    public static final int ID_SMALL_NOTIFICATION = 235;

    public static final String NOTIFICATION_CHANNEL_ID = "10001";
    private Context mCtx;
    public MyNotificationManager(Context mCtx) {
        this.mCtx = mCtx;
    }


    public void createNotification(String title, String message) {

        NotificationManager mNotificationManager;
        NotificationCompat.Builder mBuilder;
        /**Creates an explicit intent for an Activity in your app**/
        Intent resultIntent = new Intent(mCtx, ChatActivity.class);
        resultIntent.putExtra("notiFrom", title);
        resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);


        PendingIntent resultPendingIntent = PendingIntent.getActivity(mCtx,
                0 /* Request code */, resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        mBuilder = new NotificationCompat.Builder(mCtx);
        mBuilder.setSmallIcon(R.drawable.firechat2);
        mBuilder.setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                .setContentIntent(resultPendingIntent);
        mNotificationManager = (NotificationManager) mCtx.getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "NOTIFICATION_CHANNEL_NAME", importance);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            assert mNotificationManager != null;
            mBuilder.setChannelId(NOTIFICATION_CHANNEL_ID);
            mNotificationManager.createNotificationChannel(notificationChannel);
        }

        assert mNotificationManager != null;
        mNotificationManager.notify(0 /* Request Code */, mBuilder.build());
    }

}