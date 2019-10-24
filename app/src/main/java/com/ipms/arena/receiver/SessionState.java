package com.ipms.arena.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.ipms.arena.activity.Login;
import com.ipms.arena.activity.R;

/**
 * Created by t-ebcruz on 3/7/2016.
 */
public class SessionState extends BroadcastReceiver
{

    SharedPreferences settings;
    SharedPreferences.Editor editor;

    @Override
    public void onReceive(Context context, Intent intent)
    {
        String action = intent.getAction();

        if(action.equals(context.getString(R.string.receiver_logout)))
        {
            settings = context.getSharedPreferences(Login.IPMS_PREFERENCES, context.MODE_PRIVATE);
            editor = settings.edit();
            editor.clear();
            editor.commit();

            intent.setClass(context, Login.class);
            intent.setFlags(intent.FLAG_ACTIVITY_CLEAR_TASK | intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }
}
