package com.maracuja_juice.spotifynotifications.com.maracuja_juice.spotifynotifications.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.maracuja_juice.spotifynotifications.R;
import com.maracuja_juice.spotifynotifications.services.OnTaskCompleted;
import com.maracuja_juice.spotifynotifications.services.SpotifyCrawlerTask;

import java.util.List;

import kaaes.spotify.webapi.android.models.Album;

public class MainActivity extends AppCompatActivity implements OnTaskCompleted {

    private static final String LOG_TAG = MainActivity.class.getName();
    private boolean isLoggedIn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Bundle receiveBundle = this.getIntent().getExtras();
        if(receiveBundle != null) {
            String token = receiveBundle.getString("token");
            int expiresIn = receiveBundle.getInt("expiresIn");
            isLoggedIn = true;

            //TODO: SpotifyCrawler that calls AlbumCrawler and ArtistCrawler. Here I will call SpotifyCrawler
            new SpotifyCrawlerTask(token, this).execute();
        }

        if(!isLoggedIn) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onTaskCompleted(Object result) {
        List<Album> albums = (List<Album>) result;
        for (int i = 0; i < albums.size(); i++) {
            Log.d(LOG_TAG, albums.get(i).name);
        }
    }
}
