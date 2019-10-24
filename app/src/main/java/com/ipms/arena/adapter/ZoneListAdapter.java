package com.ipms.arena.adapter;

/**
 * Created by t-ebcruz on 5/20/2016.
 */

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.percent.PercentRelativeLayout;
import android.text.Spannable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ipms.arena.activity.Config;
import com.ipms.arena.activity.R;
import com.ipms.arena.helper.ActionString;
import com.ipms.arena.helper.FontManager;
import com.ipms.arena.model.ViewHolder;
import com.ipms.arena.model.Zone;
import com.ipms.arena.soap.AsyncTaskContractor;
import com.ipms.arena.soap.AsyncTaskInstaller;
import com.ipms.arena.soap.AsyncTaskSoap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class ZoneListAdapter extends BaseExpandableListAdapter
{
    private Context context;
    private Object child;
    private List<Zone> listDataHeader;
    private HashMap<String, List<Object>> listDataChild;
    private HashMap<String, HashMap<String, List<Object>>> listGroupDataChild;
    private LinkedHashMap<String, Integer> groups;

    private LayoutInflater inflater;

    private ActionString as;
    private Typeface tf;

    private final static String ZONE = "zone";
    private final static String ORGANIC = "ORGANIC";
    private final static String NON = "CONTRACTOR";

    private boolean isReset = false;

    public ZoneListAdapter(Context c)
    {
        context = c;
        tf = FontManager.getTypeface(context, FontManager.FONTAWESOME);
        as = new ActionString(context);

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        listDataHeader = new ArrayList<>();
        listDataChild = new HashMap<>();

        clear();
    }

    public void addHeader(Zone zone)
    {
        listDataHeader.add(zone);
        notifyDataSetChanged();
    }

    public void clear()
    {
        listDataHeader.clear();
        listDataChild.clear();

        groups = new LinkedHashMap<>();
        groups.put(ORGANIC, 2);
        groups.put(NON, 1);

        isReset = true;
        notifyDataSetChanged();
    }

    @Override
    public Object getChild(int groupPosition, int childPosition)
    {
        Zone zone = listDataHeader.get(groupPosition);
        return listDataChild.get(zone.getZone()).get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        Zone zone = listDataHeader.get(groupPosition);
        return (zone.getZone()).hashCode();
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition, boolean isLastChild, View row, ViewGroup parent)
    {
        final Zone zone = (Zone) getGroup(groupPosition);
        final ViewHolder view;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if(row == null || ((ViewHolder)row.getTag()).expandView.getTag() != zone.getZone())
        {
            view = new ViewHolder();

            row = inflater.inflate(R.layout.content_sub_zone, parent, false);
            view.expandView = (ExpandableListView) row.findViewById(R.id.subZoneListViewXML);

            view.expandView.setSelector(android.R.color.transparent);
            view.expandView.setId(R.id.subZoneListview);
            view.expandView.setEmptyView(null);
            view.expandView.setTag(zone.getZone());
        } else {
            view = (ViewHolder) row.getTag();
        }

        if(view.expandView.getAdapter() == null)
        {
            final SubZoneListAdapter sAdapter = new SubZoneListAdapter(context, zone, groups);
            view.expandView.setAdapter(sAdapter);

            view.expandView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
                @Override
                public boolean onGroupClick(final ExpandableListView parent, View v, final int groupPosition, long id)
                {
                    final View progress = v.findViewById(R.id.progress);
                    final String group = (String) groups.keySet().toArray()[groupPosition];
                    final int key = groups.get(group);
                    final int groupChild = parent.getExpandableListAdapter().getChildrenCount(groupPosition);
                    final int nv, itemHeight = Config.dpToPx(Config.ITEM_HEIGHT);

                    if (groupChild < 1) {
                        if (key == 1) {
                            new AsyncTaskContractor(context, zone, key, new AsyncTaskSoap.AsyncResponse() {
                                @Override
                                public void progress() {
                                    progress.setVisibility(View.VISIBLE);
                                }

                                @Override
                                public void finish(List result) {
                                    List<Object> c = new ArrayList();

                                    for (Object row : result) {
                                        child = row;
                                        c.add(child);
                                    }

                                    sAdapter.addChild(group, c);
                                    progress.setVisibility(View.GONE);

                                    RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (c.size() * itemHeight) + view.expandView.getHeight());
                                    view.expandView.setLayoutParams(lp);
                                    view.expandView.requestLayout();
                                }
                            }).execute();
                        } else {
                            Bundle tag = new Bundle();
                            tag.putParcelable(ZONE, zone);
                            new AsyncTaskInstaller(context, tag, 1, new AsyncTaskSoap.AsyncResponse() {
                                @Override
                                public void progress() {
                                    progress.setVisibility(View.VISIBLE);
                                }

                                @Override
                                public void finish(List result) {
                                    List<Object> c = new ArrayList();

                                    for (Object row : result) {
                                        child = row;
                                        c.add(child);
                                    }

                                    sAdapter.addChild(group, c);
                                    progress.setVisibility(View.GONE);

                                    RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (c.size() * itemHeight) + view.expandView.getHeight());
                                    view.expandView.setLayoutParams(lp);
                                    view.expandView.requestLayout();
                                }
                            }).execute();
                        }
                    } else {
                        if (parent.isGroupExpanded(groupPosition)) {
                            nv = view.expandView.getHeight() - (groupChild * itemHeight);
                        } else {
                            nv = view.expandView.getHeight() + (groupChild * itemHeight);
                        }

                        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, nv);
                        view.expandView.setLayoutParams(lp);
                        view.expandView.requestLayout();
                    }

                    return false;
                }
            });
        }
        row.setTag(view);
        return row;
    }

    @Override
    public int getChildrenCount(int groupPosition)
    {
        return 1;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.listDataHeader.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this.listDataHeader.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        Zone zone = listDataHeader.get(groupPosition);
        return (zone.getZone()).hashCode();
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View row, ViewGroup parent)
    {
        Zone zone = (Zone) getGroup(groupPosition);
        final ViewHolder zView;

        if (row == null)
        {
            zView = new ViewHolder();
            row = inflater.inflate(R.layout.default_header, null);

            zView.legendView = (PercentRelativeLayout) row.findViewById(R.id.legendView);

            Spannable opn = as.create(context.getText(R.string.legend_open));
            Spannable ack = as.create(context.getText(R.string.legend_ack));
            Spannable dis = as.create(context.getText(R.string.legend_dis));
            Spannable cls = as.create(context.getText(R.string.legend_closed));
            Spannable rso = as.create(context.getText(R.string.legend_rso));

            zView.assgn = (TextView) row.findViewById(R.id.assgn);
            zView.assgn.setTypeface(tf);
            zView.assgn.append(opn);

            zView.ack = (TextView) row.findViewById(R.id.ack);
            zView.ack.setTypeface(tf);
            zView.ack.append(ack);

            zView.dis = (TextView) row.findViewById(R.id.dis);
            zView.dis.setTypeface(tf);
            zView.dis.append(dis);

            zView.cls = (TextView) row.findViewById(R.id.cls);
            zView.cls.setTypeface(tf);
            zView.cls.append(cls);

            zView.rso2 = (TextView) row.findViewById(R.id.rso2);
            zView.rso2.setTypeface(tf);
            zView.rso2.append(rso);

            zView.header = (TextView) row.findViewById(R.id.header);
            zView.header.setTypeface(tf);

            zView.indicator = (TextView) row.findViewById(R.id.indicator);
            zView.indicator.setTypeface(tf);

            zView.progress = (PercentRelativeLayout) row.findViewById(R.id.progress);

        } else {
            zView = (ViewHolder) row.getTag();
        }

        zView.zone = zone;
        zView.header.setText(zone.getZone());

        if(isExpanded)
        {
            if(getChildrenCount(groupPosition) > 0) {
                zView.legendView.setVisibility(View.VISIBLE);
            }
            zView.indicator.setText(context.getString(R.string.indicator_open));
        } else {
            zView.indicator.setText(context.getString(R.string.indicator_close));
            zView.legendView.setVisibility(View.GONE);
        }

        row.setTag(zView);
        return row;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}