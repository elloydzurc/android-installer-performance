package com.ipms.arena.listener;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import com.ipms.arena.fragment.LocationMap;
import com.ipms.arena.fragment.Performance;
import com.ipms.arena.model.Installer;

/**
 * Created by t-ebcruz on 4/5/2016.
 */
public class InstallerListener implements View.OnClickListener
{
    private FragmentActivity activity;
    private String type;

    final static String MAP = "map";
    final static String CHART = "chart";
    final static String PANEL = "mapChartPanel";
    final static String INSTALLER = "installer";

    public InstallerListener(Context activity, String type)
    {
        this.activity = (FragmentActivity) activity;
        this.type = type;
    }

    @Override
    public void onClick(View v)
    {
        Bundle tag = (Bundle) v.getTag();
        int container = tag.getInt(PANEL);
        Installer installer = tag.getParcelable(INSTALLER);

        FragmentManager fm = activity.getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        Fragment currentFragment = fm.findFragmentById(container);
        Fragment newFragment;

        String fragmentTag;
        boolean fragmentPop;

        if(type.equals(CHART)) {
            if(!(currentFragment instanceof Performance) || !installer.isShowChart())
            {
                newFragment = new Performance(activity);
                newFragment.setArguments(tag);
                fragmentTag = newFragment.getClass().getName() + container;

                fragmentPop = fm.popBackStackImmediate(fragmentTag, 0);
                //if(!fragmentPop && fm.findFragmentByTag(fragmentTag) == null) {
                    ft.replace(container, newFragment, fragmentTag);
                    ft.commit();
                //}
            } else if(currentFragment != null || installer.isShowChart()) {
                ft.remove(currentFragment).commit();
            }
        } else if(type.equals(MAP)) {
            if(!(currentFragment instanceof LocationMap) || !installer.isShowMap())
            {
                newFragment = new LocationMap();
                newFragment.setArguments(tag);
                fragmentTag = newFragment.getClass().getName() + container;

                fragmentPop = fm.popBackStackImmediate(fragmentTag, 0);
                //if(!fragmentPop && fm.findFragmentByTag(fragmentTag) == null) {
                    ft.replace(container, newFragment, fragmentTag);
                    ft.commit();
                //}
            } else if(currentFragment != null || installer.isShowMap()) {
                ft.remove(currentFragment).commit();
            }
        }
    }
}