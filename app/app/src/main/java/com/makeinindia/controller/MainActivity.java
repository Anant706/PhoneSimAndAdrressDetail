package com.makeinindia.controller;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.provider.Settings;
import android.support.design.widget.NavigationView;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;


import bankingservice.BankFragment;
import dialog.MyDialogFragment;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        DeviceInfoPager.OnFragmentInteractionListener,
        DeviceInfoPage1.OnFragmentInteractionListener,
        DeviceInfoPage2.OnFragmentInteractionListener,
        AudioManager.OnFragmentInteractionListener,
        CallerInformation.OnFragmentInteractionListener,
        Search.OnFragmentInteractionListener,
        SystemUsage.OnFragmentInteractionListener,
        SystemInfoPage1.OnFragmentInteractionListener,
        SystemInfoPage2.OnFragmentInteractionListener,
        SIMFragment.OnFragmentInteractionListener,
        BankFragment.OnFragmentInteractionListener {

    public static int ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE = 5469;
    ActivityManager activityManager;
    NavigationView navigationView;
    InterstitialAd mInterstitialAd;
    AdView adView;
    AdRequest adRequest;
    String PACKAGE_NAME = "com.makeinindia.controller";
    String shareText = "Know your Phone and Address details";
    String email = "makeinindia706@gmail.com";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        checkDrawPermission();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);

        adRequest = new AdRequest.Builder()
                .build();
        requestNewInterstitial();

        fabMenuInit();


        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);

        navigationView.setNavigationItemSelectedListener(this);
        if (savedInstanceState == null) {
            navigationView.getMenu().performIdentifierAction(R.id.nav_system, 0);
        }

    }

    private void requestNewInterstitial() {
        mInterstitialAd = new InterstitialAd(getApplicationContext());
        if (mInterstitialAd != null) {
            mInterstitialAd.setAdUnitId(getString(R.string.interstitial_ad_unit_id));
        }
        // AdRequest adRequest = new AdRequest.Builder().build();
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
    protected void onResume() {
        super.onResume();
    }

    private void fabMenuInit() {
        final FrameLayout frameLayout = (FrameLayout) findViewById(R.id.frame_layout);
        frameLayout.getBackground().setAlpha(0);
        final FloatingActionsMenu fabMenu = (FloatingActionsMenu) findViewById(R.id.fab_menu);

        fabMenu.setOnFloatingActionsMenuUpdateListener(new FloatingActionsMenu.OnFloatingActionsMenuUpdateListener() {
            @Override
            public void onMenuExpanded() {
                frameLayout.getBackground().setAlpha(100);
                frameLayout.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        fabMenu.collapse();
                        return true;
                    }
                });
            }

            @Override
            public void onMenuCollapsed() {
                frameLayout.getBackground().setAlpha(0);
                frameLayout.setOnTouchListener(null);
            }
        });


        final FloatingActionButton fabStarButton = (FloatingActionButton) findViewById(R.id.fab_star);
        fabStarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rateUsPlayStore();
            }
        });

        final FloatingActionButton fabShareButton = (FloatingActionButton) findViewById(R.id.fab_share);
        fabShareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareTextUrl();
            }
        });

        final FloatingActionButton fabFeedbackButton = (FloatingActionButton) findViewById(R.id.fab_feedback);
        fabFeedbackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent Email = new Intent(Intent.ACTION_SEND);
                Email.setType("text/email");
                Email.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
                Email.putExtra(Intent.EXTRA_SUBJECT, "Feedback");
                startActivity(Intent.createChooser(Email, "Send Feedback:"));
            }
        });
    }

    private void rateUsPlayStore() {
        // Insert your Application Package Name

        Uri uri = Uri.parse("http://play.google.com/store/apps/details?id=" + PACKAGE_NAME);

        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        try {
            startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id="
                            + PACKAGE_NAME)));
        }
    }

    private void shareTextUrl() {
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("text/plain");
        share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        share.putExtra(Intent.EXTRA_SUBJECT, shareText);
        share.putExtra(
                Intent.EXTRA_TEXT,
                "Download Now:https://play.google.com/store/apps/details?id=" + PACKAGE_NAME);

        startActivity(Intent.createChooser(share, "Share link!"));
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            Intent close = new Intent(this, MyDialogFragment.class);
            startActivityForResult(close,121);


            // super.onBackPressed();
            /*new AlertDialog.Builder(MainActivity.this)
                    .setIcon(getResources().getDrawable(R.drawable.logo))
                    .setTitle("Exit")
                    .setMessage("Do you want to Exit from this app?")
                    .setPositiveButton("Yes",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(
                                        DialogInterface paramAnonymousDialogInterface,
                                        int paramAnonymousInt) {
                                    MainActivity.this.finish();
                                }
                            }).setNegativeButton("No", null).show();*/
        }

    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_system) {
            setTitle("System Usage");
            loadFragment(0);
        } else if (id == R.id.nav_device) {
            setTitle("Device Info");
            loadFragment(1);
        } else if (id == R.id.nav_location) {
            loadFragment(2);
        } else if (id == R.id.nav_sound) {
            setTitle("Audio Manager");
            loadFragment(3);
        } /*else if (id == R.id.nav_caller_info) {
            setTitle("Caller Information");
            loadFragment(4);
        }*/ else if (id == R.id.nav_search_no) {
            setTitle("Search User");
            loadFragment(5);
        } else if (id == R.id.nav_data_usage) {
            loadFragment(6);
        } else if (id == R.id.nav_sim) {
            setTitle("SIM Information");
            loadFragment(7);
        } else if (id == R.id.banking) {
            setTitle("Banking Service");
            loadFragment(8);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        // setTitle(item.getItemId().getTitle());
        return true;
    }

    @Override
    public void setTitle(CharSequence title) {
        //mTitle = title;
        try {
            getSupportActionBar().setTitle(title);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadFragment(int i) {
        Fragment f = null;
        switch (i) {
            case 0:
                f = SystemUsage.newInstance();
                break;
            case 1:
                f = DeviceInfoPager.newInstance();
                if (mInterstitialAd != null && mInterstitialAd.isLoaded()) {
                    mInterstitialAd.show();
                }
                break;
            case 2:
                try {
                    Intent locationDetails = new Intent(this, LocationDetails.class);
                    startActivity(locationDetails);
                } catch (Exception e) {
                    Log.d("MainActivity Exception ", e.toString());
                }
                break;
            case 3:
                f = AudioManager.newInstance();
                if (mInterstitialAd != null && mInterstitialAd.isLoaded()) {
                    mInterstitialAd.show();
                }
                break;
            case 4:
                f = CallerInformation.newInstance();
                if (mInterstitialAd != null && mInterstitialAd.isLoaded()) {
                    mInterstitialAd.show();
                }
                break;
            case 5:
                f = Search.newInstance();
                if (mInterstitialAd != null && mInterstitialAd.isLoaded()) {
                    mInterstitialAd.show();
                }
                break;
            case 6:
                try {
                    Intent intent = new Intent();
                    intent.setComponent(new ComponentName("com.android.settings", "com.android.settings.Settings$DataUsageSummaryActivity"));
                    startActivity(intent);
                } catch (Exception e) {
                    Log.d("MainActivity Exception ", e.toString());
                }
                break;
            case 7:
                f = SIMFragment.newInstance();
                if (mInterstitialAd != null && mInterstitialAd.isLoaded()) {
                    mInterstitialAd.show();
                }
                break;
            case 8:
                f = BankFragment.newInstance();
                if (mInterstitialAd != null && mInterstitialAd.isLoaded()) {
                    mInterstitialAd.show();
                }
                break;
        }

        if (f != null) {
            android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
            android.support.v4.app.FragmentTransaction ft = fm.beginTransaction().setCustomAnimations(R.anim.slide_down, R.anim.slide_up, R.anim.slide_down, R.anim.slide_up);
            ft.replace(R.id.fragment_container, f);

            ft.commit();
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    public void checkDrawPermission() {
        Log.d("hello", " " + 1);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE);
                Log.d("hello", " " + 2);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!Settings.canDrawOverlays(this)) {

                    checkDrawPermission();
                }
            }
        } else if (requestCode == 121) {
            if(resultCode == Activity.RESULT_OK){
                finish();
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }
}
