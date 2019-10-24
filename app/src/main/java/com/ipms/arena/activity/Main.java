package com.ipms.arena.activity;

import android.app.Dialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.TextView;

import com.ipms.arena.fragment.ZoneList;
import com.ipms.arena.receiver.NetworkState;
import com.ipms.arena.receiver.SessionState;

public class Main extends AppCompatActivity implements NetworkState.NetworkStateReceiverListener {

    private SessionState reciever;

    private FragmentManager fm;
    private FragmentTransaction ft;

    private Fragment list;
    private ActionBar actionBar;

    private static String TAG = "mainFragment";
    private NetworkState networkStateReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setTitle(getString(R.string.app_main_title));
        actionBar.setSubtitle(getString(R.string.app_name));

        fm = getSupportFragmentManager();
        ft = fm.beginTransaction();

        fm.addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                int count = fm.getBackStackEntryCount();
                boolean show = count > 1;
                actionBar.setDisplayHomeAsUpEnabled(show);
            }
        });

        list = new ZoneList(this);
        ft.add(android.R.id.content, list, TAG);

        ft.addToBackStack(null);
        ft.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();
        switch(id)
        {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_logout:
                onLogout();
                return true;
            case R.id.action_about:
                showAbout();
                return true;
            case R.id.action_settings:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        reciever = new SessionState();
        IntentFilter intentFilter = new IntentFilter();

        networkStateReceiver = new NetworkState();
        networkStateReceiver.addListener(this);
        registerReceiver(networkStateReceiver, new IntentFilter(android.net.ConnectivityManager.CONNECTIVITY_ACTION));

        intentFilter.addAction(getString(R.string.receiver_logout));
        registerReceiver(reciever, intentFilter);
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        if(reciever != null) {
            unregisterReceiver(reciever);
            reciever = null;
        }

        if(networkStateReceiver != null) {
            unregisterReceiver(networkStateReceiver);
            networkStateReceiver = null;
        }
    }

    @Override
    public void onBackPressed()
    {
        if(fm.getBackStackEntryCount() != 1) {
            super.onBackPressed();
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        fm.getFragment(savedInstanceState, "mContent");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        fm.putFragment(outState, "mContent", list);
        super.onSaveInstanceState(outState);
    }

    public void onLogout()
    {
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(getString(R.string.receiver_logout));
        sendBroadcast(broadcastIntent);
    }

    public void showAbout()
    {
        Dialog dialog = new Dialog(this, R.style.versionDialog);

        dialog.setContentView(R.layout.version);
        TextView v = (TextView) dialog.findViewById(R.id.version);
        v.append("" + BuildConfig.VERSION_NAME);

        dialog.show();
    }

    @Override
    public void networkAvailable() {
        actionBar.setTitle(getString(R.string.app_main_title));
    }

    @Override
    public void networkUnavailable() {
        actionBar.setTitle(getString(R.string.app_main_title_offline));
    }
}