package com.ipms.arena.fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
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
import android.widget.AbsListView;
import android.widget.ListView;

import com.ipms.arena.activity.R;
import com.ipms.arena.adapter.InstallerListAdapter;
import com.ipms.arena.model.Installer;
import com.ipms.arena.soap.AsyncTaskInstaller;
import com.ipms.arena.soap.AsyncTaskSoap;

import java.util.List;

/**
 * Created by t-ebcruz on 3/10/2016.
 */
@SuppressLint("ValidFragment")
public class InstallerList extends Fragment
{
    Context context;
    ProgressDialog dialog;
    InstallerListAdapter adapter;

    private AsyncTask installerTask;
    private MenuItem refresh;

    private View view;
    private View parent;
    private View footer;
    private View empty;
    private ListView listView;

    private Bundle tag;
    private Installer installer;

    private int saveFirst = -1;
    private int page = 1;
    private int itemPerPage = 10;

    private boolean first = true;
    private AlertDialog.Builder builder;
    private AlertDialog alert;

    public InstallerList() {}

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        context = getActivity();
        tag = getArguments();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.content_installers, container, false);
        parent = view.getRootView();
        footer = inflater.inflate(R.layout.default_footer, null, false);
        empty = inflater.inflate(R.layout.default_empty, null, false);

        listView = (ListView) view.findViewById(R.id.installer_list);
        listView.addFooterView(footer);

        adapter = new InstallerListAdapter(context, tag);

        dialog = ProgressDialog.show(context, null, context.getString(R.string.async_status), true);
        tag = getArguments();
        getInstallers(false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (visibleItemCount < totalItemCount && (firstVisibleItem + visibleItemCount == totalItemCount)) {
                    if (firstVisibleItem != saveFirst) {
                        saveFirst = firstVisibleItem;
                        page += 1;
                        // Enable pagination
                        //getInstallers(false);
                    }
                }
            }

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }
        });
    }

    private void getInstallers(final boolean isRefresh)
    {
        installerTask = new AsyncTaskInstaller(context, tag, page, new AsyncTaskSoap.AsyncResponse()
        {
            @Override
            public void progress() {
                if(isRefresh) {
                    refresh.setActionView(R.layout.actionbar_progress);
                }
            }

            @Override
            public void finish(List result)
            {
                if(isRefresh) {
                    adapter.clear();
                    first = true;
                }

                for(Object row : result) {
                    installer = (Installer) row;
                    if(first) {
                        adapter.addSectionHeader(installer);
                    }

                    adapter.addItem(installer);
                    first = false;
                }

                adapter.setHidden();

                // Remove this if pagination is enable
                //if(result.size() < itemPerPage) {
                    listView.removeFooterView(footer);
                //}

                if(result.size() < 1) {
                    listView.addFooterView(empty);
                }

                if(listView.getAdapter() == null) {
                    listView.setAdapter(adapter);
                    if(dialog.isShowing() && dialog != null) {
                        dialog.dismiss();
                        dialog = null;
                    }
                }

                if(isRefresh) {
                    listView.setAdapter(adapter);
                    refresh.setActionView(null);
                }
            }
        }).execute();

        if(installerTask.isCancelled() && dialog.isShowing() && dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();

        switch(id)
        {
            case R.id.action_refresh:
                getInstallers(true);
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
