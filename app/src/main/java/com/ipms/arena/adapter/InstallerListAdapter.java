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
import android.widget.BaseAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

import com.ipms.arena.activity.R;
import com.ipms.arena.helper.ActionString;
import com.ipms.arena.helper.FontManager;
import com.ipms.arena.listener.InstallerListener;
import com.ipms.arena.model.Contractor;
import com.ipms.arena.model.Installer;
import com.ipms.arena.model.ViewHolder;

import java.util.ArrayList;
import java.util.TreeSet;

/**
 * Created by t-ebcruz on 4/6/2016.
 */
public class InstallerListAdapter extends BaseAdapter
{
    Context context;
    Bundle args;
    LayoutInflater inflater;

    private Contractor contractor;
    private ArrayList<Installer> installers;
    public TreeSet<Integer> sectionHeader;

    private static final int TYPE_ITEM = 0;
    private static final int TYPE_SEPARATOR = 1;

    public static String CHART = "chart";
    public static String MAP = "map";
    public static String PANEL = "mapChartPanel";

    public static String CONTRACTOR = "contractor";
    public static String INSTALLER = "installer";
    private static String POSITION = "position";
    private static String SPOC = "SPOCID";
    private static String ORGANIC = "organic";

    private Typeface tf;
    private ActionString as;

    private ViewHolder iView;
    private Bundle tag;

    private String header;
    private boolean[] hidden;

    public InstallerListAdapter(Context context, Bundle args)
    {
        this.context = context;
        this.args = args;

        tf = FontManager.getTypeface(context, FontManager.FONTAWESOME);
        as = new ActionString(context);

        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        contractor = args.getParcelable(CONTRACTOR);
        header = String.format(context.getResources().getString(R.string.breadcrumps), contractor.getZone(), contractor.getContractor());

        clear();
    }

    public void addItem(final Installer installer)
    {
        installers.add(installer);
        notifyDataSetChanged();
    }

    public void addSectionHeader(final Installer installer)
    {
        installers.add(installer);
        sectionHeader.add(installers.size() - 1);
        notifyDataSetChanged();
    }

    public void updateItem(Installer installer)
    {
        int position = installers.indexOf(installer);
        installers.set(position, installer);
        notifyDataSetChanged();
    }

    public void setHidden()
    {
        hidden = new boolean[installers.size()];

        for (int i = 0; i < installers.size(); i++) {
            hidden[i] = false;
        }
    }

    public void clear()
    {
        this.installers = new ArrayList<>();
        this.sectionHeader = new TreeSet<>();
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View row, ViewGroup parent)
    {
        Installer installer = installers.get(position);
        int rowType = getItemViewType(position);

        if(row == null)
        {
            iView = new ViewHolder();

            switch(rowType) {
                case TYPE_ITEM:

                    row = inflater.inflate(R.layout.table_installer_list, null);
                    iView.mapChart = (PercentRelativeLayout) row.findViewById(R.id.mapChartPanel);

                    iView.installer = (TextView) row.findViewById(R.id.headerName);
                    iView.dataContainer = (PercentRelativeLayout) row.findViewById(R.id.dataPanelValue);
                    iView.panel = (PercentRelativeLayout) row.findViewById(R.id.fragmentData);

                    iView.opnIcon = (TextView) row.findViewById(R.id.openIcon);
                    iView.ackIcon = (TextView) row.findViewById(R.id.ackIcon);
                    iView.disIcon = (TextView) row.findViewById(R.id.disIcon);
                    iView.comIcon = (TextView) row.findViewById(R.id.completeIcon);
                    iView.rsoIcon = (TextView) row.findViewById(R.id.rsoIcon);

                    iView.ratingBar = (RatingBar) row.findViewById(R.id.ratingBar);
                    iView.stars = (LayerDrawable) iView.ratingBar.getProgressDrawable();

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        iView.stars.setTint(Color.YELLOW);
                        iView.stars.setTintMode(PorterDuff.Mode.SRC_ATOP);
                    } else {
                        iView.stars.getDrawable(2).setColorFilter(Color.YELLOW, PorterDuff.Mode.SRC_ATOP);
                    }

                    iView.headerPerformance = (TextView) row.findViewById(R.id.headerPerformance);
                    iView.headerLocation = (TextView) row.findViewById(R.id.headerLocation);

                    iView.opnIcon.setTypeface(tf);
                    iView.ackIcon.setTypeface(tf);
                    iView.disIcon.setTypeface(tf);
                    iView.comIcon.setTypeface(tf);
                    iView.rsoIcon.setTypeface(tf);

                    iView.headerPerformance.setTypeface(tf);
                    iView.headerLocation.setTypeface(tf);

                    break;
                case TYPE_SEPARATOR:

                    row = inflater.inflate(R.layout.default_header, null);
                    Spannable opn = as.create(context.getText(R.string.legend_open));
                    Spannable ack = as.create(context.getText(R.string.legend_ack));
                    Spannable dis = as.create(context.getText(R.string.legend_dis));
                    Spannable cls = as.create(context.getText(R.string.legend_closed));
                    Spannable rso = as.create(context.getText(R.string.legend_rso));

                    iView.header = (TextView) row.findViewById(R.id.header);
                    iView.header.setTypeface(tf);

                    iView.legendView = (PercentRelativeLayout) row.findViewById(R.id.legendView);
                    iView.legendView.setVisibility(View.VISIBLE);

                    iView.assgn = (TextView) row.findViewById(R.id.assgn);
                    iView.assgn.setTypeface(tf);
                    iView.assgn.append(opn);

                    iView.ack = (TextView) row.findViewById(R.id.ack);
                    iView.ack.setTypeface(tf);
                    iView.ack.append(ack);

                    iView.dis = (TextView) row.findViewById(R.id.dis);
                    iView.dis.setTypeface(tf);
                    iView.dis.append(dis);

                    iView.cls = (TextView) row.findViewById(R.id.cls);
                    iView.cls.setTypeface(tf);
                    iView.cls.append(cls);

                    iView.rso2 = (TextView) row.findViewById(R.id.rso2);
                    iView.rso2.setTypeface(tf);
                    iView.rso2.append(rso);

                break;
            }
        } else {
            iView = (ViewHolder) row.getTag();
        }

        if(installer != null)
        {
            String open = (String) context.getText(R.string.label_open_d);
            Spannable opn = as.create(String.format(open, installer.getOpen()));
            String openIcon = (String) context.getText(R.string.ic_open);

            String ack = (String) context.getText(R.string.label_ack_d);
            Spannable ackS = as.create(String.format(ack, installer.getAcknowledge()));
            String ackIcon = (String) context.getText(R.string.ic_ack);

            String dis = (String) context.getText(R.string.label_dis_d);
            Spannable disS = as.create(String.format(dis, installer.getDispatched()));
            String disIcon = (String) context.getText(R.string.ic_dis);

            String closed = (String) context.getText(R.string.label_closed_d);
            Spannable com = as.create(String.format(closed, installer.getClosed()));
            String closedIcon = (String) context.getText(R.string.ic_closed);

            String rso = (String) context.getText(R.string.label_rso_d);
            Spannable rsod = as.create(String.format(rso, installer.getRso()));
            String rsoIcon = (String) context.getText(R.string.ic_rso);

            switch(rowType)
            {
                case TYPE_ITEM:

                    iView.installer.setText(installer.getName());
                    iView.ratingBar.setRating(installer.getRating());

                    iView.opnIcon.setText(openIcon);
                    iView.opnIcon.append(opn);

                    iView.ackIcon.setText(ackIcon);
                    iView.ackIcon.append(ackS);

                    iView.disIcon.setText(disIcon);
                    iView.disIcon.append(disS);

                    iView.comIcon.setText(closedIcon);
                    iView.comIcon.append(com);

                    iView.rsoIcon.setText(rsoIcon);
                    iView.rsoIcon.append(rsod);

                    int mapChartId = Integer.parseInt(Integer.toString(position) + Integer.toString(installer.getKey()));
                    iView.mapChart.setId(mapChartId);

                    tag = new Bundle();
                    tag.putParcelable(INSTALLER, installer);
                    tag.putInt(POSITION, position);
                    tag.putInt(PANEL, mapChartId);
                    tag.putBoolean(ORGANIC, false);

                    if(installer.isShowMap() || installer.isShowChart()) {
                        iView.mapChart.setVisibility(View.VISIBLE);
                    } else {
                        iView.mapChart.setVisibility(View.GONE);
                    }

                    if(installer.getClosed() > 0 || installer.getRso() > 0)
                    {
                        iView.headerPerformance.setOnClickListener(new InstallerListener(context, CHART));
                        iView.headerPerformance.setTag(tag);
                        if(installer.isShowChart()) {
                            iView.headerPerformance.setTextColor(ContextCompat.getColor(context, R.color.active_icon));
                            iView.headerLocation.setTextColor(ContextCompat.getColor(context, R.color.inactive_icon));
                        } else {
                            iView.headerPerformance.setTextColor(ContextCompat.getColor(context, R.color.inactive_icon));
                        }
                    } else {
                        iView.headerLocation.setTextColor(ContextCompat.getColor(context, R.color.dark_gray));
                        iView.headerPerformance.setTextColor(ContextCompat.getColor(context, R.color.dark_gray));
                    }

                    if(installer.getClosed() > 0 || installer.getRso() > 0 || installer.getOpen() > 0 || installer.getAcknowledge() > 0 || installer.getDispatched() > 0)
                    {
                        iView.headerLocation.setOnClickListener(new InstallerListener(context, MAP));
                        iView.headerLocation.setTag(tag);
                        if(installer.isShowMap()) {
                            iView.headerLocation.setTextColor(ContextCompat.getColor(context, R.color.active_icon));
                            //iView.headerPerformance.setTextColor(ContextCompat.getColor(context, R.color.inactive_icon));
                        } else {
                            iView.headerLocation.setTextColor(ContextCompat.getColor(context, R.color.inactive_icon));
                        }
                    } else {
                        iView.headerLocation.setTextColor(ContextCompat.getColor(context, R.color.dark_gray));
                        iView.headerPerformance.setTextColor(ContextCompat.getColor(context, R.color.dark_gray));
                    }

                    iView.ratingBar.setVisibility(View.VISIBLE);
                    iView.headerPerformance.setVisibility(View.VISIBLE);
                    iView.headerLocation.setVisibility(View.VISIBLE);

                    break;
                case TYPE_SEPARATOR:
                    iView.header.setText(header + " (" + (installers.size() - 1) + ")");
                    break;
            }
        }
        row.setTag(iView);
        return row;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        return sectionHeader.contains(position) ? TYPE_SEPARATOR : TYPE_ITEM;
    }

    @Override
    public int getCount() {
        return installers.size();
    }

    @Override
    public Object getItem(int position) {
        return installers.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}
