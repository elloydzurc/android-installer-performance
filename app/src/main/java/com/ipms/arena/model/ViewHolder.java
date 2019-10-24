package com.ipms.arena.model;

import android.graphics.drawable.LayerDrawable;
import android.support.percent.PercentRelativeLayout;
import android.widget.ExpandableListView;
import android.widget.RatingBar;
import android.widget.TextView;

/**
 * Created by t-ebcruz on 4/11/2016.
 */
public class ViewHolder
{
    public TextView header;
    public TextView indicator;
    public TextView headerPerformance;
    public TextView headerInstaller;
    public TextView headerLocation;

    public PercentRelativeLayout progress;
    public PercentRelativeLayout dataContainer;
    public PercentRelativeLayout panel;
    public PercentRelativeLayout mapChart;

    public PercentRelativeLayout legendView;
    public TextView assgn;
    public TextView ack;
    public TextView cls;
    public TextView rso2;
    public TextView dis;

    public TextView opnIcon;

    public TextView com;
    public TextView comIcon;

    public TextView ackIcon;
    public TextView disIcon;

    public RatingBar ratingBar;
    public LayerDrawable stars;

    public TextView rso;
    public TextView rsoIcon;

    public TextView installer;
    public Zone zone;

    public int type;

    public ExpandableListView expandView;
}