package com.makeinindia.controller;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AudioManager.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AudioManager#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AudioManager extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final int ALARM = 1003;
    private static final int NOTIFICATION = 1002;
    private static final int RINGTONE = 1001;
    SharedPreferences.Editor ed;
    CheckBox vibrate_call;
    CheckBox out_call;
    SharedPreferences sp;
    int i;
    ImageButton ringtoneButton, vibrationButton, notificationButton, alarmButton;
    View rootView;
    InterstitialAd mInterstitialAd;
    AdRequest adRequest;
    AdView adView;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private OnFragmentInteractionListener mListener;

    public AudioManager() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AudioManager.
     */
    // TODO: Rename and change types and number of parameters
    public static AudioManager newInstance(String param1, String param2) {
        AudioManager fragment = new AudioManager();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public static AudioManager newInstance() {
        AudioManager fragment = new AudioManager();
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


        adRequest = new AdRequest.Builder().build();

        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_audio_manager, container, false);


        ringtoneButton = (ImageButton) rootView.findViewById(R.id.ringtone_select);
        vibrationButton = (ImageButton) rootView.findViewById(R.id.vibration_select);
        notificationButton = (ImageButton) rootView.findViewById(R.id.notification_select);
        alarmButton = (ImageButton) rootView.findViewById(R.id.alarm_select);

        this.sp = getActivity().getSharedPreferences("vibrate_when_ringing", 0);
        this.ed = this.sp.edit();
        this.vibrate_call = ((CheckBox) rootView.findViewById(R.id.virb_ringing));
        i = android.provider.Settings.System.getInt(getActivity().getApplicationContext().getContentResolver(),
                "vibrate_when_ringing", 0);

        Log.d("Ringer", "sys vibrate value " + i);
        if (i == 0)  // setting is not checked
            this.vibrate_call.setChecked(false);
        else if (i == 1)
            this.vibrate_call.setChecked(true);

        requestNewInterstitial();

        this.vibrate_call.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton paramAnonymousCompoundButton, boolean paramAnonymousBoolean) {
                AudioManager.this.ed.putBoolean("vibrate_when_ringing", paramAnonymousBoolean);
                AudioManager.this.ed.commit();
                if (i == 0)
                    android.provider.Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(),
                            "vibrate_when_ringing", 1);
                else if (i == 1)
                    android.provider.Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(),
                            "vibrate_when_ringing", 0);
            }
        });
        ringtoneButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                final Uri currentTone = RingtoneManager.getActualDefaultRingtoneUri(getActivity(), 1);
                Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_RINGTONE);
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select Ringtone");
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, currentTone);
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, true);
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, false);

                startActivityForResult(intent, RINGTONE);

            }
        });

        notificationButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent paramAnonymousView = new Intent("android.intent.action.RINGTONE_PICKER");
                paramAnonymousView.putExtra("android.intent.extra.ringtone.TYPE", 2);
                paramAnonymousView.putExtra("android.intent.extra.ringtone.TITLE", "Select Notification Tone");
                paramAnonymousView.putExtra("android.intent.extra.ringtone.EXISTING_URI", RingtoneManager.getActualDefaultRingtoneUri(AudioManager.this.getActivity(), 2));
                AudioManager.this.startActivityForResult(paramAnonymousView, NOTIFICATION);
            }
        });

        vibrationButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.setClassName("com.android.settings",
                        "com.android.settings.personalvibration.SelectPatternDialog");
                try {
                    startActivity(intent);
                } catch (Exception e) {

                }
            }
        });


        alarmButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent paramAnonymousView = new Intent("android.intent.action.RINGTONE_PICKER");
                paramAnonymousView.putExtra("android.intent.extra.ringtone.TYPE", 4);
                paramAnonymousView.putExtra("android.intent.extra.ringtone.TITLE", "Select Alarm Tone");
                paramAnonymousView.putExtra("android.intent.extra.ringtone.EXISTING_URI", RingtoneManager.getActualDefaultRingtoneUri(AudioManager.this.getActivity(), 4));
                AudioManager.this.startActivityForResult(paramAnonymousView, ALARM);
            }
        });
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
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {


        Log.d("TRing", "result code " + resultCode + "Activity.RESULT_OK" + Activity.RESULT_OK + " request code " + requestCode);
        if (resultCode != Activity.RESULT_OK) {
            //getActivity().finish();
            return;

        } else {

            Uri uri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);

            switch (requestCode) {

                case 1001:
                    RingtoneManager.setActualDefaultRingtoneUri(getActivity(), 1, uri);
                    break;

                case 1002:
                    RingtoneManager.setActualDefaultRingtoneUri(getActivity(), 2, uri);
                    break;

                case 1003:
                    RingtoneManager.setActualDefaultRingtoneUri(getActivity(), 4, uri);
                    break;
                default:
                    break;
            }

        }
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
