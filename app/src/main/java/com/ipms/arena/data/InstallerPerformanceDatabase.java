package com.ipms.arena.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.ipms.arena.helper.Session;
import com.ipms.arena.model.ContractorPerformance;
import com.ipms.arena.model.InstallerPerformance;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by apflores on 5/11/2016.
 */
public class InstallerPerformanceDatabase extends SQLiteOpenHelper
{
    Context context;
    Session session;

    public static final String DATABASE_NAME = "IPMS.db";
    public static final String INSTALLER_PERFORMANCE_TABLE = "INSTALLER_PERFORMANCE";

    public static final String USER_ID = "USER_ID";
    public static final String ZONE_ID = "ZONE_ID";
    public static final String CONTRACTOR_ID = "CONTRACTOR_ID";
    public static final String TECH_ID = "TECH_ID";
    public static final String SERVICE_ORDER = "SERVICE_ORDER";
    public static final String STATUS_DATE = "STATUS_DATE";
    public static final String STATUS_TIME = "STATUS_TIME";
    public static final String TYPE = "TYPE";

    private InstallerPerformance performance;
    private List performances = new ArrayList<>();
    private SQLiteDatabase db;
    private Cursor cursor;
    private String DATE_FORMAT = "MM/dd/yyyy";
    private String date;

    public InstallerPerformanceDatabase(Context context)
    {
        super(context, DATABASE_NAME, null, 1);
        this.context = context;
        this.session = new Session(context);

        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        date = sdf.format(new Date());
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + INSTALLER_PERFORMANCE_TABLE +
                " (USER_ID integer, ZONE_ID integer, CONTRACTOR_ID integer, TECH_ID integer, SERVICE_ORDER text PRIMARY KEY, " +
                "STATUS_DATE text, STATUS_TIME text, TYPE integer)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL("DROP TABLE IF EXISTS " + INSTALLER_PERFORMANCE_TABLE);
        onCreate(db);
    }

    public boolean insertList(List list)
    {
        performances = list;

        db = this.getWritableDatabase();
        onCreate(db);

        db.beginTransaction();

        for(Object p : performances)
        {
            performance = (InstallerPerformance) p;
            insert(performance);
        }

        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();

        return true;
    }

    public boolean insert(InstallerPerformance p)
    {
        db = this.getWritableDatabase();
        ContentValues data = new ContentValues();

        data.put(USER_ID, session.getID());
        data.put(CONTRACTOR_ID, p.getContractorId());
        data.put(ZONE_ID, p.getZoneId());
        data.put(TECH_ID, p.getTechKey());
        data.put(SERVICE_ORDER, p.getServiceOrder());
        data.put(STATUS_DATE, p.getStatusDate());
        data.put(STATUS_TIME, p.getStatusTime());
        data.put(TYPE, p.getType());

        db.insertWithOnConflict(INSTALLER_PERFORMANCE_TABLE, null, data, SQLiteDatabase.CONFLICT_REPLACE);
        return true;
    }

    public int size() {
        db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, INSTALLER_PERFORMANCE_TABLE);

        return numRows;
    }

    public Cursor getInstallerPerformance(int zone, int contactor, int key, int type)
    {
        db = this.getReadableDatabase();
        onCreate(db);

        cursor = db.rawQuery("SELECT * FROM " + INSTALLER_PERFORMANCE_TABLE + " WHERE " + CONTRACTOR_ID + " = " + contactor + " AND "
                + ZONE_ID + " = " + zone + " AND " + TECH_ID + " = " + key + " AND " + TYPE + " = " + type + " AND " + STATUS_DATE + " = '" + date + "'", null);

        return cursor;
    }
}
