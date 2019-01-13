package dialog;

/**
 * Created by maa on 27-Aug-17.
 */

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.makeinindia.controller.R;

public class MyDialogFragment extends AppCompatActivity {

    AdRequest adRequest;
    AdView adView;
    InterstitialAd mInterstitialAd;
    Intent returnIntent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_fragment);
        adRequest = new AdRequest.Builder().build();
        Button close = (Button) findViewById(R.id.close);

        returnIntent = new Intent();

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        });
        final Button dismiss = (Button) findViewById(R.id.dismiss);
        dismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(Activity.RESULT_CANCELED, returnIntent);
                finish();
            }
        });
        requestNewInterstitial();
    }

    private void requestNewInterstitial() {

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
}