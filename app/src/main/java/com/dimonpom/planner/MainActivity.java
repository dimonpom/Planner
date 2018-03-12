package com.dimonpom.planner;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import com.dimonpom.planner.data.PlanContract;
import com.dimonpom.planner.data.PlanDBHelper;
import java.util.ArrayList;
import java.util.Arrays;



public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";


    private PlanDBHelper mDbHelper;
    private ListView PlanList;
    private boolean alreadyPressed=false;
    private int RequestCode;

    @Override
    public void onBackPressed() {
        android.app.AlertDialog.Builder quitDialog = new android.app.AlertDialog.Builder(MainActivity.this);
        quitDialog.setTitle("Завершить работу приложения?");
        quitDialog.setPositiveButton("Да", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            finish();

            }
        });
        quitDialog.setNegativeButton("Нет", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        quitDialog.show();
    }

    AlertDialog.Builder ad;
    Context context;
    String currentTitle="", currentBody="", currentDate;
    byte[] currentImage;

    public void DeleteAlarm(int RqC){
        AlarmManager alarmM = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent Notifyintent = new Intent(this, MyAlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                RqC,
                Notifyintent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );
        if (alarmM != null) {
            alarmM.cancel(pendingIntent);
        }
    }

    private void DeletionAsk(final TextView itemSelected){
        ad = new AlertDialog.Builder(context);
        ad.setTitle("Удалить элемент?");
        ad.setPositiveButton("Да", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Log.d(TAG,"Выбран ответ: Да");
                String IS = String.valueOf(itemSelected.getText());
                Log.d(TAG, IS);
                SQLiteDatabase db =mDbHelper.getWritableDatabase();
                boolean found=false;
                String[] projection ={
                        PlanContract.PlanEntry._ID,
                        PlanContract.PlanEntry.COLUMN_TITLE,
                        PlanContract.PlanEntry.COLUMN_BODY,
                        PlanContract.PlanEntry.COLUMN_DATE,
                        PlanContract.PlanEntry.COLUMN_IMG
                };
                Cursor cursor = db.query(
                        PlanContract.PlanEntry.TABLE_NAME,
                        projection,
                        null,
                        null,
                        null,
                        null,
                        PlanContract.PlanEntry.COLUMN_DATE + " DESC"
                );

                while(cursor.moveToNext() && !found){
                    int titleColumnIndex = cursor.getColumnIndex(PlanContract.PlanEntry.COLUMN_TITLE);
                    currentTitle = cursor.getString(titleColumnIndex);
                    if(currentTitle.equals(IS)){
                        found= true;
                        int dateColumnIndex = cursor.getColumnIndex(PlanContract.PlanEntry.COLUMN_DATE);
                        currentDate = cursor.getString(dateColumnIndex);
                        String[] splitDate = currentDate.split("\\s+");
                        String[] splitDate1 = splitDate[0].split("/");
                        String[] splitDate2 = splitDate[1].split(":");
                        String StringRequestCode = splitDate1[1]+splitDate1[2]+splitDate2[0]+splitDate2[1];
                        RequestCode = Integer.parseInt(StringRequestCode);
                        DeleteAlarm(RequestCode);
                    }
                }
                cursor.close();
                db.delete(PlanContract.PlanEntry.TABLE_NAME, PlanContract.PlanEntry.COLUMN_TITLE + " = ?",
                        new String[]{IS});
                db.close();
               // DeleteAlarm();
                updateUI();
                alreadyPressed=false;
            }
        });
        ad.setNegativeButton("Нет", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Log.d(TAG,"Выбран ответ: Нет");
                alreadyPressed=false;
            }
        });
        ad.show();

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = MainActivity.this;
       // String ad_title = ""

        PlanList = findViewById(R.id.lv_plans);
        mDbHelper = new PlanDBHelper(this);

        updateUI();

        PlanList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View itemClicked, int i, long l) {
                if (alreadyPressed){
                    Log.d(TAG, "AlreadyPressed");
                }else{


                boolean found=false;
                TextView taskTextView = itemClicked.findViewById(R.id.task_title);
                String task = String.valueOf(taskTextView.getText());
                Log.d(TAG, task);
                SQLiteDatabase db = mDbHelper.getReadableDatabase();
                String[] projection ={
                        PlanContract.PlanEntry._ID,
                        PlanContract.PlanEntry.COLUMN_TITLE,
                        PlanContract.PlanEntry.COLUMN_BODY,
                        PlanContract.PlanEntry.COLUMN_DATE,
                        PlanContract.PlanEntry.COLUMN_IMG
                };
                Cursor cursor = db.query(
                        PlanContract.PlanEntry.TABLE_NAME,
                        projection,
                        null,
                        null,
                        null,
                        null,
                        PlanContract.PlanEntry.COLUMN_DATE + " DESC"
                );

                while(cursor.moveToNext() && !found){

                    int titleColumnIndex = cursor.getColumnIndex(PlanContract.PlanEntry.COLUMN_TITLE);
                    currentTitle = cursor.getString(titleColumnIndex);

                    if(currentTitle.equals(task)){
                        found= true;
                        int bodyColumnIndex = cursor.getColumnIndex(PlanContract.PlanEntry.COLUMN_BODY);
                        int dateColumnIndex = cursor.getColumnIndex(PlanContract.PlanEntry.COLUMN_DATE);
                        int imageColumnInsex = cursor.getColumnIndex(PlanContract.PlanEntry.COLUMN_IMG);

                        currentBody = cursor.getString(bodyColumnIndex);
                        currentDate = cursor.getString(dateColumnIndex);
                        currentImage = cursor.getBlob(imageColumnInsex);

                        Intent intent = new Intent(MainActivity.this, PlaningActivity.class);
                        intent.putExtra("title", currentTitle);
                        intent.putExtra("body", currentBody);
                        intent.putExtra("date",currentDate);
                        intent.putExtra("image", currentImage);
                        startActivity(intent);

                    }
                }
                cursor.close();
                db.close();
                finish();
                }
            }
        });
        PlanList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                alreadyPressed=true;
                Log.d(TAG, "ДООООлгое зажатииие");
                TextView itemSelected = view.findViewById(R.id.task_title);
                DeletionAsk(itemSelected);
                return false;
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;

    }

    public void ActivateCalendar(View view) {
        Intent intent = new Intent(MainActivity.this, PlaningActivity.class);
        startActivity(intent);
        finish();
    }

    private Bitmap convertToBitmap(byte[] b){
        return BitmapFactory.decodeByteArray(b,0,b.length);
    }


    private void updateUI(){
        ArrayList<String> taskList = new ArrayList<>();
        ArrayList<String> dateList = new ArrayList<>();
        ArrayList<Bitmap> imgList = new ArrayList<>();
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor cursor = db.query(
                PlanContract.PlanEntry.TABLE_NAME,
                new String[]{
                        PlanContract.PlanEntry._ID, PlanContract.PlanEntry.COLUMN_TITLE, PlanContract.PlanEntry.COLUMN_IMG, PlanContract.PlanEntry.COLUMN_DATE
                },
                null, null, null, null,
                PlanContract.PlanEntry.COLUMN_DATE + " ASC");
        while (cursor.moveToNext()){
            int idImage = cursor.getColumnIndex(PlanContract.PlanEntry.COLUMN_IMG);
            int idTitle = cursor.getColumnIndex(PlanContract.PlanEntry.COLUMN_TITLE);
            int idDate = cursor.getColumnIndex(PlanContract.PlanEntry.COLUMN_DATE);
            dateList.add(cursor.getString(idDate));
            taskList.add(cursor.getString(idTitle));
            if (cursor.getBlob(idImage)!=null)
                imgList.add(convertToBitmap(cursor.getBlob(idImage)));
            else
                imgList.add(null);
        }
        CustomList adapter = new CustomList(MainActivity.this, taskList, imgList, dateList);
        PlanList.setAdapter(adapter);
        cursor.close();
        db.close();
    }

    public void RefreshPage(MenuItem item) {
        updateUI();
    }
}
