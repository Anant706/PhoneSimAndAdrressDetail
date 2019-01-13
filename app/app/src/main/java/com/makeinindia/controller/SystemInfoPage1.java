package com.makeinindia.controller;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.common.api.GoogleApiClient;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SystemInfoPage1.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SystemInfoPage1#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SystemInfoPage1 extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final int SECOND = 1000;
    private static final int MINUTE = 60 * SECOND;
    private static final int HOUR = 60 * MINUTE;
    private static final int DAY = 24 * HOUR;
    public String phonestate, roamingState;
    TextView batt_level, batt_status, batt_tech, batt_volt, batt_temp, batt_health, system_uptime;
    Intent batteryIntent;
    ProgressBar pb;
    Button batterUsage;
    InterstitialAd mInterstitialAd;
    AdView adView;
    View rootView;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private OnFragmentInteractionListener mListener;
    private GoogleApiClient client;

    public SystemInfoPage1() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static SystemInfoPage1 newInstance(String param1, String param2) {
        SystemInfoPage1 fragment = new SystemInfoPage1();
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

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_system_info_page1, container, false);
        batteryIntent = getActivity().registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        batt_level = (TextView) rootView.findViewById(R.id.batt_level_value);
        batt_status = (TextView) rootView.findViewById(R.id.batt_status_val);
        batt_tech = (TextView) rootView.findViewById(R.id.batt_tech_val);
        batt_volt = (TextView) rootView.findViewById(R.id.batt_voltage_val);
        batt_temp = (TextView) rootView.findViewById(R.id.batt_temp_val);
        batt_health = (TextView) rootView.findViewById(R.id.batt_health_val);
        pb = (ProgressBar) rootView.findViewById(R.id.progressBar);
        system_uptime = (TextView) rootView.findViewById(R.id.system_up_time);


        updateValues();

        requestNewInterstitial();

        return rootView;
    }

    private void requestNewInterstitial() {
        mInterstitialAd = new InterstitialAd(getActivity());
        if (mInterstitialAd != null)
            mInterstitialAd.setAdUnitId(getString(R.string.interstitial_ad_unit_id));

        AdRequest adRequest = new AdRequest.Builder().build();
        if (mInterstitialAd != null)
            mInterstitialAd.loadAd(adRequest);

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

    @Override
    public void onResume() {
        super.onResume();
        updateValues();
    }

    public void updateValues() {
        if (batt_level != null)
            batt_level.setText(String.valueOf(batteryLevel()));
        if (batt_status != null)
            batt_status.setText(batteryStatus());
        if (batt_tech != null)
            batt_tech.setText(batteryTech());
        if (batt_volt != null)
            batt_volt.setText(batteryVolt());
        if (batt_temp != null)
            batt_temp.setText(batteryTemp());
        if (batt_health != null)
            batt_health.setText(batteryHealth());
        if (system_uptime != null)
            system_uptime.setText(uptimeMillis());
    }

    private String uptimeMillis() {

        long ms = SystemClock.elapsedRealtime();

        //Log.d("hello", "uptime : " + ms);
        StringBuffer text = new StringBuffer("");
        if (ms > DAY) {
            text.append(ms / DAY).append(" days ");
            ms %= DAY;
        }
        if (ms > HOUR) {
            text.append(ms / HOUR).append(" hours ");
            ms %= HOUR;
        }
        if (ms > MINUTE) {
            text.append(ms / MINUTE).append(" min. ");
            ms %= MINUTE;
        }
        if (ms > SECOND) {
            text.append(ms / SECOND).append(" sec.");
            ms %= SECOND;
        }
        //text.append(ms + " ms");
        return text.toString();
    }

    public String batteryHealth() {

        int health = batteryIntent.getIntExtra("health", BatteryManager.BATTERY_HEALTH_UNKNOWN);
        String strHealth = null;
        if (health == BatteryManager.BATTERY_HEALTH_GOOD) {
            strHealth = "Good";
        } else if (health == BatteryManager.BATTERY_HEALTH_OVERHEAT) {
            strHealth = "Over Heat";
        } else if (health == BatteryManager.BATTERY_HEALTH_DEAD) {
            strHealth = "Dead";
        } else if (health == BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE) {
            strHealth = "Over Voltage";
        } else if (health == BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE) {
            strHealth = "Unspecified Failure";
        } else {
            strHealth = "Unknown";
        }
        return strHealth;
    }

    public String batteryStatus() {

        String status = null;

        switch (batteryIntent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1)) {

            case 1:
                status = "AC Charging";
                break;
            case 2:
                status = "USB Charging";
                break;
            case 4:
                status = "WIRELESS Charging";
                break;
            default:
                status = "NOT Charging";
                break;
        }

        return status;
    }

    public float batteryLevel() {
        int level = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

        // Error checking that probably isn't needed but I added just in case.
        if (level == -1 || scale == -1) {
            return 50.0f;
        }
        pb.setProgress(level);
        return ((float) level / (float) scale) * 100.0f;
    }

    public String batteryTech() {

        // return batteryIntent.getIntExtra(BatteryManager.EXTRA_TECHNOLOGY,0);
        String technology = batteryIntent.getExtras().getString(BatteryManager.EXTRA_TECHNOLOGY);
        return technology;
    }

    public String batteryVolt() {
        int voltage = batteryIntent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1);
        return String.valueOf(voltage);
    }

    public String batteryTemp() {
        float temp = ((float) batteryIntent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1)) / 10;
        return String.valueOf(temp) + " *C";
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


}
