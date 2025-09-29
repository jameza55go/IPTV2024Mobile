package com.iptv.newiptvmobile;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.gson.Gson;
import com.iptv.newiptvmobile.model.Api;
import com.iptv.newiptvmobile.ui.tool.topup.TopUpActivity;

import java.io.IOException;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AndroidPlayer extends AppCompatActivity implements MediaPlayer.OnCompletionListener{

    VideoView videoView;
    String url_media,id,username,password,type,uid,tid,apiurl;
    int playcontinue = 0;
    private Timer timer;
    SharedPreferences db;
    SharedPreferences.Editor editor;
    boolean login;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_android_player);
        db = getApplicationContext().getSharedPreferences("iptv", MODE_PRIVATE);
        uid=db.getString("uid", "");
        username=db.getString("user", "");
        password=db.getString("pass", "");
        apiurl=db.getString("apiurl",getString(R.string.api_main));
        login=db.getBoolean("login", false);
        editor = getSharedPreferences("iptv",MODE_PRIVATE).edit();
        url_media = getIntent().getExtras().getString("url_media");
        id = getIntent().getExtras().getString("id");
        tid = getIntent().getExtras().getString("tid");
        type = getIntent().getExtras().getString("type");
        playcontinue=db.getInt(id, 0);
        videoView = findViewById(R.id.androidplay);
        videoView.setMediaController(new MediaController(this));
        videoView.setOnCompletionListener(this);
        videoView.setVideoURI(Uri.parse(url_media));
        if(playcontinue==0){
            videoView.start();
        }else {
            videoView.seekTo(playcontinue);
            videoView.start();
        }
        if(login == false){
            Intent intent2 = new Intent(AndroidPlayer.this, LoginActivity.class);
            startActivity(intent2);
            finish();
        }
        timerCounter();
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
                        editor.putInt(id,videoView.getCurrentPosition());
                        editor.apply();
                    }
                });
            }
        };
        timer.scheduleAtFixedRate(timerTask,10000,10000);
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        editor.putInt(id,0);
        editor.apply();
        new LoadDataPlayEp().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private class LoadDataPlayEp extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {
            OkHttpClient okHttpClient2 = new OkHttpClient();

            RequestBody formBody = new FormBody.Builder()
                    .add("id", id)
                    .add("type", type)
                    .add("tid", tid)
                    .add("uid", uid)
                    .add("username", username)
                    .add("password", password)
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
                Toast.makeText(AndroidPlayer.this, "Please check your internet!", Toast.LENGTH_SHORT).show();
            } else if (string.contains("ServerError")) {
                Toast.makeText(AndroidPlayer.this, "Server Error!", Toast.LENGTH_SHORT).show();
            } else {
                showData(string);
            }
        }

        @SuppressLint("NotifyDataSetChanged")
        private void showData(String jsonString) {
            Gson gson = new Gson();
            final Api blog = gson.fromJson(jsonString, Api.class);
            if (blog.getStatus().equals("success")){
                id = blog.getId();
                videoView.setVideoURI(Uri.parse(blog.getUrl()));
                videoView.start();
            }else {
                AlertDialog.Builder builder = new AlertDialog.Builder(AndroidPlayer.this);
                builder.setMessage(blog.getMessage())
                        .setCancelable(false)
                        .setPositiveButton(R.string.bt_yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                                Intent intent = new Intent(AndroidPlayer.this, TopUpActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        }
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
    protected void onStop() {
        super.onStop();
        timer.cancel();
        timer.purge();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        timer.cancel();
        timer.purge();
    }
}