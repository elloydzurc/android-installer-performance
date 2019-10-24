package com.ipms.arena.listener;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.RadarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.RadarData;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.IRadarDataSet;
import com.ipms.arena.activity.R;
import com.ipms.arena.chart.BarChartView;
import com.ipms.arena.chart.RadarChartView;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by t-ebcruz on 4/8/2016.
 */
public class ChartListener implements View.OnClickListener
{
    private Context context;
    private Object chart;

    private RadarData rDataSet;
    private BarData bDataSet;

    private RadarChart radar;
    private BarChart bar;

    private TextView textView;

    public ChartListener(Context context, RadarChart chart, ArrayList<IRadarDataSet> dataSet)
    {
        this.context = context;
        this.chart = chart;

        this.radar = chart;
        this.rDataSet = new RadarData(RadarChartView.legends, dataSet);
    }

    public ChartListener(Context context, BarChart chart, ArrayList<IBarDataSet> dataSet)
    {
        this.context = context;
        this.chart = chart;

        this.bar = chart;
        this.bDataSet = new BarData(BarChartView.legends, dataSet);
    }

    @Override
    public void onClick(View v)
    {
        ViewGroup viewGroup = (ViewGroup) v.getParent();
        textView = (TextView) v;

        if(chart instanceof RadarChart)
        {
            radar.setData(rDataSet);

            for(int i = 0; i < viewGroup.getChildCount(); i++)
            {
                if(viewGroup.getChildAt(i) instanceof TextView) {
                    TextView tv = (TextView) viewGroup.getChildAt(i);
                    tv.setBackgroundColor(context.getResources().getColor(R.color.colorPrimaryLight));
                }
            }

            if(textView.getId() != R.id.chartAll) {
                rDataSet.setDrawValues(true);
                rDataSet.setValueTextColor(context.getResources().getColor(R.color.colorSecondary));
                rDataSet.setValueTextSize(10f);
            } else {
                rDataSet.setDrawValues(false);
            }

            textView.setBackgroundColor(context.getResources().getColor(android.R.color.white));
            radar.notifyDataSetChanged();
            radar.invalidate();
        }
        else if(chart instanceof BarChart)
        {
            textView = (TextView) v;
            bar.setData(bDataSet);

            for(int i = 0; i < viewGroup.getChildCount(); i++)
            {
                if(viewGroup.getChildAt(i) instanceof TextView) {
                    TextView tv = (TextView) viewGroup.getChildAt(i);
                    tv.setBackgroundColor(context.getResources().getColor(R.color.colorPrimaryLight));
                }
            }

            textView.setBackgroundColor(context.getResources().getColor(android.R.color.white));
            bar.notifyDataSetChanged();
            bar.invalidate();
        }
    }
}