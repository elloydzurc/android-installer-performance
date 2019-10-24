package com.ipms.arena.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.ipms.arena.activity.R;
import com.ipms.arena.helper.ActionString;
import com.ipms.arena.helper.FontManager;
import com.ipms.arena.model.Installer;
import com.ipms.arena.model.InstallerLocation;

/**
 * Created by t-ebcruz on 5/30/2016.
 */
public class MapInfoWindowAdapter implements GoogleMap.InfoWindowAdapter
{
    private View markerView;
    private LayoutInflater inflater;
    private InstallerLocation location;
    private ActionString as;
    private Context context;
    private Typeface tf;

    public MapInfoWindowAdapter(Context context)
    {
        this.context = context;
        as = new ActionString(context);
        as.setBold(true);
        inflater = LayoutInflater.from(context);
        tf = FontManager.getTypeface(context, FontManager.FONTAWESOME);
    }

    @Override
    public View getInfoWindow(Marker marker) {
        render(marker, markerView);
        return markerView;
    }

    @Override
    public View getInfoContents(Marker marker)
    {
        markerView = inflater.inflate(R.layout.info_maps, null);

        TextView so = (TextView) markerView.findViewById(R.id.so);
        so.setText(location.getServiceOrder());

        Spannable sType = as.create(context.getString(R.string.service_type));
        TextView service = (TextView) markerView.findViewById(R.id.serviceType);
        service.setTypeface(tf);
        service.setText(sType + location.getServiceType());

        Spannable oType = as.create(context.getString(R.string.order_type));
        TextView order = (TextView) markerView.findViewById(R.id.orderType);
        order.setTypeface(tf);
        order.setText(oType + location.getOrderType());

        Spannable nType = as.create(context.getString(R.string.ne_type));
        TextView ne = (TextView) markerView.findViewById(R.id.neType);
        ne.setTypeface(tf);
        ne.setText(nType + location.getNeType());

        Spannable stat = as.create(context.getString(R.string.so_status));
        TextView status = (TextView) markerView.findViewById(R.id.status);
        status.setTypeface(tf);
        status.setText(stat + location.getStatus());

        Spannable add = as.create(context.getString(R.string.address));
        TextView address = (TextView) markerView.findViewById(R.id.address);
        address.setTypeface(tf);
        address.setText(add + location.getAddress2());

        return markerView;
    }

    private void render(Marker marker, View view) {

    }

    public void setLocation(InstallerLocation location) {
        this.location = location;
    }
}
