package com.makeinindia.controller;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DeviceInfoPage1.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DeviceInfoPage1#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DeviceInfoPage1 extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    TextView device_name, device_user_name, brand_name, prod_name, imei_name, back_camera, front_camera, resolution, density, refresh, physical_size, version, api_level, bin_type, product_code;
    TelephonyManager telephonyManager;
    Display display;
    DisplayMetrics dm;
    DecimalFormat twoDecimalForm;
    double deviceSize;
    View rootView;
    InterstitialAd mInterstitialAd;
    AdRequest adRequest;
    AdView adView;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private OnFragmentInteractionListener mListener;

    public DeviceInfoPage1() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static DeviceInfoPage1 newInstance(String param1, String param2) {
        DeviceInfoPage1 fragment = new DeviceInfoPage1();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_device_info_page1, container, false);

        device_name = (TextView) rootView.findViewById(R.id.device_name);
        device_user_name = (TextView) rootView.findViewById(R.id.device_user_name);
        brand_name = (TextView) rootView.findViewById(R.id.brand_name);
        prod_name = (TextView) rootView.findViewById(R.id.product_name);
        imei_name = (TextView) rootView.findViewById(R.id.imei_name);
        back_camera = (TextView) rootView.findViewById(R.id.back_camera);
        front_camera = (TextView) rootView.findViewById(R.id.front_camera);
        resolution = (TextView) rootView.findViewById(R.id.resolution);
        density = (TextView) rootView.findViewById(R.id.density);
        refresh = (TextView) rootView.findViewById(R.id.refresh);
        physical_size = (TextView) rootView.findViewById(R.id.physical_size);
        version = (TextView) rootView.findViewById(R.id.version);
        api_level = (TextView) rootView.findViewById(R.id.api_level);

        telephonyManager = (TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE);
        display = ((WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        deviceSize = Math.sqrt(((dm.widthPixels / dm.xdpi) * (dm.widthPixels / dm.xdpi))
                + ((dm.heightPixels / dm.ydpi) * (dm.heightPixels / dm.ydpi)));

        twoDecimalForm = new DecimalFormat("#.##");
        adRequest = new AdRequest.Builder().build();
        requestNewInterstitial();
        return rootView;
    }

    private void requestNewInterstitial() {
        mInterstitialAd = new InterstitialAd(getActivity());
        if (mInterstitialAd != null)
            mInterstitialAd.setAdUnitId(getString(R.string.interstitial_ad_unit_id));

        if (mInterstitialAd != null) {
            mInterstitialAd.loadAd(adRequest);
        }

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                // Load the next interstitial.
                mInterstitialAd.loadAd(adRequest);
            }

        });
        adView = (AdView) rootView.findViewById(R.id.myAdView);
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

        if (adView != null)
            adView.loadAd(adRequest);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }



    @Override
    public void onResume() {
        super.onResume();

        if (device_name != null)
            device_name.setText(Build.MODEL);
        if (device_user_name != null)
            device_user_name.setText(getPhoneName());
        if (brand_name != null)
            brand_name.setText(Build.BRAND);
        if (prod_name != null)
            prod_name.setText(Build.PRODUCT);
        if (imei_name != null)
            imei_name.setText(telephonyManager.getDeviceId());

        if (resolution != null)
            resolution.setText(dm.widthPixels + "w" + " X " + dm.heightPixels + "h");
        if (density != null)
            density.setText(getDensityInfo());
        if (refresh != null)
            refresh.setText(display.getRefreshRate() + "Hz");
        if (physical_size != null)
            physical_size.setText(twoDecimalForm.format(deviceSize) + "\"(" + twoDecimalForm.format(deviceSize * 2.54D) + " cm)");
        if (version != null)
            version.setText(Build.VERSION.RELEASE + "( " + getAndroidVersionName() + ")");
        if (api_level != null)
            api_level.setText(String.valueOf(Build.VERSION.SDK_INT));

        new TaskActivityFront().execute();
        new TaskActivityBack().execute();
    }

    public String getPhoneName() {
        BluetoothAdapter myDevice = BluetoothAdapter.getDefaultAdapter();
        String deviceName;
        if (myDevice != null) {
            deviceName = myDevice.getName();
            return deviceName;
        } else return " ";
    }

    private String getAndroidVersionName() {
        int i = Build.VERSION.SDK_INT;
        if (i == 7) {
            return "Eclair";
        }
        if (i == 8) {
            return "Froyo";
        }
        if ((i == 9) || (i == 10)) {
            return "Gingerbread";
        }
        if ((i == 11) || (i == 12) || (i == 13)) {
            return "Honeycomb";
        }
        if ((i == 14) || (i == 15)) {
            return "IceCream Sandwich";
        }
        if ((i == 16) || (i == 17) || (i == 18)) {
            return "Jelly Bean";
        }
        if (i == 19) {
            return "KitKat";
        }
        if ((i == 21) || (i == 22)) {
            return "Lollipop";
        }
        if ((i == 23)) {
            return "Marshmallow";
        }
        if ((i == 24) || (i == 25)) {
            return "Naugat";
        }
        return null;
    }

    String getDensityInfo() {
        DisplayMetrics localDisplayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(localDisplayMetrics);
        if (localDisplayMetrics.densityDpi == 120) {
            return "120 dpi (Low)";
        }
        if (localDisplayMetrics.densityDpi == 160) {
            return "160 dpi (Medium)";
        }
        if (localDisplayMetrics.densityDpi == 240) {
            return "240 dpi (High)";
        }
        if (localDisplayMetrics.densityDpi == 320) {
            return "320 dpi (X High)";
        }
        if (localDisplayMetrics.densityDpi == 480) {
            return "480 dpi (XX High)";
        }
        if (localDisplayMetrics.densityDpi == 640) {
            return "640 dpi (XXX High)";
        }
        if (localDisplayMetrics.densityDpi == 213) {
            return "TV";
        }
        if (localDisplayMetrics.densityDpi == 400) {
            return "400 dpi";
        }
        return "Unknown";
    }

    public String getCameraMegaPixels(int val) {
        Camera camera = null;
        Camera.Parameters params;
        List<Size> sizes = null;
        Size result = null;
        double wid = 0;
        double heigh = 0 ;
        if (camera == null) {
            try {
                camera = Camera.open(val);
                params = camera.getParameters();
                sizes = params.getSupportedPictureSizes();
            } catch (RuntimeException e) {
                Log.e("Camera Error..", e.getMessage());
            }
        }



        ArrayList<Integer> arrayListForWidth = new ArrayList<Integer>();
        ArrayList<Integer> arrayListForHeight = new ArrayList<Integer>();


        if (sizes != null) {
            for (int i = 0; i < sizes.size(); i++) {
                result = sizes.get(i);
                arrayListForWidth.add(result.width);
                arrayListForHeight.add(result.height);
            }
        }
        if (arrayListForWidth.size() != 0 && arrayListForHeight.size() != 0) {
            System.out.println("max W :" + Collections.max(arrayListForWidth));              // Gives Maximum Width
            System.out.println("max H :" + Collections.max(arrayListForHeight));                 // Gives Maximum Height
            //  System.out.println("Back Megapixel :"+ calSize(Collections.max(arrayListForWidth) * Collections.max(arrayListForHeight) / 1024000 ) );
            wid = Collections.max(arrayListForWidth);
            heigh = Collections.max(arrayListForHeight);
        }

        Log.d("Camera size ", "Camera pixel " + calSize(wid * heigh / 1024000));
        if(camera!=null) {
            camera.release();
        }
        return calSize(wid * heigh / 1024000);
    }

    public String calSize(double value) {

        String lastValue = " ";

        lastValue = twoDecimalForm.format(value).concat(" Megapixel");
        return lastValue;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public class TaskActivityFront extends AsyncTask<String, Void, String> {
        String frontCamera;

        @Override
        protected String doInBackground(String... params) {
            frontCamera = getCameraMegaPixels(1);

            return null;
        }

        @Override
        protected void onPostExecute(String s) {

            if (front_camera != null)
                front_camera.setText(frontCamera);
        }
    }
    public class TaskActivityBack extends AsyncTask<String, Void, String> {
        String backCameraValue;

        @Override
        protected String doInBackground(String... params) {

            backCameraValue = getCameraMegaPixels(0);
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            if (back_camera != null) {
                back_camera.setText(backCameraValue);
            }

        }
    }
}
