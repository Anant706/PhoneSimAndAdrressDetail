package com.makeinindia.controller;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;

public class LocationDetails extends AppCompatActivity implements LocationListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = "LocationDetails";
    // Request code to use when launching the resolution activity
    private static final int REQUEST_RESOLVE_ERROR = 1001;
    // Unique tag for the error dialog fragment
    private static final String DIALOG_ERROR = "dialog_error";
    private static final long INTERVAL = 1000 * 60 * 1;
    private static final long FASTEST_INTERVAL = 1000 * 60 * 1;
    private static final int REQUEST_CODE = 0;
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    Location mCurrentLocation;
    String mLastUpdateTime;
    // How many Geocoder should return our GPSTracker
    int geocoderMaxResults = 1;
    LatLng currentLocation;
    TextView latitudeField, longitudeField, cityField, stateField, countryField, addressField;
    Button showMap;
    Geocoder geocoder;
    List<Address> addresses;
    InterstitialAd mInterstitialAd;
    AdRequest adRequest;
    AdView adView;
    LocationManager locationManager;
    // Bool to track whether the app is already resolving an error
    private boolean mResolvingError = false;
    private GoogleMap map;
    private boolean enableCaching = true;
    private boolean mRequestingLocationUpdates = false;

    @Override
    public void onConnectionSuspended(int i) {

    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest
                .setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
    }

    private boolean isGooglePlayServicesAvailable() {
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        Log.d(TAG, "onStart fired .google play .............");
        if (ConnectionResult.SUCCESS == status) {
            return true;
        } else {
            GooglePlayServicesUtil.getErrorDialog(status, this, 0).show();
            return false;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_location_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        try {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Location Details");
        } catch (Exception e) {
            Log.d("ActionBar not supported", "ActionBar not supported");
        }
        if (!isGooglePlayServicesAvailable()) {
            // finish();
        }
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API).addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this).build();
        }
        adRequest = new AdRequest.Builder().build();
        requestNewInterstitial();
        new Criteria();
        createLocationRequest();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onResume();
            }
        });
    }


    private void showGPSDisabledAlertToUser() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("GPS is disabled. Would you like to enable it?")
                .setCancelable(false)
                .setPositiveButton("Enable",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent callGPSSettingIntent = new Intent(
                                        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(callGPSSettingIntent);
                            }
                        });
        alertDialogBuilder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        finish();
                    }
                });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    private void requestNewInterstitial() {
        mInterstitialAd = new InterstitialAd(getApplicationContext());
        if (mInterstitialAd != null)
            mInterstitialAd.setAdUnitId(getString(R.string.interstitial_ad_unit_id));

        if (mInterstitialAd != null && adRequest != null) {
            mInterstitialAd.loadAd(adRequest);

        }
        if (mInterstitialAd != null && adRequest != null)
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                // Load the next interstitial.
                mInterstitialAd.loadAd(adRequest);
            }

        });
        adView = (AdView) findViewById(R.id.myAdView);
        if (adView != null)
            adView.setAdListener(new AdListener() {

                @Override
                public void onAdLoaded() {
                    adView.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAdFailedToLoad(int error) {
                    adView.setVisibility(View.GONE);
                }
            });

        if (adView != null && adRequest != null)
            adView.loadAd(adRequest);
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart fired ..............");
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();

        Log.d(TAG, "onStop fired ..............");
        mGoogleApiClient.disconnect();
        Log.d(TAG,
                "isConnected ...............: "
                        + mGoogleApiClient.isConnected());
    }

    @Override
    protected void onDestroy() {
        if (mInterstitialAd != null && mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        }
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mGoogleApiClient.isConnected()) {
            stopLocationUpdates();
        }
    }

    protected void stopLocationUpdates() {

        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
        Log.d(TAG, "Location update stopped .......................");
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mGoogleApiClient.isConnected()) {
            startLocationUpdates();
            Log.d(TAG, "Location update resumed .....................");
        }

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

            latitudeField = (TextView) findViewById(R.id.latitude);
            longitudeField = (TextView) findViewById(R.id.longitude);
            cityField = (TextView) findViewById(R.id.fieldCity);
            stateField = (TextView) findViewById(R.id.fieldState);
            addressField = (TextView) findViewById(R.id.fieldAddressLine);
            countryField = (TextView) findViewById(R.id.fieldCountry);
            showMap = (Button) findViewById(R.id.showMap);
            showMap.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent mapDetails = new Intent(getApplicationContext(), MapDetails.class);
                    startActivity(mapDetails);
                    mRequestingLocationUpdates = true;
                    // LocationInfo.this.overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                }
            });
        } else {
            showGPSDisabledAlertToUser();
        }

    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG, "onConnected - isConnected ...............: "
                + mGoogleApiClient.isConnected());
        startLocationUpdates();
    }

    protected void startLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
        Log.d(TAG, "Location update started ..............: ");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "Connection failed: " + connectionResult.toString());

    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG,
                "Firing onLocationChanged..............................................");
        mCurrentLocation = location;
        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
        updateUI();
    }

    public List<Address> getGeocoderAddress(Context context) {
        if (mCurrentLocation != null) {

            try {
                /**
                 * Geocoder.getFromLocation - Returns an array of Addresses that
                 * are known to describe the area immediately surrounding the
                 * given latitude and longitude.
                 */
                geocoder = new Geocoder(getApplicationContext(), Locale.ENGLISH);

                addresses = geocoder.getFromLocation(
                        mCurrentLocation.getLatitude(),
                        mCurrentLocation.getLongitude(),
                        this.geocoderMaxResults);
                Log.d(TAG, "Address in Geocoder" + addresses);
                return addresses;
            } catch (IOException e) {
                // e.printStackTrace();
                Log.e(TAG, "Impossible to connect to Geocoder", e);
            }
        }

        return null;
    }

    public String getAddressLine(Context context) {
        // List<Address> addresses = getGeocoderAddress(context);

        if (addresses != null && addresses.size() > 0) {
            Address address = addresses.get(0);
            LinkedHashMap<String, String> getAddressLineMap = new LinkedHashMap<>();

            getAddressLineMap.put("Address Line 0", address.getAddressLine(0));
            getAddressLineMap.put("Address Line 1", address.getAddressLine(1));
            getAddressLineMap.put("Address Line 2", address.getAddressLine(2));
            getAddressLineMap.put("Address Line 3", address.getAddressLine(3));

            // String addressLine = address.getAddressLine(1);
            String info = "";

            info += getAddressLineMap.get("Address Line 1");
            info += "\n" + getAddressLineMap.get("Address Line 2");
            info += " [" + getAddressLineMap.get("Address Line 3") + "].";

            return info;
        } else {
            return null;
        }
    }

    public String getLocality(Context context) {
        List<Address> addresses = getGeocoderAddress(context);

        if (addresses != null && addresses.size() > 0) {
            Address address = addresses.get(0);
            String locality = address.getLocality();

            return locality;
        } else {
            return null;
        }
    }

    public String getState(Context context) {
        List<Address> addresses = getGeocoderAddress(context);

        if (addresses != null && addresses.size() > 0) {
            Address address = addresses.get(0);
            String locality = address.getAdminArea();

            return locality;
        } else {
            return null;
        }
    }

    public String getCountryName(Context context) {
        List<Address> addresses = getGeocoderAddress(context);
        if (addresses != null && addresses.size() > 0) {
            Address address = addresses.get(0);
            String countryName = address.getCountryName();

            return countryName;
        } else {
            return null;
        }
    }

    private void updateUI() {
        Log.d(TAG, "UI update initiated .............");
        if (null != mCurrentLocation) {
            /*
             * tvLocation.setText("At Time: " + mLastUpdateTime + "\n" +
			 * "Latitude: " + lat + "\n" + "Longitude: " + lng + "\n" +
			 * "Accuracy: " + mCurrentLocation.getAccuracy() + "\n" +
			 * "Provider: " + mCurrentLocation.getProvider());
			 */

            String stringLatitude = String.valueOf(mCurrentLocation
                    .getLatitude());
            String stringLongitude = String.valueOf(mCurrentLocation
                    .getLongitude());
            /**//*textview1.setText("Latitude : " + stringLatitude + " , "
                    + "Longitude : " + stringLongitude);
            showLocation.setEnabled(true);*/

            // text2.setText("Longitude : " + stringLongitude);
            addresses = getGeocoderAddress(getApplicationContext());
            if (addresses != null && addresses.size() > 0) {
                Address address = addresses.get(0);
                String city = address.getLocality();
                String country = address.getCountryName();
                String addressState = address.getAdminArea();
                String addressLine = getAddressLine(this);

                if (latitudeField != null && longitudeField != null &&
                        cityField != null && countryField != null && stateField != null && addressField != null) {

                    latitudeField.setText(stringLatitude);
                    longitudeField.setText(stringLongitude);
                    countryField.setText(country);
                    cityField.setText(city);
                    stateField.setText(addressState);
                    addressField.setText(addressLine);
                }
            }
            // LatLng currentLocation= new
            // LatLng(Double.parseDouble(stringLatitude),
            // Double.parseDouble(stringLongitude));

            currentLocation = new LatLng(mCurrentLocation.getLatitude(),
                    mCurrentLocation.getLongitude());

           /* if (map != null) {
                // map.addMarker(options)
                Log.d(TAG, "location is not null map now ...............");
                map.setMyLocationEnabled(true);
                map.setTrafficEnabled(true);
                map.getUiSettings().setZoomControlsEnabled(true);
                map.addMarker(new MarkerOptions()
                        .position(currentLocation)
                        .title("Your Location at: \n"
                                + DateFormat.getTimeInstance().format(
                                new Date()))
                        .icon(BitmapDescriptorFactory
                                .defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(
                        currentLocation, 15));
				*//*
                 * CameraPosition cameraPosition = new CameraPosition.Builder()
				 * .target(currentLocation) .zoom(17) // Sets the zoom
				 * .bearing(90) // Sets the orientation of the camera to east
				 * .tilt(40) // Sets the tilt of the camera to 30 degrees
				 * .build(); // Creates a CameraPosition from the builder
				 * map.animateCamera
				 * (CameraUpdateFactory.newCameraPosition(cameraPosition));
				 *//*

            } else {
                Log.d(TAG, "location is null map now ...............");
            }
        } else {

            textview1
                    .setText("Couldn't get the location. Make sure GPS and Mobile Data is enabled on the device");
            Log.d(TAG, "location is null ...............");
        }*/
        }
    }

    private void showErrorDialog(int errorCode) {
        // Create a fragment for the error dialog
        ErrorDialogFragment dialogFragment = new ErrorDialogFragment();
        // Pass the error that should be displayed
        Bundle args = new Bundle();
        args.putInt(DIALOG_ERROR, errorCode);
        dialogFragment.setArguments(args);
        dialogFragment.show(getSupportFragmentManager(), "errordialog");
    }

    /* Called from ErrorDialogFragment when the dialog is dismissed. */
    public void onDialogDismissed() {
        mResolvingError = false;
    }

    /* A fragment to display an error dialog */
    public static class ErrorDialogFragment extends DialogFragment {
        public ErrorDialogFragment() {
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Get the error code and retrieve the appropriate dialog
            int errorCode = this.getArguments().getInt(DIALOG_ERROR);
            return GooglePlayServicesUtil.getErrorDialog(errorCode,
                    this.getActivity(), REQUEST_RESOLVE_ERROR);
        }

        @Override
        public void onDismiss(DialogInterface dialog) {
            ((LocationDetails) getActivity()).onDialogDismissed();
        }
    }

}
