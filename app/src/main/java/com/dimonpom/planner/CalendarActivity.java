package com.dimonpom.planner;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.CalendarView;

import java.util.Calendar;


public class CalendarActivity extends AppCompatActivity {

    private static final String TAG = "CalendarActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calendar_layout);
        CalendarView mCalendarView = findViewById(R.id.calendarView);

        mCalendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int year, int months, int day) {
                String date = day + "/" + (months+1) + "/" + year+" "+ Calendar.HOUR_OF_DAY+":"+Calendar.MINUTE;
                Log.d(TAG, "onSelectedDayChange: date: "+date);

                Intent intent = new Intent(CalendarActivity.this, PlaningActivity.class);
                intent.putExtra("date",date);
                startActivity(intent);
            }
        });

    }

}
