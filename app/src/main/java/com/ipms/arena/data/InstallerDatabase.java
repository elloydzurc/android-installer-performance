package com.ipms.arena.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;

import com.ipms.arena.helper.Session;
import com.ipms.arena.model.Contractor;
import com.ipms.arena.model.Installer;
import com.ipms.arena.model.Zone;

/**
 * Created by apflores on 5/12/2016.
 */
public class InstallerDatabase extends SQLiteOpenHelper
{
    Context context;
    Session session;

    public static final String DATABASE_NAME = "IPMS.db";
    public static final String INSTALLER_TABLE = "INSTALLER";

    public static final String USER_ID = "USER_ID";
    public static final String INSTALLER_ZONE = "ZONE";
    public static final String INSTALLER_CONTRACTOR = "CONTRACTOR";
    public static final String INSTALLER_KEY = "INSTALLER_KEY";
    public static final String INSTALLER_ID = "INSTALLER_ID";
    public static final String INSTALLER_NAME = "INSTALLER_NAME";
    public static final String INSTALLER_OPEN = "OPEN";
    public static final String INSTALLER_CLOSED = "CLOSED";
    public static final String INSTALLER_RSO = "RSO";
    public static final String INSTALLER_ACKNOWLEDGE = "ACKNOWLEDGE";
    public static final String INSTALLER_DISPATCHED = "DISPATCHED";

    private String CONTRACTOR = "contractor";
    private String ZONE = "zone";
    private Contractor contractor;
    private Zone zone;
    private SQLiteDatabase db;
    private Cursor cursor;

    public InstallerDatabase(Context context)
    {
        super(context, DATABASE_NAME, null, 1);
        this.context = context;
        this.session = new Session(context);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + INSTALLER_TABLE +
                        " (USER_ID integer, ZONE integer, CONTRACTOR integer, INSTALLER_KEY integer, INSTALLER_ID text, INSTALLER_NAME text," +
                        " OPEN integer, CLOSED integer, RSO integer, ACKNOWLEDGE integer, DISPATCHED integer)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL("DROP TABLE IF EXISTS " + INSTALLER_TABLE);
        onCreate(db);
    }

    public boolean insert(Installer installer)
    {
        db = this.getWritableDatabase();
        ContentValues data = new ContentValues();

        data.put(USER_ID, session.getID());
        data.put(INSTALLER_ZONE, installer.getZoneId());
        data.put(INSTALLER_CONTRACTOR, installer.getContractorId());
        data.put(INSTALLER_KEY, installer.getKey());
        data.put(INSTALLER_ID, installer.getId());
        data.put(INSTALLER_NAME, installer.getName());
        data.put(INSTALLER_OPEN, installer.getOpen());
        data.put(INSTALLER_CLOSED, installer.getClosed());
        data.put(INSTALLER_RSO, installer.getRso());
        data.put(INSTALLER_ACKNOWLEDGE, installer.getAcknowledge());
        data.put(INSTALLER_DISPATCHED, installer.getDispatched());

        db.insert(INSTALLER_TABLE, null, data);
        return true;
    }

    public boolean update(Installer installer)
    {
        db = this.getWritableDatabase();
        ContentValues data = new ContentValues();

        data.put(INSTALLER_OPEN, installer.getOpen());
        data.put(INSTALLER_CLOSED, installer.getClosed());
        data.put(INSTALLER_RSO, installer.getRso());
        data.put(INSTALLER_ACKNOWLEDGE, installer.getAcknowledge());
        data.put(INSTALLER_DISPATCHED, installer.getDispatched());

        db.update(INSTALLER_TABLE, data, INSTALLER_ID + " = ? AND " + INSTALLER_ZONE + " = ?",
                new String[]{String.valueOf(installer.getId()), String.valueOf(installer.getZoneId())});

        return true;
    }

    public int size()
    {
        db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, INSTALLER_TABLE);
        return numRows;
    }

    public Cursor getInstaller(int zone, int key)
    {
        db = this.getReadableDatabase();
        onCreate(db);

        cursor = db.rawQuery("SELECT * FROM " + INSTALLER_TABLE + " WHERE " + INSTALLER_ZONE + " = " + zone +
                " AND " + INSTALLER_KEY + " = " + key + " AND " + USER_ID + " = " + session.getID(), null);

        return cursor;
    }

    public Cursor getInstallers(int page, int limit, Bundle tag)
    {
        int start = (page-1) * limit;
        int id = session.getID();
        int zid = 0;
        int cid = 0;

        if(tag.getParcelable(CONTRACTOR) != null) {
            contractor = tag.getParcelable(CONTRACTOR);
            zid = contractor.getZoneId();
            cid = contractor.getContractorId();
        } else {
            zone = tag.getParcelable(ZONE);
            zid = zone.getZoneId();
        }

        db = this.getReadableDatabase();
        onCreate(db);

        cursor = db.rawQuery("SELECT * FROM " + INSTALLER_TABLE + " WHERE " + USER_ID + " = " + id + " AND " + INSTALLER_ZONE +
                " = " + zid + " AND " + INSTALLER_CONTRACTOR + " = " + cid + " ORDER BY rowid" /*+ INSTALLER_OPEN + " DESC " LIMIT " + start + ", " + limit*/, null);

        return cursor;
    }
}
