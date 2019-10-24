package com.ipms.arena.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.percent.PercentRelativeLayout;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

import com.ipms.arena.activity.R;
import com.ipms.arena.helper.ActionString;
import com.ipms.arena.helper.FontManager;
import com.ipms.arena.listener.ContractorListener;
import com.ipms.arena.listener.InstallerListener;
import com.ipms.arena.model.Contractor;
import com.ipms.arena.model.Installer;
import com.ipms.arena.model.ViewHolder;
import com.ipms.arena.model.Zone;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by t-ebcruz on 8/23/2016.
 */
public class SubZoneListAdapter extends BaseExpandableListAdapter
{
    private Context context;
    private ViewHolder gView;
    private ViewHolder cView;
    private LinkedHashMap<String, Integer> groups;
    private HashMap<String, List<Object>> child;
    private HashMap<String, HashMap<String, List<Object>>> groupChild;

    private Typeface tf;
    private ActionString as;
    private Bundle tag;
    private Zone zone;
    private LayoutInflater inflater;

    public final static String LIST = "list";
    public final static String CHART = "chart";
    public final static String MAP = "map";

    public static String PANEL = "mapChartPanel";
    private static String POSITION = "position";

    private static String ZONE = "zone";
    private static String CONTRACTOR = "contractor";
    private static String INSTALLER = "installer";
    private static String ORGANIC = "organic";

    public SubZoneListAdapter(Context context, Zone zone, LinkedHashMap<String, Integer> groups)
    {
        this.context = context;
        this.groups = groups;
        this.zone = zone;
        this.child = new HashMap<>();
        this.groupChild = new HashMap<>();

        tf = FontManager.getTypeface(context, FontManager.FONTAWESOME);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        as = new ActionString(context);

        clear();
    }

    public void addChild(String type, List<Object> value)
    {
        child.put(type, value);
        notifyDataSetChanged();
    }

    public void updateChild(String type, Object obj)
    {
        List<Object> list = child.get(type);

        if(list != null)
        {
            if(list.indexOf(obj) >= 0) {
                int position = list.indexOf(obj);
                list.set(position, obj);
            }

            child.put(type, list);
        }

        notifyDataSetChanged();
    }

    public void clear()
    {
        child.clear();
        groupChild.clear();
        notifyDataSetChanged();
    }


    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public int getGroupCount() {
        return groups.size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return groups.keySet().toArray()[groupPosition];
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View row, ViewGroup parent)
    {
        String header = (String) getGroup(groupPosition);
        String count =   " (" + (groupPosition == 1 ? zone.getContractors() : zone.getOrganic()) + ") ";

        if(row == null)
        {
            row = inflater.inflate(R.layout.default_sub_header, null);

            gView = new ViewHolder();
            gView.header = (TextView) row.findViewById(R.id.header);
            gView.header.setTypeface(tf);

            gView.indicator = (TextView) row.findViewById(R.id.headerIcon);
            gView.indicator.setTypeface(tf);

            row.setTag(gView);
        } else {
            gView = (ViewHolder) row.getTag();
        }

        gView.header.setText(header + count);
        gView.type = groups.get(header);

        if(isExpanded && getChildrenCount(groupPosition) > 0) {
            gView.indicator.setText(context.getString(R.string.collapse));
        } else {
            gView.indicator.setText(context.getString(R.string.expand));
        }

        return row;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        int count = 0;
        String group = (String) groups.keySet().toArray()[groupPosition];
        if(child.get(group) != null) {
            count = child.get(group).size();
        }
        return count;
    }

    public Object getGroupChild(int groupPosition)
    {
        Object c = null;
        String group = (String) groups.keySet().toArray()[groupPosition];
        if(child.get(group) != null) {
            c = child.get(group);
        }
        return c;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        String group = (String) groups.keySet().toArray()[groupPosition];
        return child.get(group).get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View row, ViewGroup parent)
    {
        Object obj = getChild(groupPosition, childPosition);
        Contractor contractor = null;
        Installer installer = null;

        String name = "";
        int vOpen = 0, vAck = 0, vDis = 0, vClosed = 0, vRso = 0, id = 0;
        float rating = 0;
        boolean chart = false, map = false;

        if(groupPosition == 1)
        {
            if(obj instanceof  Contractor)
            {
                contractor = (Contractor) obj;
                contractor.setPosition(childPosition);

                vOpen = contractor.getOpen();
                vAck = contractor.getAcknowledge();
                vDis = contractor.getDispatched();
                vClosed = contractor.getClosed();
                vRso = contractor.getRso();

                id = contractor.getContractorId();
                name = contractor.getContractor();
                rating = contractor.getRating();
                chart = contractor.isShowMapChart();
            }
        }
        else
        {
            if(obj instanceof  Installer)
            {
                installer = (Installer) obj;

                vOpen = installer.getOpen();
                vAck = installer.getAcknowledge();
                vDis = installer.getDispatched();
                vClosed = installer.getClosed();
                vRso = installer.getRso();

                id = installer.getKey();
                name = installer.getName();
                rating = installer.getRating();

                map = installer.isShowMap();
                chart = installer.isShowChart();
            }
        }

        String open = (String) context.getText(R.string.label_open_d);
        Spannable opn = as.create(String.format(open, vOpen));
        String openIcon = (String) context.getText(R.string.ic_open);

        String ack = (String) context.getText(R.string.label_ack_d);
        Spannable ackS = as.create(String.format(ack, vAck));
        String ackIcon = (String) context.getText(R.string.ic_ack);

        String dis = (String) context.getText(R.string.label_dis_d);
        Spannable disS = as.create(String.format(dis, vDis));
        String disIcon = (String) context.getText(R.string.ic_dis);

        String closed = (String) context.getText(R.string.label_closed_d);
        Spannable com = as.create(String.format(closed, vClosed));
        String closedIcon = (String) context.getText(R.string.ic_closed);

        String rso = (String) context.getText(R.string.label_rso_d);
        Spannable rsod = as.create(String.format(rso, vRso));
        String rsoIcon = (String) context.getText(R.string.ic_rso);

        if(row == null)
        {
            cView = new ViewHolder();
            row = inflater.inflate(R.layout.table_contractor_list, null);

            cView.mapChart = (PercentRelativeLayout) row.findViewById(R.id.mapChartPanel);

            cView.installer = (TextView) row.findViewById(R.id.headerName);
            cView.dataContainer = (PercentRelativeLayout) row.findViewById(R.id.dataPanelValue);

            cView.panel = (PercentRelativeLayout) row.findViewById(R.id.fragmentData);

            cView.opnIcon = (TextView) row.findViewById(R.id.openIcon);
            cView.ackIcon = (TextView) row.findViewById(R.id.ackIcon);
            cView.disIcon = (TextView) row.findViewById(R.id.disIcon);
            cView.comIcon = (TextView) row.findViewById(R.id.completeIcon);
            cView.rsoIcon = (TextView) row.findViewById(R.id.rsoIcon);

            cView.ratingBar = (RatingBar) row.findViewById(R.id.ratingBar);
            cView.stars = (LayerDrawable) cView.ratingBar.getProgressDrawable();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                cView.stars.setTint(Color.YELLOW);
                cView.stars.setTintMode(PorterDuff.Mode.SRC_ATOP);
            } else {
                cView.stars.getDrawable(1).setColorFilter(Color.YELLOW, PorterDuff.Mode.DST_OUT);
                cView.stars.getDrawable(2).setColorFilter(Color.YELLOW, PorterDuff.Mode.SRC_IN);
            }

            cView.headerPerformance = (TextView) row.findViewById(R.id.headerPerformance);
            cView.headerInstaller = (TextView) row.findViewById(R.id.headerInstaller);
            cView.headerLocation = (TextView) row.findViewById(R.id.headerLocation);

            cView.opnIcon.setTypeface(tf);
            cView.ackIcon.setTypeface(tf);
            cView.disIcon.setTypeface(tf);
            cView.comIcon.setTypeface(tf);
            cView.rsoIcon.setTypeface(tf);

            cView.headerPerformance.setTypeface(tf);
            cView.headerInstaller.setTypeface(tf);
            cView.headerLocation.setTypeface(tf);

            row.setTag(cView);

        } else {
            cView = (ViewHolder) row.getTag();
        }

        if(groupPosition == 1) {
            cView.headerLocation.setVisibility(View.GONE);
            cView.headerInstaller.setVisibility(View.VISIBLE);
        } else {
            cView.headerLocation.setVisibility(View.VISIBLE);
            cView.headerInstaller.setVisibility(View.GONE);
        }

        cView.installer.setText(name);
        cView.ratingBar.setRating(rating);

        cView.opnIcon.setText(openIcon);
        cView.opnIcon.append(opn);

        cView.ackIcon.setText(ackIcon);
        cView.ackIcon.append(ackS);

        cView.disIcon.setText(disIcon);
        cView.disIcon.append(disS);

        cView.comIcon.setText(closedIcon);
        cView.comIcon.append(com);

        cView.rsoIcon.setText(rsoIcon);
        cView.rsoIcon.append(rsod);

        int mapChartId = Integer.parseInt(Integer.toString(childPosition) + Integer.toString(id));
        cView.mapChart.setId(mapChartId);

        tag = new Bundle();
        tag.putParcelable(ZONE, zone);
        tag.putInt(POSITION, childPosition);
        tag.putInt(PANEL, mapChartId);

        if(groupPosition == 1)
        {
            tag.putParcelable(CONTRACTOR, contractor);

            cView.headerInstaller.setText(context.getString(R.string.ic_installer));
            cView.headerInstaller.setOnClickListener(new ContractorListener(context, LIST));
            cView.headerInstaller.setTextColor(ContextCompat.getColor(context, R.color.inactive_icon));
            cView.headerInstaller.setTag(tag);

            if(vClosed > 0 || vRso > 0)
            {
                cView.headerPerformance.setOnClickListener(new ContractorListener(context, CHART));
                cView.headerPerformance.setTag(tag);
                if(chart) {
                    cView.headerPerformance.setTextColor(ContextCompat.getColor(context, R.color.active_icon));
                } else {
                    cView.headerPerformance.setTextColor(ContextCompat.getColor(context, R.color.inactive_icon));
                }
            } else {
                cView.headerPerformance.setTextColor(ContextCompat.getColor(context, R.color.dark_gray));
            }
        }
        else
        {
            tag.putParcelable(INSTALLER, installer);
            tag.putBoolean(ORGANIC, true);

            cView.headerLocation.setText(context.getString(R.string.ic_map));
            if(vClosed > 0 || vRso > 0)
            {
                cView.headerPerformance.setOnClickListener(new InstallerListener(context, CHART));
                cView.headerPerformance.setTag(tag);

                if(chart) {
                    cView.headerPerformance.setTextColor(ContextCompat.getColor(context, R.color.active_icon));
                    cView.headerLocation.setTextColor(ContextCompat.getColor(context, R.color.inactive_icon));
                } else {
                    cView.headerPerformance.setTextColor(ContextCompat.getColor(context, R.color.inactive_icon));
                }
            } else {
                cView.headerLocation.setTextColor(ContextCompat.getColor(context, R.color.dark_gray));
                cView.headerPerformance.setTextColor(ContextCompat.getColor(context, R.color.dark_gray));
            }

            if(vClosed > 0 || vRso > 0 || vOpen > 0 || vAck > 0 || vDis > 0)
            {
                cView.headerLocation.setOnClickListener(new InstallerListener(context, MAP));
                cView.headerLocation.setTag(tag);
                if(map) {
                    cView.headerLocation.setTextColor(ContextCompat.getColor(context, R.color.active_icon));
                } else {
                    cView.headerLocation.setTextColor(ContextCompat.getColor(context, R.color.inactive_icon));
                }
            } else {
                cView.headerLocation.setTextColor(ContextCompat.getColor(context, R.color.dark_gray));
                cView.headerPerformance.setTextColor(ContextCompat.getColor(context, R.color.dark_gray));
            }
        }

        if(chart || map) {
            cView.mapChart.setVisibility(View.VISIBLE);
        } else {
            cView.mapChart.setVisibility(View.GONE);
        }

        return row;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
