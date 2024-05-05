package com.agrtech.fbstatus;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.ads.MobileAds;
import com.google.android.ump.ConsentDebugSettings;
import com.google.android.ump.ConsentForm;
import com.google.android.ump.ConsentInformation;
import com.google.android.ump.ConsentRequestParameters;
import com.google.android.ump.UserMessagingPlatform;

import java.util.concurrent.atomic.AtomicBoolean;


public class HomeActivity extends AppCompatActivity {

    CustomPref customPref;
    MediaPlayer mediaPlayer;
    AdmobAds admobAds;
    private void mSound(int sound) {
        if (customPref.getSound()){
            float volume = 0.1f; // 50% volume
            mediaPlayer = MediaPlayer.create(getApplicationContext(), sound);
            mediaPlayer.setVolume(volume, volume);
            mediaPlayer.start();
            mediaPlayer.setOnCompletionListener(mediaPlayer -> mediaPlayer.release());
        }
    }

    private ConsentInformation consentInformation;

    private final AtomicBoolean isMobileAdsInitializeCalled = new AtomicBoolean(false);



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        customPref = new CustomPref(this);
        admobAds = new AdmobAds(this)
                .setAdStatus(getResources().getBoolean(R.bool.ADS_ON))
                .init()
                .build();
        admobAds.loadAppOpenAd();


        ConsentDebugSettings debugSettings = new ConsentDebugSettings.Builder(this)
                .addTestDeviceHashedId("751B5CF61D4A80111D4014558FD6A736")
                .setDebugGeography(ConsentDebugSettings.DebugGeography.DEBUG_GEOGRAPHY_EEA)
                .build();







        ConsentRequestParameters params = new ConsentRequestParameters
                .Builder()
               .setConsentDebugSettings(debugSettings)
                
                .setTagForUnderAgeOfConsent(false)
                .build();

        consentInformation = UserMessagingPlatform.getConsentInformation(this);
        consentInformation.requestConsentInfoUpdate(
                this,
                params,
                (ConsentInformation.OnConsentInfoUpdateSuccessListener) () -> {
                    UserMessagingPlatform.loadAndShowConsentFormIfRequired(
                            this,
                            (ConsentForm.OnConsentFormDismissedListener) loadAndShowError -> {
                                if (loadAndShowError != null) {
                                    // Consent gathering failed.
                                    Log.w(TAG, String.format("%s: %s",
                                            loadAndShowError.getErrorCode(),
                                            loadAndShowError.getMessage()));
                                }

                                // Consent has been gathered.
                                if (consentInformation.canRequestAds()) {
                                    initializeMobileAdsSdk();
                                }
                            }
                    );
                },
                (ConsentInformation.OnConsentInfoUpdateFailureListener) requestConsentError -> {
                    // Consent gathering failed.
                    Log.w(TAG, String.format("%s: %s",
                            requestConsentError.getErrorCode(),
                            requestConsentError.getMessage()));
                });

        // Check if you can initialize the Google Mobile Ads SDK in parallel
        // while checking for new consent information. Consent obtained in
        // the previous session can be used to request ads.
        if (consentInformation.canRequestAds()) {
            initializeMobileAdsSdk();
        }








    }//////////////////

    private void initializeMobileAdsSdk() {
        if (isMobileAdsInitializeCalled.getAndSet(true)) {
            return;
        }

        // Initialize the Google Mobile Ads SDK.
        MobileAds.initialize(this);

        // TODO: Request an ad.
        // InterstitialAd.load(...);
    }


    public void start(View view) {
        mSound(R.raw.like_1);
        startActivity(new Intent(this, MainActivity.class));
    }

    public void favorite(View view) {
        mSound(R.raw.like_1);
        startActivity(new Intent(this, Liked.class));
    }




    public void rate_us(View view) {
        mSound(R.raw.like_1);
        try{
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id="+getPackageName())));
        }
        catch (ActivityNotFoundException e){
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id="+getPackageName())));
        }
    }




    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {

        mSound(R.raw.like_1);

        new AlertDialog.Builder(HomeActivity.this)
                .setTitle("Confirm Exit")
                .setMessage("Are you sure want to close this app..?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mSound(R.raw.like_1);
                        finishAndRemoveTask();
                    }
                })
                .setNeutralButton("Rate Us", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mSound(R.raw.like_1);
                        try {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getPackageName())));
                        } catch (Exception e) {
                            Toast.makeText(HomeActivity.this, "", Toast.LENGTH_SHORT).show();
                        }

                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mSound(R.raw.like_1);
                        dialogInterface.dismiss();
                    }
                })
                .show();


    }



    @Override
    protected void onResume() {
        super.onResume();
        admobAds.appOpenCall();
    }
}