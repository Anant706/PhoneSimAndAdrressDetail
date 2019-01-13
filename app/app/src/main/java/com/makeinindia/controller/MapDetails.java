package com.makeinindia.controller;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapLoadedCallback;
import com.google.android.gms.maps.GoogleMap.SnapshotReadyCallback;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;

import static com.google.android.gms.maps.GoogleMap.MAP_TYPE_HYBRID;
import static com.google.android.gms.maps.GoogleMap.MAP_TYPE_NONE;
import static com.google.android.gms.maps.GoogleMap.MAP_TYPE_NORMAL;
import static com.google.android.gms.maps.GoogleMap.MAP_TYPE_SATELLITE;
import static com.google.android.gms.maps.GoogleMap.MAP_TYPE_TERRAIN;


public class MapDetails extends FragmentActivity implements
        LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, OnItemSelectedListener {
    private static final String TAG = "MapDetails";
    private static final long INTERVAL = 1000 * 60 * 1;
    private static final long FASTEST_INTERVAL = 1000 * 60 * 1;
    private static final int PICK_PHOTO = 0;
    private static final float[] MARKER_HUES = new float[]{
            BitmapDescriptorFactory.HUE_RED,
            BitmapDescriptorFactory.HUE_ORANGE,
            BitmapDescriptorFactory.HUE_YELLOW,
            BitmapDescriptorFactory.HUE_GREEN,
            BitmapDescriptorFactory.HUE_CYAN,
            BitmapDescriptorFactory.HUE_AZURE,
            BitmapDescriptorFactory.HUE_BLUE,
            BitmapDescriptorFactory.HUE_VIOLET,
            BitmapDescriptorFactory.HUE_MAGENTA,
            BitmapDescriptorFactory.HUE_ROSE,
    };
    private static final int PLACE_PICKER_REQUEST = 1;

    boolean mUpdatesRequested = false;

    // Request code to use when launching the resolution activity
    // Unique tag for the error dialog fragment
    // Bool to track whether the app is already resolving an error
    ImageView viewMap;

    Location mCurrentLocation;
    String mLastUpdateTime;
    // How many Geocoder should return our GPSTracker
    int geocoderMaxResults = 1;
    LatLng currentLocation, mCurrentLocationOnMapClick;
    String stringLatitude;
    String stringLongitude;
    LinearLayout addressLayout;
    int count = 0;

    int onMapClickCount = 0;
    SupportMapFragment mapFragment;
    InterstitialAd mInterstitialAd;
    AdRequest adRequest;
    AdView adView;
    // A request to connect to Location Services
    private LocationRequest mLocationRequest;
    // Stores the current instantiation of the location client in this object
    private GoogleApiClient mGoogleApiClient;
    private boolean mLocationCheckUpdate = false;
    private Geocoder geocoder;
    private List<Address> addresses;
    private TextView currentAddress;
    private GoogleMap map;
    private RelativeLayout leftMenu;
    private RelativeLayout rightMenu;
    private LinearLayout viewRegionLayout;
    private CheckBox mTrafficCheckbox;
    private CheckBox mMyLocationCheckbox;
    private CheckBox mBuildingsCheckbox;
    private CheckBox mIndoorCheckbox;
    private GoogleMap globalMap;
    private int addMenuCount = 0;

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adRequest = new AdRequest.Builder().build();

        if (!isGooglePlayServicesAvailable()) {
            //finish();
        }


        getSystemService(Context.LOCATION_SERVICE);
        new Criteria();

        createLocationRequest();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        try {
            geocoder = new Geocoder(getApplicationContext(), Locale.ENGLISH);
            Log.e(TAG, "Geocoder is  initialised ");
        } catch (Exception e) {

            Log.e(TAG, "Geocoder is not initialised ", e);
        }


        try {
            setContentView(R.layout.activity_map);
        } catch (Exception e) {
        }

        //mInterstitialAd.setAdUnitId(getString(R.string.interstitial_ad_unit_id));
        requestNewInterstitial();

        //	markerText = (TextView) findViewById(R.id.locationMarkertext);
        currentAddress = (TextView) findViewById(R.id.adressText);
        //markerLayout = (LinearLayout) findViewById(R.id.locationMarker);
        viewRegionLayout = (LinearLayout) findViewById(R.id.viewRegion);
        addressLayout = (LinearLayout) findViewById(R.id.addressLayout);

        Spinner spinner = (Spinner) findViewById(R.id.layers_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.layers_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        mTrafficCheckbox = (CheckBox) findViewById(R.id.traffic);
        mMyLocationCheckbox = (CheckBox) findViewById(R.id.my_location);
        mBuildingsCheckbox = (CheckBox) findViewById(R.id.buildings);
        mIndoorCheckbox = (CheckBox) findViewById(R.id.indoor);


        //locationMenu = (LinearLayout) findViewById(R.id.optionMenu);
        leftMenu = (RelativeLayout) findViewById(R.id.leftMenu);
        rightMenu = (RelativeLayout) findViewById(R.id.rightMenu);
        viewMap = (ImageView) findViewById(R.id.mapView);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                addMenuCount++;
                //Toast.makeText(getApplicationContext(), "addMenuCount : value : " + addMenuCount,5000).show();
                if (addMenuCount == 4) {
                    try {
                        if (mInterstitialAd.isLoaded())
                            mInterstitialAd.show();

                    } catch (Exception e) {

                    }
                    addMenuCount = 0;
                }


                if ((leftMenu != null) && (rightMenu != null)) {

                    leftMenu.setVisibility(leftMenu.getVisibility() != View.VISIBLE ? View.VISIBLE : View.INVISIBLE);
                    rightMenu.setVisibility(rightMenu.getVisibility() != View.VISIBLE ? View.VISIBLE : View.INVISIBLE);
                    viewRegionLayout.setVisibility(View.INVISIBLE);

                }
            }
        });


        viewMap.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                viewRegionLayout.setVisibility(viewRegionLayout.getVisibility() != View.VISIBLE ? View.VISIBLE : View.INVISIBLE);


            }
        });
        /*locationMenu.setOnClickListener(new OnClickListener() {



			@Override
			public void onClick(View arg0) {

				addMenuCount++;
				//Toast.makeText(getApplicationContext(), "addMenuCount : value : " + addMenuCount,5000).show();
				if(addMenuCount == 4) {

					addMenuCount=0;
				}

				if((leftMenu != null ) && (rightMenu != null)) {

					leftMenu.setVisibility (leftMenu.getVisibility() != View.VISIBLE ?  View.VISIBLE : View.INVISIBLE);
					rightMenu.setVisibility (rightMenu.getVisibility() != View.VISIBLE ?  View.VISIBLE : View.INVISIBLE);
					viewRegionLayout.setVisibility (View.INVISIBLE);

				}
			}
		});*/


        // Getting Google Play availability status
        // check if GPS enabled
        if (mapFragment == null) {
            mapFragment = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map));

            mapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    map = googleMap;
                    updateTraffic();
                    updateMyLocation();
                    updateBuildings();
                    updateIndoor();
                }
            });
        }

        if (map != null)
            map.animateCamera(CameraUpdateFactory.zoomBy(.3f));
    }

    private void requestNewInterstitial() {
        mInterstitialAd = new InterstitialAd(getApplicationContext());
        if (mInterstitialAd != null)
            mInterstitialAd.setAdUnitId(getString(R.string.interstitial_ad_unit_id));


        if (mInterstitialAd != null && adRequest != null) {
            mInterstitialAd.loadAd(adRequest);

            mInterstitialAd.setAdListener(new AdListener() {
                @Override
                public void onAdClosed() {
                    // Load the next interstitial.
                    mInterstitialAd.loadAd(adRequest);
                }

            });
        }

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

    private boolean checkReady() {
        if (map == null) {
            Toast.makeText(this, R.string.map_not_ready, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    /**
     * Called when the Traffic checkbox is clicked.
     */
    public void onTrafficToggled(View view) {
        updateTraffic();
    }

    private void updateTraffic() {
        if (!checkReady()) {
            return;
        }
        map.setTrafficEnabled(mTrafficCheckbox.isChecked());
    }

    /**
     * Called when the MyLocation checkbox is clicked.
     */
    public void onMyLocationToggled(View view) {
        updateMyLocation();
    }

    private void updateMyLocation() {
        if (!checkReady()) {
            return;
        }
        map.setMyLocationEnabled(mMyLocationCheckbox.isChecked());
    }

    /**
     * Called when the Buildings checkbox is clicked.
     */
    public void onBuildingsToggled(View view) {
        updateBuildings();
    }

    private void updateBuildings() {
        if (!checkReady()) {
            return;
        }
        map.setBuildingsEnabled(mBuildingsCheckbox.isChecked());
    }

    /**
     * Called when the Indoor checkbox is clicked.
     */
    public void onIndoorToggled(View view) {
        updateIndoor();
    }

    private void updateIndoor() {
        if (!checkReady()) {
            return;
        }
        map.setIndoorEnabled(mIndoorCheckbox.isChecked());
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mGoogleApiClient.isConnected()) {
            startLocationUpdates();
            // Log.d(TAG, "Location update resumed .....................");
        }


        count++;
        if (count == 2) {

            count = 0;
        }

    }

    public List<Address> getGeocoderAddress(Context context) {
        if (mCurrentLocation != null) {
            try {
                /**
                 * Geocoder.getFromLocation - Returns an array of Addresses
                 * that are known to describe the area immediately surrounding the given latitude and longitude.
                 */
                addresses = geocoder.getFromLocation(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude(), this.geocoderMaxResults);
                // Log.d(TAG, "Address in Geocoder" + addresses);
                return addresses;
            } catch (IOException e) {
                //e.printStackTrace();
                // Log.e(TAG, "Impossible to connect to Geocoder", e);
            }
        }

        return null;
    }

    /**
     * Try to get AddressLine
     *
     * @return null or addressLine
     */
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
                info += " " + getAddressLineMap.get("Address Line 1");
            if (getAddressLineMap.get("Address Line 2") != null)
                info += " " + getAddressLineMap.get("Address Line 2");
            if (getAddressLineMap.get("Address Line 3") != null)
                info += " [" + getAddressLineMap.get("Address Line 3") + "].";

            currentAddress.setText(info);
            return info;
        } else {
            return null;
        }
    }

    /**
     * Try to get Locality
     *
     * @return null or locality
     */

    @Override
    public void onStart() {
        super.onStart();
        // Log.d(TAG, "onStart fired ..............");
        mGoogleApiClient.connect();
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
        // Log.d(TAG, "Location update stopped .......................");
    }

    @Override
    public void onStop() {
        super.onStop();
        // Log.d(TAG, "onStop fired ..............");
        mGoogleApiClient.disconnect();
        // Log.d(TAG, "isConnected ...............: " + mGoogleApiClient.isConnected());
    }

    private boolean isGooglePlayServicesAvailable() {
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        // Log.d(TAG, "onStart fired .google play .............");
        if (ConnectionResult.SUCCESS == status) {
            return true;
        } else {
            GooglePlayServicesUtil.getErrorDialog(status, this, 0).show();
            return false;
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        //  Log.d(TAG, "onConnected - isConnected ...............: " + mGoogleApiClient.isConnected());
        startLocationUpdates();
    }

    protected void startLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
        // Log.d(TAG, "Location update started ..............: ");
        mLocationCheckUpdate = true;


    }

    @Override
    public void onConnectionSuspended(int i) {

    }






	/*@Override public boolean onTouchEvent(MotionEvent event) { 
        switch (event.getAction())
		{ 
		case MotionEvent.ACTION_DOWN : 
		{ 
			startY = event.getY(); 
			break ;
		} case MotionEvent.ACTION_UP: 
		{ 
			float endY = event.getY(); 
			if (endY < startY)
			{ 
				System.out.println("Move UP");
				ll.setVisibility(View.VISIBLE);
			} 
			else { 
				ll.setVisibility(View.GONE);
			}
		} } 
		return true; } */

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // Log.d(TAG, "Connection failed: " + connectionResult.toString());

    }

    @Override
    public void onLocationChanged(Location location) {
        // Log.d(TAG, "Firing onLocationChanged..............................................");
        mCurrentLocation = location;
        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());


        stringLatitude = String.valueOf(mCurrentLocation.getLatitude());
        stringLongitude = String.valueOf(mCurrentLocation.getLongitude());

        if (!mUpdatesRequested) {
            updateUI();
            mUpdatesRequested = true;
        }

		/*try {
            new GetLocationAsync(center.latitude, center.longitude)
			.execute();

		} catch (Exception e) {
		}*/
    }

    private void updateUI() {

        // Log.d(TAG, "UI update initiated .............");
        if (null != mCurrentLocation) {
            /*tvLocation.setText("At Time: " + mLastUpdateTime + "\n" +
                "Latitude: " + lat + "\n" +
				"Longitude: " + lng + "\n" +
				"Accuracy: " + mCurrentLocation.getAccuracy() + "\n" +
				"Provider: " + mCurrentLocation.getProvider());*/


            //LatLng  currentLocation= new LatLng(Double.parseDouble(stringLatitude), Double.parseDouble(stringLongitude));


            currentLocation = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
            try {
                addresses = geocoder.getFromLocation(currentLocation.latitude, currentLocation.longitude, 1);
            } catch (Exception e) {
            }

            if (map != null) {
                globalMap = map;
                // Creates a draggable marker. Long press to drag.
                map.addMarker(new MarkerOptions()
                        .position(currentLocation)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.pegman)));

				/*map.addCircle(new CircleOptions()
				.center(new LatLng(mCurrentLocation.getAltitude(), mCurrentLocation.getLongitude()))
				.radius(400)
				.fillColor(Color.TRANSPARENT)  //default
				.strokeColor(Color.BLUE));*/

                map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15.0f));
                map.setMyLocationEnabled(true);
                map.setOnMapClickListener(new OnMapClickListener() {

                    @Override
                    public void onMapClick(LatLng mCurrentLocation) {
                        if (mCurrentLocation != null) {
                            mCurrentLocationOnMapClick = mCurrentLocation;
                            try {
                                /**
                                 * Geocoder.getFromLocation - Returns an array of Addresses
                                 * that are known to describe the area immediately surrounding the given latitude and longitude.
                                 */
                                addresses = geocoder.getFromLocation(mCurrentLocation.latitude, mCurrentLocation.longitude, 1);
                                //   Log.d(TAG, "current lat and long." + mCurrentLocation.latitude + "  " + mCurrentLocation.longitude);
                                globalMap.clear();
                                if (addressLayout != null)
                                    addressLayout.setVisibility(View.VISIBLE);
                                getAddressLine(addresses);
								/*globalMap.addCircle(new CircleOptions()
								.center(new LatLng(mCurrentLocation.latitude, mCurrentLocation.longitude))
								.radius(500)
								.fillColor(Color.TRANSPARENT)  //default
								.strokeColor(Color.BLUE));*/

                                globalMap.addMarker(new MarkerOptions()
                                        .position(mCurrentLocation)
                                        .title(" Lat:" + new DecimalFormat("##.####").format(mCurrentLocation.latitude) + ", Long:" +
                                                new DecimalFormat("##.####").format(mCurrentLocation.longitude) + " ")
                                        .draggable(true)
                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.pegman)));
                                // Log.d(TAG, "Address in Geocoder" + addresses);
                                //return addresses;
                            } catch (IOException e) {
                                //e.printStackTrace();
                                //   Log.e(TAG, "Impossible to connect to Geocoder", e);
                            }
                            //getAddressLine(addresses);
                        }


                        onMapClickCount++;
                        if (onMapClickCount == 4) {

                            onMapClickCount = 0;
                        }

                    }
                });
                map.getUiSettings().setCompassEnabled(true);
                map.getUiSettings().setRotateGesturesEnabled(true);
                map.getUiSettings().setZoomGesturesEnabled(true);
                map.getUiSettings().setZoomControlsEnabled(true);

            }
        } else {


            // Log.d(TAG, "location is null ...............");
        }


    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // This is also called by the Android framework in onResume(). The map may not be created at
        // this stage yet.
        if (map != null) {
            setLayer((String) parent.getItemAtPosition(position));
        }
    }

    private void setLayer(String layerName) {
        if (layerName.equals(getString(R.string.normal))) {
            map.setMapType(MAP_TYPE_NORMAL);
        } else if (layerName.equals(getString(R.string.hybrid))) {
            map.setMapType(MAP_TYPE_HYBRID);


        } else if (layerName.equals(getString(R.string.satellite))) {
            map.setMapType(MAP_TYPE_SATELLITE);
        } else if (layerName.equals(getString(R.string.terrain))) {
            map.setMapType(MAP_TYPE_TERRAIN);
        } else if (layerName.equals(getString(R.string.none_map))) {
            map.setMapType(MAP_TYPE_NONE);
        } else {
            Log.i("LDA", "Error setting layer with name " + layerName);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // Do nothing.
    }

    public void onScreenshot(View view) {
        takeSnapshot();
    }

    private void takeSnapshot() {
        if (map == null) {
            return;
        }


        // final ImageView snapshotHolder = (ImageView) findViewById(R.id.snapshot_holder);

        final SnapshotReadyCallback callback = new SnapshotReadyCallback() {
            @Override
            public void onSnapshotReady(Bitmap snapshot) {
                // Callback is called from the main thread, so we can modify the ImageView safely.
                saveSnapShot(snapshot);
            }


        };

        if (map == null) {
            map.setOnMapLoadedCallback(new OnMapLoadedCallback() {
                @Override
                public void onMapLoaded() {
                    map.snapshot(callback);
                }
            });
        } else {
            map.snapshot(callback);
        }
    }

    public void saveSnapShot(Bitmap snapshot) {
        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/SimPhoneAddress");
        myDir.mkdirs();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd'T'HHmmss");
        String timeStamp = dateFormat.format(new Date());

        String fname = "MapCapture" + timeStamp + ".jpg";
        File file = new File(myDir, fname);
        if (file.exists()) file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            snapshot.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
            Toast.makeText(getApplicationContext(), "Image Saved in SimPhoneAddress folder", Toast.LENGTH_LONG).show();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                Intent mediaScanIntent = new Intent(
                        Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                Uri contentUri = Uri.fromFile(file); //out is your output file
                mediaScanIntent.setData(contentUri);
                this.sendBroadcast(mediaScanIntent);
            } else {
                sendBroadcast(new Intent(
                        Intent.ACTION_MEDIA_MOUNTED,
                        Uri.parse("file://"
                                + Environment.getExternalStorageDirectory())));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void viewSnapShot(View v) {
        try {


        } catch (Exception e) {

        }
        Intent intent = new Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_PHOTO);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Dialog dialog = new Dialog(MapDetails.this);

        dialog.setContentView(R.layout.places_detail);
        dialog.setTitle("Location Details");


        TextView title = (TextView) dialog.findViewById(R.id.title);
        TextView phone = (TextView) dialog.findViewById(R.id.phone);
        TextView address = (TextView) dialog.findViewById(R.id.address);

        TextView website = (TextView) dialog.findViewById(R.id.website);
        //TextView latlong = (TextView) dialog.findViewById(R.id.latlong);
        String path = "";
        switch (requestCode) {
            case PICK_PHOTO:
                if (resultCode == RESULT_OK) {
                    Uri uri = data.getData();
                    path = pathFromUri(uri); // Now you can get the path of your selected image from here

                    Intent startView = new Intent(getApplicationContext(), ViewImageSnapShot.class);
                    startView.putExtra("pos", path);
                    startActivity(startView);
                }
                break;
            case PLACE_PICKER_REQUEST:
                if (resultCode == RESULT_OK) {

                    Place place = PlacePicker.getPlace(data, this);
                    String toastMsg = String.format("%s", place.getWebsiteUri());
                    if (place.getName() != null)
                        title.setText(place.getName());

                    if (place.getPhoneNumber() != null)
                        phone.setText(place.getPhoneNumber());
                    else
                        phone.setVisibility(View.GONE);

                    if (place.getAddress() != null)
                        address.setText(place.getAddress());

                    // price.setText(place.getPriceLevel());
                    //rating.setText(String.valueOf(place.getRating()));
                    website.setText(toastMsg);

				/*if(place.getViewport() != null)
					latlong.setText(place.getViewport().toString());
				else if(place.getLatLng() != null)
					latlong.setText(place.getLatLng().toString());
				else
					latlong.setVisibility(View.GONE);*/

                    dialog.show();
                }
                break;

        }
    }

    public void getOnMap(View v) {

    }

    private String pathFromUri(Uri uri) {
        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(uri, filePathColumn, null,
                null, null);
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String filePath = cursor.getString(columnIndex);
        cursor.close();
        return filePath;
    }

    public void panormaView(View v) {


        Intent startView = new Intent(getApplicationContext(), SplitStreetViewPanoramaAndMapDemoActivity.class);
        //Toast.makeText(getApplicationContext(), "mCurrent lat and Long " + stringLatitude + "   " + stringLongitude , 5000).show();
        startView.putExtra("Latitude", stringLatitude);
        startView.putExtra("Longitude", stringLongitude);
        startActivity(startView);
        MapDetails.this.overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        Toast.makeText(getApplicationContext(), "Panorama Will be show if Available", Toast.LENGTH_LONG).show();


    }

    public void drawCircle(View v) {
        // Construct an intent for the place picker


        try {
            PlacePicker.IntentBuilder intentBuilder =
                    new PlacePicker.IntentBuilder();
            Intent intent = intentBuilder.build(this);
            // Start the intent by requesting a result,
            // identified by a request code.
            startActivityForResult(intent, PLACE_PICKER_REQUEST);

        } catch (GooglePlayServicesRepairableException e) {
            // ...
        } catch (GooglePlayServicesNotAvailableException e) {
            // ...
        }
    }

    public void GetLocation(View v) {
        String uri = null;

        if (mCurrentLocationOnMapClick != null) {

            //geo:%f,%f - to current location on map

            uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?saddr=%f,%f (%s)", mCurrentLocationOnMapClick.latitude, mCurrentLocationOnMapClick.longitude, "Current Location");

        } else if (mCurrentLocation != null) {

            //geo:%f,%f - to current location on map

            uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?saddr=%f,%f (%s)", mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude(), "Current Location");

        }

        if (uri != null) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
            intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
            try {
                startActivity(intent);
            } catch (ActivityNotFoundException ex) {
                try {
                    Intent unrestrictedIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                    startActivity(unrestrictedIntent);
                } catch (ActivityNotFoundException innerEx) {
                    Toast.makeText(getApplicationContext(), "Please install a maps application", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    public void StartNavigation(View v) {


        //Toast.makeText(getApplicationContext(), "Choose your new Destination on Map", Toast.LENGTH_LONG).show();
        Uri gmmIntentUri = null;

        if (mCurrentLocationOnMapClick != null) {
            gmmIntentUri = Uri.parse("google.navigation:q= " + mCurrentLocationOnMapClick.latitude + "," + mCurrentLocationOnMapClick.longitude);

        } else if (mCurrentLocation != null) {
            gmmIntentUri = Uri.parse("google.navigation:q= " + mCurrentLocation.getLatitude() + "," + mCurrentLocation.getLongitude());


        }
        //geo:%f,%f - to current location on map

        //Uri gmmIntentUri = Uri.parse("geo:%f,%f?0&q=" + "delhi");

        //Uri gmmIntentUri = Uri.parse("google.navigation:q= " + "delhi");
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        try {
            startActivity(mapIntent);
        } catch (ActivityNotFoundException ex) {
            try {
                Intent unrestrictedIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                startActivity(unrestrictedIntent);
            } catch (ActivityNotFoundException innerEx) {
                Toast.makeText(getApplicationContext(), "Please install a maps application", Toast.LENGTH_LONG).show();
            }
        }
    }

    public void AroundMe(View v) {

        try {

        } catch (Exception e) {

        }

        Intent aroundMe = new Intent(MapDetails.this, LocationNearMe.class);
        aroundMe.putExtra("Latitude", stringLatitude);
        aroundMe.putExtra("Longitude", stringLongitude);
        startActivity(aroundMe);
    }

    public void Search(View v) {

        Uri gmmIntentUri = null;

        if (mCurrentLocationOnMapClick != null) {
            gmmIntentUri = Uri.parse("geo:%f,%f?0&q=" + mCurrentLocationOnMapClick.latitude + "," + mCurrentLocationOnMapClick.longitude);

        } else if (mCurrentLocation != null) {
            gmmIntentUri = Uri.parse("geo:%f,%f?0&q=" + mCurrentLocation.getLatitude() + "," + mCurrentLocation.getLongitude());


        }

        //geo:%f,%f - to current location on map
        //Toast.makeText(getApplicationContext(), "Enter your new search location on Map", Toast.LENGTH_LONG).show();

        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");

        try {
            startActivity(mapIntent);
        } catch (ActivityNotFoundException ex) {
            try {
                Intent unrestrictedIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                startActivity(unrestrictedIntent);
            } catch (ActivityNotFoundException innerEx) {
                Toast.makeText(getApplicationContext(), "Please install a maps application", Toast.LENGTH_LONG).show();
            }
        }


    }

    public void Share(View v) {

        try {
            if (mInterstitialAd.isLoaded())
                mInterstitialAd.show();

        } catch (Exception e) {

        }

        String message = null;


        if (mCurrentLocationOnMapClick != null) {
            message = "http://maps.google.com/maps?q=" + mCurrentLocationOnMapClick.latitude + "," + mCurrentLocationOnMapClick.longitude + "&iwloc=A";

        } else if (mCurrentLocation != null) {
            message = "http://maps.google.com/maps?q=" + mCurrentLocation.getLatitude() + "," + mCurrentLocation.getLongitude() + "&iwloc=A";

        }

        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("text/plain");
        share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        share.putExtra(Intent.EXTRA_SUBJECT,
                "My Current Postal Location ." + getAddressLine(addresses) +
                        "" +
                        "\n\nCheck on Google Map");
        share.putExtra(
                Intent.EXTRA_TEXT, message);
        startActivity(Intent.createChooser(share, "Share link!"));
    }

    @Override
    public void onBackPressed() {
        if (mInterstitialAd.isLoaded() && mInterstitialAd != null)
            mInterstitialAd.show();
        super.onBackPressed();
    }

    private class GetLocationAsync extends AsyncTask<String, Void, String> {

        // boolean duplicateResponse;
        double x, y;
        StringBuilder str;

        public GetLocationAsync(double latitude, double longitude) {

            x = latitude;
            y = longitude;
        }

        @Override
        protected void onPreExecute() {
            currentAddress.setText(" Getting location ");
        }

        @Override
        protected String doInBackground(String... params) {

            try {
                geocoder = new Geocoder(MapDetails.this, Locale.ENGLISH);
                addresses = geocoder.getFromLocation(x, y, 1);
                str = new StringBuilder();
                if (Geocoder.isPresent()) {
                    try {
                        Address returnAddress = addresses.get(0);

                        String city = returnAddress.getLocality();
                        String country = returnAddress.getCountryName();
                        String addressState = returnAddress.getAdminArea();
                        //String addressLine = getAddressLine(returnAddress);

                        str.append(city + "");

                    } catch (Exception e) {
                    }
                } else {
                }
            } catch (IOException e) {
                Log.e("tag", e.getMessage());
            }
            return null;

        }

        @Override
        protected void onPostExecute(String result) {
            try {
                //if(getAddressLine(getApplicationContext()) == null)
                currentAddress.setText("Getting Location..");
				/*else
					currentAddress.setText(getAddressLine(getApplicationContext()));*/
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void onProgressUpdate(Void... values) {

        }
    }
}