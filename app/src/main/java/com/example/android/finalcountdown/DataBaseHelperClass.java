package com.example.android.finalcountdown;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DataBaseHelperClass extends SQLiteOpenHelper
{
    public static  final  String DATABASE_NAME = "date.db";
    public static  final  String TABLE_NAME = "date_table";
    //public static  final  String ID = "ID";
    public static  final  String HOURS = "HOURS";
    public static  final  String MINUTES = "MINUTES";
    public static  final  String SECONDS = "SECONDS";
    public static  final  String DAY = "DAY";
    public static  final  String MONTH = "MONTH";
    public static  final  String YEAR = "YEAR";

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //Constructor
    ////////////////////////////////////////////////////////////////////////////////////////////////
    public DataBaseHelperClass(Context context)
    {
        super(context, DATABASE_NAME, null, 1);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //Oncreate method
    ////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL("create table " + TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, HOURS INTEGER, MINUTES INTEGER, SECONDS INTEGER, DAY INTEGER," +
                "MONTH INTEGER, YEAR INTEGER)");
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //Calls if previous version af a db is different from a current one.
    ////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //Inserts data into a db
    ////////////////////////////////////////////////////////////////////////////////////////////////
    public boolean InsertData(CustomDate obj)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();

        contentValues.put(HOURS, obj.getHours());
        contentValues.put(MINUTES, obj.getMinutes());
        contentValues.put(SECONDS, obj.getSeconds());
        contentValues.put(DAY, obj.getDay());
        contentValues.put(MONTH, obj.getMonth());
        contentValues.put(YEAR, obj.getYear());

        long result = db.insert(TABLE_NAME, null, contentValues);

        if(result == -1)
            return false;

        return true;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //Returns the whole db
    ////////////////////////////////////////////////////////////////////////////////////////////////
    public Cursor GetAllData()
    {
        SQLiteDatabase db = this.getWritableDatabase();

        return db.rawQuery("select * from " + TABLE_NAME, null);
    }
}
