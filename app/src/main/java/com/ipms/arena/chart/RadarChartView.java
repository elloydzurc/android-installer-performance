package com.ipms.arena.chart;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.support.percent.PercentRelativeLayout;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.RadarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.RadarData;
import com.github.mikephil.charting.data.RadarDataSet;
import com.github.mikephil.charting.interfaces.datasets.IRadarDataSet;
import com.ipms.arena.activity.R;
import com.ipms.arena.adapter.ChartSpinnerAdapter;
import com.ipms.arena.helper.ActionString;
import com.ipms.arena.helper.FontManager;
import com.ipms.arena.listener.ChartChangeListener;
import com.ipms.arena.listener.ChartListener;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by t-ebcruz on 4/7/2016.
 */
public class RadarChartView
{
    Context context;
    private View layout;

    private com.github.mikephil.charting.charts.RadarChart radarChart;

    private String description;
    private ArrayList<Entry> data;

    private int allColor;
    private String allLabel;

    private int changeColor;
    private String changeLabel;
    private ArrayList<Entry> open;
    private RadarDataSet openDS;

    private int closedColor;
    private String closedLabel;
    private ArrayList<Entry> closed;
    private RadarDataSet closedDS;

    private int rsoColor;
    private String rsoLabel;
    private ArrayList<Entry> rso;
    private RadarDataSet rsoDS;

    private String[] labels;

    float line = 2f;
    float textSize =8f;

    private RadarData rData;
    private ArrayList<IRadarDataSet> rDataSet;
    private Object rawData;

    public static ArrayList<String> legends;

    public RadarChartView(Context context, View layout)
    {
        this.context = context;
        this.layout = layout;

        labels = context.getResources().getStringArray(R.array.graph_label);
    }

    public void create()
    {
        PercentRelativeLayout panel = (PercentRelativeLayout) layout.findViewById(R.id.chartPanel);
        RadarChart radar = (com.github.mikephil.charting.charts.RadarChart) layout.findViewById(R.id.radarChart);

        panel.setVisibility(View.VISIBLE);
        radar.setVisibility(View.VISIBLE);
        radar.setDescription(description);

        radarChart = radar;
        setData();

        radar.setWebLineWidth(1.5f);
        radar.setWebLineWidthInner(0.75f);
        radar.setWebAlpha(100);

        allLabel = context.getString(R.string.label_all);
        closedLabel = context.getString(R.string.label_closed);
        rsoLabel = context.getString(R.string.label_rso);
        changeLabel = context.getString(R.string.label_change);

        legends = new ArrayList<String>();

        for (int i = 0; i < labels.length; i++) {
            legends.add(labels[i % labels.length]);
        }

        Resources res = context.getResources();
        allColor = res.getColor(R.color.all);
        closedColor = res.getColor(R.color.closed);
        rsoColor = res.getColor(R.color.rso);
        changeColor = res.getColor(R.color.open);

        closedDS = new RadarDataSet(closed, closedLabel);
        closedDS.setColor(closedColor);
        closedDS.setFillColor(closedColor);
        closedDS.setDrawFilled(true);
        closedDS.setLineWidth(line);

        rsoDS = new RadarDataSet(rso, rsoLabel);
        rsoDS.setColor(rsoColor);
        rsoDS.setFillColor(rsoColor);
        rsoDS.setDrawFilled(true);
        rsoDS.setLineWidth(line);

        rDataSet = new ArrayList<>();
        rDataSet.add(openDS);
        rDataSet.add(closedDS);
        rDataSet.add(rsoDS);

        rData = new RadarData(legends, rDataSet);
        rData.setValueTextSize(textSize);
        rData.setDrawValues(false);

        radar.setData(rData);
        radar.invalidate();

        radar.setHighlightPerTapEnabled(true);
        radar.animateXY(
                1400, 1400,
                Easing.EasingOption.EaseInOutQuad,
                Easing.EasingOption.EaseInOutQuad);

        XAxis xAxis = radar.getXAxis();
        xAxis.setTextSize(textSize);

        YAxis yAxis = radar.getYAxis();
        yAxis.setLabelCount(5, false);
        yAxis.setTextSize(textSize);
        yAxis.setAxisMinValue(0f);

        Legend l = radar.getLegend();
        l.setEnabled(false);
        createController();
    }

    public void createController()
    {
        ActionString as = new ActionString(context);
        ViewGroup controller = (ViewGroup) layout.findViewById(R.id.chartDataControl);
        Typeface tf = FontManager.getTypeface(context, FontManager.FONTAWESOME);

        TextView all = (TextView) controller.findViewById(R.id.chartAll);
        TextView closed = (TextView) controller.findViewById(R.id.chartClosed);
        TextView rso = (TextView) controller.findViewById(R.id.chartRso);
        Spinner change = (Spinner) controller.findViewById(R.id.changeGraph);

        ChartSpinnerAdapter adapter = new ChartSpinnerAdapter(context, R.layout.chart_spinner, Arrays.asList(context.getResources().getStringArray(R.array.chart_type_icon)));
        change.setAdapter(adapter);
        change.setOnItemSelectedListener(new ChartChangeListener((FragmentActivity) context, layout, radarChart, rDataSet));

        // Set typeface for ICON, TEXT, COLOR & DATA for each button(TextView)
        all.setTypeface(tf);
        all.setTextColor(allColor);
        all.append(as.create(allLabel));

        closed.setTypeface(tf);
        closed.setTextColor(closedColor);
        closed.append(as.create(closedLabel));

        rso.setTypeface(tf);
        rso.setTextColor(rsoColor);
        rso.append(as.create(rsoLabel));

        ArrayList closedIRDS = new ArrayList<>();
        closedIRDS.add(closedDS);

        ArrayList rsoIRDS = new ArrayList<>();
        rsoIRDS.add(rsoDS);

        all.setOnClickListener(new ChartListener(context, radarChart, rDataSet));
        closed.setOnClickListener(new ChartListener(context, radarChart, closedIRDS));
        rso.setOnClickListener(new ChartListener(context, radarChart, rsoIRDS));

        controller.setVisibility(View.VISIBLE);
    }

    private void setData()
    {
        int mult = 150;

        open = new ArrayList<>();
        closed = new ArrayList<>();
        rso = new ArrayList<>();

        for (int i = 0; i < labels.length; i++) {
            open.add(new Entry((float) (Math.random() * mult) + mult / 2, i));
        }

        for (int i = 0; i < labels.length; i++) {
            closed.add(new Entry((float) (Math.random() * mult) + mult / 2, i));
        }

        for (int i = 0; i < labels.length; i++) {
            rso.add(new Entry((float) (Math.random() * mult) + mult / 2, i));
        }
    }

    public ArrayList<Entry> getData() {
        return data;
    }

    public void setData(ArrayList<Entry> data) {
        this.data = data;
    }
}
