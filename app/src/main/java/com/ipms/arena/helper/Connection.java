package com.ipms.arena.helper;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.ipms.arena.soap.AsyncTaskSoap;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by t-ebcruz on 2/24/2016.
 */
public class Connection
{
    private Context context;

    public Connection(Context context) {
        this.context = context;
    }

    public boolean isAvailable()
    {
        boolean hasWifi;
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        hasWifi = activeNetworkInfo != null && activeNetworkInfo.isConnected();

        if(hasWifi) {
            hasWifi = hasInternetAccess();
        }

        return hasWifi;
    }

    public boolean hasInternetAccess()
    {
        HttpURLConnection urlc = null;
        try {
            urlc = (HttpURLConnection) (new URL(AsyncTaskSoap.PING_URL).openConnection());
            urlc.setRequestProperty("User-Agent", "Android");
            urlc.setRequestProperty("Connection", "close");
            urlc.setConnectTimeout(com.ipms.arena.activity.Config.TIMEOUT);
            urlc.connect();
            return (urlc.getResponseCode() == 200);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
