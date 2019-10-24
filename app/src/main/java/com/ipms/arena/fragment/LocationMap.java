package com.ipms.arena.fragment;

import android.app.Dialog;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.percent.PercentRelativeLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.HeaderViewListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.ipms.arena.activity.Config;
import com.ipms.arena.activity.R;
import com.ipms.arena.adapter.InstallerListAdapter;
import com.ipms.arena.adapter.MapInfoWindowAdapter;
import com.ipms.arena.adapter.SubZoneListAdapter;
import com.ipms.arena.data.InstallerLocationDatabase;
import com.ipms.arena.helper.ActionString;
import com.ipms.arena.helper.Connection;
import com.ipms.arena.helper.FontManager;
import com.ipms.arena.model.Installer;
import com.ipms.arena.model.InstallerLocation;
import com.ipms.arena.soap.AsyncTaskInstallerLocation;
import com.ipms.arena.soap.AsyncTaskSoap;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class LocationMap extends Fragment implements GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnMarkerClickListener, View.OnClickListener {

    private LocationMap self;
    private LayoutInflater li;
    private MapView map;
    private GoogleMap googleMap;
    private HashMap<String, InstallerLocation> markers;
    private ArrayList<Marker> markerList;
    private ArrayList<InstallerLocation> missingLocation;

    private InstallerLocationDatabase iDb;
    private LatLng firstView;
    private ActionString as;

    private int running;
    private int finish;

    private View view;
    private ViewGroup vGroup;
    private ViewGroup root;
    private ViewGroup parent;
    private Context context;
    private LinearLayout progress;
    private Dialog dialog;
    private ExpandableListView eListView;
    private SubZoneListAdapter sAdapter;

    private TextView open;
    private TextView acknowledged;
    private TextView dispatched;
    private TextView closed;
    private TextView rso;
    private TextView missing;

    private ImageView transparent;
    private ListView listView;

    private Installer installer;
    private HeaderViewListAdapter headerAdapter;
    private InstallerListAdapter iAdapter;
    private MapInfoWindowAdapter infoAdapter;
    private Connection conn;

    private Bundle tag;
    final static String ERROR = "Error";
    final static String INSTALLER = "installer";
    final static String ASSG = "Assigned";
    final static String COMP = "Completed";
    final static String RSO = "RSO";
    final static String ACK = "Acknowledged";
    final static String DIS = "Dispatched";
    final static String MISSING = "Missing";
    final static String ORGANIC = "organic";

    private int total = 0, current = 0;
    private float color;
    private String lastClick;
    private LatLng defView;
    private Typeface tf;
    private boolean organic = false;
    final static double DEFAULT_LAT = 12.8797;
    final static double DEFAULT_LON = 121.7740;

    public LocationMap() {
        markerList = new ArrayList<>();
        missingLocation = new ArrayList<>();
        self = this;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        markers = new HashMap<>();
        context = getActivity();
        tf = FontManager.getTypeface(context, FontManager.FONTAWESOME);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        li = inflater;
        vGroup = container;

        conn = new Connection(context);
        iDb = new InstallerLocationDatabase(context);
        dialog = new Dialog(context, R.style.mapDialog);
        as = new ActionString(context);

        parent = (ViewGroup) container.getParent();
        root = (ViewGroup) container.getRootView();

        view = li.inflate(R.layout.fragment_map, vGroup, false);
        progress = (LinearLayout) view.findViewById(R.id.progress);
        listView = (ListView) root.findViewById(R.id.installer_list);

        missing = (TextView) view.findViewById(R.id.missingBtn);
        missing.setTypeface(tf);
        missing.setTag(MISSING);
        missing.setOnClickListener(this);

        open = (TextView) parent.findViewById(R.id.openIcon);
        open.setTag(ASSG);
        open.setOnClickListener(this);

        acknowledged = (TextView) parent.findViewById(R.id.ackIcon);
        acknowledged.setTag(ACK);
        acknowledged.setOnClickListener(this);

        dispatched = (TextView) parent.findViewById(R.id.disIcon);
        dispatched.setTag(DIS);
        dispatched.setOnClickListener(this);

        closed = (TextView) parent.findViewById(R.id.completeIcon);
        closed.setTag(COMP);
        closed.setOnClickListener(this);

        rso = (TextView) parent.findViewById(R.id.rsoIcon);
        rso.setTag(RSO);
        rso.setOnClickListener(this);

        tag = getArguments();
        installer = tag.getParcelable(INSTALLER);
        organic = tag.getBoolean(ORGANIC);

        map = (MapView) view.findViewById(R.id.map);
        map.onCreate(savedInstanceState);
        map.onResume();

        try {
            MapsInitializer.initialize(context);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        defView = new LatLng(DEFAULT_LAT, DEFAULT_LON);

        map.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap gMap) {

                googleMap = map.getMap();

                googleMap.getUiSettings().setScrollGesturesEnabled(true);
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defView, 6));
                googleMap.setOnMarkerClickListener(self);

                googleMap = gMap;
                googleMap.getUiSettings().setMapToolbarEnabled(false);
            }
        });
    }

    @Override
    public void onStart()
    {
        super.onStart();
        map.setVisibility(View.VISIBLE);

        // Open, Dispatched, Acknowledged
        new AsyncTaskInstallerLocation(context, installer, 1, new AsyncTaskSoap.AsyncResponse()
        {
            @Override
            public void progress() { running++; }

            @Override
            public void finish(List result)
            {
                finish++;
                if(result.size() > 0)
                {
                    total += result.size();
                    for (Object row : result) {
                        InstallerLocation location = (InstallerLocation) row;
                        new LocationMapTask(location).execute();
                    }
                } else {
                    if(running == finish) {
                        onDestroy();
                    }
                }
            }
        }).execute();
        // Closed, RSO
        new AsyncTaskInstallerLocation(context, installer,  2, new AsyncTaskSoap.AsyncResponse()
        {
            @Override
            public void progress() {}

            @Override
            public void finish(List result)
            {
                finish++;
                if(result.size() > 0)
                {
                    total += result.size();
                    for (Object row : result) {
                        InstallerLocation location = (InstallerLocation) row;
                        new LocationMapTask(location).execute();
                    }
                } else {
                    if(running == finish) {
                        onDestroy();
                    }
                }
            }
        }).execute();

        showMap(true);
        transparent = (ImageView) view.findViewById(R.id.transparent_image);
        transparent.setOnTouchListener(new MapListener());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        showMap(false);
    }

    public void showMap(boolean show)
    {
        if(installer != null)
        {
            installer.setShowMap(show);
            if(organic)
            {
                int nv, panelHeight = Config.dpToPx(Config.MAP_CHART_HEIGHT);

                eListView = (ExpandableListView) root.findViewById(R.id.subZoneListview);
                sAdapter = (SubZoneListAdapter) eListView.getExpandableListAdapter();
                sAdapter.updateChild(ORGANIC.toUpperCase(), installer);

                if(show) {
                    nv = eListView.getHeight() + panelHeight;
                } else {
                    nv = eListView.getHeight() - panelHeight;
                }

                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, nv);
                eListView.setLayoutParams(lp);
                eListView.requestLayout();

            } else {
                if(listView.getAdapter() instanceof HeaderViewListAdapter) {
                    headerAdapter = (HeaderViewListAdapter) listView.getAdapter();
                    iAdapter = (InstallerListAdapter) headerAdapter.getWrappedAdapter();
                } else {
                    iAdapter = (InstallerListAdapter) listView.getAdapter();
                }
                iAdapter.updateItem(installer);
            }
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker)
    {
        InstallerLocation data = markers.get(marker.getId());
        infoAdapter = new MapInfoWindowAdapter(context);
        infoAdapter.setLocation(data);

        googleMap.setInfoWindowAdapter(infoAdapter);

        return false;
    }

    @Override
    public boolean onMyLocationButtonClick() {
        return false;
    }

    @Override
    public void onClick(View v)
    {
        String tag = (String) v.getTag();
        ViewGroup viewGroup = (ViewGroup) v.getParent().getParent();

        if(tag.equals(MISSING))
        {
            dialog.setContentView(R.layout.fragment_dialog);
            dialog.setTitle(context.getString(R.string.missing));
            dialog.setCancelable(true);

            LinearLayout body = (LinearLayout) dialog.findViewById(R.id.dialogBody);
            TextView shut = (TextView) dialog.findViewById(R.id.dialogHeaderClose);
            shut.bringToFront();
            shut.setTypeface(tf);

            for(InstallerLocation location : missingLocation)
            {
                View row = li.inflate(R.layout.missing_location, null);

                TextView so = (TextView) row.findViewById(R.id.so);
                so.setText(location.getServiceOrder());

                Spannable sType = as.create(context.getString(R.string.service_type));
                TextView service = (TextView) row.findViewById(R.id.serviceType);
                service.setTypeface(tf);
                service.setText(sType + location.getServiceType());

                Spannable oType = as.create(context.getString(R.string.order_type));
                TextView order = (TextView) row.findViewById(R.id.orderType);
                order.setTypeface(tf);
                order.setText(oType + location.getOrderType());

                Spannable nType = as.create(context.getString(R.string.ne_type));
                TextView ne = (TextView) row.findViewById(R.id.neType);
                ne.setTypeface(tf);
                ne.setText(nType + location.getNeType());

                Spannable stat = as.create(context.getString(R.string.so_status));
                TextView status = (TextView) row.findViewById(R.id.status);
                status.setTypeface(tf);
                status.setText(stat + location.getStatus());

                Spannable add = as.create(context.getString(R.string.address));
                TextView address = (TextView) row.findViewById(R.id.address);
                address.setTypeface(tf);
                address.setText(add + location.getAddress2());

                body.addView(row);
            }

            TextView close = (TextView) dialog.findViewById(R.id.dialogHeaderClose);
            close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }
                }
            });

            if(missingLocation.size() > 0) {
                dialog.show();
            }
        }
        else
        {
            for (Marker marker : markerList) {
                if (marker.getSnippet().equals(tag) || lastClick == tag) {
                    marker.setVisible(true);
                } else {
                    marker.setVisible(false);
                }
            }

            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                if (viewGroup.getChildAt(i) instanceof PercentRelativeLayout) {
                    TextView text = (TextView) ((PercentRelativeLayout) viewGroup.getChildAt(i)).getChildAt(0);
                    if (v.getId() != text.getId() || lastClick == tag) {
                        text.setBackgroundColor(ContextCompat.getColor(context, android.R.color.white));
                    } else {
                        text.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimaryLight));
                    }
                }
            }
        }

        lastClick = (lastClick != tag) ? tag : null;
    }

    private class MapListener implements View.OnTouchListener
    {
        @Override
        public boolean onTouch(View v, MotionEvent event)
        {
            int action = event.getAction();
            if(!organic) {
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        listView.requestDisallowInterceptTouchEvent(true);
                        return false;
                    case MotionEvent.ACTION_UP:
                        listView.requestDisallowInterceptTouchEvent(true);
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        listView.requestDisallowInterceptTouchEvent(true);
                        return false;
                    default:
                        return true;
                }
            } else {
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        eListView.requestDisallowInterceptTouchEvent(true);
                        return false;
                    case MotionEvent.ACTION_UP:
                        eListView.requestDisallowInterceptTouchEvent(true);
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        eListView.requestDisallowInterceptTouchEvent(true);
                        return false;
                    default:
                        return true;
                }
            }
        }
    }

    private class LocationMapTask extends AsyncTask<Void, Void, List<Address>>
    {
        private InstallerLocation location;

        public LocationMapTask(InstallerLocation location) {
            this.location = location;
        }

        @Override
        protected void onPreExecute() {
            TextView status = (TextView) progress.findViewById(R.id.mapStatus);
            status.setText(getString(R.string.locating));
            super.onPreExecute();
        }

        @Override
        protected List<Address> doInBackground(Void... params)
        {
            Geocoder geocoder =  new Geocoder(context);
            List<Address> address = new ArrayList<>();
            current += 1;

            if(!conn.isAvailable())
            {
                Cursor result = iDb.getInstallerLocation(location);
                Address cacheAddress = new Address(Locale.getDefault());
                result.moveToFirst();

                double lat = result.getDouble(result.getColumnIndex(iDb.LAT));
                double lon = result.getDouble(result.getColumnIndex(iDb.LON));

                cacheAddress.setLatitude(lat);
                cacheAddress.setLongitude(lon);

                if(lat == 0.0 && lon == 0) {
                    missingLocation.add(location);
                }

                address.add(cacheAddress);
            }
            else
            {
                try
                {
                    address = geocoder.getFromLocationName(location.getAddress(), 1);

                    if(!address.isEmpty())
                    {
                        Address serviceAdd = address.get(0);

                        location.setLat(serviceAdd.getLatitude());
                        location.setLon(serviceAdd.getLongitude());
                        iDb.update(location);
                    } else {
                        missingLocation.add(location);
                    }

                } catch (IOException e) {
                    Log.e(ERROR, e.getMessage());
                }
            }
            return address;
        }

        @Override
        protected void onPostExecute(List<Address> addresses)
        {
            if(addresses != null)
            {
                if(addresses.size() > 0)
                {
                    Double lat = addresses.get(0).getLatitude();
                    Double lon = addresses.get(0).getLongitude();
                    LatLng coords = new LatLng(lat, lon);

                    if(firstView == null) {
                        firstView = coords;
                    }

                    if(lat > 0 && lon > 0)
                    {
                        String snippet = location.getStatus();
                        switch(location.getStatus())
                        {
                            case COMP:
                                color = 136;
                            break;
                            case ACK:
                                color = 14;
                            break;
                            case RSO:
                                color = 0;
                            break;
                            case DIS:
                                color = 262;
                            break;
                            default:
                                color = 212;
                                snippet = ASSG;
                            break;
                        }

                        MarkerOptions marker = new MarkerOptions().position(coords)
                                .snippet(snippet)
                                .icon(BitmapDescriptorFactory.defaultMarker(color));
                        Marker plot = googleMap.addMarker(marker);

                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(firstView, 13));

                        if(plot != null) {
                            markers.put(plot.getId(), location);
                            markerList.add(plot);
                        }

                        if(progress.getVisibility() == View.VISIBLE) {
                            progress.setVisibility(View.GONE);
                        }
                    }
                }
            }

            if(total == current && missingLocation.size() > 0) {
                Toast.makeText(context, String.format(context.getString(R.string.missing_info), missingLocation.size()), Toast.LENGTH_SHORT).show();
                missing.setVisibility(View.VISIBLE);
                progress.setVisibility(View.GONE);
            }
        }
    }
}