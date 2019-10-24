package com.ipms.arena.soap;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.widget.Toast;

import com.ipms.arena.activity.R;
import com.ipms.arena.data.InstallerLocationDatabase;
import com.ipms.arena.helper.Session;
import com.ipms.arena.model.Installer;
import com.ipms.arena.model.InstallerLocation;

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
public class AsyncTaskInstallerLocation extends AsyncTaskSoap
{
    Context context;
    Session session;

    final static String ERROR = "ERROR";
    final static String METHOD = "GET_DATA_MB_XML";
    final static int OPTION_TYPE = 7;

    private static final String SERVICE_ORDER = "SERVICE_ORDER";
    private static final String PROJECT_ID = "PROJECT_ID";
    private static final String STATUS = "STATUS";
    private static final String SERVICE_TYPE = "SERVICE_TYPE";
    private static final String ORDER_TYPE = "ORDER_TYPE";
    private static final String NE_TYPE = "NE_TYPE";
    private static final String ADDRESS = "ADDRESS";
    private static final String ADDRESS2 = "ADDRESS2";

    private Installer installer;
    private InstallerLocation location;
    private InstallerLocationDatabase lDb;
    private int type;
    private Cursor cursor;

    public AsyncTaskInstallerLocation(Context context, Installer installer, int type, AsyncResponse delegate)
    {
        super(context);

        this.context = context;
        this.delegate = delegate;

        this.installer = installer;
        this.type = type;
        this.session = new Session(context);
        this.lDb = new InstallerLocationDatabase(context);

        call();
    }

    public void call()
    {
        List params = new ArrayList();

        params.add(OPTION_TYPE);
        params.add(installer.getZoneId());
        params.add("");
        params.add(installer.getKey());
        params.add(0);
        params.add(0);
        params.add(installer.getContractorId() > 0 ? 1 : 2);
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
    protected void onPostExecute(SoapObject soapObject)
    {
        super.onPostExecute(soapObject);
        List response = new ArrayList();

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
            Toast.makeText(context, context.getString(R.string.no_data), Toast.LENGTH_SHORT).show();
        }

        if(cursor != null) {
            cursor.close();
        }

        delegate.finish(response);
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();

        Cursor result = lDb.getInstallerLocation(installer);
        List response = new ArrayList();

        Log.d("TEST RESULT OFFLINE", result.toString());

        try {
            if (result.getCount() > 0) {
                while (result.moveToNext())
                {
                    String so =  result.getString(result.getColumnIndex(InstallerLocationDatabase.SERVICE_ORDER));
                    String pid = result.getString(result.getColumnIndex(InstallerLocationDatabase.PROJECT_ID));
                    String stat = result.getString(result.getColumnIndex(InstallerLocationDatabase.STATUS));
                    String stype = result.getString(result.getColumnIndex(InstallerLocationDatabase.SERVICE_TYPE));
                    String otype = result.getString(result.getColumnIndex(InstallerLocationDatabase.ORDER_TYPE));
                    String ntype = result.getString(result.getColumnIndex(InstallerLocationDatabase.NE_TYPE));
                    String address = result.getString(result.getColumnIndex(InstallerLocationDatabase.ADDRESS));
                    String address2 = result.getString(result.getColumnIndex(InstallerLocationDatabase.ADDRESS2));

                    location = new InstallerLocation(installer, so, pid, stat, stype, otype, ntype, address, address2);
                    response.add(location);
                }
            } else {
                Toast.makeText(context, context.getString(R.string.no_connection), Toast.LENGTH_SHORT).show();
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

        String so = "", pid = "", status = "", stype = "", otype = "", ntype = "", address = "", address2 = "";


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
                    case SERVICE_ORDER:
                        so = read(parser, SERVICE_ORDER);
                        break;
                    case PROJECT_ID:
                        pid = read(parser, PROJECT_ID);
                        break;
                    case STATUS:
                        status = read(parser, STATUS);
                        break;
                    case SERVICE_TYPE:
                        stype = read(parser, SERVICE_TYPE);
                        break;
                    case ORDER_TYPE:
                        otype = read(parser, ORDER_TYPE);
                        break;
                    case NE_TYPE:
                        ntype = read(parser, NE_TYPE);
                        break;
                    case ADDRESS:
                        address = read(parser, ADDRESS);
                        break;
                    case ADDRESS2:
                        address2 = read(parser, ADDRESS2);
                        break;
                    default:
                        skip(parser);
                        break;
                }
            }
        }

        location = new InstallerLocation(installer, so, pid, status, stype, otype, ntype, address, address2);
        cursor = lDb.getInstallerLocation(location);

        if(cursor.getCount() > 0) {
            lDb.update(location);
        } else {
            lDb.insert(location);
        }

        return location;
    }
}