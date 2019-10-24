package com.ipms.arena.listener;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.charts.RadarChart;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.RadarDataSet;
import com.ipms.arena.activity.R;

import java.util.ArrayList;


/**
 * Created by t-ebcruz on 4/18/2016.
 */
public class ChartChangeListener implements AdapterView.OnItemSelectedListener
{
    private FragmentActivity context;
    private FragmentManager fm;
    private FragmentTransaction ft;

    private Object chart;
    private Object dataSet;
    private View layout;

    private int touch = 0;
    private String[] label;

    private Fragment frag;

    final static String BAR = "BAR";
    final static String WEB = "WEB";

    public ChartChangeListener(FragmentActivity context, View layout, Object chart, Object dataSet)
    {
        this.context = context;
        this.layout = layout;

        this.chart = chart;
        this.dataSet = dataSet;

        this.label = context.getResources().getStringArray(R.array.chart_type);

        fm = this.context.getSupportFragmentManager();
        ft = fm.beginTransaction();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
    {
        touch += 1;
        if(touch > 1)
        {
            if(chart instanceof BarChart) {
                ArrayList<BarDataSet> dataSet = (ArrayList<BarDataSet>) this.dataSet;
            }

            if(chart instanceof RadarChart) {
                ArrayList<RadarDataSet> dataSet = (ArrayList<RadarDataSet>) this.dataSet;
            }

            if(chart instanceof LineChart) {
                ArrayList<LineDataSet> dataSet = (ArrayList<LineDataSet>) this.dataSet;
            }

            if(chart instanceof PieChart) {
                ArrayList<PieDataSet> dataSet = (ArrayList<PieDataSet>) this.dataSet;
            }

            switch(label[position])
            {
                case BAR:
                    break;
                case WEB:
                break;
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
