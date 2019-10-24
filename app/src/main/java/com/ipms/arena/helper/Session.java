package com.ipms.arena.helper;

import android.content.Context;
import android.content.SharedPreferences;

import com.ipms.arena.activity.Login;

/**
 * Created by t-ebcruz on 3/16/2016.
 */
public class Session {

    Context context;
    SharedPreferences prefs;

    public Session(Context context) {
        this.context = context;
        prefs = context.getSharedPreferences(Login.IPMS_PREFERENCES, context.MODE_PRIVATE);
    }

    public String getUser() {
        return prefs.getString(Login.NT_ACCOUNT, null);
    }

    public int getID() {
        return prefs.getInt(Login.ID, 0);
    }

    public String getUserId() {
        return getID() + "|" + getUser();
    }
}
