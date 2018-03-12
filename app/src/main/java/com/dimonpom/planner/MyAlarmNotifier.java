package com.dimonpom.planner;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

public class MyAlarmNotifier extends Service {

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String ContentText="Ничего тут и нет вовсе";
        byte SOUND_FLAG=0;
        Bundle extras = intent.getExtras();
        if (extras != null) {
            ContentText = (String) extras.get("AlarmBody");
            SOUND_FLAG = (byte) extras.get("AlarmIsMusic");
            Log.d("MyAlarmNotifier", "Extras given: "+ContentText);
        }

        int NOTIFY_ID = 1;
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder//.setContentIntent(contenetIntent)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Напоминание")
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setDefaults(Notification.DEFAULT_LIGHTS)
                .setContentText(ContentText);
        if (SOUND_FLAG==1)
        builder.setDefaults(Notification.DEFAULT_SOUND);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFY_ID, builder.build());
        Log.d("MyAlarmNotifier", "Notification shown");
        onDestroy();
        return START_STICKY;

    }

    @Override
    public void onDestroy() {
       // Toast.makeText(this, "Service Stoped", Toast.LENGTH_LONG).show();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
