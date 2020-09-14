package com.example.moviesstreamingappclient;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.moviesstreamingappclient.Adapter.MovieShowAdapter;
import com.example.moviesstreamingappclient.Model.GetVideoDetails;
import com.example.moviesstreamingappclient.Model.MovieItemClickListenerNew;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.startapp.sdk.adsbase.StartAppAd;
import com.startapp.sdk.adsbase.StartAppSDK;

import java.util.ArrayList;
import java.util.List;

public class MovieDetailsActivity extends AppCompatActivity implements MovieItemClickListenerNew {

    private ImageView MoviesThumbnail,MovieCoverImg;
    TextView tv_title,tv_description;
    FloatingActionButton play_fab;
    RecyclerView RvCast,recyclerView_similarMovies;
    MovieShowAdapter movieShowAdapter;
    DatabaseReference mDatabasereference;
    List<GetVideoDetails>  uploads,actionmovies,sportmovies,comedyvideos,romanticmovies,adventuremovies;

    String current_video_url;
    String getCurrent_video_category;
    //private AdView mAdView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        inview();
        similarmoviesRecycler();
        similarMovis();

        /*MobileAds.initialize(this, "ca-app-pub-5429232651401122~2942629086");
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);*/
        //StartAppSDK.setTestAdsEnabled(BuildConfig.DEBUG);

        //StartAppAd.showAd(this);

        play_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MovieDetailsActivity.this,MoviePlayerActivity.class);
                intent.putExtra("videoUri",current_video_url);
                startActivity(intent);
            }
        });


    }

    private void similarMovis() {
        if(getCurrent_video_category.equals("Action")){
            movieShowAdapter = new MovieShowAdapter(this,actionmovies,this);

            recyclerView_similarMovies.setAdapter(movieShowAdapter);
            recyclerView_similarMovies.setLayoutManager(new LinearLayoutManager(getApplicationContext(),
                    LinearLayoutManager.HORIZONTAL,false));
            movieShowAdapter.notifyDataSetChanged();

        }
        if(getCurrent_video_category.equals("Sports")){
            movieShowAdapter = new MovieShowAdapter(this,sportmovies,this);

            recyclerView_similarMovies.setAdapter(movieShowAdapter);
            recyclerView_similarMovies.setLayoutManager(new LinearLayoutManager(getApplicationContext(),
                    LinearLayoutManager.HORIZONTAL,false));
            movieShowAdapter.notifyDataSetChanged();

        }
        if(getCurrent_video_category.equals("Romantic")){
            movieShowAdapter = new MovieShowAdapter(this,romanticmovies,this);
            recyclerView_similarMovies.setAdapter(movieShowAdapter);
            recyclerView_similarMovies.setLayoutManager(new GridLayoutManager( getApplicationContext(), 3));
            movieShowAdapter.notifyDataSetChanged();

        }
        if(getCurrent_video_category.equals("Comedy")){
            movieShowAdapter = new MovieShowAdapter(this,comedyvideos,this);
            recyclerView_similarMovies.setAdapter(movieShowAdapter);
            recyclerView_similarMovies.setLayoutManager(new GridLayoutManager( getApplicationContext(), 3));
            movieShowAdapter.notifyDataSetChanged();

        }
        if(getCurrent_video_category.equals("Adventure")){
            movieShowAdapter = new MovieShowAdapter(this,adventuremovies,this);

            recyclerView_similarMovies.setAdapter(movieShowAdapter);
            recyclerView_similarMovies.setLayoutManager(new LinearLayoutManager(getApplicationContext(),
                    LinearLayoutManager.HORIZONTAL,false));
            movieShowAdapter.notifyDataSetChanged();

        }
    }

    private void similarmoviesRecycler() {
        uploads = new ArrayList<>();
        sportmovies = new ArrayList<>();
        comedyvideos = new ArrayList<>();
        romanticmovies = new ArrayList<>();
        actionmovies = new ArrayList<>();
        adventuremovies = new ArrayList<>();

        mDatabasereference = FirebaseDatabase.getInstance().getReference("videos");
        mDatabasereference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot postsnapshot : dataSnapshot.getChildren()){
                    GetVideoDetails upload = postsnapshot.getValue(GetVideoDetails.class);
                    if(upload.getVideo_category().equals("Action")){
                        actionmovies.add(upload);
                    }
                    if(upload.getVideo_category().equals("Sports")){
                        sportmovies.add(upload);
                    }
                    if(upload.getVideo_category().equals("Adventure")){
                        adventuremovies.add(upload);
                    }
                    if(upload.getVideo_category().equals("Comedy")){
                        comedyvideos.add(upload);
                    }
                    if(upload.getVideo_category().equals("Romantic")){
                        romanticmovies.add(upload);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void inview() {
        play_fab=findViewById(R.id.play_fab);
        tv_title = findViewById(R.id.detail_movie_title);
        tv_description = findViewById(R.id.detail_movie_desc);
        MoviesThumbnail = findViewById(R.id.details_movies_img);
        MovieCoverImg = findViewById(R.id.detail_movies_cover);
        recyclerView_similarMovies = findViewById(R.id.recycler_similar_movies);
        String moviesTitle = getIntent().getExtras().getString("title");
        String imgRecoresId = getIntent().getExtras().getString("imgURL");
        String imageCover = getIntent().getExtras().getString("imgCover");
        String moviesDetailstext = getIntent().getExtras().getString("movieDetails");
        String moviesUrl = getIntent().getExtras().getString("movieUrl");
        String moviesCategory = getIntent().getExtras().getString("movieCategory");

        current_video_url = moviesUrl;
        getCurrent_video_category = moviesCategory;

        Glide.with(this).load(imgRecoresId).into(MoviesThumbnail);
        Glide.with(this).load(imageCover).into(MovieCoverImg);
        tv_title.setText(moviesTitle);
        tv_description.setText(moviesDetailstext);
        getSupportActionBar().setTitle(moviesTitle);



    }

    @Override
    public void onMoviesClick(GetVideoDetails movie, ImageView imageView) {

        tv_title.setText(movie.getVideo_name());
        getSupportActionBar().setTitle(movie.getVideo_name());
        Glide.with(this).load(movie.getVideo_thumb()).into(MoviesThumbnail);
        Glide.with(this).load(movie.getVideo_thumb()).into(MovieCoverImg);
        tv_description.setText(movie.getVideo_description());
        current_video_url = movie.getVideo_url();
        getCurrent_video_category = movie.getVideo_category();
        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(MovieDetailsActivity.this,imageView,"sharedName");
        options.toBundle();

    }
}
