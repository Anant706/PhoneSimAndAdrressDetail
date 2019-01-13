package com.makeinindia.controller;

import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.OnStreetViewPanoramaReadyCallback;
import com.google.android.gms.maps.StreetViewPanorama;
import com.google.android.gms.maps.StreetViewPanorama.OnStreetViewPanoramaChangeListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.SupportStreetViewPanoramaFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.StreetViewPanoramaLocation;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;

/**
 * This shows how to create a simple activity with streetview and a map
 */
public class SplitStreetViewPanoramaAndMapDemoActivity extends FragmentActivity
        implements OnMarkerDragListener, OnStreetViewPanoramaChangeListener, LoaderCallbacks<Cursor> {

    private static final String MARKER_POSITION_KEY = "MarkerPosition";
    private static final String TAG = "StreetViewActivity";

    // How many Geocoder should return our GPSTracker
    int geocoderMaxResults = 1;
    GoogleMap globalMap;
    double lat, longt, longitude, latitude;
    Button myLocation;
    TextView currentAddress;
    LinearLayout addressLayout;
    private Geocoder geocoder;
    private List<Address> addresses;
    // TajMahal""
    private LatLng TAJMAHAL = new LatLng(27.1736, 78.0421);
    private StreetViewPanorama mStreetViewPanorama;
    private Marker mMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);    // Removes title bar
        this.getWindow().setFlags(WindowManager.LayoutParams.SCREEN_BRIGHTNESS_CHANGED, WindowManager.LayoutParams.SCREEN_BRIGHTNESS_CHANGED);    // Removes notification bar

        setContentView(R.layout.split_street_view_panorama_and_map_demo);
        final LatLng markerPosition, markerPos;
        try {
            geocoder = new Geocoder(getApplicationContext(), Locale.ENGLISH);
            Log.e(TAG, "Geocoder is  initialised ");
        } catch (Exception e) {

            Log.e(TAG, "Geocoder is not initialised ", e);
        }

        //Bundle bundle = in.getExtras();
        myLocation = (Button) findViewById(R.id.myLocation);
        currentAddress = (TextView) findViewById(R.id.adressText);
        addressLayout = (LinearLayout) findViewById(R.id.addressLayout);
        if ((getIntent().getStringExtra("Latitude") != null) && (getIntent().getStringExtra("Longitude") != null)) {
            lat = Double.parseDouble(getIntent().getStringExtra("Latitude"));
            longt = Double.parseDouble(getIntent().getStringExtra("Longitude"));
        }

        if (savedInstanceState == null) {
            markerPosition = TAJMAHAL;
        } else {
            markerPosition = savedInstanceState.getParcelable(MARKER_POSITION_KEY);
        }

        SupportStreetViewPanoramaFragment streetViewPanoramaFragment =
                (SupportStreetViewPanoramaFragment)
                        getSupportFragmentManager().findFragmentById(R.id.streetviewpanorama);
        streetViewPanoramaFragment.getStreetViewPanoramaAsync(
                new OnStreetViewPanoramaReadyCallback() {
                    @Override
                    public void onStreetViewPanoramaReady(StreetViewPanorama panorama) {
                        mStreetViewPanorama = panorama;
                        mStreetViewPanorama.setStreetNamesEnabled(true);
                        mStreetViewPanorama.setZoomGesturesEnabled(true);
                        mStreetViewPanorama.setUserNavigationEnabled(true);
                        mStreetViewPanorama.setPosition(markerPosition);
                        mStreetViewPanorama.setOnStreetViewPanoramaChangeListener(
                                SplitStreetViewPanoramaAndMapDemoActivity.this);
                    }
                });

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap map) {
                globalMap = map;
                map.setOnMarkerDragListener(SplitStreetViewPanoramaAndMapDemoActivity.this);
                // Creates a draggable marker. Long press to drag.
                mMarker = map.addMarker(new MarkerOptions()
                        .position(markerPosition)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.pegman))
                        .draggable(true));

                map.moveCamera(CameraUpdateFactory.newLatLngZoom(TAJMAHAL, 15.0f));
                map.setMyLocationEnabled(true);
                map.setOnMapClickListener(new OnMapClickListener() {

                    @Override
                    public void onMapClick(LatLng mCurrentLocation) {
                        if (mCurrentLocation != null) {
                            try {
                                /**
                                 * Geocoder.getFromLocation - Returns an array of Addresses
                                 * that are known to describe the area immediately surrounding the given latitude and longitude.
                                 */
                                addresses = geocoder.getFromLocation(mCurrentLocation.latitude, mCurrentLocation.longitude, 1);
                                globalMap.clear();
                                /*if(addressLayout != null)
									addressLayout.setVisibility(View.VISIBLE);
*/
                                mMarker = globalMap.addMarker(new MarkerOptions()
                                        .position(mCurrentLocation)
                                        .title(getAddressLine(addresses))
                                        .draggable(true)
                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.pegman)));
                                Log.d(TAG, "Address in Geocoder" + addresses);
                                mStreetViewPanorama.setPosition(mMarker.getPosition(), 150);
                                //return addresses;
                            } catch (IOException e) {
                                //e.printStackTrace();
                                Log.e(TAG, "Impossible to connect to Geocoder", e);
                            }
                            //getAddressLine(addresses);
                        }
                    }
                });
                map.getUiSettings().setCompassEnabled(true);
                map.getUiSettings().setRotateGesturesEnabled(true);
                map.getUiSettings().setZoomGesturesEnabled(true);
                map.getUiSettings().setZoomControlsEnabled(true);


            }
        });

        myLocation.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();


    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(MARKER_POSITION_KEY, mMarker.getPosition());
    }

    @Override
    public void onStreetViewPanoramaChange(StreetViewPanoramaLocation location) {
        if (location != null) {
            mMarker.setPosition(location.position);
        }
    }

    @Override
    public void onMarkerDragStart(Marker marker) {
    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        mStreetViewPanorama.setPosition(marker.getPosition(), 150);
    }

    @Override
    public void onMarkerDrag(Marker marker) {
    }

    @Override
    public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> arg0, Cursor arg1) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onLoaderReset(Loader<Cursor> arg0) {
        // TODO Auto-generated method stub

    }


    public String getAddressLine(List<Address> addresses) {

        if (addresses != null && addresses.size() > 0) {
            Address address = addresses.get(0);
            LinkedHashMap<String, String> getAddressLineMap = new LinkedHashMap<>();

            getAddressLineMap.put("Address Line 0", address.getAddressLine(0));
            getAddressLineMap.put("Address Line 1", address.getAddressLine(1));
            getAddressLineMap.put("Address Line 2", address.getAddressLine(2));
            getAddressLineMap.put("Address Line 3", address.getAddressLine(3));


            //String addressLine = address.getAddressLine(1);
            String info = "";

            if (getAddressLineMap.get("Address Line 0") != null)
                info += getAddressLineMap.get("Address Line 0");
            if (getAddressLineMap.get("Address Line 1") != null)
                info += getAddressLineMap.get("Address Line 1");
            if (getAddressLineMap.get("Address Line 2") != null)
                info += " " + getAddressLineMap.get("Address Line 2");
            if (getAddressLineMap.get("Address Line 3") != null)
                info += " [" + getAddressLineMap.get("Address Line 3") + "].";

            //currentAddress.setText(info);
            return info;
        } else {
            return null;
        }
    }
}
