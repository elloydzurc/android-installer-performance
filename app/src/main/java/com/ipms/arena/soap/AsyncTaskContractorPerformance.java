package com.ipms.arena.soap;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.widget.Toast;

import com.ipms.arena.activity.R;
import com.ipms.arena.data.ContractorPerformanceDatabase;
import com.ipms.arena.helper.Session;
import com.ipms.arena.model.Contractor;
import com.ipms.arena.model.ContractorPerformance;
import com.ipms.arena.model.Hourly;

import org.ksoap2.serialization.SoapObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Created by t-ebcruz on 3/29/2016.
 */
public class AsyncTaskContractorPerformance extends AsyncTaskSoap
{
    Context context;
    Session session;

    final static String ERROR = "ERROR";
    final static String METHOD = "GET_DATA_MB_XML";
    final static int OPTION_TYPE = 12;

    private static final String oId = "MOFFICEID";
    private static final String cCode = "CONTRACTORCODE";
    private static final String sOrder = "SERVICE_ORDER";
    private static final String sDate = "STATUS_DATE";
    private static final String sTime = "STATUS_TIME";
    private static final String DATE_FORMAT = "hh:mm:ss";

    private Contractor contractor;
    private ContractorPerformance performance;
    private ContractorPerformanceDatabase cDb;
    private int type;

    private SimpleDateFormat date;
    private Calendar calendar;
    private Date time;
    private Hourly hourly;

    private int hour;
    private int time0, time1, time2, time3, time4, time5, time6, time7, time8, time9, time10, time11,
            time12, time13, time14, time15, time16, time17, time18, time19, time20, time21, time22, time23;

    public AsyncTaskContractorPerformance(Context context, Contractor contactor, int type, AsyncResponse delegate)
    {
        super(context);

        this.context = context;
        this.delegate = delegate;

        this.contractor = contactor;
        this.type = type;
        this.session = new Session(context);

        this.calendar = GregorianCalendar.getInstance();
        this.date = new SimpleDateFormat(DATE_FORMAT);

        call();
    }

    public void call()
    {
        List params = new ArrayList();

        params.add(OPTION_TYPE);
        params.add(contractor.getZoneId());
        params.add(contractor.getContractorId());
        params.add("");
        params.add(0);
        params.add(0);
        params.add(0);
        params.add(0);
        params.add(type);
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

        time0 = 0; time1 = 0; time2 = 0; time3 = 0; time4 = 0; time5 = 0; time6 = 0; time7 = 0; time8 = 0; time9 = 0; time10 = 0; time11 = 0;
        time12 = 0; time13 = 0; time14 = 0; time15 = 0; time16 = 0; time17 = 0; time18 = 0; time19 = 0; time20 = 0; time21 = 0; time22 = 0; time23 = 0;

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
                    Log.e(ERROR, " " + e.getMessage());
                }
            }
            else {
                Toast.makeText(context, context.getString(R.string.no_data), Toast.LENGTH_SHORT).show();
            }
        }

        hourly = new Hourly(time0, time1, time2, time3, time4, time5, time6, time7, time8, time9, time10, time11, time12, time13, time14, time15, time16, time17, time18, time19, time20, time21, time22, time23);
        response.add(hourly);
        delegate.finish(response);
    }

    @Override
    protected void onCancelled(SoapObject soapObject)
    {
        time0 = 0; time1 = 0; time2 = 0; time3 = 0; time4 = 0; time5 = 0; time6 = 0; time7 = 0; time8 = 0; time9 = 0; time10 = 0; time11 = 0;
        time12 = 0; time13 = 0; time14 = 0; time15 = 0; time16 = 0; time17 = 0; time18 = 0; time19 = 0; time20 = 0; time21 = 0; time22 = 0; time23 = 0;

        cDb = new ContractorPerformanceDatabase(context);
        Cursor result = cDb.getContractorPerformances(contractor.getZoneId(), contractor.getContractorId(), type);
        List response = new ArrayList();

        Log.d("TEST RESULT OFFLINE", result.toString());

        if(result.getCount() > 0) {
            try {
                while (result.moveToNext())
                {
                    int userId = result.getInt(result.getColumnIndex(ContractorPerformanceDatabase.USER_ID));
                    String serviceOrder = result.getString(result.getColumnIndex(ContractorPerformanceDatabase.SERVICE_ORDER));
                    String statusDate = result.getString(result.getColumnIndex(ContractorPerformanceDatabase.STATUS_DATE));
                    String statusTime = result.getString(result.getColumnIndex(ContractorPerformanceDatabase.STATUS_TIME));

                    performance = new ContractorPerformance(userId, contractor.getZoneId(), contractor.getContractorId(), serviceOrder, statusDate, statusTime, type);
                    response.add(performance);
                    aggregateTime(performance);
                }
            } finally {
                result.close();
            }
        } else {
            Toast.makeText(context, context.getString(R.string.no_connection), Toast.LENGTH_SHORT).show();
        }

        hourly = new Hourly(time0, time1, time2, time3, time4, time5, time6, time7, time8, time9, time10, time11, time12, time13, time14, time15, time16, time17, time18, time19, time20, time21, time22, time23);
        response.add(hourly);
        delegate.finish(response);
    }

    @Override
    protected Object readRow(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, NS, row);

        int zoneId = 0;
        int contractorCode = 0;
        String serviceOrder = "";
        String statusDate = "";
        String statusTime = "";

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
                    case oId:
                        zoneId = Integer.parseInt(read(parser, oId));
                        break;
                    case cCode:
                        contractorCode = Integer.parseInt(read(parser, cCode));
                        break;
                    case sOrder:
                        serviceOrder = read(parser, sOrder);
                        break;
                    case sDate:
                        statusDate = read(parser, sDate);
                        break;
                    case sTime:
                        statusTime = read(parser, sTime);
                        break;
                    default:
                        skip(parser);
                        break;
                }
            }
        }

        performance = new ContractorPerformance(session.getID(), zoneId, contractorCode, serviceOrder, statusDate, statusTime, type);
        aggregateTime(performance);

        return performance;
    }

    private void aggregateTime(ContractorPerformance performance)
    {
        try
        {
            time = date.parse(performance.getStatusTime());
            calendar.setTime(time);
            hour = (calendar.get(Calendar.HOUR_OF_DAY));

            switch(hour)
            {
                case 0: time0++; break;
                case 1: time1++; break;
                case 2: time2++; break;
                case 3: time3++; break;
                case 4: time4++; break;
                case 5: time5++; break;
                case 6: time6++; break;
                case 7: time7++; break;
                case 8: time8++; break;
                case 9: time9++; break;
                case 10: time10++; break;
                case 11: time11++; break;
                case 12: time12++; break;
                case 13: time13++; break;
                case 14: time14++; break;
                case 15: time15++; break;
                case 16: time16++; break;
                case 17: time17++; break;
                case 18: time18++; break;
                case 19: time19++; break;
                case 20: time20++; break;
                case 21: time21++; break;
                case 22: time22++; break;
                case 23: time23++; break;
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}