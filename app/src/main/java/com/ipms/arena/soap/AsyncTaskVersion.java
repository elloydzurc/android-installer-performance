package com.ipms.arena.soap;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.ipms.arena.activity.R;
import com.ipms.arena.model.LoginResponse;

import org.ksoap2.serialization.SoapObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by t-ebcruz on 9/9/2016.
 */
public class AsyncTaskVersion extends AsyncTaskSoap
{
    private static final String ERROR = "ERROR";

    private static final String METHOD = "GET_DATA_MB_XML";
    private static final String version = "VERSION_NO";
    final static int OPTION_TYPE = 16;

    private Context context;
    private SoapObject soapObject;

    public AsyncTaskVersion(Context context, AsyncResponse delegate)
    {
        super(context);
        this.context = context;
        this.delegate = delegate;

        call();
    }

    public void call()
    {
        List params = new ArrayList();

        params.add(OPTION_TYPE);
        params.add("");
        params.add("");
        params.add("");
        params.add(0);
        params.add(0);
        params.add(0);
        params.add(0);
        params.add(0);
        params.add(0);
        params.add(0);
        params.add(0);
        params.add(this.generateApplicationKey());

        this.setMethod(METHOD);
        this.setParams(params);
    }

    @Override
    protected void onPreExecute()
    {
        super.onPreExecute();
        delegate.progress();
    }

    @Override
    protected void onPostExecute(SoapObject result)
    {
        super.onPostExecute(soapObject);
        List response = new ArrayList();
        soapObject = result;

        if(soapObject != null)
        {
            if(soapObject.getPropertyCount() > 0)
            {
                try
                {
                    String xml = soapObject.getProperty(0).toString();
                    InputStream stream = new ByteArrayInputStream(xml.getBytes(Charset.forName("UTF-8")));
                    response = parse(stream);
                } catch (XmlPullParserException|IOException e) {
                    Log.e(ERROR, e.getMessage());
                }
            } else {
                Toast.makeText(context, context.getString(R.string.error_incorrect_password), Toast.LENGTH_LONG).show();
            }
        }
        delegate.finish(response);
    }

    @Override
    protected void onCancelled(SoapObject soapObject) {
        super.onCancelled(soapObject);
        delegate.finish(null);
    }

    @Override
    protected Object readRow(XmlPullParser parser) throws XmlPullParserException, IOException
    {
        parser.require(XmlPullParser.START_TAG, NS, row);
        double ver = 0;

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
                    case version:
                        ver = Double.parseDouble(read(parser, version));
                    break;
                    default:
                        skip(parser);
                        break;
                }
            }
        }
        return ver;
    }
}