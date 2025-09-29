package com.iptv.newiptvmobile;

import android.net.Uri;
import android.os.Bundle;
import android.widget.VideoView;
import androidx.appcompat.app.AppCompatActivity;

public class AdsPlayer extends AppCompatActivity{

    VideoView videoView;
    String url_media;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ads_player);
        url_media = getIntent().getExtras().getString("url_media");
        videoView = findViewById(R.id.androidplay);
        videoView.setVideoURI(Uri.parse(url_media));
        videoView.setOnPreparedListener(mp -> {
            mp.setLooping(true);
            videoView.start();
        });
        videoView.setOnCompletionListener(mp -> {
            videoView.start();
        });
        videoView.setOnErrorListener((mp, what, extra) -> {
            videoView.start();
            return false;
        });
    }
}