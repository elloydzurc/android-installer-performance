package com.ipms.arena.listener;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import com.ipms.arena.fragment.InstallerList;
import com.ipms.arena.fragment.Performance;
import com.ipms.arena.model.Contractor;

/**
 * Created by t-ebcruz on 4/5/2016.
 */
public class ContractorListener implements View.OnClickListener
{
    private FragmentActivity activity;
    private String type;

    final static String CHART = "chart";
    final static String LIST = "list";

    final static String PANEL = "mapChartPanel";
    final static String MAIN = "mainFragment";
    final static String CONTRACTOR = "contractor";

    public ContractorListener(Context activity, String type)
    {
        this.activity = (FragmentActivity) activity;
        this.type = type;
    }

    @Override
    public void onClick(View v)
    {
        Bundle tag = (Bundle) v.getTag();
        int container = tag.getInt(PANEL);
        Contractor contractor = tag.getParcelable(CONTRACTOR);

        FragmentManager fm = activity.getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        Fragment currentFragment =  fm.findFragmentByTag(CHART + container);
        Fragment newFragment;
        String fragmentTag;

        if(type.equals(LIST))
        {
            newFragment = new InstallerList();
            newFragment.setArguments(tag);
            fragmentTag = LIST + container;

            ft.hide(fm.findFragmentByTag(MAIN));
            ft.add(android.R.id.content, newFragment, fragmentTag).addToBackStack(fragmentTag);
            ft.commit();
        }
        else if(type.equals(CHART))
        {
            if(!(currentFragment instanceof Performance) || !contractor.isShowMapChart())
            {
                newFragment = new Performance(activity);
                newFragment.setArguments(tag);
                fragmentTag = CHART + container;

                ft.add(container, newFragment, fragmentTag);
            }

            if(currentFragment != null || contractor.isShowMapChart()) {
                ft.remove(currentFragment);
            }

            ft.commit();
        }
    }
}