package com.agrtech.fbstatus;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
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


import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class StatusActivity extends AppCompatActivity {

    public static ArrayList<HashMap<String, String>> statusList = new ArrayList<>();
    Toolbar toolbar;
    RecyclerView recyclerview;
    StatusAdapter statusAdapter;

    CustomPref customPref;
    MediaPlayer mediaPlayer;

    int current = 0;
    int[] status_bg = {
            R.drawable.a_12,
            R.drawable.a_13,
            R.drawable.a_14,
            R.drawable.a_15,
            R.drawable.a_16,
            R.drawable.p_13,
            R.drawable.p_14,
            R.drawable.status_bg3,
            R.drawable.q_1,
            R.drawable.q_2,
            R.drawable.q_3,
            R.drawable.q_4,
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
            R.drawable.a_11,
            R.drawable.a_12,
            R.drawable.a_13,
            R.drawable.a_14,
            R.drawable.a_15,
            R.drawable.a_16




    };

    private void mSound(int sound) {
        if (customPref.getSound()){
            float volume = 0.1f;
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
        toolbar = findViewById(R.id.toolbar);
        customPref = new CustomPref(StatusActivity.this);


        setSupportActionBar(toolbar);
        setTitle(getIntent().getStringExtra("title"));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        statusAdapter = new StatusAdapter(this, new Onclick() {
            @Override
            public void CategoryOnclick(String category_title, int position) {

            }

            @Override
            public void StatusOnclick() {

            }
        });



        recyclerview = findViewById(R.id.recyclerview);
        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            // Screen is in portrait mode
            recyclerview.setLayoutManager(new LinearLayoutManager(StatusActivity.this));
        } else if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // Screen is in landscape mode
            recyclerview.setLayoutManager(new LinearLayoutManager(StatusActivity.this));
        }
        recyclerview.setHasFixedSize(true);
        recyclerview.setAdapter(statusAdapter);



    }

    public class StatusAdapter extends RecyclerView.Adapter<StatusAdapter.ViewHolder>{
        Context context;

        Onclick onclick;
        public StatusAdapter(Context context, Onclick onclick) {
            this.context = context;
            this.onclick = onclick;
        }

        @NonNull
        @Override
        public StatusAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_status, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull StatusAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
            HashMap<String, String> status = statusList.get(position);
            holder.status_text.setText(status.get("status"));
            try {
                current++;
                if (current >= status_bg.length) {
                    current = 0;
                    holder.status_image.setImageResource(status_bg[current]);
                }else holder.status_image.setImageResource(status_bg[current]);
            }catch (Exception e){
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            holder.status_content.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mSound(R.raw.like_1);
                    DetailsActivity.statusListView = statusList;
                    Intent intent = new Intent(context, DetailsActivity.class);
                    intent.putExtra("position", position);
                    if (admobAds.isLoaded()) {
                        admobAds.show(new AdmobAds.Dismissed() {
                            @Override
                            public void onclick() {
                                startActivity(intent);
                            }
                        });

                    }else {
                        startActivity(intent);
                    }
                }
            });



            DatabaseHelper databaseHelper = new DatabaseHelper(context);
            ContentValues fav = new ContentValues();

            if (databaseHelper.getFavouriteById(status.get("id"))){
                holder.btn_like.setImageResource(R.drawable.ic_liked);}
            else {holder.btn_like.setImageResource(R.drawable.ic_like);}


            holder.like.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mSound(R.raw.like_1);
                    if (databaseHelper.getFavouriteById(status.get("id"))){
                        databaseHelper.removeFavouriteById(status.get("id"));
                        holder.btn_like.setImageResource(R.drawable.ic_like);
                        Toast.makeText(context, "Unlike Success..", Toast.LENGTH_SHORT).show();
                    }else {
                        fav.put(DatabaseHelper.KEY_ID, status.get("id"));
                        fav.put(DatabaseHelper.KEY_TITLE, status.get("status"));
                        databaseHelper.addFavourite(fav, null);
                        holder.btn_like.setImageResource(R.drawable.ic_liked);
                        Toast.makeText(context, "Like Successful", Toast.LENGTH_SHORT).show();
                    }
                }
            });


            holder.copy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mSound(R.raw.like_1);
                    ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("status", status.get("status"));
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(context, "Copied To Clipboard", Toast.LENGTH_SHORT).show();
                }
            });

            holder.share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //======================
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
                                    i.putExtra(android.content.Intent.EXTRA_TEXT, status.get("status"));
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
            return statusList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            LinearLayout like, copy, share;
            LinearLayout status_content;
            ImageView status_image;
            TextView status_text;
            ImageView btn_like;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                btn_like = itemView.findViewById(R.id.btn_like);
                like = itemView.findViewById(R.id.like);
                copy = itemView.findViewById(R.id.copy);
                share = itemView.findViewById(R.id.share);
                status_content = itemView.findViewById(R.id.status_content);
                status_image = itemView.findViewById(R.id.status_image);
                status_text = itemView.findViewById(R.id.status_text);
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