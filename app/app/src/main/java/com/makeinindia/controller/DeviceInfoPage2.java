package com.makeinindia.controller;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.telephony.TelephonyManager;
import android.text.format.Formatter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import org.apache.http.conn.util.InetAddressUtils;

import java.io.File;
import java.io.FileFilter;
import java.io.RandomAccessFile;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DeviceInfoPage2.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DeviceInfoPage2#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DeviceInfoPage2 extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final int SECOND = 1000;
    private static final int MINUTE = 60 * SECOND;
    private static final int HOUR = 60 * MINUTE;
    private static final int DAY = 24 * HOUR;
    public String phonestate, roamingState;
    TextView processor, core, max_frequency, instruct_set, network_type, ip_address, wifi_add, telephony, wifi, ip_add, operator, country, roaming, service_state;
    TelephonyManager telephonyManager;
    View rootView;
    InterstitialAd mInterstitialAd;
    AdRequest adRequest;
    AdView adView;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public DeviceInfoPage2() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DeviceInfoPage1.
     */
    // TODO: Rename and change types and number of parameters
    public static DeviceInfoPage2 newInstance(String param1, String param2) {
        DeviceInfoPage2 fragment = new DeviceInfoPage2();
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
        rootView = inflater.inflate(R.layout.fragment_device_info_page2, container, false);

        // processor = (TextView)rootView.findViewById(R.id.processor);
        core = (TextView) rootView.findViewById(R.id.core);
        max_frequency = (TextView) rootView.findViewById(R.id.max_frequency);
        instruct_set = (TextView) rootView.findViewById(R.id.instruction_set);
        network_type = (TextView) rootView.findViewById(R.id.network_type);
        // ip_address = (TextView)rootView.findViewById(R.id.ip_address);
        //  telephony = (TextView)rootView.findViewById(R.id.telephony);
        // wifi = (TextView)rootView.findViewById(R.id.wifi);
        ip_add = (TextView) rootView.findViewById(R.id.ip_add);
        wifi_add = (TextView) rootView.findViewById(R.id.wifi_add);
        operator = (TextView) rootView.findViewById(R.id.operator);
        country = (TextView) rootView.findViewById(R.id.country);
        roaming = (TextView) rootView.findViewById(R.id.roaming);
        service_state = (TextView) rootView.findViewById(R.id.service_state);
        telephonyManager = (TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE);
        telephonyManager.listen(new PhoneStateListener() {
            @Override
            public void onServiceStateChanged(ServiceState serviceStateTemp) {
                super.onServiceStateChanged(serviceStateTemp);

                // You can also check roaming state using this
                if (serviceStateTemp.getRoaming()) {
                    // In Roaming
                } else {
                    // Not in Roaming
                }
                switch (serviceStateTemp.getState()) {

                    case 2:
                        phonestate = "STATE_EMERGENCY_ONLY";
                        if (service_state != null)
                            service_state.setText(phonestate);
                        break;
                    case 0:
                        phonestate = "STATE_IN_SERVICE";
                        if (service_state != null)
                            service_state.setText(phonestate);
                        break;
                    case 1:
                        phonestate = "STATE_OUT_OF_SERVICE";
                        if (service_state != null)
                            service_state.setText(phonestate);
                        break;
                    case 3:
                        phonestate = "STATE_POWER_OFF";
                        if (service_state != null)
                            service_state.setText(phonestate);
                        break;
                    default:
                        phonestate = "Unknown";
                        if (service_state != null)
                            service_state.setText(phonestate);
                        break;
                }
            }
        }, PhoneStateListener.LISTEN_SERVICE_STATE);
        adRequest = new AdRequest.Builder().build();
        requestNewInterstitial();
        return rootView;
    }
    @Override
    public void onPause() {
        super.onPause();

    }
    @Override
    public void onDestroyView() {
        if(mInterstitialAd != null && mInterstitialAd.isLoaded()){
            mInterstitialAd.show();
        }
        super.onDestroyView();
    }
    private void requestNewInterstitial() {
        mInterstitialAd = new InterstitialAd(getActivity());
        if (mInterstitialAd != null)
            mInterstitialAd.setAdUnitId(getString(R.string.interstitial_ad_unit_id));

        if (mInterstitialAd != null) {
            mInterstitialAd.loadAd(adRequest);
            mInterstitialAd.show();
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
    public void onResume() {
        super.onResume();
        Log.d("Add","Add id coming 11" );
        if (core != null)
            core.setText(String.valueOf(getNumCores()));
        if (instruct_set != null)
            instruct_set.setText(getInstructionSets());
        String cpuMaxFreq = "";
        RandomAccessFile reader = null;
        try {
            reader = new RandomAccessFile("/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq", "r");
            cpuMaxFreq = reader.readLine();
            reader.close();
        } catch (Exception e) {

        }

        if (operator != null)
            operator.setText(telephonyManager.getNetworkOperatorName());
        if (country != null)
            country.setText(telephonyManager.getNetworkCountryIso());
        if (max_frequency != null)
            max_frequency.setText(cpuMaxFreq + "Hz");
        //network
        if (network_type != null)
            network_type.setText(getPhoneType());
        if (roaming != null)
            roaming.setText(roamingState());
        if (ip_add != null)
            ip_add.setText(getIPAddress(true));
        if (wifi_add != null)
            wifi_add.setText(getMACAddress("wlan0"));
        // if(ref)
    }

    public String getMACAddress(String interfaceName) {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());

            for (NetworkInterface intf : interfaces) {
                if (interfaceName != null) {
                    if (!intf.getName().equalsIgnoreCase(interfaceName)) continue;
                }
                byte[] mac = intf.getHardwareAddress();
                if (mac == null) return "No H/W";
                StringBuilder buf = new StringBuilder();
                for (int idx = 0; idx < mac.length; idx++)
                    buf.append(String.format("%02X:", mac[idx]));
                if (buf.length() > 0) buf.deleteCharAt(buf.length() - 1);
                return buf.toString();
            }
        } catch (Exception ex) {
        } // for now eat exceptions
        return "";

    }

    public String getIPAddress(boolean useIPv4) {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());

            for (NetworkInterface intf : interfaces) {
                List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
                for (InetAddress addr : addrs) {
                    if (!addr.isLoopbackAddress()) {
                        String sAddr = Formatter.formatIpAddress(addr.hashCode());
                        boolean isIPv4 = InetAddressUtils.isIPv4Address(sAddr);

                        if (isIPv4) {
                            return sAddr;
                        } else {
                            int delim = sAddr.indexOf('%'); // drop ip6 port suffix
                            return delim < 0 ? sAddr : sAddr.substring(0, delim);
                        }
                    }
                }
            }
        } catch (Exception ex) {
        } // for now eat exceptions
        return "";
    }

    private String roamingState() {
        //getting information if phone is in roaming
        boolean isRoaming = telephonyManager.isNetworkRoaming();
        if (isRoaming)
            return "In Roaming";
        else
            return "Not Roaming";
    }

    private String getPhoneType() {
        //Get the phone type
        String strphoneType = "";

        int phoneType = telephonyManager.getPhoneType();

        switch (phoneType) {
            case (TelephonyManager.PHONE_TYPE_CDMA):
                strphoneType = "CDMA";
                break;
            case (TelephonyManager.PHONE_TYPE_GSM):
                strphoneType = "GSM";
                break;
            case (TelephonyManager.PHONE_TYPE_NONE):
                strphoneType = "NONE";
                break;
        }
        return strphoneType;
    }

    private String getInstructionSets() {
        String str2 = Build.CPU_ABI;
        String str1 = str2;
        if (Build.VERSION.SDK_INT >= 8) {
            str1 = str2;
            if (Build.CPU_ABI2 != null) {
                str1 = str2;
                if (!Build.CPU_ABI2.equals("unknown")) {
                    str1 = str2 + ", " + Build.CPU_ABI2;
                }
            }
        }
        return str1;
    }

    private int getNumCores() {
        //Private Class to display only CPU devices in the directory listing
        class CpuFilter implements FileFilter {
            @Override
            public boolean accept(File pathname) {
                //Check if filename is "cpu", followed by a single digit number
                return Pattern.matches("cpu[0-9]+", pathname.getName());
            }
        }

        try {
            //Get directory containing CPU info
            File dir = new File("/sys/devices/system/cpu/");
            //Filter to only list the devices we care about
            File[] files = dir.listFiles(new CpuFilter());
            //Return the number of cores (virtual CPU devices)
            return files.length;
        } catch (Exception e) {
            //Default to return 1 core
            return 1;
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
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
