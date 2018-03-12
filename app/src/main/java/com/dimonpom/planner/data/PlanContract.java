package com.dimonpom.planner.data;


import android.provider.BaseColumns;

public class PlanContract {
    private PlanContract(){}


    public static final class PlanEntry implements BaseColumns{
      //  byte[] smt =

        public final static String TABLE_NAME = "plan";

        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_TITLE = "title";
        public final static String COLUMN_BODY = "body";
        public final static String COLUMN_DATE = "date";
        public final static String COLUMN_IMG = "img";
    }
}
