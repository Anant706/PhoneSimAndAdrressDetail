package bankingservice;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.makeinindia.controller.R;

import java.util.List;

import bankingservice.model.BankModel;
import dialog.MyDialogFragment;


public class CheckBankBalance extends AppCompatActivity {

    private static final String TAG = "CheckBankBalance";
    Button chkBalance, chkCustomer;
    TextView txtBalance, txtCustomer;
    String enquiry, customer, bankName, image;
    int pos;
    android.support.v7.app.ActionBar actionBar;
    AdView adView;
    InterstitialAd mInterstitialAd;
    private List<BankModel> bankModelList;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.check_balance_container);

        try {
            actionBar = getSupportActionBar();
            assert actionBar != null;
            actionBar.setDisplayHomeAsUpEnabled(true);
            //actionBar.setHomeButtonEnabled(true);
        } catch (Exception e) {
            Log.d("ActionBar not supported", "ActionBar not supported");
        }
        requestNewInterstitial();

        imageView = (ImageView) findViewById(R.id.banner);
        chkBalance = (Button) findViewById(R.id.checkBalance);
        chkCustomer = (Button) findViewById(R.id.checkCustomer);
        txtBalance = (TextView) findViewById(R.id.balanceNumber);
        txtCustomer = (TextView) findViewById(R.id.customerNumber);
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
        Log.d(TAG, "onResume");
        //pos = getIntent().getIntExtra("pos", 5);
        enquiry = getIntent().getStringExtra("enquiry");
        customer = getIntent().getStringExtra("customer");
        bankName = getIntent().getStringExtra("bankName");
        image = getIntent().getStringExtra("image");
        int res = getResources().getIdentifier("drawable/" + image, null, this.getPackageName());

        if (imageView != null)
            imageView.setBackgroundResource(res);

        if (enquiry != null)
            txtBalance.setText(enquiry);
        if (customer != null)
            txtCustomer.setText(customer);

        if (actionBar != null) {
            actionBar.setTitle(bankName);
        }

        chkBalance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Dial the USSD code
                if (enquiry != null) {
                    Intent i = new Intent(
                            Intent.ACTION_CALL, Uri
                            .parse("tel:" + enquiry));
                    startActivity(i);
                }
            }
        });
        chkCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Dial the USSD code
                if (customer != null) {
                    Intent i = new Intent(
                            Intent.ACTION_CALL, Uri
                            .parse("tel:" + customer));
                    startActivity(i);
                }
            }
        });
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
            e.printStackTrace();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // No call for super(). Bug on API Level > 11.
    }


}
