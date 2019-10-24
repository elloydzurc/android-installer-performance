package com.ipms.arena.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.ipms.arena.activity.R;
import com.ipms.arena.helper.ActionString;
import com.ipms.arena.helper.FontManager;

import java.util.List;

/**
 * Created by t-ebcruz on 4/18/2016.
 */
public class ChartSpinnerAdapter extends ArrayAdapter<String>
{
    Context context;
    Typeface font;

    String[] label;
    String[] color;
    ActionString as;

    public ChartSpinnerAdapter(Context context, int resource, List<String> objects)
    {
        super(context, resource, objects);

        this.context = context;
        this.font = FontManager.getTypeface(context, FontManager.FONTAWESOME);

        this.label = this.context.getResources().getStringArray(R.array.chart_type);
        this.color = this.context.getResources().getStringArray(R.array.chart_color);
        this.as = new ActionString(this.context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        TextView view = (TextView) super.getView(position, convertView, parent);
        view.setTypeface(font);
        view.setTextColor(Color.parseColor(color[position]));
        view.append(as.create(label[position]));
        view.setGravity(View.TEXT_ALIGNMENT_CENTER);

        return view;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent)
    {
        TextView view = (TextView) super.getDropDownView(position, convertView, parent);
        view.setTypeface(font);
        view.setTextColor(Color.parseColor(color[position]));
        view.append(as.create(label[position]));

        return view;
    }
}
