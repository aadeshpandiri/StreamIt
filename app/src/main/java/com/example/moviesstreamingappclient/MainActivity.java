package com.example.moviesstreamingappclient;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.moviesstreamingappclient.Adapter.MovieShowAdapter;
import com.example.moviesstreamingappclient.Adapter.SliderPagerAdapterNew;
import com.example.moviesstreamingappclient.Model.GetVideoDetails;
import com.example.moviesstreamingappclient.Model.MovieItemClickListenerNew;
import com.example.moviesstreamingappclient.Model.SliderSide;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.startapp.sdk.adsbase.StartAppSDK;

public class MainActivity extends AppCompatActivity implements MovieItemClickListenerNew {

    MovieShowAdapter movieShowAdapter;
    DatabaseReference mDatabasereference;
    private List<GetVideoDetails> uploads,uploadslistLatest,uploadslistPopular;
    private List<GetVideoDetails> actionmovies,sportmovies,comedyvideos,romanticmovies,adventuremovies;
    private ViewPager sliderPager;
    private List<SliderSide> uploadsSlider;
    private TabLayout indicator,tabmoviesactions;
    private RecyclerView MoviesRv,moviesRvWeek,tab;
    ProgressDialog progressDialog;
   // private AdView mAdView;
   // FloatingActionButton play_fab;

    @SuppressLint("WrongConstant")

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar);

        progressDialog = new ProgressDialog(this);


        //String moviesUrl = getIntent().getExtras().getString("movieUrl");



        inViews();
        addAllMovies();
        iniPopularMovies();
        iniWeekMovies();
        moviesViewTab();


       // StartAppSDK.setTestAdsEnabled(BuildConfig.DEBUG);


    }

    private void addAllMovies(){

        uploads = new ArrayList<>();
        uploadslistLatest = new ArrayList<>();
        uploadslistPopular = new ArrayList<>();
        uploadsSlider = new ArrayList<>();
        actionmovies = new ArrayList<>();
        adventuremovies = new ArrayList<>();
        comedyvideos = new ArrayList<>();
        romanticmovies = new ArrayList<>();
        sportmovies = new ArrayList<>();

        mDatabasereference = FirebaseDatabase.getInstance().getReference("videos");
        progressDialog.setMessage("loading.....");
        progressDialog.show();

        mDatabasereference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                    GetVideoDetails upload = postSnapshot.getValue(GetVideoDetails.class);
                    SliderSide slide = postSnapshot.getValue(SliderSide.class);
                    if(upload.getVideo_type().equals("Latest Movies")){
                        uploadslistLatest.add(upload);
                    }
                    if(upload.getVideo_type().equals("Best Popular Movies")){
                        uploadslistPopular.add(upload);
                    }
                    if(upload.getVideo_category().equals("Action")){
                        actionmovies.add(upload);
                    }
                    if(upload.getVideo_category().equals("Adventure")){
                        adventuremovies.add(upload);
                    }
                    if(upload.getVideo_category().equals("Romantic")){
                        romanticmovies.add(upload);
                    }
                    if(upload.getVideo_category().equals("Comedy")){
                        comedyvideos.add(upload);
                    }
                    if(upload.getVideo_category().equals("Sports")){
                        sportmovies.add(upload);
                    }
                    if(upload.getVideo_slide().equals("Slide Movies")){
                        uploadsSlider.add(slide);
                    }

                    uploads.add(upload);
                }
                iniSlider();
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void iniSlider() {

        SliderPagerAdapterNew adapterNew = new SliderPagerAdapterNew(this,uploadsSlider);
        sliderPager.setAdapter(adapterNew);
        adapterNew.notifyDataSetChanged();

        Timer  timer = new Timer();
        timer.scheduleAtFixedRate(new SliderTimer(),4000,6000);
        indicator.setupWithViewPager(sliderPager,true);


    }

    private void iniWeekMovies(){
        movieShowAdapter = new MovieShowAdapter(this,uploadslistLatest,this);
        moviesRvWeek.setAdapter(movieShowAdapter);
        moviesRvWeek.setLayoutManager(new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.HORIZONTAL,true));
        movieShowAdapter.notifyDataSetChanged();
    }

    private void iniPopularMovies(){
        movieShowAdapter = new MovieShowAdapter(this,uploadslistPopular,this);
        MoviesRv.setAdapter(movieShowAdapter);
        MoviesRv.setLayoutManager(new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.HORIZONTAL,true));
        movieShowAdapter.notifyDataSetChanged();
    }


    private void moviesViewTab(){
        getActionMovies();
        tabmoviesactions.addTab(tabmoviesactions.newTab().setText("PRIME"));
        tabmoviesactions.addTab(tabmoviesactions.newTab().setText("NETFLIX"));
        tabmoviesactions.addTab(tabmoviesactions.newTab().setText("HOTSTAR"));
        tabmoviesactions.addTab(tabmoviesactions.newTab().setText("OTHERS"));
        tabmoviesactions.addTab(tabmoviesactions.newTab().setText("WEB SERIES"));

        tabmoviesactions.setTabGravity(TabLayout.GRAVITY_FILL);
        tabmoviesactions.setTabTextColors(ColorStateList.valueOf(Color.WHITE));

        tabmoviesactions.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()){
                    case 0:
                        getActionMovies();
                        break;
                    case 1:
                        getAdventureMovies();
                        break;
                    case 2:
                        getComedyMovies();
                        break;
                    case 3:
                        getRomanticMovies();
                        break;
                    case 4:
                        getSportMovies();
                        break;

                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


    }
    private void inViews(){
        tabmoviesactions = findViewById(R.id.tabActionMovies);
        sliderPager = findViewById(R.id.slider_pager);
        indicator = findViewById(R.id.indicator);
        moviesRvWeek = findViewById(R.id.rv_movies_week);
        MoviesRv = findViewById(R.id.Rv_movies);
        tab = findViewById(R.id.tabrecycler);



    }

    @Override
    public void onMoviesClick(GetVideoDetails movie, ImageView imageView) {

        Intent in = new Intent(this,MovieDetailsActivity.class);
        in.putExtra("title",movie.getVideo_name());
        in.putExtra("imgURL",movie.getVideo_thumb());
        in.putExtra("imgCover",movie.getVideo_thumb());
        in.putExtra("movieDetails",movie.getVideo_description());
        in.putExtra("movieUrl",movie.getVideo_url());
        in.putExtra("movieCategory",movie.getVideo_category());
        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(MainActivity.this,imageView,"sharedName");
        startActivity(in,options.toBundle());

    }


    private class SliderTimer extends TimerTask {
        public void run(){
            MainActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    if(sliderPager.getCurrentItem()<uploadsSlider.size()-1){
                        sliderPager.setCurrentItem(sliderPager.getCurrentItem()+1);

                    }else{
                        sliderPager.setCurrentItem(0);
                    }

                }
            });
        }
    }

    private void getActionMovies(){
        movieShowAdapter = new MovieShowAdapter(this,actionmovies,this);
        tab.setAdapter(movieShowAdapter);
        tab.setLayoutManager(new GridLayoutManager( getApplicationContext(), 3,LinearLayoutManager.VERTICAL,true));

        movieShowAdapter.notifyDataSetChanged();
    }

    private void getSportMovies(){
        movieShowAdapter = new MovieShowAdapter(this,sportmovies,this);
        tab.setAdapter(movieShowAdapter);
        tab.setLayoutManager(new GridLayoutManager( getApplicationContext(), 3,LinearLayoutManager.VERTICAL,true));
        movieShowAdapter.notifyDataSetChanged();
    }

    private void getRomanticMovies(){
        movieShowAdapter = new MovieShowAdapter(this,romanticmovies,this);
        tab.setAdapter(movieShowAdapter);
        tab.setLayoutManager(new GridLayoutManager( getApplicationContext(), 3,LinearLayoutManager.VERTICAL,true));
        movieShowAdapter.notifyDataSetChanged();
    }

    private void getComedyMovies(){
        movieShowAdapter = new MovieShowAdapter(this,comedyvideos,this);
        tab.setAdapter(movieShowAdapter);
        tab.setLayoutManager(new GridLayoutManager( getApplicationContext(), 3,LinearLayoutManager.VERTICAL,true));
        movieShowAdapter.notifyDataSetChanged();
    }
    private void getAdventureMovies(){
        movieShowAdapter = new MovieShowAdapter(this,adventuremovies,this);
        tab.setAdapter(movieShowAdapter);
        tab.setLayoutManager(new GridLayoutManager( getApplicationContext(), 3,LinearLayoutManager.VERTICAL,true));
        movieShowAdapter.notifyDataSetChanged();
    }



}
