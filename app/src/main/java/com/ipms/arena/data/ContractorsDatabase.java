package com.ipms.arena.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.ipms.arena.helper.Session;
import com.ipms.arena.model.Contractor;

import java.util.ArrayList;

/**
 * Created by apflores on 5/11/2016.
 */
public class ContractorsDatabase extends SQLiteOpenHelper
{
    Context context;
    Session session;

    public static final String DATABASE_NAME = "IPMS.db";
    public static final String CONTRACTOR_TABLE = "CONTRACTOR";

    public static final String FLAG = "FLAG";
    public static final String CONTRACTOR_ID = "CONTRACTOR_ID";
    public static final String CON_ZONE_ID = "ZONE_ID";
    public static final String CON_ZONE = "ZONE";
    public static final String CONTRACTOR = "CONTRACTOR";
    public static final String CON_OPEN = "OPEN";
    public static final String CON_COMPLETED = "CLOSED";
    public static final String CON_RSO = "RSO";
    public static final String CON_ACKNOWLEDGE = "ACKNOWLEDGE";
    public static final String CON_DISPATCHED = "DISPATCHED";
    public static final String USER_ID = "USER_ID";
    public static final String INSTALLER = "INSTALLER";

    private ArrayList<Contractor> contractors = new ArrayList<>();
    private SQLiteDatabase db;
    private Cursor cursor;

    public ContractorsDatabase(Context context)
    {
        super(context, DATABASE_NAME, null, 1);
        this.context = context;
        this.session = new Session(context);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + CONTRACTOR_TABLE +
                        " (USER_ID integer, CONTRACTOR_ID integer, ZONE_ID integer, ZONE text, " +
                        "CONTRACTOR text, OPEN integer, CLOSED integer, RSO integer, ACKNOWLEDGE integer, DISPATCHED integer, INSTALLER integer, FLAG integer)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL("DROP TABLE IF EXISTS " + CONTRACTOR_TABLE);
        onCreate(db);
    }

    public boolean insertArray(ArrayList<Contractor> list)
    {
        contractors = new ArrayList<>();
        contractors = list;

        db = this.getWritableDatabase();
        onCreate(db);

        for(Contractor c : contractors) {
           insert(c);
        }

        return true;
    }

    public boolean insert(Contractor c)
    {
        db = this.getWritableDatabase();
        ContentValues data = new ContentValues();

        data.put(USER_ID, session.getID());
        data.put(CONTRACTOR_ID, c.getContractorId());
        data.put(CON_ZONE_ID, c.getZoneId());
        data.put(CON_ZONE, c.getZone());
        data.put(CONTRACTOR, c.getContractor());
        data.put(CON_OPEN, c.getOpen());
        data.put(CON_COMPLETED, c.getClosed());
        data.put(CON_RSO, c.getRso());
        data.put(CON_ACKNOWLEDGE, c.getAcknowledge());
        data.put(CON_DISPATCHED, c.getDispatched());
        data.put(INSTALLER, c.getInstaller());
        data.put(FLAG, c.getFlag());

        db.insert(CONTRACTOR_TABLE, null, data);
        return true;
    }

    public boolean update(Contractor con)
    {
        db = this.getWritableDatabase();
        ContentValues data = new ContentValues();

        data.put(CON_OPEN, con.getOpen());
        data.put(CON_COMPLETED, con.getClosed());
        data.put(CON_RSO, con.getRso());
        data.put(CON_ACKNOWLEDGE, con.getAcknowledge());
        data.put(CON_DISPATCHED, con.getDispatched());
        data.put(INSTALLER, con.getInstaller());

        db.update(CONTRACTOR_TABLE, data, CONTRACTOR_ID + " = ? AND " + CON_ZONE_ID + " = ?",
                new String[]{String.valueOf(con.getContractorId()), String.valueOf(con.getZoneId())});

        return true;
    }

    public int size() {
        db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, CONTRACTOR_TABLE);

        return numRows;
    }

    public Cursor getContractor(int contractorId, int zoneId)
    {
        db = this.getReadableDatabase();
        onCreate(db);

        cursor = db.rawQuery("SELECT * FROM " + CONTRACTOR_TABLE + " WHERE " + CONTRACTOR_ID + "='" + contractorId + "'" +
                    " AND " + CON_ZONE_ID + "='" + zoneId + "' AND " + USER_ID + " = " + session.getID(), null);

        return cursor;
    }

    public Cursor getContractors(int zone, int flag)
    {
        int id = session.getID();
        db = this.getReadableDatabase();
        onCreate(db);

        cursor = db.rawQuery("SELECT * FROM " + CONTRACTOR_TABLE + " WHERE " + USER_ID + " = " + id + " AND " + CON_ZONE_ID + " = " +
                 zone + " AND " + FLAG + " = " + flag, null);
        return cursor;
    }
}
