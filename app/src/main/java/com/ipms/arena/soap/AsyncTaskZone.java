package com.ipms.arena.soap;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.widget.Toast;

import com.ipms.arena.activity.R;
import com.ipms.arena.data.ZoneDatabase;
import com.ipms.arena.helper.Session;
import com.ipms.arena.model.Zone;

import org.ksoap2.serialization.SoapObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by t-ebcruz on 3/29/2016.
 */
public class AsyncTaskZone extends AsyncTaskSoap
{
    Context context;
    Session session;

    final static String ERROR = "ERROR";
    final static String METHOD = "GET_DATA_MB_XML";
    final static int OPTION_TYPE = 13;

    private static final String zid = "MOFFICEID";
    private static final String zname = "MOFFICENAME";
    private static final String count = "CONTRACTORS";

    private ZoneDatabase zDb;
    private Zone zone;

    private Cursor cursor;
    private int type;

    public AsyncTaskZone(Context context, int type, AsyncResponse delegate)
    {
        super(context);

        this.context = context;
        this.delegate = delegate;

        this.zDb = new ZoneDatabase(context);
        this.session = new Session(context);
        this.type = type;

        call();
    }

    public void call()
    {
        List params = new ArrayList();

        params.add(OPTION_TYPE);
        params.add("");
        params.add("");
        params.add("");
        params.add(session.getID());
        params.add(0);
        params.add(0);
        params.add(type);
        params.add(0);
        params.add(0);
        params.add(0);
        params.add(session.getUserId());
        params.add(generateApplicationKey());

        this.setMethod(METHOD);
        this.setParams(params);

        Log.d("TEST PARAMS", params.toString());
    }

    @Override
    protected void onPreExecute() {
        delegate.progress();
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(SoapObject soapObject)
    {
        super.onPostExecute(soapObject);
        List response = new ArrayList();

        if (soapObject != null) {
            if(soapObject.getPropertyCount() > 0)
            {
                try
                {
                    String xml = soapObject.getProperty(0).toString();
                    Log.d("TEST RESULT", xml);
                    InputStream stream = new ByteArrayInputStream(xml.getBytes(Charset.forName("UTF-8")));
                    response = this.parse(stream);
                }
                catch (XmlPullParserException|IOException e) {
                    Log.e(ERROR, " " + e.getMessage());
                }
            }
            else {
                Toast.makeText(context, context.getString(R.string.no_data), Toast.LENGTH_LONG).show();
            }
        }

        if(cursor != null) {
            cursor.close();
        }

        delegate.finish(response);
    }

    @Override
    protected void onCancelled(SoapObject soapObject)
    {
        super.onCancelled(soapObject);

        zDb = new ZoneDatabase(context);
        Cursor result = zDb.getZones();
        List response = new ArrayList();

        Log.d("TEST RESULT OFFLINE", result.toString());

        try {
            if(result.getCount() > 0)
            {
                while (result.moveToNext())
                {
                    int zid = result.getInt(result.getColumnIndex(ZoneDatabase.ZONE_ID));
                    int count = result.getInt(result.getColumnIndex(ZoneDatabase.CONTRACTORS));
                    int organic = result.getInt(result.getColumnIndex(ZoneDatabase.ORGANIC));
                    String zoneName = result.getString(result.getColumnIndex(ZoneDatabase.ZONE_NAME));

                    zone = new Zone(zid, zoneName, count, organic);
                    response.add(zone);
                }
            }
        } finally {
            result.close();
        }

        delegate.finish(response);
    }

    @Override
    protected Object readRow(XmlPullParser parser) throws XmlPullParserException, IOException
    {
        parser.require(XmlPullParser.START_TAG, NS, row);

        int zoneId = 0;
        int totalContractors = 0;
        String zoneName = "";

        while (parser.next() != XmlPullParser.END_TAG)
        {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            String name = parser.getName();
            if(!name.equals("null"))
            {
                switch(name)
                {
                    case zid:
                        zoneId = Integer.parseInt(read(parser, zid));
                        break;
                    case zname:
                        zoneName = read(parser, zname);
                        break;
                    case count:
                        totalContractors = Integer.parseInt(read(parser, count));
                        break;
                    default:
                        skip(parser);
                        break;
                }
            }
        }

        zone = new Zone(zoneId, zoneName, totalContractors);
        cursor = zDb.getZone(zoneId);

        if(cursor.getCount() < 1 && type == 1) {
            zDb.insert(zone);
        } else {
            zDb.updateZone(type, zone);
        }

        return zone;
    }
}