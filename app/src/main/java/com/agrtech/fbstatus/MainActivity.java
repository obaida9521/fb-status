package com.agrtech.fbstatus;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.core.view.MenuItemCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.BuildConfig;
import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    DrawerLayout drawerLayout;
    ImageView openMenu, liked;
    NavigationView navigationView;
    RecyclerView recyclerView;
    CategoryAdapter categoryAdapter;
    CustomPref customPref;
    MediaPlayer mediaPlayer;



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
        setContentView(R.layout.drawer);
        AddItem.addItem();
        loadInterstitial();
        admobAds.loadAppOpenAd();
        customPref = new CustomPref(MainActivity.this);
        navigationView = findViewById(R.id.navigationView);
        drawerLayout = findViewById(R.id.drawerLayout);
        openMenu = findViewById(R.id.openMenu);
        liked = findViewById(R.id.fav);
        openMenu.setOnClickListener(view -> {
            mSound(R.raw.like_1);

            if (!drawerLayout.isDrawerOpen(GravityCompat.END)){
                drawerLayout.openDrawer(GravityCompat.END);
            }
        });

        liked.setOnClickListener(view -> {
            if (admobAds.isLoaded()){
                admobAds.show(new AdmobAds.Dismissed() {
                    @Override
                    public void onclick() {
                        startActivity(new Intent(getApplicationContext(),Liked.class));
                    }
                });
            }else {
                startActivity(new Intent(getApplicationContext(),Liked.class));
            }

            mSound(R.raw.like_1);
        });







        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                mSound(R.raw.like_1);
                switch (item.getItemId()){

                    case R.id.nev_fav:
                        startActivity(new Intent(getApplicationContext(), Liked.class));
                        break;

                    case R.id.share:
                        Intent sendIntent = new Intent();
                        sendIntent.setAction(Intent.ACTION_SEND);
                        sendIntent.putExtra(Intent.EXTRA_TEXT,getResources().getString(R.string.Share_app_massege) +
                                        ": https://play.google.com/store/apps/details?id="+getPackageName());
                        sendIntent.setType("text/plain");
                        startActivity(sendIntent);
                        break;
                    case R.id.PrivacyPolicy:
                        try {
                            Uri uri = Uri.parse(getResources().getString(R.string.PrivacyPolicy));
                            Intent fbi = new Intent(Intent.ACTION_VIEW, uri);
                            startActivity(fbi);

                        } catch (ActivityNotFoundException e) {
                            Toast.makeText(getApplicationContext(), "No application can handle this request."
                                    + " Please install a webrowser", Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                        }

                        break;

                    case R.id.retus:
                        try{
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id="+getPackageName())));
                        }
                        catch (ActivityNotFoundException e){
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id="+getPackageName())));
                        }
                        break;

                }
                return true;
            }
        });
        Switch mySwitch = (Switch) MenuItemCompat.getActionView(navigationView.getMenu().findItem(R.id.menu_switch));
        mySwitch.setChecked(customPref.getSound());
        mySwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            mSound(R.raw.like_1);
            customPref.setSound(isChecked);
            if (customPref.getSound()){
                mySwitch.setChecked(customPref.getSound());
                Toast.makeText(getApplicationContext(), "Sound ON", Toast.LENGTH_SHORT).show();
                mSound(R.raw.like_1);
            }else {
                mySwitch.setChecked(customPref.getSound());
                Toast.makeText(getApplicationContext(), "Sound OFF", Toast.LENGTH_SHORT).show();
            }
        });


        //searchView();



        categoryAdapter = new CategoryAdapter(AddItem.categoryArrayList, this, new Onclick() {
            @Override
            public void CategoryOnclick(String category_title, int position) {
                mSound(R.raw.like_1);
                StatusActivity.statusList = AddItem.statusArrayList.get(position);
                Intent intent = new Intent(MainActivity.this, StatusActivity.class);
                intent.putExtra("title", category_title);
                if (admobAds.isLoaded()){
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

            @Override
            public void StatusOnclick() {

            }
        });
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(MainActivity.this, 2));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(categoryAdapter);



    }



    /*private void searchView() {
        EditText edSearch = findViewById(R.id.edSearch);
        ImageView clear_text = findViewById(R.id.clear_text);
        edSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!edSearch.getText().toString().isEmpty()){
                    clear_text.setVisibility(View.VISIBLE);
                }else clear_text.setVisibility(View.GONE);
                mSound(R.raw.click);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                filter(String.valueOf(editable));
            }
        });

        clear_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edSearch.setText("");
                mSound(R.raw.click);
            }
        });


    }*/

    /*private void filter(String text) {

        ArrayList<HashMap<String, String>> filteredList = new ArrayList<>();

        for (HashMap<String, String> item : AddItem.categoryArrayList) {
            if (item.get("category_name").toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(item);
            }
        }

        categoryAdapter.filterList(filteredList);
    }*/

    public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder>{

        ArrayList<HashMap<String, String>> arrayList;

        Context context;
        Onclick onclick;
        int current = 0;

        public CategoryAdapter(ArrayList<HashMap<String, String>> arrayList, Context context, Onclick onclick) {
            this.arrayList = arrayList;
            this.context = context;
            this.onclick = onclick;
        }

        @NonNull
        @Override
        public CategoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_category, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull CategoryAdapter.ViewHolder holder, int position) {
            HashMap<String, String> mHashMap = arrayList.get(position);
            List<Integer> colors = new ArrayList<Integer>();
            colors.add(context.getResources().getColor(R.color.color_1));
            colors.add(context.getResources().getColor(R.color.color_2));
            colors.add(context.getResources().getColor(R.color.color_3));
            colors.add(context.getResources().getColor(R.color.color_4));
            colors.add(context.getResources().getColor(R.color.color_5));
            colors.add(context.getResources().getColor(R.color.color_6));

            holder.category_title.setText(mHashMap.get("category_name"));
            holder.category_logo.setImageResource(Integer.parseInt(mHashMap.get("logo")));
            holder.category_card.setOnClickListener(view -> {
                onclick.CategoryOnclick(mHashMap.get("category_name"), position);
            });
            int nightModeFlags = context.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
            switch (nightModeFlags) {
                case Configuration.UI_MODE_NIGHT_YES:
                    holder.category_card.setCardBackgroundColor(context.getResources().getColor(R.color.primary));
                    break;
                case Configuration.UI_MODE_NIGHT_NO:

                    try {
                        current++;
                        if (current >= colors.size()) {
                            current = 0;
                            holder.category_card.setCardBackgroundColor(colors.get(current));
                        }else holder.category_card.setCardBackgroundColor(colors.get(current));
                    }catch (Exception e){

                    }

                    break;
                case Configuration.UI_MODE_NIGHT_UNDEFINED:

                    try {
                        current++;
                        if (current >= colors.size()) {
                            current = 0;
                            holder.category_card.setCardBackgroundColor(colors.get(current));
                        }else holder.category_card.setCardBackgroundColor(colors.get(current));
                    }catch (Exception e){

                    }

                    break;
            }






        }

        @Override
        public int getItemCount() {
            return arrayList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView category_title;
            CardView category_card;
            CircleImageView category_logo;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                category_title = itemView.findViewById(R.id.category_title);
                category_card = itemView.findViewById(R.id.category_card);
                category_logo = itemView.findViewById(R.id.category_logo);




            }
        }

       /* public void filterList(ArrayList<HashMap<String, String>> filteredList) {
            arrayList = filteredList;
            notifyDataSetChanged();
        }*/

    }




    @Override
    protected void onResume() {
        super.onResume();
        admobAds.appOpenCall();
    }

}