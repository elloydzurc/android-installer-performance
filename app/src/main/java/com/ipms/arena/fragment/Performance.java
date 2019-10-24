package com.ipms.arena.fragment;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.HeaderViewListAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.ipms.arena.activity.Config;
import com.ipms.arena.activity.R;
import com.ipms.arena.adapter.InstallerListAdapter;
import com.ipms.arena.adapter.SubZoneListAdapter;
import com.ipms.arena.chart.BarChartView;
import com.ipms.arena.data.ContractorPerformanceDatabase;
import com.ipms.arena.data.InstallerPerformanceDatabase;
import com.ipms.arena.model.Contractor;
import com.ipms.arena.model.Hourly;
import com.ipms.arena.model.Installer;
import com.ipms.arena.model.Zone;
import com.ipms.arena.soap.AsyncTaskContractorPerformance;
import com.ipms.arena.soap.AsyncTaskInstallerPerformance;
import com.ipms.arena.soap.AsyncTaskSoap;

import java.util.List;

/**
 * Created by t-ebcruz on 4/9/2016.
 */
@SuppressLint("ValidFragment")
public class Performance extends Fragment
{
    private ViewGroup parent;
    private View view;

    private Zone zone;
    private Contractor contractor;
    private Installer installer;

    private FragmentActivity context;

    final static String ZONE = "zone";
    final static String CONTRACTOR = "contractor";
    final static String INSTALLER = "installer";
    final static String ORGANIC = "organic";

    private Bundle tag;
    private ListView listView;
    private LinearLayout progress;
    private ExpandableListView eListView;
    private HeaderViewListAdapter headerAdapter;

    private InstallerListAdapter iAdapter;
    private SubZoneListAdapter sAdapter;

    private AsyncTask performanceTask;
    private ContractorPerformanceDatabase cDb;
    private InstallerPerformanceDatabase iDb;

    private Hourly dataSet;
    private Hourly dataSetClosed;
    private Hourly dataSetRso;
    private BarChartView bar;

    private boolean organic = false;
    private int task = 0, taskDone = 0;

    public Performance() {}

    public Performance(FragmentActivity context)
    {
        this.context = context;
        this.cDb = new ContractorPerformanceDatabase(context);
        this.iDb = new InstallerPerformanceDatabase(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        tag = getArguments();
        zone = tag.getParcelable(ZONE);
        organic = tag.getBoolean(ORGANIC);
        installer = tag.getParcelable(INSTALLER);
        contractor = tag.getParcelable(CONTRACTOR);

        /*** 1 = CLOSED; 2 = RSO ***/
        if(contractor != null) {
            getContractorPerformance(1);
            getContractorPerformance(2);
        } else if(installer != null) {
            getInstallerPerformance(1);
            getInstallerPerformance(2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        parent = (ViewGroup) container.getRootView();
        view = inflater.inflate(R.layout.fragment_graph, container, false);
        progress = (LinearLayout) view.findViewById(R.id.progress);

        dataSet = new Hourly();
        dataSetRso = new Hourly();
        dataSetClosed = new Hourly();

        bar = new BarChartView(context, view);
        bar.create();

        showGraph(true);
        return view;
    }

    @Override
    public void onDestroy()
    {
        showGraph(false);
        performanceTask.cancel(true);
        super.onDestroy();
    }

    public void showGraph(boolean show)
    {
        int nv, panelHeight = Config.dpToPx(Config.MAP_CHART_HEIGHT);

        if(contractor != null)
        {
            eListView = (ExpandableListView) parent.findViewById(R.id.subZoneListview);
            contractor.setShowMapChart(show);

            sAdapter = (SubZoneListAdapter) eListView.getExpandableListAdapter();
            sAdapter.updateChild(CONTRACTOR.toUpperCase(), contractor);
        }
        else if(installer != null)
        {
            installer.setShowChart(show);

            if(!organic)
            {
                listView = (ListView) parent.findViewById(R.id.installer_list);

                if(listView.getAdapter() instanceof HeaderViewListAdapter) {
                    headerAdapter = (HeaderViewListAdapter) listView.getAdapter();
                    iAdapter = (InstallerListAdapter) headerAdapter.getWrappedAdapter();
                } else {
                    iAdapter = (InstallerListAdapter) listView.getAdapter();
                }

                iAdapter.updateItem(installer);
            }
            else
            {
                eListView = (ExpandableListView) parent.findViewById(R.id.subZoneListview);
                sAdapter = (SubZoneListAdapter) eListView.getExpandableListAdapter();
                sAdapter.updateChild(ORGANIC.toUpperCase(), installer);
            }
        }

        // Calculate the size of Expandable ListView
        if(contractor != null || (organic && installer != null))
        {
            String stat = show ? "show" : "hide";
            if(show) {
                nv = eListView.getHeight() + panelHeight;
            } else {
                nv = eListView.getHeight() - panelHeight;
            }

            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, nv);
            eListView.setLayoutParams(lp);
            eListView.requestLayout();
        }
    }

    public void getContractorPerformance(final int type)
    {
        task++;
        performanceTask = new AsyncTaskContractorPerformance(context, contractor, type, new AsyncTaskSoap.AsyncResponse() {
            @Override
            public void progress() {}

            @Override
            public void finish(List result)
            {
                if(result.size() > 0)
                {
                    dataSet = (Hourly) result.get(result.size() - 1);
                    if(type == 1) {
                        dataSetClosed = dataSet;
                    } else {
                        dataSetRso = dataSet;
                    }
                    result.remove(result.size() - 1);
                    cDb.insertList(result);
                } else {
                    if(task == taskDone) {
                        onDestroy();
                    }
                }

                taskDone++;
                if(task == taskDone) {
                    bar.setData(1, dataSetClosed);
                    bar.setData(2, dataSetRso);
                    progress.setVisibility(View.GONE);
                }
            }
        }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void getInstallerPerformance(final int type)
    {
        task++;
        performanceTask = new AsyncTaskInstallerPerformance(context, installer, type, new AsyncTaskSoap.AsyncResponse() {
            @Override
            public void progress() {}

            @Override
            public void finish(List result)
            {
                if(result.size() > 0)
                {
                    dataSet = (Hourly) result.get(result.size() - 1);
                    if(type == 1) {
                        dataSetClosed = dataSet;
                    } else {
                        dataSetRso = dataSet;
                    }
                    result.remove(result.size() - 1);
                    iDb.insertList(result);
                } else {
                    if(task == taskDone) {
                        onDestroy();
                    }
                }

                taskDone++;
                if(task == taskDone) {
                    bar.setData(1, dataSetClosed);
                    bar.setData(2, dataSetRso);
                    progress.setVisibility(View.GONE);
                }
            }
        }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }
}
