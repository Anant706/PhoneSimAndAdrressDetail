package com.makeinindia.controller;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SIMFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SIMFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SIMFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    TelephonyManager telephonyManager;
    Resources res;
    String[] values;
    int simState;
    View rootView;
    InterstitialAd mInterstitialAd;
    AdView adView;
    private String mParam1;
    private String mParam2;
    private int count = 0;
    private GridView gridView;
    private OnFragmentInteractionListener mListener;

    public SIMFragment() {
        // Required empty public constructor
    }

    public static SIMFragment newInstance() {
        SIMFragment fragment = new SIMFragment();

        return fragment;
    }

    public static SIMFragment newInstance(String param1, String param2) {
        SIMFragment fragment = new SIMFragment();
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
        rootView = container;

        // Inflate the layout for this fragment
        try {
            rootView = inflater.inflate(R.layout.sim_fragment, container, false);

        } catch (InflateException e) {

        }

        telephonyManager = (TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE);
        // get the listview
        gridView = (GridView) rootView.findViewById(R.id.gridView);

        simState = telephonyManager.getSimState();

        requestNewInterstitial();

        res = getResources();
        values = res.getStringArray(R.array.planets_array);
        gridView.setAdapter(new MAP(getActivity(), values));

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent in = new Intent(getActivity(), SIM_Info.class);
                in.putExtra("pos", position);
                startActivity(in);
            }
        });

        return rootView;

    }

    private void requestNewInterstitial() {

        mInterstitialAd = new InterstitialAd(getActivity());
        mInterstitialAd.setAdUnitId(getString(R.string.interstitial_ad_unit_id));
        AdRequest adRequest = new AdRequest.Builder()
                .build();

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
    public void onDestroyView() {
        super.onDestroyView();

        if (rootView != null) {
            ViewGroup parentViewGroup = (ViewGroup) rootView.getParent();
            if (parentViewGroup != null) {
                Log.d("TAG", "destroy od device");
                parentViewGroup.removeAllViews();
            }
        }


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

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}


