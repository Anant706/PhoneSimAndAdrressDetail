package com.makeinindia.controller;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.text.DecimalFormat;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SystemInfoPage2.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SystemInfoPage2#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SystemInfoPage2 extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ERROR = null;
    public long total = 0;
    public long free = 0;
    TextView ramSize, ramUsed, ramFree, ramJvmSize;
    BufferedReader localBufferedReader;
    String str;
    String[] arrayOfString;
    Object localObject3;
    TextView sd_total, sd_free, sdcard_free, sdcard_total;
    List<PackageInfo> packs;
    AppDetails appDetails;
    View rootView;
    InterstitialAd mInterstitialAd;
    AdView adView;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private OnFragmentInteractionListener mListener;

    public SystemInfoPage2() {
        // Required empty public constructor
    }

    public static boolean externalMemoryAvailable() {
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
    }

    public static String getAvailableInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return formatSize(availableBlocks * blockSize);
    }

    public static String getTotalInternalMemorySize() {
        File path = Environment.getDataDirectory();
        //

//path =
        //Log.d("Total" , "device total size " + )
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long totalBlocks = stat.getBlockCount();
        return formatSize(totalBlocks * blockSize);
    }

    public static String getAvailableExternalMemorySize() {
        if (externalMemoryAvailable()) {
            // Log.d("present","sd card " );
            File path = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSize();
            long availableBlocks = stat.getAvailableBlocks();
            return formatSize(availableBlocks * blockSize);
        } else {
            return ERROR;
        }
    }

    public static String getTotalExternalMemorySize() {
        if (externalMemoryAvailable()) {
            File path = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSize();
            long totalBlocks = stat.getBlockCount();
            return formatSize(totalBlocks * blockSize);
        } else {
            return ERROR;
        }
    }

    public static String formatSize2(long size) {
        String suffix = null;

        if (size >= 1024) {
            suffix = "KB";
            size /= 1024;
            if (size >= 1024) {
                suffix = "MB";
                size /= 1024;
            }
        }

        StringBuilder resultBuffer = new StringBuilder(Long.toString(size));

        int commaOffset = resultBuffer.length() - 3;
        while (commaOffset > 0) {
            resultBuffer.insert(commaOffset, ',');
            commaOffset -= 3;
        }

        if (suffix != null) resultBuffer.append(suffix);
        return resultBuffer.toString();
    }

    public static String formatSize(double value) {


        DecimalFormat twoDecimalForm = new DecimalFormat("#.##");
        double kb = value / 1024.0;
        double mb = value / 1048576.0;
        double gb = value / 1073741824.0;
        double tb = value / (1073741824.0 * 1024);
        String lastValue = "";
        if (tb > 1) {
            lastValue = twoDecimalForm.format(tb).concat(" TB");
        } else if (gb > 1) {
            lastValue = twoDecimalForm.format(gb).concat(" GB");
        } else if (mb > 1) {
            lastValue = twoDecimalForm.format(mb).concat(" MB");
        } else {
            lastValue = twoDecimalForm.format(value).concat(" KB");
        }
        return lastValue;
    }

    // TODO: Rename and change types and number of parameters
    public static SystemInfoPage2 newInstance(String param1, String param2) {
        SystemInfoPage2 fragment = new SystemInfoPage2();
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
        rootView = inflater.inflate(R.layout.fragment_system_info_page2, container, false);
        ramSize = (TextView) rootView.findViewById(R.id.ram_size);
        ramUsed = (TextView) rootView.findViewById(R.id.ram_used);
        ramFree = (TextView) rootView.findViewById(R.id.ram_free);
        //ramJvmSize = (TextView) rootView.findViewById(R.id.ram_jvm_memory);
        sd_total = (TextView) rootView.findViewById(R.id.sd_total);
        sd_free = (TextView) rootView.findViewById(R.id.sd_free);
        sdcard_free = (TextView) rootView.findViewById(R.id.sd_used);
        sdcard_total = (TextView) rootView.findViewById(R.id.sdcard_total);
        try {
            localBufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(new File("/proc/meminfo"))));
        } catch (Exception e) {

        }
        appDetails = new AppDetails(getActivity());
        getMemorySize();

        requestNewInterstitial();

        Boolean isSDPresent = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);

        if (isSDPresent) {
            // yes SD-card is present
        } else {
            // Sorry
        }
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

    public void updateInfo() {

        getMemorySize();
        double jvmMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        if (ramSize != null)
            ramSize.setText(calSize(total));
        //ramJvmSize.setText("JVM used memory " + calSize(jvmMemory));
        if (ramSize != null)
            ramFree.setText(calSize(free));
        if (ramUsed != null)
            ramUsed.setText(calSize(total - free));

        if (sd_total != null)
            sd_total.setText(getTotalInternalMemorySize());
        if (sd_free != null)
            sd_free.setText(getAvailableInternalMemorySize());
        if (sdcard_free != null)
            sdcard_free.setText(getAvailableExternalMemorySize());
        if (sdcard_total != null)
            sdcard_total.setText(getTotalExternalMemorySize());
    }

    public String calSize(double value) {

   /*     ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        activityManager.getMemoryInfo(mi);
        long availableMegs = mi.availMem / 1048576L;*/

        DecimalFormat twoDecimalForm = new DecimalFormat("#.##");
        double kb = value / 1024.0;
        double mb = value / 1048576.0;
        double gb = value / 1073741824.0;
        double tb = value / (1073741824.0 * 1024);
        String lastValue = "";
        if (tb > 1) {
            lastValue = twoDecimalForm.format(tb).concat(" TB");
        } else if (gb > 1) {
            lastValue = twoDecimalForm.format(gb).concat(" GB");
        } else if (mb > 1) {
            lastValue = twoDecimalForm.format(mb).concat(" MB");
        } else {
            lastValue = twoDecimalForm.format(value).concat(" KB");
        }
        return lastValue;
    }

    @Override
    public void onResume() {
        super.onResume();

        updateInfo();

    }

    private void getMemorySize() {
        final Pattern PATTERN = Pattern.compile("([a-zA-Z]+):\\s*(\\d+)");

        String line;
        try {
            RandomAccessFile reader = new RandomAccessFile("/proc/meminfo", "r");
            while ((line = reader.readLine()) != null) {
                Matcher m = PATTERN.matcher(line);
                if (m.find()) {
                    String name = m.group(1);
                    String size = m.group(2);
                    // System.out.println("Ram free: " + m);
                    if (name.equalsIgnoreCase("MemTotal")) {
                        total = Long.parseLong(size);
                    } else if (name.equalsIgnoreCase("MemFree") ||
                            name.equalsIgnoreCase("SwapFree")) {
                        free = Long.parseLong(size);
                    }
                }
            }
            reader.close();

            total *= 1024;
            free *= 1024;
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public String getTotalRAM() {

        RandomAccessFile reader = null;
        String load = null;
        DecimalFormat twoDecimalForm = new DecimalFormat("#.##");
        double totRam = 0;
        String lastValue = "";
        try {
            reader = new RandomAccessFile("/proc/meminfo", "r");
            load = reader.readLine();

            // Get the Number value from the string
            Pattern p = Pattern.compile("(\\d+)");
            Matcher m = p.matcher(load);
            String value = "";
            while (m.find()) {
                value = m.group(1);

            }
            reader.close();

            totRam = Double.parseDouble(value);
            // totRam = totRam / 1024;

            double mb = totRam / 1024.0;
            double gb = totRam / 1048576.0;
            double tb = totRam / 1073741824.0;

            if (tb > 1) {
                lastValue = twoDecimalForm.format(tb).concat(" TB");
            } else if (gb > 1) {
                lastValue = twoDecimalForm.format(gb).concat(" GB");
            } else if (mb > 1) {
                lastValue = twoDecimalForm.format(mb).concat(" MB");
            } else {
                lastValue = twoDecimalForm.format(totRam).concat(" KB");
            }


        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            // Streams.close(reader);
        }
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
}
