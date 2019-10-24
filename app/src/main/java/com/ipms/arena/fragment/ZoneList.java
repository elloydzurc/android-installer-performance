package com.ipms.arena.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.ipms.arena.activity.R;
import com.ipms.arena.adapter.ZoneListAdapter;
import com.ipms.arena.model.Zone;
import com.ipms.arena.soap.AsyncTaskSoap;
import com.ipms.arena.soap.AsyncTaskZone;

import java.util.List;

/**
 * Created by t-ebcruz on 5/20/2016.
 */
public class ZoneList extends Fragment {

    private ZoneList me;
    private Context context;
    private MenuItem refresh;

    private View view;
    private ProgressDialog progress;
    private ExpandableListView listView;

    private AsyncTask zoneTask;
    private AsyncTask organicTask;
    private ZoneListAdapter listAdapter;

    private Zone zone;
    private int task = 0, taskDone = 0;
    private List zones;
    private List organic;

    public ZoneList() { }

    public ZoneList(Context context) {
        this.context = context;
        me = this;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        listAdapter = new ZoneListAdapter(context);
        progress = ProgressDialog.show(context, null, context.getString(R.string.async_status), true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);
        setHasOptionsMenu(true);

        view = inflater.inflate(R.layout.content_zone, container, false);
        listView = (ExpandableListView) view.findViewById(R.id.zoneList);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        getZones(false);
        getZoneOrganic(false);

        if((zoneTask.isCancelled() || organicTask.isCancelled()) && progress.isShowing() && progress != null) {
            progress.dismiss();
        }

        super.onActivityCreated(savedInstanceState);
    }

    public void getZones(final boolean isRefresh)
    {
        task++;
        zoneTask = new AsyncTaskZone(context, 1, new AsyncTaskSoap.AsyncResponse()
        {
            @Override
            public void progress() {
                if(isRefresh) {
                    refresh.setActionView(R.layout.actionbar_progress);
                }
            }

            @Override
            public void finish(final List result)
            {
                taskDone++;
                me.zones = result;
                if(task == taskDone)
                {
                    if(isRefresh) {
                        listAdapter.clear();
                    }

                    for(Object row : me.zones)
                    {
                        zone = (Zone) row;
                        if(organic.indexOf(zone) >= 0) {
                            zone.setOrganic(((Zone) organic.get(organic.indexOf(zone))).getContractors());
                        }
                        listAdapter.addHeader(zone);
                    }

                    if(listView.getAdapter() == null) {
                        if(progress != null && progress.isShowing()) {
                            progress.dismiss();
                            progress = null;
                        }
                        listView.setAdapter(listAdapter);
                    }

                    if(isRefresh) {
                        listView.setAdapter(listAdapter);
                        refresh.setActionView(null);
                    }
                }
            }
        }).execute();
    }

    public void getZoneOrganic(final boolean isRefresh)
    {
        task++;
        organicTask = new AsyncTaskZone(context, 2, new AsyncTaskSoap.AsyncResponse()
        {
            @Override
            public void progress() {}

            @Override
            public void finish(List result)
            {
                taskDone++;
                me.organic = result;
                if(task == taskDone)
                {
                    if(isRefresh) {
                        listAdapter.clear();
                    }

                    for(Object row : me.zones)
                    {
                        zone = (Zone) row;
                        if(organic.indexOf(zone) >= 0) {
                            Zone z = (Zone) organic.get(organic.indexOf(zone));
                            int count = z.getOrganic() > 0 ? z.getOrganic() : z.getContractors();
                            zone.setOrganic(count);
                        }

                        listAdapter.addHeader(zone);
                    }

                    if(listView.getAdapter() == null) {
                        if(progress != null && progress.isShowing()) {
                            progress.dismiss();
                            progress = null;
                        }
                        listView.setAdapter(listAdapter);
                    }

                    if(isRefresh) {
                        listView.setAdapter(listAdapter);
                        refresh.setActionView(null);
                    }
                }
            }
        }).execute();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();

        switch(id)
        {
            case R.id.action_refresh:
                task = 0;
                taskDone = 0;
                getZones(true);
                getZoneOrganic(true);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        refresh = menu.findItem(R.id.action_refresh);
    }
}