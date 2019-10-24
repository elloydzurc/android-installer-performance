package com.ipms.arena.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.ipms.arena.model.ServiceLocation;

/**
 * Created by t-ebcruz on 3/3/2016.
 */
public class Geolocation extends SQLiteOpenHelper
{
    public static final String DATABASE_NAME = "IPMS.db";
    public static final String GEOLOCATION_TBL = "GEOLOCATION";

    public static final String GEO_ID = "ID";
    public static final String GEO_SERVICE_NUMBER = "SERVICE_NUMBER";
    public static final String GEO_SERVICE_ADDRESS = "SERVICE_ADDRESS";
    public static final String GEO_LAT = "LATITUDE";
    public static final String GEO_LON = "LONGTITUDE";

    public Geolocation(Context context)
    {
        super(context, DATABASE_NAME , null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE IF NOT EXISTS " + GEOLOCATION_TBL +
                        " (ID integer primary key, SERVICE_NUMBER text, SERVICE_ADDRESS text, LATITUDE real, LONGTITUDE real)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + GEOLOCATION_TBL);
        onCreate(db);
    }

    public boolean insert(ServiceLocation location)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues data = new ContentValues();

        data.put(GEO_SERVICE_NUMBER, location.getServiceNumber());
        data.put(GEO_SERVICE_ADDRESS, location.getServiceAddress());
        data.put(GEO_LAT, location.getLatitude());
        data.put(GEO_LON, location.getLongtitude());

        db.insert(GEOLOCATION_TBL, null, data);
        return true;
    }

    public int size()
    {
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, GEOLOCATION_TBL);
        return numRows;
    }

    public boolean update(ServiceLocation location)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues data = new ContentValues();

        data.put(GEO_SERVICE_ADDRESS, location.getServiceAddress());
        data.put(GEO_LAT, location.getLatitude());
        data.put(GEO_LON, location.getLongtitude());

        db.update(GEOLOCATION_TBL, data, "SERVICE_NUMBER = ?", new String[]{ location.getServiceNumber() });
        return true;
    }

    public Cursor get(String serviceNumber)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        onCreate(db);

        Cursor res =  db.rawQuery("SELECT * FROM " + GEOLOCATION_TBL + " WHERE " + GEO_SERVICE_NUMBER + "='" + serviceNumber + "'", null );
        return res;
    }

    public Cursor all()
    {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery("SELECT * FROM " + GEOLOCATION_TBL, null );

        return res;
    }
}
