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
 * Created by t-ebcruz on 3/22/2016.
 */
public class AsyncTaskLogin extends AsyncTaskSoap
{
    private static final String ERROR = "ERROR";
    private static final String CHARSET = "UTF-8";

    private static final String METHOD = "Logon";
    private static final String uid = "USERID";
    private static final String uname = "USERNAME";
    private static final String error = "RETMSG";

    private Context context;
    private SoapObject soapObject;

    private String username;
    private String password;
    private boolean isNT;

    public AsyncTaskLogin(Context context, String username, String password, boolean isNt, AsyncResponse delegate)
    {
        super(context);
        this.context = context;
        this.delegate = delegate;

        this.username = username;
        this.password = password;
        this.isNT = isNt;

        call();
    }

  public void call()
  {
    ArrayList<String> params = new ArrayList<>();

    String domain = isNT ? "PLDT-DOMAIN" : " ";
    String nt = isNT ? "1" : "0";

    params.add(username);
    params.add(password);
    params.add(nt);
    params.add(domain);
    params.add(generateApplicationKey());

    setMethod(METHOD);
    setParams(params);
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
                  InputStream stream = new ByteArrayInputStream(xml.getBytes(Charset.forName(CHARSET)));
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

        int id = 0;
        String username = null;
        String err = null;

        while (parser.next() != XmlPullParser.END_TAG)
        {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
              continue;
            }

            String name = parser.getName();
            if(!name.equals("null"))
            {
                if (name.equals(uid)) {
                    id = Integer.parseInt(read(parser, uid));
                } else if (name.equals(uname)) {
                    username = read(parser, uname);
                } else if (name.equals(error)) {
                    err = read(parser, error);
                } else {
                    skip(parser);
                }
            }
        }
        return new LoginResponse(id, username, err);
    }
}