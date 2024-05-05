package com.agrtech.fbstatus;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import yuku.ambilwarna.AmbilWarnaDialog;

public class DetailsActivity extends AppCompatActivity {

    public static ArrayList<HashMap<String, String>> statusListView = new ArrayList<>();

    public static ArrayList<StatusModel> likedStatusList;

    ImageView back, save, copy, share, next, status_image;
    TextView status_text;
    int position;
    int current;
    int defoleColor;
    ImageView font,fontcolor,fontsize,watermark;
    CustomPref customPref;
    MediaPlayer mediaPlayer;
    RelativeLayout status_content;
    HashMap<String, String> status;
    boolean liked;
    StatusModel statusModel;
    int length;
int water = 0;
    private void mSound(int sound) {
        if (customPref.getSound()){
            float volume = 0.1f;
            mediaPlayer = MediaPlayer.create(getApplicationContext(), sound);
            mediaPlayer.setVolume(volume, volume);
            mediaPlayer.start();
            mediaPlayer.setOnCompletionListener(mediaPlayer -> mediaPlayer.release());
        }
    }
    int[] status_bg = {

            R.drawable.p23,
            R.drawable.a_1,
            R.drawable.a_2,
            R.drawable.a_3,
            R.drawable.a_4,
            R.drawable.m8,
            R.drawable.p24_,
            R.drawable.p25,
            R.drawable.p26,
            R.drawable.p27,
            R.drawable.a_14,
            R.drawable.a_15,
            R.drawable.p28,
            R.drawable.p29_,
            R.drawable.p30,
            R.drawable.a_10,
            R.drawable.a_11,
            R.drawable.p31,
            R.drawable.status_bg4,
            R.drawable.p_4,
            R.drawable.p_5,
            R.drawable.p_1,
            R.drawable.p_2,
            R.drawable.p_3,
            R.drawable.a_7,
            R.drawable.a_8,
            R.drawable.p_6,
            R.drawable.p_7,
            R.drawable.p_8,
            R.drawable.p_9,
            R.drawable.status_bg3,
            R.drawable.p_12,
            R.drawable.p_13,
            R.drawable.p_14,
            R.drawable.status_bg5,
            R.drawable.p_16,
            R.drawable.p_17,
            R.drawable.q_1,
            R.drawable.q_2,
            R.drawable.q_3,
            R.drawable.a_3,
            R.drawable.a_4,
            R.drawable.p18,
            R.drawable.p19,
            R.drawable.p20,
            R.drawable.p21_,
            R.drawable.p22,
            R.drawable.p32,
            R.drawable.m6,
            R.drawable.p33,
            R.drawable.p34,
            R.drawable.status_bg2,
            R.drawable.m5,
            R.drawable.q_4,
            R.drawable.status_bg1,
            R.drawable.m1,
            R.drawable.m3,
            R.drawable.a_1,
            R.drawable.a_2,
            R.drawable.a_3,
            R.drawable.a_4,
            R.drawable.a_5,
            R.drawable.a_6,
            R.drawable.a_7,
            R.drawable.a_8,
            R.drawable.a_9,
            R.drawable.a_10,
            R.drawable.m2,
            R.drawable.status_bg3,
            R.drawable.a_11,
            R.drawable.a_12,
            R.drawable.a_13,
            R.drawable.a_14,
            R.drawable.a_15,
            R.drawable.a_16

    };

    AdmobAds admobAds;
    private void loadInterstitial(){
        LinearLayout adContainer = findViewById(R.id.banner_container);
        admobAds = new AdmobAds(this)
                .init()
                .setAdStatus(getResources().getBoolean(R.bool.ADS_ON))
                .setInterstitialId(getResources().getString(R.string.INTERSTITIAL_ID))
                .setClick(getResources().getInteger(R.integer.AD_INTERVAL))
                .setBannerId(getResources().getString(R.string.BANNER_ID))
                .loadBanner(adContainer)
                .build();
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

TextView watermarklogo = findViewById(R.id.watermarklogo);
        font = findViewById(R.id.font);
        fontcolor = findViewById(R.id.fontcolor);
        fontsize = findViewById(R.id.fontsize);
        watermark = findViewById(R.id.watermark);
        customPref = new CustomPref(DetailsActivity.this);
        loadInterstitial();
        back = findViewById(R.id.back);
        save = findViewById(R.id.save);
        copy = findViewById(R.id.copy);
        share = findViewById(R.id.share);
        next = findViewById(R.id.next);
        status_content = findViewById(R.id.status_content);
        status_image = findViewById(R.id.status_image);
        status_text = findViewById(R.id.status_text);

        position = getIntent().getIntExtra("position", 0);
        liked = getIntent().getBooleanExtra("liked", false);
        current = position;
        if (liked) length = likedStatusList.size();
        else length = statusListView.size();
        set_res(position);

        font.setOnClickListener(v -> {
          water++;

          if (water==1) status_text.setTypeface(ResourcesCompat.getFont(DetailsActivity.this,R.font.banglafont1));
          else if (water==2) status_text.setTypeface(ResourcesCompat.getFont(DetailsActivity.this,R.font.bangla_english));
          else if (water==3) status_text.setTypeface(ResourcesCompat.getFont(DetailsActivity.this,R.font.bangla2));
          else if (water==4){
              status_text.setTypeface(ResourcesCompat.getFont(DetailsActivity.this,R.font.bangla3));
              water=0;
          }

        });

        fontsize.setOnClickListener(v -> {
           // if (admobAds.isLoaded()) admobAds.show();
            water++;

            if (water==1){
                status_text.setTextSize(15);
            }else if (water==2){
                status_text.setTextSize(17);
            }else if (water==3){
                status_text.setTextSize(20);

            }else if (water==4){
                status_text.setTextSize(22);

            }else if (water==5){
                status_text.setTextSize(24);
                water=0;
            }

        });

        fontcolor.setOnClickListener(v -> opencolorpicker());

        watermark.setOnClickListener(v -> {
            if (admobAds.isLoaded()) {
                admobAds.show();

                watermarklogo.setVisibility(View.GONE);
            } else {
                watermarklogo.setVisibility(View.GONE);

            }


           // water++;
           // if (water==1){
           //     Toast.makeText(DetailsActivity.this, "please click agen", Toast.LENGTH_SHORT).show();
           // }else if (water==2){
          //      if (admobAds.isLoaded()) {
               //     admobAds.show();

               //     watermarklogo.setVisibility(View.GONE);
              //  } else {
               //     watermarklogo.setVisibility(View.GONE);

            //    }

         //   }


        });

        status_content.setOnClickListener(view -> {
            if (admobAds.isLoaded()) admobAds.show();
            mSound(R.raw.like_1);
            try {
                    current++;
                    if (current >= status_bg.length) {
                        current = 0;
                        status_image.setImageResource(status_bg[current]);
                    }else status_image.setImageResource(status_bg[current]);
                }catch (Exception e){
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }

        });


        back.setOnClickListener(view -> {
            mSound(R.raw.like_1);
            try {
                current--;
                if (current >= length) {
                    current = 0;
                    set_res(current);
                }else set_res(current);
            }catch (Exception e){
                Toast.makeText(this, "End", Toast.LENGTH_SHORT).show();
            }

        });


        save.setOnClickListener(view -> {
            mSound(R.raw.like_1);
            if (admobAds.isLoaded()) admobAds.show();
            status_content.setDrawingCacheEnabled(true);
            status_content.buildDrawingCache();
            status_content.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
            Bitmap bitmap = status_content.getDrawingCache();

            String fileName = random()+".jpg";
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
            Uri uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            try {
                OutputStream outputStream = getContentResolver().openOutputStream(uri);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                outputStream.close();
                Toast.makeText(getApplicationContext(), "Image saved to gallery", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                Toast.makeText(getApplicationContext(), "Error saving image", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }



        });


        copy.setOnClickListener(view -> {
            if (admobAds.isLoaded()) admobAds.show();//ads show
            mSound(R.raw.like_1);
            try {
                if (admobAds.isLoaded()) admobAds.show();
                status = statusListView.get(current);
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip;

                if (liked){
                    statusModel = likedStatusList.get(current);
                    clip = ClipData.newPlainText("status",statusModel.getStatus());
                }else {
                    clip = ClipData.newPlainText("status", status.get("status"));
                }
                clipboard.setPrimaryClip(clip);
                Toast.makeText(getApplicationContext(), "Copied To Clipboard", Toast.LENGTH_SHORT).show();

            }catch (Exception e){
                Toast.makeText(this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


        share.setOnClickListener(view -> {
            if (admobAds.isLoaded()) admobAds.show();
            mSound(R.raw.like_1);
            status = statusListView.get(current);
            PopupMenu popup = new PopupMenu(getApplicationContext(), view);
            popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());

            popup.setOnMenuItemClickListener(menuItem -> {
                mSound(R.raw.like_1);
                switch (menuItem.getItemId()) {
                    case R.id.text:
                        Intent i = new Intent(Intent.ACTION_SEND);
                        i.setType("text/plain");
                        if (liked){
                            statusModel = likedStatusList.get(current);
                            i.putExtra(Intent.EXTRA_TEXT, statusModel.getStatus());
                        }else {
                            i.putExtra(Intent.EXTRA_TEXT, status.get("status"));
                        }
                        startActivity(Intent.createChooser(i, "Share Text Status"));


                        return true;
                    case R.id.image:
                        status_content.setDrawingCacheEnabled(true);
                        status_content.buildDrawingCache();
                        status_content.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
                        Bitmap bitmap = status_content.getDrawingCache();
                        Uri uri = getImageUri(getApplicationContext(), bitmap);
                        Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.setType("image/*");
                        intent.putExtra(Intent.EXTRA_STREAM, uri);
                        Intent chooser = Intent.createChooser(intent, "Share Image Status");
                        startActivity(chooser);
                        status_content.setDrawingCacheEnabled(false);
                        return true;
                    default:
                        return false;
                }
            });


            popup.show();

        });


        next.setOnClickListener(view -> {
            mSound(R.raw.like_1);

            watermarklogo.setVisibility(View.VISIBLE);

            try {
                current++;
                if (current >= length) {
                    current = length;
                    set_res(current);
                }else set_res(current);
            }catch (Exception e){
                Toast.makeText(this, "End", Toast.LENGTH_SHORT).show();
            }

        });

    }

    public void set_res(int pos){
        if (liked){
            statusModel = likedStatusList.get(pos);
            status_text.setText(statusModel.getStatus());
        }else {
            status = statusListView.get(pos);
            status_text.setText(status.get("status"));
        }
    }

    private Uri getImageUri(Context context, Bitmap bitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, "Title", null);
        return Uri.parse(path);
    }


    public static String random() {
        Random generator = new Random();
        StringBuilder randomStringBuilder = new StringBuilder();
        int randomLength = generator.nextInt(10);
        char tempChar;
        for (int i = 0; i < randomLength; i++){
            tempChar = (char) (generator.nextInt(96) + 32);
            randomStringBuilder.append(tempChar);
        }
        return randomStringBuilder.toString();
    }




// color picar libray========implementation 'com.github.yukuku:ambilwarna:2.0.1'=======================

    private void opencolorpicker() {

        AmbilWarnaDialog ambilWarnaDialog = new AmbilWarnaDialog(this, defoleColor, new AmbilWarnaDialog.OnAmbilWarnaListener() {
            @Override
            public void onCancel(AmbilWarnaDialog dialog) {

            }

            @Override
            public void onOk(AmbilWarnaDialog dialog, int color) {
                defoleColor = color;
                // textView.setBackgroundColor(defoleColor);
                status_text.setTextColor(defoleColor);

            }
        });

        ambilWarnaDialog.show();
    }


}