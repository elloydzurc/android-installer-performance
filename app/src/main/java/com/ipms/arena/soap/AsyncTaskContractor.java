package com.ipms.arena.soap;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.widget.Toast;

import com.ipms.arena.activity.R;
import com.ipms.arena.data.ContractorsDatabase;
import com.ipms.arena.helper.Session;
import com.ipms.arena.model.Contractor;
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
public class AsyncTaskContractor extends AsyncTaskSoap
{
    Context context;
    Session session;

    final static String ERROR = "ERROR";
    final static String METHOD = "GET_DATA_MB_XML";
    final static int OPTION_TYPE = 14;

    private static final String flag = "MEW";
    private static final String zid = "MOFFICEID";
    private static final String cid = "CONTRACTID";
    private static final String cname = "CONTRACTOR";
    private static final String ins = "INSTALLER";

    private static final String open = "OPEN";
    private static final String closed = "CLOSED";
    private static final String rso = "RSO";
    private static final String ack = "ACKNOWLEDGE";
    private static final String dis = "DISPATCHED";

    private Contractor contractor;
    private ContractorsDatabase cDb;
    private Zone zone;
    private Cursor cursor;
    private int type;

    public AsyncTaskContractor(Context context, Zone zone, int type, AsyncResponse delegate)
    {
        super(context);

        this.context = context;
        this.zone = zone;
        this.delegate = delegate;
        this.type = type;

        this.cDb = new ContractorsDatabase(context);
        this.session = new Session(context);

        call();
    }

    public void call()
    {
        List params = new ArrayList();

        params.add(OPTION_TYPE);
        params.add(zone.getZoneId());
        params.add("");
        params.add("");
        params.add(0);
        params.add(0);
        params.add(0);
        params.add(type);
        params.add(0);
        params.add(0);
        params.add(0);
        params.add(session.getUserId());
        params.add(this.generateApplicationKey());

        this.setMethod(METHOD);
        this.setParams(params);

        Log.d("TEST PARAMS", params.toString());
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        delegate.progress();
    }

    @Override
    protected void onPostExecute(SoapObject soapObject)
    {
        super.onPostExecute(soapObject);
        List response = new ArrayList();

        if(soapObject != null) {
            if(soapObject.getPropertyCount() > 0)
            {
                try
                {
                    String xml = soapObject.getProperty(0).toString();
                    Log.d("TEST RESULT", xml);
                    InputStream stream = new ByteArrayInputStream(xml.getBytes(Charset.forName("UTF-8")));
                    response = parse(stream);
                }
                catch (XmlPullParserException|IOException e) {
                    Log.e(ERROR, " " + e.getMessage());
                }
            }
            else {
                Toast.makeText(context, context.getString(R.string.no_data), Toast.LENGTH_LONG).show();
            }
        } else {

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

        cDb = new ContractorsDatabase(context);
        Cursor result = cDb.getContractors(zone.getZoneId(), type);
        List response = new ArrayList();

        Log.d("TEST RESULT OFFLINE", result.toString());

        try
        {
            if(result.getCount() > 0) {
                while(result.moveToNext())
                {
                    int zoneId = result.getInt(result.getColumnIndex(ContractorsDatabase.CON_ZONE_ID));
                    String zoneName = result.getString(result.getColumnIndex(ContractorsDatabase.CON_ZONE));
                    int conId = result.getInt(result.getColumnIndex(ContractorsDatabase.CONTRACTOR_ID));
                    String conName = result.getString(result.getColumnIndex(ContractorsDatabase.CONTRACTOR));
                    int cOpen = result.getInt(result.getColumnIndex(ContractorsDatabase.CON_OPEN));
                    int cClosed = result.getInt(result.getColumnIndex(ContractorsDatabase.CON_COMPLETED));
                    int cRso = result.getInt(result.getColumnIndex(ContractorsDatabase.CON_RSO));
                    int cAck = result.getInt(result.getColumnIndex(ContractorsDatabase.CON_ACKNOWLEDGE));
                    int cDis = result.getInt(result.getColumnIndex(ContractorsDatabase.CON_DISPATCHED));
                    int installer = result.getInt(result.getColumnIndex(ContractorsDatabase.INSTALLER));
                    int flag = result.getInt(result.getColumnIndex(ContractorsDatabase.FLAG));
                    
                    contractor = new Contractor(zoneId, zoneName, conId, conName, cOpen, cClosed, cRso, cAck, cDis, installer, flag);
                    response.add(contractor);
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

        int zoneId = zone.getZoneId();
        String zoneName = zone.getZone();
        int conId = 0;
        String conName = "";
        int cOpen = 0;
        int cClosed = 0;
        int cRso = 0;
        int cAck = 0;
        int cDis = 0;
        int installers = 0;
        int flg = 0;

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
                    case cid:
                        conId = Integer.parseInt(read(parser, cid));
                        break;
                    case cname:
                        conName = read(parser, cname);
                        break;
                    case open:
                        cOpen = Integer.parseInt(read(parser, open));
                        break;
                    case closed:
                        cClosed = Integer.parseInt(read(parser, closed));
                        break;
                    case rso:
                        cRso = Integer.parseInt(read(parser, rso));
                        break;
                    case ack:
                        cAck = Integer.parseInt(read(parser, ack));
                        break;
                    case dis:
                        cDis = Integer.parseInt(read(parser, dis));
                        break;
                    case ins:
                        installers = Integer.parseInt(read(parser, ins));
                        break;
                    case flag:
                        flg = Integer.parseInt(read(parser, flag));
                        break;
                    default:
                        skip(parser);
                        break;
                }
            }
        }

        contractor = new Contractor(zoneId, zoneName, conId, conName, cOpen, cClosed, cRso, cAck, cDis, installers, flg);
        cursor = cDb.getContractor(conId, zoneId);

        if(cursor.getCount() > 0) {
            cDb.update(contractor);
        } else {
            cDb.insert(contractor);
        }

        return contractor;
    }
}