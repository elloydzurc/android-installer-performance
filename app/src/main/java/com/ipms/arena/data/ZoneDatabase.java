package com.ipms.arena.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.ipms.arena.helper.Session;
import com.ipms.arena.model.Zone;

import java.util.ArrayList;

/**
 * Created by apflores on 5/11/2016.
 */
public class ZoneDatabase extends SQLiteOpenHelper
{
    Context context;
    Session session;

    public static final String DATABASE_NAME = "IPMS.db";
    public static final String ZONE_TABLE = "ZONE";

    public static final String USER_ID = "USER_ID";
    public static final String ZONE_ID = "ZONE_ID";
    public static final String ZONE_NAME = "ZONE_NAME";
    public static final String CONTRACTORS = "CONTRACTORS";
    public static final String ORGANIC = "ORGANIC";

    private ArrayList<Zone> zones = new ArrayList<>();
    private SQLiteDatabase db;
    private Cursor cursor;

    public ZoneDatabase(Context context)
    {
        super(context, DATABASE_NAME, null, 1);
        this.context = context;
        this.session = new Session(context);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + ZONE_TABLE +
            " (USER_ID integer, ZONE_ID integer, ZONE_NAME text, CONTRACTORS integer, ORGANIC integer)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL("DROP TABLE IF EXISTS " + ZONE_TABLE);
        onCreate(db);
    }

    public boolean insertArray(ArrayList<Zone> list)
    {
        zones = new ArrayList<>();
        zones = list;

        db = this.getWritableDatabase();
        onCreate(db);

        for(Zone zone : zones) {
           insert(zone);
        }

        return true;
    }

    public boolean insert(Zone zone)
    {
        db = this.getWritableDatabase();
        ContentValues data = new ContentValues();

        data.put(USER_ID, session.getID());
        data.put(ZONE_ID, zone.getZoneId());
        data.put(ZONE_NAME, zone.getZone());
        data.put(CONTRACTORS, zone.getContractors());

        db.insert(ZONE_TABLE, null, data);
        return true;
    }

    public int size()
    {
        db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, ZONE_TABLE);

        return numRows;
    }

    public Cursor getZone(int zoneId)
    {
        db = this.getReadableDatabase();
        onCreate(db);

        cursor = db.rawQuery("SELECT * FROM " + ZONE_TABLE + " WHERE " + ZONE_ID + " = " + zoneId +
                " AND " + USER_ID + " = " + session.getID(), null);

        return cursor;
    }

    public boolean updateZone(int type, Zone zone)
    {
        db = this.getWritableDatabase();
        ContentValues data = new ContentValues();

        data.put((type == 1) ? CONTRACTORS : ORGANIC, zone.getContractors());

        db.update(ZONE_TABLE, data, ZONE_ID + " = ? AND " + USER_ID + " = ?",
                new String[]{String.valueOf(zone.getZoneId()), String.valueOf(session.getID())});

        return true;
    }

    public Cursor getZones()
    {
        int id = session.getID();
        db = this.getReadableDatabase();
        onCreate(db);

        onCreate(db);
        cursor = db.rawQuery("SELECT * FROM " + ZONE_TABLE + " WHERE " + USER_ID + " = " + id + " ORDER BY " + ZONE_NAME, null);

        return cursor;
    }
}
