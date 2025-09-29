package com.iptv.newiptvmobile;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.media3.common.C;
import androidx.media3.common.MediaItem;
import androidx.media3.common.MimeTypes;
import androidx.media3.common.PlaybackException;
import androidx.media3.common.PlaybackParameters;
import androidx.media3.common.Player;
import androidx.media3.common.util.Log;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.common.util.Util;
import androidx.media3.datasource.DataSource;
import androidx.media3.datasource.DefaultDataSource;
import androidx.media3.datasource.DefaultHttpDataSource;
import androidx.media3.datasource.HttpDataSource;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.exoplayer.dash.DashMediaSource;
import androidx.media3.exoplayer.dash.DefaultDashChunkSource;
import androidx.media3.exoplayer.hls.HlsMediaSource;
import androidx.media3.exoplayer.source.MediaSource;
import androidx.media3.exoplayer.source.ProgressiveMediaSource;
import androidx.media3.extractor.DefaultExtractorsFactory;
import androidx.media3.extractor.Extractor;
import androidx.media3.ui.PlayerView;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.gson.Gson;
import com.iptv.newiptvmobile.model.Api;
import com.iptv.newiptvmobile.ui.liveTV.FwplayerActivity;
import com.iptv.newiptvmobile.ui.liveTV.LiveActivity;
import com.iptv.newiptvmobile.ui.tool.topup.TopUpActivity;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.dnsoverhttps.DnsOverHttps;


@UnstableApi public class PlayerExoPlayerActivity extends AppCompatActivity {

    PlayerView exo_player;
    String url_media,id,username,password,type,uid,tid,ep,apiurl,deviceId;
    ExoPlayer player;
    Long playcontinue;
    private Timer timer;
    SharedPreferences db;
    SharedPreferences.Editor editor;
    boolean login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_exo_player);
        db = getApplicationContext().getSharedPreferences("iptv", MODE_PRIVATE);
        deviceId = android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
        uid=db.getString("uid", "");
        username=db.getString("user", "");
        password=db.getString("pass", "");
        apiurl=db.getString("apiurl",getString(R.string.api_main));
        editor = getSharedPreferences("iptv",MODE_PRIVATE).edit();
        url_media = getIntent().getExtras().getString("url_media");
        ep = getIntent().getExtras().getString("id");
        id = "f"+getIntent().getExtras().getString("id");
        tid = getIntent().getExtras().getString("tid");
        type = getIntent().getExtras().getString("type");
        playcontinue=db.getLong(id,0);
        exo_player = findViewById(R.id.exo_player);
        login=db.getBoolean("login", false);
        if(login == false){
            Intent intent2 = new Intent(PlayerExoPlayerActivity.this, LoginActivity.class);
            startActivity(intent2);
            finish();
        }
        initializePlayerExoPlayer();
    }

    private void initializePlayerExoPlayer() {
        player = new ExoPlayer.Builder(PlayerExoPlayerActivity.this)
                .build();
        exo_player.setPlayer(player);
        player.setTrackSelectionParameters(
                player.getTrackSelectionParameters()
                        .buildUpon()
                        .build());

        int type = Util.inferContentType(url_media);
        if (type == C.CONTENT_TYPE_HLS){
            DataSource.Factory dataSourceFactory = new DefaultHttpDataSource.Factory();
            HlsMediaSource hlsMediaSource =
                    new HlsMediaSource.Factory(dataSourceFactory).createMediaSource(MediaItem.fromUri(url_media));
            player.setMediaSource(hlsMediaSource);
        }else {
            MediaItem mediaItem = new MediaItem.Builder()
                    .setUri(url_media)
                    .build();
            player.setMediaItem(mediaItem);
        }
        if(playcontinue>0){
            player.seekTo(playcontinue);
        }
        player.prepare();
        player.play();

        player.addListener(new Player.Listener() {
            @Override
            public void onPlaybackStateChanged(@Player.State int playbackState) {
                if (playbackState == Player.STATE_ENDED) {
                    editor.putLong(id,0);
                    editor.apply();
                    new LoadDataPlayEp().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }
            }
            @Override
            public void onPlayerError(@NonNull PlaybackException error) {
                Toast.makeText(PlayerExoPlayerActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        timerCounter();
    }

    private class LoadDataPlayEp extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {
            DnsOverHttps dohDns;
            try {
                dohDns = new DnsOverHttps.Builder()
                        .client(new OkHttpClient())
                        .url(HttpUrl.get("https://cloudflare-dns.com/dns-query"))
                        .bootstrapDnsHosts(
                                InetAddress.getByName("1.1.1.1"),
                                InetAddress.getByName("1.0.0.1")
                        )
                        .includeIPv6(false)
                        .build();
            } catch (UnknownHostException e) {
                return "DnsInitError";
            }
            OkHttpClient okHttpClient2 = new OkHttpClient().newBuilder()
                    .dns(dohDns)
                    .build();

            RequestBody formBody = new FormBody.Builder()
                    .add("id", ep)
                    .add("type", type)
                    .add("tid", tid)
                    .add("uid", uid)
                    .add("username", username)
                    .add("password", password)
                    .add("device", deviceId)
                    .build();
            Request request = new Request.Builder()
                    .url(apiurl + "play/nextep")
                    .post(formBody)
                    .build();

            try {
                Response response = okHttpClient2.newCall(request).execute();
                if (response.isSuccessful()) {
                    return Objects.requireNonNull(response.body()).string();
                } else {
                    return "ServerError";
                }
            } catch (IOException e) {
                return "InternetError";
            }
        }

        @Override
        protected void onPostExecute(String string) {
            if (string.contains("InternetError")) {
                Toast.makeText(PlayerExoPlayerActivity.this, "Please check your internet!", Toast.LENGTH_SHORT).show();
            } else if (string.contains("ServerError")) {
                Toast.makeText(PlayerExoPlayerActivity.this, "Server Error!", Toast.LENGTH_SHORT).show();
            } else {
                showData(string);
            }
        }

        @SuppressLint("NotifyDataSetChanged")
        private void showData(String jsonString) {
            Gson gson = new Gson();
            final Api blog = gson.fromJson(jsonString, Api.class);
            if (blog.getStatus().equals("success")){
                id = "f"+blog.getId();
                ep = blog.getId();
                url_media = blog.getUrl();
                int type = Util.inferContentType(url_media);
                if (type == C.CONTENT_TYPE_HLS){
                    DataSource.Factory dataSourceFactory = new DefaultHttpDataSource.Factory();
                    HlsMediaSource hlsMediaSource =
                            new HlsMediaSource.Factory(dataSourceFactory).createMediaSource(MediaItem.fromUri(url_media));
                    player.setMediaSource(hlsMediaSource);
                }else {
                    MediaItem mediaItem = new MediaItem.Builder()
                            .setUri(url_media)
                            .build();
                    player.setMediaItem(mediaItem);
                }
                player.prepare();
                player.play();
            }else if(blog.getStatus().equals("device")){
                AlertDialog.Builder builder = new AlertDialog.Builder(PlayerExoPlayerActivity.this);
                builder.setMessage(blog.getMessage())
                        .setCancelable(false)
                        .setPositiveButton(R.string.bt_yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                                Intent intent = new Intent(PlayerExoPlayerActivity.this, LoginActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }else if (blog.getStatus().equals("finish")){
                AlertDialog.Builder builder = new AlertDialog.Builder(PlayerExoPlayerActivity.this);
                builder.setMessage(blog.getMessage())
                        .setCancelable(false)
                        .setPositiveButton(R.string.bt_yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                                finish();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }else {
                AlertDialog.Builder builder = new AlertDialog.Builder(PlayerExoPlayerActivity.this);
                builder.setMessage(blog.getMessage())
                        .setCancelable(false)
                        .setPositiveButton(R.string.bt_yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                                Intent intent = new Intent(PlayerExoPlayerActivity.this, TopUpActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        }
    }

    public void timerCounter(){
        timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void run() {
                        if(player.isPlaying()) {
                            editor.putLong(id, player.getCurrentPosition());
                            editor.apply();
                        }
                    }
                });
            }
        };
        timer.scheduleAtFixedRate(timerTask,10000,10000);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            timer.cancel();
            timer.purge();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {

        int action = event.getAction();
        int keyCode = event.getKeyCode();

        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_CENTER:
                if (event.getRepeatCount() == 0){
                    onCenterLongPressUp();
                }
                if (action == KeyEvent.ACTION_DOWN && event.isLongPress()) {
                    onCenterLongPress();
                    return true;
                }

            default:
                return super.dispatchKeyEvent(event);
        }
    }

    private void onCenterLongPressUp() {
        player.setPlaybackSpeed(1f);
        exo_player.setUseController(true);
        exo_player.showController();
    }

    private void onCenterLongPress() {
        player.setPlaybackSpeed(2f);
        exo_player.hideController();
        exo_player.setUseController(false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (player != null) {
            player.setPlayWhenReady(false);
            player.stop();
            timer.cancel();
            timer.purge();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        player.release();
        player = null;
        timer.cancel();
        timer.purge();
    }

}