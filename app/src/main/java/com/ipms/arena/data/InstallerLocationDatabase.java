package com.ipms.arena.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.ipms.arena.helper.Session;
import com.ipms.arena.model.Installer;
import com.ipms.arena.model.InstallerLocation;

import java.util.ArrayList;

/**
 * Created by apflores on 5/12/2016.
 */
public class InstallerLocationDatabase extends SQLiteOpenHelper
{
    Context context;
    Session session;

    public static final String DATABASE_NAME = "IPMS.db";
    public static final String INSTALLER_LOCATION_TABLE = "INSTALLER_LOCATION";

    public static final String TECH_KEY = "TECH_KEY";
    public static final String TECH_ID = "TECH_ID";
    public static final String SERVICE_ORDER = "SERVICE_ORDER";
    public static final String PROJECT_ID = "PROJECT_ID";
    public static final String STATUS = "STATUS";
    public static final String SERVICE_TYPE = "SERVICE_TYPE";
    public static final String ORDER_TYPE = "ORDER_TYPE";
    public static final String NE_TYPE = "NE_TYPE";
    public static final String ADDRESS = "ADDRESS";
    public static final String ADDRESS2 = "ADDRESS2";
    public static final String ZONE = "ZONE";
    public static final String LON = "LON";
    public static final String LAT = "LAT";

    private SQLiteDatabase db;
    private Cursor cursor;

    public InstallerLocationDatabase(Context context)
    {
        super(context, DATABASE_NAME, null, 1);
        this.context = context;
        this.session = new Session(context);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + INSTALLER_LOCATION_TABLE +
                   " (ZONE integer, TECH_KEY integer, TECH_ID text, SERVICE_ORDER text, PROJECT_ID text, STATUS text, SERVICE_TYPE text, ORDER_TYPE text, NE_TYPE text, ADDRESS text, ADDRESS2 text, LAT real, LON real)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL("DROP TABLE IF EXISTS " + INSTALLER_LOCATION_TABLE);
        onCreate(db);
    }

    public boolean insertArray(ArrayList<InstallerLocation> locations)
    {
        db = this.getWritableDatabase();
        onCreate(db);

        for(InstallerLocation location : locations) {
            insert(location);
        }

        return true;
    }

    public boolean insert(InstallerLocation location)
    {
        db = this.getWritableDatabase();
        ContentValues data = new ContentValues();

        data.put(ZONE, location.getInstaller().getZoneId());
        data.put(TECH_KEY, location.getInstaller().getKey());
        data.put(TECH_ID, location.getInstaller().getId());
        data.put(SERVICE_ORDER, location.getServiceOrder());
        data.put(PROJECT_ID, location.getProjectId());
        data.put(STATUS, location.getStatus());
        data.put(SERVICE_TYPE, location.getServiceType());
        data.put(ORDER_TYPE, location.getOrderType());
        data.put(NE_TYPE, location.getNeType());
        data.put(ADDRESS, location.getAddress());
        data.put(ADDRESS2, location.getAddress2());
        data.put(LAT, location.getLat());
        data.put(LON, location.getLon());

        db.insert(INSTALLER_LOCATION_TABLE, null, data);
        return true;
    }

    public boolean update(InstallerLocation location)
    {
        db = this.getWritableDatabase();
        ContentValues data = new ContentValues();

        data.put(ZONE, location.getInstaller().getZoneId());
        data.put(TECH_KEY, location.getInstaller().getKey());
        data.put(TECH_ID, location.getInstaller().getId());
        data.put(SERVICE_ORDER, location.getServiceOrder());
        data.put(PROJECT_ID, location.getProjectId());
        data.put(STATUS, location.getStatus());
        data.put(SERVICE_TYPE, location.getServiceType());
        data.put(ORDER_TYPE, location.getOrderType());
        data.put(NE_TYPE, location.getNeType());
        data.put(ADDRESS, location.getAddress());
        data.put(ADDRESS2, location.getAddress2());
        data.put(LAT, location.getLat());
        data.put(LON, location.getLon());

        db.update(INSTALLER_LOCATION_TABLE, data, TECH_KEY + " = ? AND " + SERVICE_ORDER + " = ?", new String[]{String.valueOf(location.getInstaller().getKey()), location.getServiceOrder()});
        return true;
    }

    public int size()
    {
        db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, INSTALLER_LOCATION_TABLE);
        return numRows;
    }

    public Cursor getInstallerLocation(InstallerLocation location)
    {
        db = this.getReadableDatabase();
        onCreate(db);

        cursor = db.rawQuery("SELECT * FROM " + INSTALLER_LOCATION_TABLE + " WHERE " + TECH_KEY + " = " + location.getInstaller().getKey()
                + " AND " + SERVICE_ORDER + " = '" + location.getServiceOrder() + "' AND " + ZONE + " = " + location.getInstaller().getZoneId(), null);
        return cursor;
    }

    public Cursor getInstallerLocation(Installer installer)
    {
        db = this.getReadableDatabase();
        onCreate(db);

        cursor = db.rawQuery("SELECT * FROM " + INSTALLER_LOCATION_TABLE + " WHERE " + TECH_KEY + " = " + installer.getKey() + " AND " + ZONE + " = " + installer.getZoneId(), null);
        return cursor;
    }
}
