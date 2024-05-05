package com.agrtech.fbstatus;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;

import android.content.Context;
import android.content.Intent;

import android.content.res.Configuration;

import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;

import android.os.Bundle;

import android.os.Handler;
import android.provider.MediaStore;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;



import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class Liked extends AppCompatActivity {
    ArrayList<StatusModel> likedStatusList;
    Toolbar toolbar;
    RecyclerView recyclerview;
    LikedAdapter likedAdapter;
    CustomPref customPref;
    MediaPlayer mediaPlayer;
    DatabaseHelper databaseHelper;


    int current = 0;
    int[] status_bg = {R.drawable.status_bg4,
            R.drawable.status_bg2,
            R.drawable.status_bg1,
            R.drawable.status_bg3};


    private void mSound(int sound) {
        if (customPref.getSound()){
            float volume = 0.1f; // 50% volume
            mediaPlayer = MediaPlayer.create(getApplicationContext(), sound);
            mediaPlayer.setVolume(volume, volume);
            mediaPlayer.start();
            mediaPlayer.setOnCompletionListener(mediaPlayer -> mediaPlayer.release());
        }
    }

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
        setContentView(R.layout.activity_status);
        loadInterstitial();
        admobAds.loadAppOpenAd();
        customPref = new CustomPref(Liked.this);
        toolbar = findViewById(R.id.toolbar);
        setTitle("Liked Status");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        databaseHelper = new DatabaseHelper(this);
        likedStatusList = new ArrayList<>();
        likedStatusList = databaseHelper.getFavourite();
        likedAdapter = new LikedAdapter(Liked.this, new Onclick() {
            @Override
            public void CategoryOnclick(String category_title, int position) {
            }

            @Override
            public void StatusOnclick() {
                mSound(R.raw.like_1);
            }
        });
        recyclerview = findViewById(R.id.recyclerview);
        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            // Screen is in portrait mode
            recyclerview.setLayoutManager(new GridLayoutManager(Liked.this, 1));
        } else if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // Screen is in landscape mode
            recyclerview.setLayoutManager(new GridLayoutManager(Liked.this, 1));
        }
        recyclerview.setHasFixedSize(true);
        recyclerview.setAdapter(likedAdapter);


    }

    public class LikedAdapter extends RecyclerView.Adapter<LikedAdapter.ViewHolder>{
        Context context;
        Onclick onclick;

        public LikedAdapter(Context context, Onclick onclick) {
            this.context = context;
            this.onclick = onclick;
        }

        @NonNull
        @Override
        public LikedAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_status, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull LikedAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
            StatusModel statusModel = likedStatusList.get(position);




            holder.status_text.setText(statusModel.getStatus());
            holder.btn_like.setImageResource(R.drawable.ic_liked);
            holder.text_like.setText("Unlike");
            DatabaseHelper databaseHelper = new DatabaseHelper(context);

            try {
                current++;
                if (current >= status_bg.length) {
                    current = 0;
                    holder.status_image.setImageResource(status_bg[current]);
                }else holder.status_image.setImageResource(status_bg[current]);
            }catch (Exception e){
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }


            //     holder.status_content.setOnClickListener(new View.OnClickListener() {
                //       @Override
                        //      public void onClick(View view) {



                    //      mSound(R.raw.like_1);
                    //      DetailsActivity.likedStatusList =likedStatusList;
                    //    Intent intent = new Intent(context, DetailsActivity.class);
                    //    intent.putExtra("position", position);

                    //    if (admobAds.isLoaded()) {
                       //        admobAds.show(new AdmobAds.Dismissed() {
                            //          @Override
                  //          public void onclick() {
                                    //           startActivity(intent);
                  //          }
                 //       });

               //     }else {
               //       startActivity(intent);
                //    }
             //   }
          //  });

            holder.like.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mSound(R.raw.like_1);
                    databaseHelper.removeFavouriteById(String.valueOf(statusModel.getId()));
                    likedStatusList.remove(position);
                    likedAdapter.notifyItemRemoved(position);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            recreate();                        }
                    }, 500);
                    Toast.makeText(context, "Unlike Successful", Toast.LENGTH_SHORT).show();
                }
            });


            holder.copy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mSound(R.raw.like_1);
                    ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("status", statusModel.getStatus());
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(context, "Copied To Clipboard", Toast.LENGTH_SHORT).show();
                }
            });
            // Create a new file with a unique name
            holder.share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mSound(R.raw.like_1);
                    PopupMenu popup = new PopupMenu(context, view);
                    popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());

                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {
                            mSound(R.raw.like_1);
                            switch (menuItem.getItemId()) {
                                case R.id.text:

                                    Intent i = new Intent(android.content.Intent.ACTION_SEND);
                                    i.setType("text/plain");
                                    i.putExtra(android.content.Intent.EXTRA_TEXT, statusModel.getStatus());
                                    startActivity(Intent.createChooser(i, "Share Text Status"));


                                    return true;
                                case R.id.image:
                                    holder.status_content.setDrawingCacheEnabled(true);
                                    holder.status_content.buildDrawingCache();
                                    holder.status_content.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
                                    Bitmap bitmap = holder.status_content.getDrawingCache();
                                    Uri uri = getImageUri(context, bitmap);
                                    Intent intent = new Intent(Intent.ACTION_SEND);
                                    intent.setType("image/*");
                                    intent.putExtra(Intent.EXTRA_STREAM, uri);
                                    Intent chooser = Intent.createChooser(intent, "Share Image Status");
                                    startActivity(chooser);
                                    holder.status_content.setDrawingCacheEnabled(false);
                                    return true;
                                default:
                                    return false;
                            }
                        }
                    });


                    popup.show();

                }
            });
        }

        @Override
        public int getItemCount() {
            return likedStatusList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            LinearLayout like,copy, share;
            LinearLayout status_content;
            ImageView status_image;
            ImageView btn_like;
            TextView status_text;
            TextView text_like;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                like = itemView.findViewById(R.id.like);
                copy = itemView.findViewById(R.id.copy);
                share = itemView.findViewById(R.id.share);
                status_content = itemView.findViewById(R.id.status_content);
                status_image = itemView.findViewById(R.id.status_image);
                status_text = itemView.findViewById(R.id.status_text);
                text_like = itemView.findViewById(R.id.text_like);
                btn_like = itemView.findViewById(R.id.btn_like);
            }
        }
    }


    private Uri getImageUri(Context context, Bitmap bitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, "Title", null);
        return Uri.parse(path);
    }

    @Override
    public void onBackPressed() {
        mSound(R.raw.like_1);
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
        admobAds.appOpenCall();
    }
}
