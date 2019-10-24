package com.ipms.arena.soap;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.ipms.arena.activity.R;
import com.ipms.arena.data.InstallerDatabase;
import com.ipms.arena.helper.Session;
import com.ipms.arena.model.Contractor;
import com.ipms.arena.model.Installer;
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
public class AsyncTaskInstaller extends AsyncTaskSoap
{
    Context context;
    Bundle tag;
    Session session;

    final static String ERROR = "ERROR";
    final static String METHOD = "GET_DATA_MB_XML";
    final static int OPTION_TYPE = 9;

    private static final String TECH_KEY = "TECHKEY";
    private static final String TECH_ID = "TECHID";
    private static final String TECH_NAME = "INSTALLER_NAME";

    private final static String CONTRACTOR = "contractor";
    private final static String ZONE = "zone";

    private static final String open = "OPEN";
    private static final String closed = "CLOSED";
    private static final String rso = "RSO";
    private static final String ack = "ACKNOWLEDGE";
    private static final String dis = "DISPATCHED";
    private static final int itemPerPage = 10;

    private int page;
    private int zone;
    private int contractor_id = 0;
    private int flag = 2;

    private Contractor contractor;
    private Installer installer;

    private InstallerDatabase iDb;
    private Cursor cursor;

    public AsyncTaskInstaller(Context context, Bundle tag, int page, AsyncResponse delegate)
    {
        super(context);
        this.context = context;

        this.tag = tag;
        this.page = page;
        this.delegate = delegate;
        this.session = new Session(context);

        this.iDb = new InstallerDatabase(context);
        call();
    }

    @Override
    protected void onPreExecute() {
        delegate.progress();
        super.onPreExecute();
    }

    public void call()
    {
        List params = new ArrayList();

        if(tag.getParcelable(CONTRACTOR) != null)
        {
            contractor = tag.getParcelable(CONTRACTOR);
            contractor_id = contractor.getContractorId();
            zone = contractor.getZoneId();
            flag = contractor.getFlag();
        }
        else {
            zone = ((Zone) tag.getParcelable(ZONE)).getZoneId();
        }

        params.add(OPTION_TYPE);
        params.add(zone);
        params.add(contractor_id);
        params.add(0);
        params.add(0);
        params.add(page);
        params.add(0);
        params.add(flag);
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
    protected void onPostExecute(SoapObject soapObject)
    {
        super.onPostExecute(soapObject);
        List response = new ArrayList();

        if(soapObject != null)
        {
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
                    Log.e(ERROR, e.getMessage());
                }
            }
            else {
                Toast.makeText(context, context.getString(R.string.no_data), Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(context, context.getString(R.string.no_connection), Toast.LENGTH_LONG).show();
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

        iDb = new InstallerDatabase(context);
        Cursor result = iDb.getInstallers(page, itemPerPage, tag);
        List response = new ArrayList();

        Log.d("TEST RESULT OFFLINE", result.toString());

        if(result.getCount() > 0) {
            try {
                while (result.moveToNext())
                {
                    int zone = result.getInt(result.getColumnIndex(InstallerDatabase.INSTALLER_ZONE));
                    int contractor_id = result.getInt(result.getColumnIndex(InstallerDatabase.INSTALLER_CONTRACTOR));
                    int techKey = result.getInt(result.getColumnIndex(InstallerDatabase.INSTALLER_KEY));
                    String techId = result.getString(result.getColumnIndex(InstallerDatabase.INSTALLER_ID));
                    String techName = result.getString(result.getColumnIndex(InstallerDatabase.INSTALLER_NAME));
                    int cOpen = result.getInt(result.getColumnIndex(InstallerDatabase.INSTALLER_OPEN));
                    int cClosed = result.getInt(result.getColumnIndex(InstallerDatabase.INSTALLER_CLOSED));
                    int cRso = result.getInt(result.getColumnIndex(InstallerDatabase.INSTALLER_RSO));
                    int cAck = result.getInt(result.getColumnIndex(InstallerDatabase.INSTALLER_ACKNOWLEDGE));
                    int cDis = result.getInt(result.getColumnIndex(InstallerDatabase.INSTALLER_DISPATCHED));

                    installer = new Installer(zone, contractor_id, techKey, techId, techName, cOpen, cClosed, cRso, cAck, cDis);
                    response.add(installer);
                }
            } finally {
                result.close();
            }
        }

        delegate.finish(response);
    }

    @Override
    protected Object readRow(XmlPullParser parser) throws XmlPullParserException, IOException
    {
        parser.require(XmlPullParser.START_TAG, NS, row);

        int techKey = 0;
        String techId = null;
        String techName = null;
        int cOpen = 0;
        int cClosed = 0;
        int cRso = 0;
        int cAck = 0;
        int cDis = 0;

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
                    case TECH_KEY:
                        techKey = Integer.parseInt(read(parser, TECH_KEY));
                    break;
                    case TECH_NAME:
                        techName = read(parser, TECH_NAME);
                    break;
                    case TECH_ID:
                        techId = read(parser, TECH_ID);
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
                    default:
                        skip(parser);
                    break;
                }
            }
        }

        installer = new Installer(zone, contractor_id, techKey, techId, techName, cOpen, cClosed, cRso, cAck, cDis);
        cursor = iDb.getInstaller(zone, techKey);

        if(cursor.getCount() > 0) {
            iDb.update(installer);
        } else {
            iDb.insert(installer);
        }

        return installer;
    }
}