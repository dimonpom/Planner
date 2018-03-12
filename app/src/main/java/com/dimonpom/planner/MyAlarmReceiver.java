package com.dimonpom.planner;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;


public class MyAlarmReceiver extends BroadcastReceiver {


    public MyAlarmReceiver() {

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle extras = intent.getExtras();
        byte flag=0;
        String impInfo="ContentText not synchronized";
       // Toast.makeText(context, "-------Alarm Received-------", Toast.LENGTH_LONG).show();
        if (extras != null) {
            impInfo = (String) extras.get("NT_Title");
            flag = (byte) extras.get("NT_MusicFlag");
            Log.d("MyAlarmReceiver", "Extras given: "+impInfo);
        }
       // Log.d("MyAlarmReceiver", "-------Alarm Received-------");
        Intent i = new Intent(context, MyAlarmNotifier.class);
        i.putExtra("AlarmBody", impInfo);
        i.putExtra("AlarmIsMusic", flag);
        context.startService(i);

    }
}
