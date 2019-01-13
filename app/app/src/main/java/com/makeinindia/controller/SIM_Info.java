package com.makeinindia.controller;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ExpandableListView.OnGroupCollapseListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.firebase.client.Firebase;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class SIM_Info extends AppCompatActivity {

    private static final String TAG = "SimInfo_Firebase";
    TextView sim_status;
    TelephonyManager telephonyManager;
    // List implementation
    ELA listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;
    Resources res;
    String[] values;
    AdView adView;
    InterstitialAd mInterstitialAd;
    int position, pos;
    android.support.v7.app.ActionBar actionBar;

    Firebase mRef;
    List<String> balance, offer, service, utils;
    private int count = 0;
    RelativeLayout relativeLayout;
    Snackbar snackbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.si);

        requestNewInterstitial();

        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

        expListView = (ExpandableListView) findViewById(R.id.lvExp);
        sim_status = (TextView) findViewById(R.id.sim_status);
        relativeLayout = (RelativeLayout) findViewById(R.id.parent);
        //sim_status.setTypeface(font);
        int simState = telephonyManager.getSimState();

        try {
            actionBar = getSupportActionBar();
            actionBar.setHomeButtonEnabled(true);
        } catch (Exception e) {
        }
        pos = getIntent().getIntExtra("pos", position);
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();
        balance = new ArrayList<String>();
        service = new ArrayList<String>();
        utils = new ArrayList<String>();
        offer = new ArrayList<String>();
        //right link of firebase
        //getRightFirebase(pos);

        if (!(simState == TelephonyManager.SIM_STATE_READY)) {

            if (sim_status != null) {
                sim_status.setVisibility(View.VISIBLE);
                sim_status
                        .setText("Couldn't get the SIM information. Make sure your SIM is present and is not Locked.");
            }
        } else {

            // preparing list data
            prepareListData();

            listAdapter = new ELA(this, listDataHeader,
                    listDataChild);

            // setting list adapter
            expListView.setAdapter(listAdapter);

            expListView.expandGroup(0);

            expListView.setOnTouchListener(new OnTouchListener() {

                @Override
                public boolean onTouch(View v, MotionEvent arg1) {
                    int action = arg1.getAction();
                    switch (action) {
                        case MotionEvent.ACTION_DOWN:
                            // Disallow ScrollView to intercept touch events.
                            v.getParent().requestDisallowInterceptTouchEvent(true);
                            break;

                        case MotionEvent.ACTION_UP:
                            // Allow ScrollView to intercept touch events.
                            v.getParent().requestDisallowInterceptTouchEvent(false);
                            break;
                    }

                    // Handle ListView touch events.
                    v.onTouchEvent(arg1);
                    return true;
                }
            });

            // Listview Group click listener
            expListView.setOnGroupClickListener(new OnGroupClickListener() {

                @Override
                public boolean onGroupClick(ExpandableListView parent, View v,
                                            int groupPosition, long id) {
                    // Toast.makeText(getApplicationContext(),
                    // "Group Clicked " + listDataHeader.get(groupPosition),
                    // Toast.LENGTH_SHORT).show();
                    // if( parent.isGroupExpanded(0))

                    return false;
                }
            });

            // Listview Group expanded listener
            expListView.setOnGroupExpandListener(new OnGroupExpandListener() {

                @Override
                public void onGroupExpand(int groupPosition) {

                }
            });

            // Listview Group collasped listener
            expListView
                    .setOnGroupCollapseListener(new OnGroupCollapseListener() {

                        @Override
                        public void onGroupCollapse(int groupPosition) {

                            if (groupPosition == 0)
                                expListView.expandGroup(0);

                        }
                    });

            // Listview on child click listener
            expListView.setOnChildClickListener(new OnChildClickListener() {

                @Override
                public boolean onChildClick(ExpandableListView parent, View v,
                                            int groupPosition, int childPosition, long id) {

                    // string which is getting displayed
                    String str = listDataChild.get(
                            listDataHeader.get(groupPosition)).get(
                            childPosition);
                    String callString = null;
                    String subStr = null;
                    Log.d(TAG, "String str : " + str);
                    if (str != null) {
                        int start = str.indexOf("[");
                        int last = str.indexOf("]");
                        try {
                            subStr = str.substring(start + 1, last - 1);

                            // Log.d("Position ","start pos : " + start +
                            // " last pos : " + last);
                            // sub-string without "#"
                            Log.d(TAG, "Sub String subStr : " + subStr);
                        } catch (Exception e) {

                        }
                        // append "#" to subString
                        if (subStr != null)
                            callString = subStr + Uri.encode("#");
                        Log.d(TAG, "CallString String  : " + callString);

                        // Dial the USSD code
                        if (callString != null) {
                            Intent i = new Intent(
                                    Intent.ACTION_CALL, Uri
                                    .parse("tel:" + callString));
                            startActivity(i);
                        }


                    }
                    return false;

                }
            });

        }

    }

    // Preparing the list data
    private void prepareListData() {

        String mcc = null;
        res = getResources();

        // Adding child data
        String simOperator = telephonyManager.getSimOperator().toString();
        Log.d(TAG, "mcc + mnc code value :" + simOperator);
        try {
            mcc = simOperator.substring(0, 3);
        } catch (Exception e) {
        }

        String name = telephonyManager.getNetworkOperatorName();
        listDataHeader.add("Current SIM Detail");

        List<String> nowShowing = new ArrayList<String>();
        nowShowing.add("SIM Operator name : "
                + telephonyManager.getNetworkOperatorName());
        nowShowing.add("SIM Country ISO : "
                + telephonyManager.getNetworkCountryIso());
        nowShowing.add("SIM Serial No : "
                + telephonyManager.getSimSerialNumber());
        nowShowing.add("SIM State : " + getSimState());
        /*	nowShowing.add("SIM Operator Code : "
                + telephonyManager.getSimOperator());*/
        listDataChild.put(listDataHeader.get(0), nowShowing); // Header, Child
        int operator_code = 0;                                        // data
        listDataHeader.add("Check Balance");
        listDataHeader.add("Check Offers");
        listDataHeader.add("Check Services");
        listDataHeader.add("Check Utility");
        bindOperator(pos);


    }

    private void bindOperator(int pos) {
        switch (pos) {
            case 0:

                if (actionBar != null) {
                    actionBar.setTitle("Airtel Operator");
                }
                values = res.getStringArray(R.array.airtel_balance);
                for (String str : values)
                    balance.add(str);

                values = res.getStringArray(R.array.airtel_offer);
                for (String str : values)
                    offer.add(str);

                values = res.getStringArray(R.array.airtel_service);
                for (String str : values)
                    service.add(str);

                values = res.getStringArray(R.array.airtel_utility);
                for (String str : values)
                    utils.add(str);
                listDataChild.put(listDataHeader.get(1), balance);
                listDataChild.put(listDataHeader.get(2), offer);
                listDataChild.put(listDataHeader.get(3), service);
                listDataChild.put(listDataHeader.get(4), utils);

                break;
            case 1:
                if (actionBar != null) {
                    actionBar.setTitle("Vodafone Operator");
                }
                values = res.getStringArray(R.array.vodafone_balance);
                for (String str : values)
                    balance.add(str);

                values = res.getStringArray(R.array.vodafone_offer);
                for (String str : values)
                    offer.add(str);

                values = res.getStringArray(R.array.vodafone_service);
                for (String str : values)
                    service.add(str);

                values = res.getStringArray(R.array.vodafone_utils);
                for (String str : values)
                    utils.add(str);

                listDataChild.put(listDataHeader.get(1), balance);
                listDataChild.put(listDataHeader.get(2), offer);
                listDataChild.put(listDataHeader.get(3), service);
                listDataChild.put(listDataHeader.get(4), utils);
                break;
            case 2:
                if (actionBar != null) {
                    actionBar.setTitle("Idea Operator");
                }
                values = res.getStringArray(R.array.idea_balance);
                for (String str : values)
                    balance.add(str);

                values = res.getStringArray(R.array.idea_offer);
                for (String str : values)
                    offer.add(str);

                values = res.getStringArray(R.array.idea_service);
                for (String str : values)
                    service.add(str);

                values = res.getStringArray(R.array.idea_utils);
                for (String str : values)
                    utils.add(str);
                listDataChild.put(listDataHeader.get(1), balance);
                listDataChild.put(listDataHeader.get(2), offer);
                listDataChild.put(listDataHeader.get(3), service);
                listDataChild.put(listDataHeader.get(4), utils);
                break;
            case 3:
                if (actionBar != null) {
                    actionBar.setTitle("Telenor Operator");
                }
                values = res.getStringArray(R.array.telenor_balance);
                for (String str : values)
                    balance.add(str);

                values = res.getStringArray(R.array.telenor_offer);
                for (String str : values)
                    offer.add(str);

                values = res.getStringArray(R.array.telenor_service);
                for (String str : values)
                    service.add(str);

                values = res.getStringArray(R.array.telenor_utils);
                for (String str : values)
                    utils.add(str);
                listDataChild.put(listDataHeader.get(1), balance);
                listDataChild.put(listDataHeader.get(2), offer);
                listDataChild.put(listDataHeader.get(3), service);
                listDataChild.put(listDataHeader.get(4), utils);
                break;
            case 4:
                if (actionBar != null) {
                    actionBar.setTitle("Aircel Operator");
                }
                values = res.getStringArray(R.array.aircel_balance);
                for (String str : values)
                    balance.add(str);

                values = res.getStringArray(R.array.aircel_offer);
                for (String str : values)
                    offer.add(str);

                values = res.getStringArray(R.array.aircel_service);
                for (String str : values)
                    service.add(str);

                values = res.getStringArray(R.array.aircel_utils);
                for (String str : values)
                    utils.add(str);
                listDataChild.put(listDataHeader.get(1), balance);
                listDataChild.put(listDataHeader.get(2), offer);
                listDataChild.put(listDataHeader.get(3), service);
                listDataChild.put(listDataHeader.get(4), utils);
                break;
            case 5:
                if (actionBar != null) {
                    actionBar.setTitle("Reliance Operator");
                }
                values = res.getStringArray(R.array.reliance_balance);
                for (String str : values)
                    balance.add(str);

                values = res.getStringArray(R.array.reliance_offer);
                for (String str : values)
                    offer.add(str);

                values = res.getStringArray(R.array.reliance_service);
                for (String str : values)
                    service.add(str);

                values = res.getStringArray(R.array.reliance_utils);
                for (String str : values)
                    utils.add(str);
                listDataChild.put(listDataHeader.get(1), balance);
                listDataChild.put(listDataHeader.get(2), offer);
                listDataChild.put(listDataHeader.get(3), service);
                listDataChild.put(listDataHeader.get(4), utils);
                break;
            case 6:
                if (actionBar != null) {
                    actionBar.setTitle("BSNL Operator");
                }
                values = res.getStringArray(R.array.bsnl_balance);
                for (String str : values)
                    balance.add(str);

                values = res.getStringArray(R.array.bsnl_offer);
                for (String str : values)
                    offer.add(str);

                values = res.getStringArray(R.array.bsnl_service);
                for (String str : values)
                    service.add(str);

                values = res.getStringArray(R.array.bsnl_utils);
                for (String str : values)
                    utils.add(str);
                listDataChild.put(listDataHeader.get(1), balance);
                listDataChild.put(listDataHeader.get(2), offer);
                listDataChild.put(listDataHeader.get(3), service);
                listDataChild.put(listDataHeader.get(4), utils);
                break;
            case 7:
                if (actionBar != null) {
                    actionBar.setTitle("Docomo Operator");
                }
                values = res.getStringArray(R.array.tata_balance);
                for (String str : values)
                    balance.add(str);

                values = res.getStringArray(R.array.tata_offer);
                for (String str : values)
                    offer.add(str);

                values = res.getStringArray(R.array.tata_service);
                for (String str : values)
                    service.add(str);

                values = res.getStringArray(R.array.tata_utils);
                for (String str : values)
                    utils.add(str);
                listDataChild.put(listDataHeader.get(1), balance);
                listDataChild.put(listDataHeader.get(2), offer);
                listDataChild.put(listDataHeader.get(3), service);
                listDataChild.put(listDataHeader.get(4), utils);
                break;

            case 8:
                if (actionBar != null) {
                    actionBar.setTitle("Videocon Operator");
                }
                values = res.getStringArray(R.array.videocon_balance);
                for (String str : values)
                    balance.add(str);

                values = res.getStringArray(R.array.videocon_offer);
                for (String str : values)
                    offer.add(str);

                values = res.getStringArray(R.array.videocon_service);
                for (String str : values)
                    service.add(str);

                values = res.getStringArray(R.array.videocon_utils);
                for (String str : values)
                    utils.add(str);
                listDataChild.put(listDataHeader.get(1), balance);
                listDataChild.put(listDataHeader.get(2), offer);
                listDataChild.put(listDataHeader.get(3), service);
                listDataChild.put(listDataHeader.get(4), utils);
                break;
            case 9:
                if (actionBar != null) {
                    actionBar.setTitle("MTS Operator");
                }
                values = res.getStringArray(R.array.mts_balance);
                for (String str : values)
                    balance.add(str);

                values = res.getStringArray(R.array.mts_offer);
                for (String str : values)
                    offer.add(str);

                values = res.getStringArray(R.array.mts_service);
                for (String str : values)
                    service.add(str);

                values = res.getStringArray(R.array.mts_utils);
                for (String str : values)
                    utils.add(str);
                listDataChild.put(listDataHeader.get(1), balance);
                listDataChild.put(listDataHeader.get(2), offer);
                listDataChild.put(listDataHeader.get(3), service);
                listDataChild.put(listDataHeader.get(4), utils);
                break;
            case 10:
                if (actionBar != null) {
                    actionBar.setTitle("MTNL Operator");
                }
                values = res.getStringArray(R.array.mtnl_balance);
                for (String str : values)
                    balance.add(str);

                values = res.getStringArray(R.array.mtnl_offer);
                for (String str : values)
                    offer.add(str);

                values = res.getStringArray(R.array.mtnl_service);
                for (String str : values)
                    service.add(str);

                values = res.getStringArray(R.array.mtnl_utils);
                for (String str : values)
                    utils.add(str);
                listDataChild.put(listDataHeader.get(1), balance);
                listDataChild.put(listDataHeader.get(2), offer);
                listDataChild.put(listDataHeader.get(3), service);
                listDataChild.put(listDataHeader.get(4), utils);
                break;
            case 11:
                if (actionBar != null) {
                    actionBar.setTitle("Virgin Operator");
                }
                values = res.getStringArray(R.array.virgin_balance);
                for (String str : values)
                    balance.add(str);

                values = res.getStringArray(R.array.virgin_offer);
                for (String str : values)
                    offer.add(str);

                values = res.getStringArray(R.array.virgin_service);
                for (String str : values)
                    service.add(str);

                values = res.getStringArray(R.array.virgin_utils);
                for (String str : values)
                    utils.add(str);
                listDataChild.put(listDataHeader.get(1), balance);
                listDataChild.put(listDataHeader.get(2), offer);
                listDataChild.put(listDataHeader.get(3), service);
                listDataChild.put(listDataHeader.get(4), utils);
                break;
            case 12:
                if (actionBar != null) {
                    actionBar.setTitle("Loop Operator");
                }
                values = res.getStringArray(R.array.loop_balance);
                for (String str : values)
                    balance.add(str);

                values = res.getStringArray(R.array.loop_offer);
                for (String str : values)
                    offer.add(str);

                values = res.getStringArray(R.array.loop_service);
                for (String str : values)
                    service.add(str);

                values = res.getStringArray(R.array.loop_utils);
                for (String str : values)
                    utils.add(str);
                listDataChild.put(listDataHeader.get(1), balance);
                listDataChild.put(listDataHeader.get(2), offer);
                listDataChild.put(listDataHeader.get(3), service);
                listDataChild.put(listDataHeader.get(4), utils);
                break;

            default:
                break;
        }
    }

    private String getSimState() {

        String simState = "";
        switch (telephonyManager.getSimState()) {
            case 0:
                simState = "SIM_STATE_UNKNOWN";
                break;
            case 1:
                simState = "SIM_ABSENT";
                break;
            case 2:
                simState = "SIM_PIN_REQUIRED";
                break;
            case 3:
                simState = "SIM_PUK_REQUIRED";
                break;
            case 4:
                simState = "SIM_NETWORK_LOCKED";
                break;
            case 5:
                simState = "SIM_READY";
                break;
            default:
                break;
        }

        return simState;
    }

    private void requestNewInterstitial() {
        mInterstitialAd = new InterstitialAd(getApplicationContext());
        if (mInterstitialAd != null)
            mInterstitialAd.setAdUnitId(getString(R.string.interstitial_ad_unit_id));

        AdRequest adRequest = new AdRequest.Builder().build();
        if (mInterstitialAd != null)
            mInterstitialAd.loadAd(adRequest);

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

        if (adView != null)
            adView.loadAd(adRequest);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void callSnack() {
        snackbar = Snackbar
                .make(relativeLayout, "No Internet. Check your Connection !", Snackbar.LENGTH_LONG);
                        /*.setAction("TRY AGAIN", new View.OnClickListener() {
                            @Override
							public void onClick(View view) {

							}
						});*/

        // Message text color
        //snackbar.setActionTextColor(Color.WHITE);

        // Action button text color
        View snackBarView = snackbar.getView();
        TextView textView = (TextView) snackBarView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.RED);
        snackbar.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "Destroy");

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        try {
            if (mInterstitialAd.isLoaded())
                mInterstitialAd.show();

        } catch (Exception e) {

        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // No call for super(). Bug on API Level > 11.
    }



  /*
    @Override
    protected void onStart() {
        super.onStart();

        Log.d(TAG, "onStart");
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean value_exist = dataSnapshot.hasChildren();
                //Log.d("FireBase", "values_exist :  " + value_exist);
                if (value_exist) {
                    long value_count = dataSnapshot.getChildrenCount();
                    //Log.d("FireBase", "values count is :  " + value_count);

                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        String category = data.getKey();
                        *//**//*for (DataSnapshot child : data.getChildren()){
                            String child_value = child.getKey() + child.getValue();
                            Log.d("FireBase", " Category is :   " +    category    +   "child values  is :  " + child_value);
*//**//*
                        snackbar.dismiss();

                        bindOperatorFirebase(pos, category, data.getChildren());
                        //  }
                    }


                } else {
                    Log.d(TAG, "Internet not synced");
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.d(TAG, "firebaseError is :  " + firebaseError);
            }
        });

        mRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {


            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

    }

    private void getRightFirebase(int pos) {
        switch (pos) {
            case 0:
                mRef = new Firebase("https://comentertaindeviceinfo-9d5d8.firebaseio.com/airtel");
                break;
            case 1:
                mRef = new Firebase("https://comentertaindeviceinfo-9d5d8.firebaseio.com/vodafone");
                break;
            case 2:
                mRef = new Firebase("https://comentertaindeviceinfo-9d5d8.firebaseio.com/idea");
                break;
            case 3:
                mRef = new Firebase("https://comentertaindeviceinfo-9d5d8.firebaseio.com/telenor");
                break;
            case 4:
                mRef = new Firebase("https://comentertaindeviceinfo-9d5d8.firebaseio.com/aircel");
                break;
            case 5:
                mRef = new Firebase("https://comentertaindeviceinfo-9d5d8.firebaseio.com/reliance");
                break;
            case 6:
                mRef = new Firebase("https://comentertaindeviceinfo-9d5d8.firebaseio.com/bsnl");
                break;
            case 7:
                mRef = new Firebase("https://comentertaindeviceinfo-9d5d8.firebaseio.com/docomo");
                break;
            case 8:
                mRef = new Firebase("https://comentertaindeviceinfo-9d5d8.firebaseio.com/videocon");
                break;
            case 9:
                mRef = new Firebase("https://comentertaindeviceinfo-9d5d8.firebaseio.com/mts");
                break;
            case 10:
                mRef = new Firebase("https://comentertaindeviceinfo-9d5d8.firebaseio.com/mtnl");
                break;
            case 11:
                mRef = new Firebase("https://comentertaindeviceinfo-9d5d8.firebaseio.com/virgin");
                break;
            case 12:
                mRef = new Firebase("https://comentertaindeviceinfo-9d5d8.firebaseio.com/loop");
                break;
            default:
                break;

        }
        mRef.keepSynced(true);
        //Log.d(TAG, "Keep synced is done true");
    }

    private void bindOperatorFirebase(int pos, String categorie, Iterable<DataSnapshot> children) {

        switch (pos) {
            case 0:

                if (actionBar != null) {
                    actionBar.setTitle("Airtel Operator");
                }
                // values = res.getStringArray(R.array.airtel_balance);

                if (categorie.equals("airtel_balance")) {
                    balance.clear();
                    for (DataSnapshot str : children) {
                        balance.add(str.getKey() + str.getValue());
                        // Log.d("FireBase", " Category is :   " + categorie + "  child values  is :  " + str.getKey() + str.getValue());
                    }
                }
                if (categorie.equals("airtel_offer")) {
                    offer.clear();
                    for (DataSnapshot str : children) {
                        offer.add(str.getKey() + str.getValue());
                        //Log.d("FireBase", " Category is :   " + categorie + " child values  is :  " + str.getKey() + str.getValue());
                    }
                }
                if (categorie.equals("airtel_service")) {
                    service.clear();
                    for (DataSnapshot str : children) {
                        service.add(str.getKey() + str.getValue());
                        // Log.d("FireBase", " Category is :   " + categorie + " child values  is :  " + str.getKey() + str.getValue());
                    }
                }
                if (categorie.equals("airtel_utils")) {
                    utils.clear();
                    for (DataSnapshot str : children) {
                        utils.add(str.getKey() + str.getValue());
                        //Log.d("FireBase", " Category is :   " + categorie + " child values  is :  " + str.getKey() + str.getValue());
                    }
                }
                listDataChild.put(listDataHeader.get(1), balance);
                listDataChild.put(listDataHeader.get(2), offer);
                listDataChild.put(listDataHeader.get(3), service);
                listDataChild.put(listDataHeader.get(4), utils);

                break;
            case 1:
                if (actionBar != null) {
                    actionBar.setTitle("Vodafone Operator");
                }
                if (categorie.equals("vodafone_balance")) {
                    balance.clear();
                    for (DataSnapshot str : children) {
                        balance.add(str.getKey() + str.getValue());
                        // Log.d("FireBase", " Category is :   " + categorie + "  child values  is :  " + str.getKey() + str.getValue());
                    }
                }
                if (categorie.equals("vodafone_offer")) {
                    offer.clear();
                    for (DataSnapshot str : children) {
                        offer.add(str.getKey() + str.getValue());
                        //Log.d("FireBase", " Category is :   " + categorie + " child values  is :  " + str.getKey() + str.getValue());
                    }
                }
                if (categorie.equals("vodafone_service")) {
                    service.clear();
                    for (DataSnapshot str : children) {
                        service.add(str.getKey() + str.getValue());
                        // Log.d("FireBase", " Category is :   " + categorie + " child values  is :  " + str.getKey() + str.getValue());
                    }
                }
                if (categorie.equals("vodafone_utils")) {
                    utils.clear();
                    for (DataSnapshot str : children) {
                        utils.add(str.getKey() + str.getValue());
                        //Log.d("FireBase", " Category is :   " + categorie + " child values  is :  " + str.getKey() + str.getValue());
                    }
                }
                listDataChild.put(listDataHeader.get(1), balance);
                listDataChild.put(listDataHeader.get(2), offer);
                listDataChild.put(listDataHeader.get(3), service);
                listDataChild.put(listDataHeader.get(4), utils);

                break;
            case 2:
                if (actionBar != null) {
                    actionBar.setTitle("Idea Operator");
                }
                if (categorie.equals("idea_balance")) {
                    balance.clear();
                    for (DataSnapshot str : children) {
                        balance.add(str.getKey() + str.getValue());
                        // Log.d("FireBase", " Category is :   " + categorie + "  child values  is :  " + str.getKey() + str.getValue());
                    }
                }
                if (categorie.equals("idea_offer")) {
                    offer.clear();
                    for (DataSnapshot str : children) {
                        offer.add(str.getKey() + str.getValue());
                        //Log.d("FireBase", " Category is :   " + categorie + " child values  is :  " + str.getKey() + str.getValue());
                    }
                }
                if (categorie.equals("idea_service")) {
                    service.clear();
                    for (DataSnapshot str : children) {
                        service.add(str.getKey() + str.getValue());
                        // Log.d("FireBase", " Category is :   " + categorie + " child values  is :  " + str.getKey() + str.getValue());
                    }
                }
                if (categorie.equals("idea_utils")) {
                    utils.clear();
                    for (DataSnapshot str : children) {
                        utils.add(str.getKey() + str.getValue());
                        //Log.d("FireBase", " Category is :   " + categorie + " child values  is :  " + str.getKey() + str.getValue());
                    }
                }
                listDataChild.put(listDataHeader.get(1), balance);
                listDataChild.put(listDataHeader.get(2), offer);
                listDataChild.put(listDataHeader.get(3), service);
                listDataChild.put(listDataHeader.get(4), utils);
                break;
            case 3:
                if (actionBar != null) {
                    actionBar.setTitle("Telenor Operator");
                }
                if (categorie.equals("telenor_balance")) {
                    balance.clear();
                    for (DataSnapshot str : children) {
                        balance.add(str.getKey() + str.getValue());
                        // Log.d("FireBase", " Category is :   " + categorie + "  child values  is :  " + str.getKey() + str.getValue());
                    }
                }
                if (categorie.equals("telenor_offer")) {
                    offer.clear();
                    for (DataSnapshot str : children) {
                        offer.add(str.getKey() + str.getValue());
                        //Log.d("FireBase", " Category is :   " + categorie + " child values  is :  " + str.getKey() + str.getValue());
                    }
                }
                if (categorie.equals("telenor_service")) {
                    service.clear();
                    for (DataSnapshot str : children) {
                        service.add(str.getKey() + str.getValue());
                        // Log.d("FireBase", " Category is :   " + categorie + " child values  is :  " + str.getKey() + str.getValue());
                    }
                }
                if (categorie.equals("telenor_utils")) {
                    utils.clear();
                    for (DataSnapshot str : children) {
                        utils.add(str.getKey() + str.getValue());
                        //Log.d("FireBase", " Category is :   " + categorie + " child values  is :  " + str.getKey() + str.getValue());
                    }
                }
                listDataChild.put(listDataHeader.get(1), balance);
                listDataChild.put(listDataHeader.get(2), offer);
                listDataChild.put(listDataHeader.get(3), service);
                listDataChild.put(listDataHeader.get(4), utils);
                break;
            // aircel operator code
            case 4:
                if (actionBar != null) {
                    actionBar.setTitle("Aircel Operator");
                }
                if (categorie.equals("aircel_balance")) {
                    balance.clear();
                    for (DataSnapshot str : children) {
                        balance.add(str.getKey() + str.getValue());
                        // Log.d("FireBase", " Category is :   " + categorie + "  child values  is :  " + str.getKey() + str.getValue());
                    }
                }
                if (categorie.equals("aircel_offer")) {
                    offer.clear();
                    for (DataSnapshot str : children) {
                        offer.add(str.getKey() + str.getValue());
                        //Log.d("FireBase", " Category is :   " + categorie + " child values  is :  " + str.getKey() + str.getValue());
                    }
                }
                if (categorie.equals("aircel_service")) {
                    service.clear();
                    for (DataSnapshot str : children) {
                        service.add(str.getKey() + str.getValue());
                        // Log.d("FireBase", " Category is :   " + categorie + " child values  is :  " + str.getKey() + str.getValue());
                    }
                }
                if (categorie.equals("aircel_utils")) {
                    utils.clear();
                    for (DataSnapshot str : children) {
                        utils.add(str.getKey() + str.getValue());
                        //Log.d("FireBase", " Category is :   " + categorie + " child values  is :  " + str.getKey() + str.getValue());
                    }
                }
                listDataChild.put(listDataHeader.get(1), balance);
                listDataChild.put(listDataHeader.get(2), offer);
                listDataChild.put(listDataHeader.get(3), service);
                listDataChild.put(listDataHeader.get(4), utils);
                break;
            case 5:
                if (actionBar != null) {
                    actionBar.setTitle("Reliance Operator");
                }
                if (categorie.equals("reliance_balance")) {
                    balance.clear();
                    for (DataSnapshot str : children) {
                        balance.add(str.getKey() + str.getValue());
                        // Log.d("FireBase", " Category is :   " + categorie + "  child values  is :  " + str.getKey() + str.getValue());
                    }
                }
                if (categorie.equals("reliance_offer")) {
                    offer.clear();
                    for (DataSnapshot str : children) {
                        offer.add(str.getKey() + str.getValue());
                        //Log.d("FireBase", " Category is :   " + categorie + " child values  is :  " + str.getKey() + str.getValue());
                    }
                }
                if (categorie.equals("reliance_service")) {
                    service.clear();
                    for (DataSnapshot str : children) {
                        service.add(str.getKey() + str.getValue());
                        // Log.d("FireBase", " Category is :   " + categorie + " child values  is :  " + str.getKey() + str.getValue());
                    }
                }
                if (categorie.equals("reliance_utils")) {
                    utils.clear();
                    for (DataSnapshot str : children) {
                        utils.add(str.getKey() + str.getValue());
                        //Log.d("FireBase", " Category is :   " + categorie + " child values  is :  " + str.getKey() + str.getValue());
                    }
                }
                listDataChild.put(listDataHeader.get(1), balance);
                listDataChild.put(listDataHeader.get(2), offer);
                listDataChild.put(listDataHeader.get(3), service);
                listDataChild.put(listDataHeader.get(4), utils);
                break;
            case 6:
                if (actionBar != null) {
                    actionBar.setTitle("BSNL Operator");
                }
                if (categorie.equals("bsnl_balance")) {
                    balance.clear();
                    for (DataSnapshot str : children) {
                        balance.add(str.getKey() + str.getValue());
                        // Log.d("FireBase", " Category is :   " + categorie + "  child values  is :  " + str.getKey() + str.getValue());
                    }
                }
                if (categorie.equals("bsnl_offer")) {
                    offer.clear();
                    for (DataSnapshot str : children) {
                        offer.add(str.getKey() + str.getValue());
                        //Log.d("FireBase", " Category is :   " + categorie + " child values  is :  " + str.getKey() + str.getValue());
                    }
                }
                if (categorie.equals("bsnl_service")) {
                    service.clear();
                    for (DataSnapshot str : children) {
                        service.add(str.getKey() + str.getValue());
                        // Log.d("FireBase", " Category is :   " + categorie + " child values  is :  " + str.getKey() + str.getValue());
                    }
                }
                if (categorie.equals("bsnl_utils")) {
                    utils.clear();
                    for (DataSnapshot str : children) {
                        utils.add(str.getKey() + str.getValue());
                        //Log.d("FireBase", " Category is :   " + categorie + " child values  is :  " + str.getKey() + str.getValue());
                    }
                }
                listDataChild.put(listDataHeader.get(1), balance);
                listDataChild.put(listDataHeader.get(2), offer);
                listDataChild.put(listDataHeader.get(3), service);
                listDataChild.put(listDataHeader.get(4), utils);
                break;
            case 7:
                if (actionBar != null) {
                    actionBar.setTitle("Docomo Operator");
                }
                if (categorie.equals("docomo_balance")) {
                    balance.clear();
                    for (DataSnapshot str : children) {
                        balance.add(str.getKey() + str.getValue());
                        // Log.d("FireBase", " Category is :   " + categorie + "  child values  is :  " + str.getKey() + str.getValue());
                    }
                }
                if (categorie.equals("docomo_offer")) {
                    offer.clear();
                    for (DataSnapshot str : children) {
                        offer.add(str.getKey() + str.getValue());
                        //Log.d("FireBase", " Category is :   " + categorie + " child values  is :  " + str.getKey() + str.getValue());
                    }
                }
                if (categorie.equals("docomo_service")) {
                    service.clear();
                    for (DataSnapshot str : children) {
                        service.add(str.getKey() + str.getValue());
                        // Log.d("FireBase", " Category is :   " + categorie + " child values  is :  " + str.getKey() + str.getValue());
                    }
                }
                if (categorie.equals("docomo_utils")) {
                    utils.clear();
                    for (DataSnapshot str : children) {
                        utils.add(str.getKey() + str.getValue());
                        //Log.d("FireBase", " Category is :   " + categorie + " child values  is :  " + str.getKey() + str.getValue());
                    }
                }
                listDataChild.put(listDataHeader.get(1), balance);
                listDataChild.put(listDataHeader.get(2), offer);
                listDataChild.put(listDataHeader.get(3), service);
                listDataChild.put(listDataHeader.get(4), utils);
                break;

            case 8:
                if (actionBar != null) {
                    actionBar.setTitle("Videocon Operator");
                }
                if (categorie.equals("videocon_balance")) {
                    balance.clear();
                    for (DataSnapshot str : children) {
                        balance.add(str.getKey() + str.getValue());
                        // Log.d("FireBase", " Category is :   " + categorie + "  child values  is :  " + str.getKey() + str.getValue());
                    }
                }
                if (categorie.equals("videocon_offer")) {
                    offer.clear();
                    for (DataSnapshot str : children) {
                        offer.add(str.getKey() + str.getValue());
                        //Log.d("FireBase", " Category is :   " + categorie + " child values  is :  " + str.getKey() + str.getValue());
                    }
                }
                if (categorie.equals("videocon_service")) {
                    service.clear();
                    for (DataSnapshot str : children) {
                        service.add(str.getKey() + str.getValue());
                        // Log.d("FireBase", " Category is :   " + categorie + " child values  is :  " + str.getKey() + str.getValue());
                    }
                }
                if (categorie.equals("videocon_utils")) {
                    utils.clear();
                    for (DataSnapshot str : children) {
                        utils.add(str.getKey() + str.getValue());
                        //Log.d("FireBase", " Category is :   " + categorie + " child values  is :  " + str.getKey() + str.getValue());
                    }
                }
                listDataChild.put(listDataHeader.get(1), balance);
                listDataChild.put(listDataHeader.get(2), offer);
                listDataChild.put(listDataHeader.get(3), service);
                listDataChild.put(listDataHeader.get(4), utils);
                break;
            case 9:
                if (actionBar != null) {
                    actionBar.setTitle("MTS Operator");
                }
                if (categorie.equals("mts_balance")) {
                    balance.clear();
                    for (DataSnapshot str : children) {
                        balance.add(str.getKey() + str.getValue());
                        // Log.d("FireBase", " Category is :   " + categorie + "  child values  is :  " + str.getKey() + str.getValue());
                    }
                }
                if (categorie.equals("mts_offer")) {
                    offer.clear();
                    for (DataSnapshot str : children) {
                        offer.add(str.getKey() + str.getValue());
                        //Log.d("FireBase", " Category is :   " + categorie + " child values  is :  " + str.getKey() + str.getValue());
                    }
                }
                if (categorie.equals("mts_service")) {
                    service.clear();
                    for (DataSnapshot str : children) {
                        service.add(str.getKey() + str.getValue());
                        // Log.d("FireBase", " Category is :   " + categorie + " child values  is :  " + str.getKey() + str.getValue());
                    }
                }
                if (categorie.equals("mts_utils")) {
                    utils.clear();
                    for (DataSnapshot str : children) {
                        utils.add(str.getKey() + str.getValue());
                        //Log.d("FireBase", " Category is :   " + categorie + " child values  is :  " + str.getKey() + str.getValue());
                    }
                }
                listDataChild.put(listDataHeader.get(1), balance);
                listDataChild.put(listDataHeader.get(2), offer);
                listDataChild.put(listDataHeader.get(3), service);
                listDataChild.put(listDataHeader.get(4), utils);
                break;

            case 10:
                if (actionBar != null) {
                    actionBar.setTitle("MTNL Operator");
                }
                if (categorie.equals("mtnl_balance")) {
                    balance.clear();
                    for (DataSnapshot str : children) {
                        balance.add(str.getKey() + str.getValue());
                        // Log.d("FireBase", " Category is :   " + categorie + "  child values  is :  " + str.getKey() + str.getValue());
                    }
                }
                if (categorie.equals("mtnl_offer")) {
                    offer.clear();
                    for (DataSnapshot str : children) {
                        offer.add(str.getKey() + str.getValue());
                        //Log.d("FireBase", " Category is :   " + categorie + " child values  is :  " + str.getKey() + str.getValue());
                    }
                }
                if (categorie.equals("mtnl_service")) {
                    service.clear();
                    for (DataSnapshot str : children) {
                        service.add(str.getKey() + str.getValue());
                        // Log.d("FireBase", " Category is :   " + categorie + " child values  is :  " + str.getKey() + str.getValue());
                    }
                }
                if (categorie.equals("mtnl_utils")) {
                    utils.clear();
                    for (DataSnapshot str : children) {
                        utils.add(str.getKey() + str.getValue());
                        //Log.d("FireBase", " Category is :   " + categorie + " child values  is :  " + str.getKey() + str.getValue());
                    }
                }
                listDataChild.put(listDataHeader.get(1), balance);
                listDataChild.put(listDataHeader.get(2), offer);
                listDataChild.put(listDataHeader.get(3), service);
                listDataChild.put(listDataHeader.get(4), utils);
                break;
            case 11:
                if (actionBar != null) {
                    actionBar.setTitle("Virgin Operator");
                }
                if (categorie.equals("virgin_balance")) {
                    balance.clear();
                    for (DataSnapshot str : children) {
                        balance.add(str.getKey() + str.getValue());
                        // Log.d("FireBase", " Category is :   " + categorie + "  child values  is :  " + str.getKey() + str.getValue());
                    }
                }
                if (categorie.equals("virgin_offer")) {
                    offer.clear();
                    for (DataSnapshot str : children) {
                        offer.add(str.getKey() + str.getValue());
                        //Log.d("FireBase", " Category is :   " + categorie + " child values  is :  " + str.getKey() + str.getValue());
                    }
                }
                if (categorie.equals("virgin_service")) {
                    service.clear();
                    for (DataSnapshot str : children) {
                        service.add(str.getKey() + str.getValue());
                        // Log.d("FireBase", " Category is :   " + categorie + " child values  is :  " + str.getKey() + str.getValue());
                    }
                }
                if (categorie.equals("virgin_utils")) {
                    utils.clear();
                    for (DataSnapshot str : children) {
                        utils.add(str.getKey() + str.getValue());
                        //Log.d("FireBase", " Category is :   " + categorie + " child values  is :  " + str.getKey() + str.getValue());
                    }
                }
                listDataChild.put(listDataHeader.get(1), balance);
                listDataChild.put(listDataHeader.get(2), offer);
                listDataChild.put(listDataHeader.get(3), service);
                listDataChild.put(listDataHeader.get(4), utils);
                break;
            case 12:
                if (actionBar != null) {
                    actionBar.setTitle("Loop Operator");
                }
                if (categorie.equals("loop_balance")) {
                    balance.clear();
                    for (DataSnapshot str : children) {
                        balance.add(str.getKey() + str.getValue());
                        // Log.d("FireBase", " Category is :   " + categorie + "  child values  is :  " + str.getKey() + str.getValue());
                    }
                }
                if (categorie.equals("loop_offer")) {
                    offer.clear();
                    for (DataSnapshot str : children) {
                        offer.add(str.getKey() + str.getValue());
                        //Log.d("FireBase", " Category is :   " + categorie + " child values  is :  " + str.getKey() + str.getValue());
                    }
                }
                if (categorie.equals("loop_service")) {
                    service.clear();
                    for (DataSnapshot str : children) {
                        service.add(str.getKey() + str.getValue());
                        // Log.d("FireBase", " Category is :   " + categorie + " child values  is :  " + str.getKey() + str.getValue());
                    }
                }
                if (categorie.equals("loop_utils")) {
                    utils.clear();
                    for (DataSnapshot str : children) {
                        utils.add(str.getKey() + str.getValue());
                        //Log.d("FireBase", " Category is :   " + categorie + " child values  is :  " + str.getKey() + str.getValue());
                    }
                }
                listDataChild.put(listDataHeader.get(1), balance);
                listDataChild.put(listDataHeader.get(2), offer);
                listDataChild.put(listDataHeader.get(3), service);
                listDataChild.put(listDataHeader.get(4), utils);
                break;

            default:
                break;
        }
    }

    */
}
