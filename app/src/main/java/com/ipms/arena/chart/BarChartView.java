package com.ipms.arena.chart;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.support.percent.PercentRelativeLayout;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.ipms.arena.activity.R;
import com.ipms.arena.adapter.ChartSpinnerAdapter;
import com.ipms.arena.helper.ActionString;
import com.ipms.arena.helper.FontManager;
import com.ipms.arena.listener.ChartChangeListener;
import com.ipms.arena.listener.ChartListener;
import com.ipms.arena.model.Hourly;
import com.ipms.arena.model.InstallerHourly;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

/**
 * Created by t-ebcruz on 4/7/2016.
 */
public class BarChartView
{
    Context context;
    private View layout;
    private TextView ctrlClosed;
    private TextView ctrlRso;
    private TextView ctrlAll;

    private BarChart barChart;
    private ArrayList<BarEntry> data;

    private int allColor;
    private String allLabel;

    private int changeColor;
    private String changeLabel;
    private ArrayList<BarEntry> change;
    private BarDataSet openDS;

    private int closedColor;
    private String closedLabel;
    private ArrayList<BarEntry> closed;
    private BarDataSet closedDS;

    private int rsoColor;
    private String rsoLabel;
    private ArrayList<BarEntry> rso;
    private BarDataSet rsoDS;

    private String[] labels;

    float legendSize = 9f;
    float valueSize = 8f;

    private Resources res;
    private BarData rData;
    private ArrayList<IBarDataSet> rDataSet;
    public static ArrayList<String> legends;

    public BarChartView(Context context, View layout)
    {
        this.context = context;
        this.layout = layout;

        labels = context.getResources().getStringArray(R.array.graph_label);
        closed = new ArrayList<>();
        rso = new ArrayList<>();
        res = context.getResources();

        rDataSet = new ArrayList<>();
    }

    public void create()
    {
        PercentRelativeLayout panel = (PercentRelativeLayout) layout.findViewById(R.id.chartPanel);
        panel.setVisibility(View.VISIBLE);

        barChart = (BarChart) layout.findViewById(R.id.barChart);
        barChart.setDescription(null);
        barChart.setVisibility(View.VISIBLE);
        barChart.setTouchEnabled(false);
        legends = new ArrayList<>();

        allLabel = context.getString(R.string.label_all);
        closedLabel = context.getString(R.string.label_closed);
        rsoLabel = context.getString(R.string.label_rso);
        changeLabel = context.getString(R.string.label_change);

        for (int i = 0; i < labels.length; i++) {
            legends.add(labels[i % labels.length]);
        }

        allColor = res.getColor(R.color.all);
        closedColor = res.getColor(R.color.closed);
        rsoColor = res.getColor(R.color.rso);
        changeColor = res.getColor(R.color.open);

        createClosedSet();
        createRsoSet();

        rData = new BarData(legends, rDataSet);
        rData.setValueTextSize(valueSize);
        rData.setDrawValues(true);

        barChart.setData(rData);
        barChart.invalidate();

        barChart.setHighlightPerTapEnabled(true);
        barChart.animateXY(1400, 1400, Easing.EasingOption.EaseInOutQuad, Easing.EasingOption.EaseInOutQuad);

        if ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) <= Configuration.SCREENLAYOUT_SIZE_NORMAL) {
            barChart.zoom(2f, 1f, 2f, 2f);
        }

        XAxis x = barChart.getXAxis();
        x.setPosition(XAxis.XAxisPosition.BOTTOM);
        x.setSpaceBetweenLabels(2);
        x.setTextSize(legendSize);

        YAxis yRight = barChart.getAxisRight();
        yRight.setEnabled(false);

        YAxis yLeft = barChart.getAxisLeft();
        yLeft.setTextSize(legendSize);
        yLeft.setAxisMinValue(0);

        Legend l = barChart.getLegend();
        l.setEnabled(false);
        createController();
    }

    public void setData(int type, Hourly dataSet)
    {
        ArrayList<BarEntry> entry = new ArrayList<>();
        ArrayList<Integer> set = dataSet.toArray();

        rData = barChart.getData();

        for (int i = 0; i < labels.length; i++) {
            entry.add(new BarEntry(set.get(i), i));
        }

        if(type == 1) {
            closed = entry;
            createClosedSet();
            rData.addDataSet(closedDS);

            ArrayList closedList = new ArrayList<>();
            closedList.add(closedDS);
            ctrlClosed.setOnClickListener(new ChartListener(context, barChart, closedList));
        } else {
            rso = entry;
            createRsoSet();
            rData.addDataSet(rsoDS);

            ArrayList rsoList = new ArrayList<>();
            rsoList.add(rsoDS);
            ctrlRso.setOnClickListener(new ChartListener(context, barChart, rsoList));
        }

        if(closed.size() > 0 && rso.size() > 0) {
            ctrlAll.setOnClickListener(new ChartListener(context, barChart, rDataSet));
            barChart.setTouchEnabled(true);
        }

        rData.setValueFormatter(new Formatter());
        barChart.notifyDataSetChanged();
        barChart.invalidate();
    }

    public void createClosedSet()
    {
        closedDS = new BarDataSet(closed, closedLabel);
        closedDS.setColor(closedColor);
    }

    public void createRsoSet()
    {
        rsoDS = new BarDataSet(rso, rsoLabel);
        rsoDS.setColor(rsoColor);
    }

    public void createController()
    {
        ActionString as = new ActionString(context);
        ViewGroup controller = (ViewGroup) layout.findViewById(R.id.chartDataControl);
        Typeface tf = FontManager.getTypeface(context, FontManager.FONTAWESOME);

        ctrlAll = (TextView) controller.findViewById(R.id.chartAll);
        ctrlClosed = (TextView) controller.findViewById(R.id.chartClosed);
        ctrlRso = (TextView) controller.findViewById(R.id.chartRso);
        Spinner change = (Spinner) controller.findViewById(R.id.changeGraph);

        ChartSpinnerAdapter adapter = new ChartSpinnerAdapter(context, R.layout.chart_spinner, Arrays.asList(context.getResources().getStringArray(R.array.chart_type_icon)));
        change.setAdapter(adapter);
        change.setSelection(0, false);
        change.setOnItemSelectedListener(new ChartChangeListener((FragmentActivity) context, layout, barChart, rDataSet));

        ctrlAll.setTypeface(tf);
        ctrlAll.setTextColor(allColor);
        ctrlAll.append(as.create(allLabel));

        ctrlClosed.setTypeface(tf);
        ctrlClosed.setTextColor(closedColor);
        ctrlClosed.append(as.create(closedLabel));

        ctrlRso.setTypeface(tf);
        ctrlRso.setTextColor(rsoColor);
        ctrlRso.append(as.create(rsoLabel));

        controller.setVisibility(View.VISIBLE);
    }

    public ArrayList<BarEntry> getData() {
        return data;
    }

    public void setData(ArrayList<BarEntry> data) {
        this.data = data;
    }

    public class Formatter implements ValueFormatter
    {
        @Override
        public String getFormattedValue(float v, Entry entry, int i, ViewPortHandler viewPortHandler) {
            return v > 0 ? Math.round(v) + "" : "";
        }
    }
}
