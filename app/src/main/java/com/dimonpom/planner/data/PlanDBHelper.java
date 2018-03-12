package com.dimonpom.planner.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class PlanDBHelper extends SQLiteOpenHelper {
    public static final String DBname = "plan.db";
    public static final int DBversion = 1;

    public PlanDBHelper(Context context) {

        super(context , DBname , null , DBversion );
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String SQL_CREATE_PLAN_TABLE = "Create Table "+ PlanContract.PlanEntry.TABLE_NAME
                +" (" + PlanContract.PlanEntry._ID + " Integer Primary Key Autoincrement, "
                + PlanContract.PlanEntry.COLUMN_TITLE + " Text, "
                + PlanContract.PlanEntry.COLUMN_BODY + " Text, "
                + PlanContract.PlanEntry.COLUMN_DATE + " Text, "
                + PlanContract.PlanEntry.COLUMN_IMG + " Blob );";

        sqLiteDatabase.execSQL(SQL_CREATE_PLAN_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
