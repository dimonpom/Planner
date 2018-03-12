package com.dimonpom.planner;


import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;
import com.dimonpom.planner.data.PlanContract;
import com.dimonpom.planner.data.PlanDBHelper;
import java.io.ByteArrayOutputStream;
import java.util.Calendar;

public class PlaningActivity extends AppCompatActivity {
    private static final String TAG = "PlaningActivity";
    private final int CAMERA_RESULT = 0;
    private byte FLAGfMUSIC =0;
    private int RequestCode;

    private PlanDBHelper mDbHelper;
    private EditText editTitle, editBody;
    private ImageView mImageView;
    private Button timeB, dateB;
    private Spinner spinner;
    private ImageButton ib1,ib2,ib3,ib4,erase_btn;
    private DrawingView mDrawingView;


    boolean isChanging=false;
    boolean isPhoto=true;
    public Calendar Timecal = Calendar.getInstance();
    String date = "", title, body;
    String New_title, New_body, New_date;
    byte[] Byteimage;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_planing, menu);
        MenuItem delBtn = menu.findItem(R.id.mi_delete);
        if(isChanging)
            delBtn.setVisible(true);

        return true;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.planing_layout);

        mDbHelper = new PlanDBHelper(this);
        editBody = findViewById(R.id.editText3);
        editTitle = findViewById(R.id.editText2);
        mImageView = findViewById(R.id.imageView);
        timeB = findViewById(R.id.timeButton);
        dateB = findViewById(R.id.dateButton);
        spinner = findViewById(R.id.spinner);
        ib1 = findViewById(R.id.iB_black);
        ib2 = findViewById(R.id.iB_red);
        ib3 = findViewById(R.id.iB_blue);
        ib4 = findViewById(R.id.iB_green);
        erase_btn = findViewById(R.id.iB_erase);
        mDrawingView = findViewById(R.id.drawingView2);



        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            title= (String) extras.get("title");
            body = (String) extras.get("body");
            date = (String) extras.get("date");
            Byteimage = (byte[]) extras.get("image");
        }
        if (title == null){
            Log.d(TAG, "Передача данных не осуществилась");
            timeB.setText(GetTimeToStr(Timecal));
            dateB.setText(GetDateToStr(Timecal));
            date = GetDateToStr(Timecal)+" "+GetTimeToStr(Timecal);
        }else {
            isChanging=true;
            editTitle.setText(title);
            editBody.setText(body);
            String[] splitDate = date.split("\\s+");
            dateB.setText(splitDate[0]);
            timeB.setText(splitDate[1]);

            String[] splitDate1 = splitDate[0].split("/");
            String[] splitDate2 = splitDate[1].split(":");
            String StringRequestCode = splitDate1[1]+splitDate1[2]+splitDate2[0]+splitDate2[1];
            RequestCode = Integer.parseInt(StringRequestCode);
            Log.d(TAG, String.valueOf(RequestCode));
            if (Byteimage!=null)
                mImageView.setImageBitmap(convertToBitmap(Byteimage));
        }


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.isChecked()) {
            item.setChecked(false);
            FLAGfMUSIC=0;
        }
        else {
            item.setChecked(true);
            FLAGfMUSIC=1;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder quitDialog = new AlertDialog.Builder(PlaningActivity.this);
        quitDialog.setTitle("Отменить все действия?");
        quitDialog.setPositiveButton("Да!", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(PlaningActivity.this, MainActivity.class);
                startActivity(intent);
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

    private Bitmap convertToBitmap(byte[] b){
        return BitmapFactory.decodeByteArray(b,0,b.length);
    }

    //-------------SQL--------------//

    public void deleteData(MenuItem item){
        if(editTitle.getText().toString().equals("")){
            Toast.makeText(this, "Первое поле обязательно для заполнения",Toast.LENGTH_SHORT).show();
        }
        else {
            SQLiteDatabase db =mDbHelper.getWritableDatabase();
            db.delete(PlanContract.PlanEntry.TABLE_NAME, PlanContract.PlanEntry.COLUMN_TITLE + " = ?",
                    new String[]{title});
            db.close();
            Intent intent = new Intent(PlaningActivity.this, MainActivity.class);
            startActivity(intent);
            DeleteAlarm(RequestCode);
            finish();
        }
    }

    public void saveData(MenuItem item) {
        if(!isPhoto){
            mDrawingView.setDrawingCacheEnabled(true);
            Bitmap bitmap = mDrawingView.getDrawingCache();
            Byteimage = getImageInByte(bitmap);
        }
        if(editTitle.getText().toString().equals("")){
            Toast.makeText(this, "Первое поле обязательно для заполнения",Toast.LENGTH_SHORT).show();
        }
        else {
                New_title = editTitle.getText().toString();
                New_body = editBody.getText().toString();
                New_date = GetDateToStr(Timecal)+" "+GetTimeToStr(Timecal);
                String RqMonth, RqDay, RqHour, RqMinute, RqWhole;
                RqMonth = String.valueOf(Timecal.get(Calendar.MONTH)+1);
                RqDay = ( Timecal.get(Calendar.DAY_OF_MONTH)<10 ?  "0"+Timecal.get(Calendar.DAY_OF_MONTH)  : Timecal.get(Calendar.DAY_OF_MONTH) )+"";
                RqHour = ( Timecal.get(Calendar.HOUR_OF_DAY)<10 ? "0"+Timecal.get(Calendar.HOUR_OF_DAY ) : Timecal.get(Calendar.HOUR_OF_DAY) )+"";
                RqMinute = ( Timecal.get(Calendar.MINUTE)<10 ? "0"+Timecal.get(Calendar.MINUTE) : Timecal.get(Calendar.MINUTE) )+"";
                RqWhole = RqMonth+RqDay+RqHour+RqMinute;
                int RequestCode_New = Integer.parseInt(RqWhole);
                Log.d(TAG, String.valueOf(RequestCode));

                Log.d(TAG,New_date);

                SQLiteDatabase db = mDbHelper.getWritableDatabase();
                ContentValues values = new ContentValues();

            if(!isChanging) {
                values.put(PlanContract.PlanEntry.COLUMN_TITLE, New_title);
                values.put(PlanContract.PlanEntry.COLUMN_BODY, New_body);
                values.put(PlanContract.PlanEntry.COLUMN_DATE, New_date);
                values.put(PlanContract.PlanEntry.COLUMN_IMG, Byteimage);


                long newRowId = db.insert(PlanContract.PlanEntry.TABLE_NAME, null, values);

                if (newRowId == -1)
                    Toast.makeText(this, "Ошибка при записи в БД", Toast.LENGTH_SHORT).show();
                MakeAlarm(RequestCode_New);

            }
            else{
                Log.d(TAG,New_title+title);
                values.put(PlanContract.PlanEntry.COLUMN_TITLE, New_title);
                values.put(PlanContract.PlanEntry.COLUMN_BODY, New_body);
                values.put(PlanContract.PlanEntry.COLUMN_DATE, New_date);
                values.put(PlanContract.PlanEntry.COLUMN_IMG, Byteimage);

                long exeption1 = db.update(PlanContract.PlanEntry.TABLE_NAME, values,
                        PlanContract.PlanEntry.COLUMN_TITLE +"= ?",
                        new String[]{title});
                if (exeption1 == -1)
                    Toast.makeText(this, "Ошибка при записи в БД", Toast.LENGTH_SHORT).show();
                isChanging=false;
                DeleteAlarm(RequestCode);
                MakeAlarm(RequestCode_New);

            }
            Intent intent = new Intent(PlaningActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    //--------------Alarm-------------//

    public void MakeAlarm (int RequestCode) {

        AlarmManager alarmM = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent Notifyintent = new Intent(this, MyAlarmReceiver.class);
        Notifyintent.putExtra("NT_Title", New_title);
        Notifyintent.putExtra("NT_MusicFlag", FLAGfMUSIC);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                RequestCode,
                Notifyintent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );
        Calendar onDateSet = Calendar.getInstance();

        Calendar alarmCal = Timecal;
        Log.d(TAG+"--Before", String.valueOf(alarmCal));
        Long mills = alarmCal.getTimeInMillis();


        byte selected = (byte) spinner.getSelectedItemId();
        switch (selected){
            case 0:
                alarmCal.set(Calendar.HOUR_OF_DAY, 0);
                alarmCal.set(Calendar.MINUTE, 0);
                break;
            case 1:
                mills-=3600000;
                break;
            case 2:
                mills-=21600000;
                break;
            case 3:
                mills-=43200000;
                break;
            case 4:
                mills-=86400000;
                break;
            case 5:
                mills-=259200000;
                break;
            case 6:
                mills-=604800000;
                break;
        }
        alarmCal.setTimeInMillis(mills);
        Log.d(TAG+"--After", String.valueOf(alarmCal));

        onDateSet.set(Calendar.YEAR, alarmCal.get(Calendar.YEAR) );
        onDateSet.set(Calendar.MONTH, alarmCal.get(Calendar.MONTH) );
        onDateSet.set(Calendar.DAY_OF_MONTH, alarmCal.get(Calendar.DAY_OF_MONTH) );
        onDateSet.set(Calendar.HOUR_OF_DAY, alarmCal.get(Calendar.HOUR_OF_DAY) );
        onDateSet.set(Calendar.MINUTE, alarmCal.get(Calendar.MINUTE) );
        onDateSet.set(Calendar.SECOND, 0);
        onDateSet.set(Calendar.MILLISECOND, 0);

        Log.d(TAG, String.valueOf(onDateSet));

        if (alarmM != null)
            alarmM.set(AlarmManager.RTC_WAKEUP, onDateSet.getTimeInMillis(), pendingIntent);
    }

    public void DeleteAlarm(int RequestCode){
        AlarmManager alarmM = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent Notifyintent = new Intent(this, MyAlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                RequestCode,
                Notifyintent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );
        if (alarmM != null) {
            alarmM.cancel(pendingIntent);
        }
    }

    TimePickerDialog.OnTimeSetListener t = new TimePickerDialog.OnTimeSetListener() {
        @SuppressLint("SetTextI18n")
        @Override
        public void onTimeSet(TimePicker timePicker, int hour, int minute) {
            Timecal.set(Calendar.HOUR_OF_DAY,hour);
            Timecal.set(Calendar.MINUTE, minute);
            timeB.setText(GetTimeToStr(Timecal));
        }
    };
    DatePickerDialog.OnDateSetListener d=new DatePickerDialog.OnDateSetListener() {
        @SuppressLint("SetTextI18n")
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            Timecal.set(Calendar.YEAR, year);
            Timecal.set(Calendar.MONTH, monthOfYear);
            Timecal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            dateB.setText(GetDateToStr(Timecal));
        }
    };

    public void setTime(View view) {
        new TimePickerDialog(PlaningActivity.this, t,
                Timecal.get(Calendar.HOUR_OF_DAY),
                Timecal.get(Calendar.MINUTE),true)
                .show();
    }
    public void setDate(View v) {
        new DatePickerDialog(PlaningActivity.this, d,
                Timecal.get(Calendar.YEAR),
                Timecal.get(Calendar.MONTH),
                Timecal.get(Calendar.DAY_OF_MONTH))
                .show();
    }

    public String GetDateToStr(Calendar c1){
        String str;
        str = c1.get(Calendar.YEAR)+"/"
                +( (c1.get(Calendar.MONTH)+1)<10 ? ( "0"+(c1.get(Calendar.MONTH)+1) ) : ((c1.get(Calendar.MONTH)+1)) )+"/"
                +( c1.get(Calendar.DAY_OF_MONTH)<10 ? ( "0"+c1.get(Calendar.DAY_OF_MONTH) ) : (c1.get(Calendar.DAY_OF_MONTH)) );
        return str;
    }
    public String GetTimeToStr(Calendar c1){
        String str;
        str =  ( c1.get(Calendar.HOUR_OF_DAY)<10 ? "0"+c1.get(Calendar.HOUR_OF_DAY ) : c1.get(Calendar.HOUR_OF_DAY) )+":"
                +( c1.get(Calendar.MINUTE)<10 ? "0"+c1.get(Calendar.MINUTE) : c1.get(Calendar.MINUTE) ) ;
        return str;
    }

    //-------------Photo-------------//

    public void photoActivator(MenuItem item) {
        isPhoto=true;
        mDrawingView.setVisibility(View.INVISIBLE);
        mImageView.setVisibility(View.VISIBLE);
        ib1.setVisibility(View.INVISIBLE);
        ib2.setVisibility(View.INVISIBLE);
        ib3.setVisibility(View.INVISIBLE);
        ib4.setVisibility(View.INVISIBLE);
        erase_btn.setVisibility(View.INVISIBLE);


        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, CAMERA_RESULT);
        }
    }

    private byte[] getImageInByte(Bitmap image){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG,100,stream);

        return stream.toByteArray();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_RESULT && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = null;
            if (extras != null)
                imageBitmap = (Bitmap) extras.get("data");
            Byteimage = getImageInByte(imageBitmap);
            mImageView.setImageBitmap(imageBitmap);
        }
    }

    //-------------Paint-------------//

    public void OpenPainting(MenuItem item){
        isPhoto=false;
        mDrawingView.setVisibility(View.VISIBLE);
        mImageView.setVisibility(View.INVISIBLE);
        ib1.setVisibility(View.VISIBLE);
        ib2.setVisibility(View.VISIBLE);
        ib3.setVisibility(View.VISIBLE);
        ib4.setVisibility(View.VISIBLE);
        erase_btn.setVisibility(View.VISIBLE);
        erase_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDrawingView.setErase(true);
            }
        });
        erase_btn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                AlertDialog.Builder quitDialog = new AlertDialog.Builder(PlaningActivity.this);
                quitDialog.setTitle("Полностью стереть рисунок?");
                quitDialog.setPositiveButton("Да!", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mDrawingView.fullErase();
                    }
                });
                quitDialog.setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                quitDialog.show();

                return true;
            }
        });
    }

    public void ChangeToBlack(View view) {
        mDrawingView.setErase(false);
        mDrawingView.setColor("BLACK");
    }

    public void ChangeToBlue(View view) {
        mDrawingView.setErase(false);
        mDrawingView.setColor("BLUE");
    }

    public void ChangeToGreen(View view) {
        mDrawingView.setErase(false);
        mDrawingView.setColor("GREEN");
    }

    public void ChangeToRed(View view) {
        mDrawingView.setErase(false);
        mDrawingView.setColor("RED");
    }
}
