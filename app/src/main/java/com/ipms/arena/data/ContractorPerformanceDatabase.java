package com.ipms.arena.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.ipms.arena.helper.Session;
import com.ipms.arena.model.ContractorPerformance;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by apflores on 5/11/2016.
 */
public class ContractorPerformanceDatabase extends SQLiteOpenHelper
{
    Context context;
    Session session;

    public static final String DATABASE_NAME = "IPMS.db";
    public static final String CONTRACTOR_PERFORMANCE_TABLE = "CONTRACTOR_PERFORMANCE";

    public static final String USER_ID = "USER_ID";
    public static final String ZONE_ID = "ZONE_ID";
    public static final String CONTRACTOR_ID = "CONTRACTOR_ID";
    public static final String SERVICE_ORDER = "SERVICE_ORDER";
    public static final String STATUS_DATE = "STATUS_DATE";
    public static final String STATUS_TIME = "STATUS_TIME";
    public static final String TYPE = "TYPE";

    private ContractorPerformance performance;
    private List performances = new ArrayList<>();
    private SQLiteDatabase db;
    private Cursor cursor;

    private String DATE_FORMAT = "MM/dd/yyyy";
    private String date;

    public ContractorPerformanceDatabase(Context context)
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
        db.execSQL("CREATE TABLE IF NOT EXISTS " + CONTRACTOR_PERFORMANCE_TABLE +
                        " (USER_ID integer, ZONE_ID integer, CONTRACTOR_ID integer,  SERVICE_ORDER text PRIMARY KEY, " +
                        "STATUS_DATE text, STATUS_TIME text, TYPE integer)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL("DROP TABLE IF EXISTS " + CONTRACTOR_PERFORMANCE_TABLE);
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
            performance = (ContractorPerformance) p;
            insert(performance);
        }

        db.setTransactionSuccessful();
        db.endTransaction();
        db.close();

        return true;
    }

    public boolean insert(ContractorPerformance p)
    {
        db = this.getWritableDatabase();
        ContentValues data = new ContentValues();

        data.put(USER_ID, session.getID());
        data.put(CONTRACTOR_ID, p.getContractorCode());
        data.put(ZONE_ID, p.getZoneId());
        data.put(SERVICE_ORDER, p.getServiceOrder());
        data.put(STATUS_DATE, p.getStatusDate());
        data.put(STATUS_TIME, p.getStatusTime());
        data.put(TYPE, p.getType());

        db.insertWithOnConflict(CONTRACTOR_PERFORMANCE_TABLE, null, data, SQLiteDatabase.CONFLICT_REPLACE);
        return true;
    }

    public boolean update(ContractorPerformance perf)
    {
        db = this.getWritableDatabase();
        ContentValues data = new ContentValues();

        data.put(STATUS_DATE, perf.getStatusDate());
        data.put(STATUS_TIME, perf.getStatusTime());

        db.update(CONTRACTOR_PERFORMANCE_TABLE, data, CONTRACTOR_ID + " = ? AND " + ZONE_ID + " = ? AND " + SERVICE_ORDER + " = ? AND " + USER_ID + " = ?",
                new String[]
                        {
                                String.valueOf(perf.getContractorCode()),
                                String.valueOf(perf.getZoneId()),
                                String.valueOf(perf.getServiceOrder()),
                                String.valueOf(perf.getUser()),
                        }
        );

        return true;
    }

    public int size() {
        db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, CONTRACTOR_PERFORMANCE_TABLE);

        return numRows;
    }

    public Cursor getContractorPerformance(ContractorPerformance cp)
    {
        db = this.getReadableDatabase();
        onCreate(db);

         cursor = db.rawQuery("SELECT * FROM " + CONTRACTOR_PERFORMANCE_TABLE + " WHERE " + CONTRACTOR_ID + "='" + cp.getContractorCode() + "'" +
                " AND " + ZONE_ID + "='" + cp.getZoneId() + "' AND " + SERVICE_ORDER + " = '" + cp.getServiceOrder() + "' AND " + TYPE + " = " + cp.getType(), null);

        return cursor;
    }

    public Cursor getContractorPerformances(int zone, int contactor, int type)
    {
        db = this.getReadableDatabase();
        onCreate(db);

        cursor = db.rawQuery("SELECT * FROM " + CONTRACTOR_PERFORMANCE_TABLE + " WHERE " + CONTRACTOR_ID + " = " + contactor + " AND "
                + ZONE_ID + " = " + zone + " AND " + TYPE + " = " + type + " AND " + STATUS_DATE + " = '" + date + "'", null);

        return cursor;
    }
}
