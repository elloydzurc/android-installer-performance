package com.ipms.arena.soap;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Xml;

import com.ipms.arena.activity.Config;
import com.ipms.arena.activity.R;
import com.ipms.arena.helper.Connection;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * Created by t-ebcruz on 3/9/2016.
 */
public abstract class AsyncTaskSoap extends AsyncTask<Void, Void, SoapObject>
{
    private static final String FORMAT = "yyyy-MM-dd";
    public final static String NAMESPACE = "http://tempuri.org/";
    public final static String PING_URL = "http://google.com";

    private List params;
    private String method;
    private SoapObject response;

    protected static final String NS = null;
    protected static final String dataSet = "NewDataSet";
    protected static final String row = "dt";

    private Context context;
    private Connection conn;
    public AsyncResponse delegate;

    public AsyncTaskSoap(Context context) {
        this.context = context;
        this.conn = new Connection(context);
        disableSSLCertificateChecking();
    }

    public interface AsyncResponse {
        void progress();
        void finish(List result);
    }

    @Override
    protected SoapObject doInBackground(Void... v)
    {
        SoapObject soapObject = new SoapObject();
        String[] setOfParams = getSetOfParams(method);;
        int index = 0;

        SoapObject request = new SoapObject(NAMESPACE, method);
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);

        SoapObject request2 = new SoapObject(NAMESPACE, method);
        SoapSerializationEnvelope envelope2 = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope2.dotNet = true;
        envelope2.setOutputSoapObject(request2);

        if(conn.isAvailable())
        {
            try
            {
                for(String param : setOfParams) {
                    request.addProperty(param, params.get(index));
                    index++;
                }

                HttpTransportSE androidHttpTransport = new HttpTransportSE(Config.WS, Config.TIMEOUT);
                androidHttpTransport.debug = true;
                androidHttpTransport.call(NAMESPACE + method, envelope);
                Object eBody = envelope.bodyIn;

                if (eBody instanceof SoapObject) {
                    soapObject = (SoapObject) eBody;
                }

                return soapObject;
            } catch (IOException|XmlPullParserException e) {
                cancel(true);
                return null;
            }
        } else {
            cancel(true);
            return null;
        }
    }

    private static void disableSSLCertificateChecking() {
        TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            @Override
            public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
                // Not implemented
            }

            @Override
            public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
                // Not implemented
            }
        } };

        try {
            SSLContext sc = SSLContext.getInstance("TLS");

            sc.init(null, trustAllCerts, new java.security.SecureRandom());

            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    /*** XML Parser Helper ***/

    public List parse(InputStream in) throws XmlPullParserException, IOException
    {
        try
        {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            return readResponse(parser);
        } finally {
            in.close();
        }
    }

    private List readResponse(XmlPullParser parser) throws XmlPullParserException, IOException
    {
        List entries = new ArrayList();
        parser.require(XmlPullParser.START_TAG, NS, dataSet);

        while (parser.next() != XmlPullParser.END_TAG)
        {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if(!name.equals("null"))
            {
                if (name.equals(row)) {
                    entries.add(readRow(parser));
                } else {
                    skip(parser);
                }
            }
        }
        return entries;
    }

    protected abstract Object readRow(XmlPullParser parser) throws XmlPullParserException, IOException;

    protected String read(XmlPullParser parser, String tag) throws IOException, XmlPullParserException
    {
        parser.require(XmlPullParser.START_TAG, NS, tag);
        String value = readText(parser);
        parser.require(XmlPullParser.END_TAG, NS, tag);
        return value;
    }

    protected String readText(XmlPullParser parser) throws IOException, XmlPullParserException
    {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }

    protected void skip(XmlPullParser parser) throws XmlPullParserException, IOException
    {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }

        int depth = 1;
        while (depth != 0)
        {
            switch (parser.next())
            {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }

    public String generateApplicationKey()
    {
        SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat(FORMAT);
        Date localDate = new Date();
        String token = String.valueOf(Hex.encodeHex(DigestUtils.sha(Config.WS_KEYWORD + localSimpleDateFormat.format(localDate))));

        return token;
    }

    private String[] getSetOfParams(String method)
    {
        String[] params = {};
        switch(method)
        {
            case "Logon":
                params = context.getResources().getStringArray(R.array.Logon_Params);
                break;
            case "GET_DATA_MB_XML":
                params = context.getResources().getStringArray(R.array.MB_Base_Params);
                break;
        }
        return params;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public void setParams(List params) {
        this.params = params;
    }

    public void setResponse(SoapObject response) {
        this.response = response;
    }

    public SoapObject getResponse() {
        return response;
    }
}
