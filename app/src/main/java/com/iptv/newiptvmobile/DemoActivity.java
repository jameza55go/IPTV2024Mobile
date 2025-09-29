package com.iptv.newiptvmobile;

import static android.widget.Toast.LENGTH_LONG;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.iptv.newiptvmobile.R;
import com.iptv.newiptvmobile.adapter.ListDemoMainAdapter;
import com.iptv.newiptvmobile.adapter.ListLiveMainAdapter;
import com.iptv.newiptvmobile.adapter.ListLiveSubAdapter;
import com.iptv.newiptvmobile.adapter.ListLiveTypeAdapter;
import com.iptv.newiptvmobile.listener.OnClickListLiveMain;
import com.iptv.newiptvmobile.listener.OnClickListLiveSub;
import com.iptv.newiptvmobile.listener.OnClickListLiveType;
import com.iptv.newiptvmobile.listener.OnFocusRvLiveMain;
import com.iptv.newiptvmobile.model.Api;
import com.iptv.newiptvmobile.model.Data;
import com.iptv.newiptvmobile.model.SubData;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.dnsoverhttps.DnsOverHttps;

public class DemoActivity extends AppCompatActivity {

    ListView listView_live_main,listView_live_sub,list_live_type;
    ListDemoMainAdapter adapterMain;
    List<Data> data;
    Context context = DemoActivity.this;
    boolean readyToLost = false;
    String type = "livethai";
    int widthList = 350;
    VideoView videoView;
    String idLink,remotenumber,tableID,apiurl;
    String fromActivity = "live";
    int item_selected = 0;
    SharedPreferences db;
    SharedPreferences.Editor db_edit;
    ProgressBar progressBar;
    TextView remote;
    boolean timerstart = false;
    boolean timerstartremote = false;
    private Timer timer;
    private Timer timerremote;
    String uid,username,password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Application.getInstance().initAppLanguage(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);
        db = getApplicationContext().getSharedPreferences("iptv", MODE_PRIVATE);
        apiurl=db.getString("apiurl",getString(R.string.api_main));
        listView_live_main = findViewById(R.id.list_live_main);
        videoView = findViewById(R.id.androidplayer);
        progressBar = findViewById(R.id.progress);
        remote = findViewById(R.id.remote);
        new LoadDataLive().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private class LoadDataLive extends AsyncTask<String, Integer, String> {

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

            Request request = new Request.Builder()
                    .url(apiurl + "demo")
                    .build();

            try {
                Response response = okHttpClient2.newCall(request).execute();
                if (response.isSuccessful()) {
                    return response.body().string();
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
                Toast.makeText(context, "Please check your internet!", Toast.LENGTH_SHORT).show();
            } else if (string.contains("ServerError")) {
                Toast.makeText(context, "Server Error!", Toast.LENGTH_SHORT).show();
            } else {
                showData(string);
            }
        }
        private void showData(String jsonString) {
            Gson gson = new Gson();
            final Api blog = gson.fromJson(jsonString, Api.class);
            data = blog.getData();
            adapterMain = new ListDemoMainAdapter(context, data, new OnClickListLiveMain() {
                @Override
                public void onClickListLiveMain(int position) {
                    item_selected = position;
                        progressBar.setVisibility(View.VISIBLE);
                        idLink = data.get(position).getId();
                        hideList();
                        new PlayLive().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }
            }, new OnFocusRvLiveMain() {
                @Override
                public void onFocusRvLiveMain(int position) {

                }
            });
            listView_live_main.setAdapter(adapterMain);
            listView_live_main.setItemsCanFocus(true);
            listView_live_main.setSelection(0);
            listView_live_main.setItemChecked(0,true);
            listView_live_main.requestFocus();
            idLink = data.get(0).getId();
            new PlayLive().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    private class PlayLive extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {
            OkHttpClient okHttpClient2 = new OkHttpClient();

            RequestBody formBody = new FormBody.Builder()
                    .add("id", idLink)
                    .build();
            Request request = new Request.Builder()
                    .url(apiurl + "demo/play")
                    .post(formBody)
                    .build();

            try {
                Response response = okHttpClient2.newCall(request).execute();
                if (response.isSuccessful()) {
                    return response.body().string();
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
                Toast.makeText(context, "Please check your internet!", Toast.LENGTH_SHORT).show();
            } else if (string.contains("ServerError")) {
                Toast.makeText(context, "Server Error!", Toast.LENGTH_SHORT).show();
            } else {
                showData(string);
            }
        }

        private void showData(String jsonString) {
            Gson gson = new Gson();
            final Api blog = gson.fromJson(jsonString, Api.class);
            if (blog.getStatus().equals("success")){
                videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                    @Override
                    public boolean onError(MediaPlayer mp, int what, int extra) {
                        progressBar.setVisibility(View.VISIBLE);
                        return true;
                    }
                });
                videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        progressBar.setVisibility(View.VISIBLE);
                    }
                });
                videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        progressBar.setVisibility(View.GONE);
                    }
                });
                videoView.setOnInfoListener(new MediaPlayer.OnInfoListener() {
                    @Override
                    public boolean onInfo(MediaPlayer mp, int what, int extra) {
                        if (what == MediaPlayer.MEDIA_INFO_BUFFERING_START) {
                            progressBar.setVisibility(View.VISIBLE);
                        } else if (what == MediaPlayer.MEDIA_INFO_BUFFERING_END) {
                            progressBar.setVisibility(View.GONE);
                        }
                        return false;
                    }
                });
                videoView.setVideoURI(Uri.parse(blog.getUrl()));
                videoView.start();
            }else{
                Toast.makeText(context,blog.getMessage(), LENGTH_LONG).show();
            }
        }
    }

    void hideList(){
        listView_live_main.setVisibility(View.GONE);
    }

    void ShowList(){
        listView_live_main.setVisibility(View.VISIBLE);
        listView_live_main.requestFocus();
        listView_live_main.setSelection(item_selected);
        listView_live_main.setItemChecked(item_selected,true);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (listView_live_main.getVisibility() == View.GONE){
                    ShowList();
                    return true;
                }
                break;
        }
        return super.dispatchTouchEvent(event);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        int currentItemList = listView_live_main.getSelectedItemPosition();
        int lastPositionItemList = listView_live_main.getCount()-1;

        if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER){
            if (listView_live_main.getVisibility() == View.GONE){
                ShowList();
                return true;
            }
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN){
            if(listView_live_main.getVisibility()==View.GONE) {
                if (item_selected >= 1) {
                    item_selected = item_selected -1;
                }else {
                    item_selected = listView_live_main.getCount()-1;
                }
                idLink = data.get(item_selected).getId();
                progressBar.setVisibility(View.VISIBLE);
                RemoteUpDown();
                return true;
            }else {
                if (lastPositionItemList == currentItemList) {
                    listView_live_main.setSelection(0);
                    return true;
                }
            }
        }else if (keyCode == KeyEvent.KEYCODE_DPAD_UP){
            if(listView_live_main.getVisibility()==View.GONE) {
                int tcount = listView_live_main.getCount() - 1;
                if (item_selected < tcount) {
                    item_selected = item_selected +1;
                }else {
                    item_selected = 0;
                }
                idLink = data.get(item_selected).getId();
                progressBar.setVisibility(View.VISIBLE);
                RemoteUpDown();
                return true;
            }else {
                if (currentItemList == 0) {
                    listView_live_main.setSelection(lastPositionItemList);
                    return true;
                }
            }
        }else if (keyCode == KeyEvent.KEYCODE_CHANNEL_DOWN){
            if(listView_live_main.getVisibility()==View.GONE) {
                if (item_selected >= 1) {
                    item_selected = item_selected -1;
                }else {
                    item_selected = listView_live_main.getCount()-1;
                }
                idLink = data.get(item_selected).getId();
                progressBar.setVisibility(View.VISIBLE);
                RemoteUpDown();
                return true;
            }else {
                if (lastPositionItemList == currentItemList) {
                    listView_live_main.setSelection(0);
                    return true;
                }
            }
        }else if (keyCode == KeyEvent.KEYCODE_CHANNEL_UP){
            if(listView_live_main.getVisibility()==View.GONE) {
                int tcount = listView_live_main.getCount() - 1;
                if (item_selected < tcount) {
                    item_selected = item_selected +1;
                }else {
                    item_selected = 0;
                }
                idLink = data.get(item_selected).getId();
                progressBar.setVisibility(View.VISIBLE);
                RemoteUpDown();
                return true;
            }else {
                if (currentItemList == 0) {
                    listView_live_main.setSelection(lastPositionItemList);
                    return true;
                }
            }
        }else if(keyCode == KeyEvent.KEYCODE_BACK){
            if(listView_live_main.getVisibility()==View.VISIBLE){
                hideList();
                return true;
            }
        }else if(keyCode >= 7 && keyCode <= 16){
            if(listView_live_main.getVisibility()==View.VISIBLE){
                hideList();
            }
            remote.setText(remote.getText() + String.valueOf(event.getNumber()));
            PullRemote();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void RemoteUpDown(){
        if(timerstartremote==false) {
            timerstartremote = true;
            timerremote = new Timer();
            TimerTask timerTaskRemote = new TimerTask() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @RequiresApi(api = Build.VERSION_CODES.M)
                        @Override
                        public void run() {
                            timerstartremote = false;
                            timerremote.cancel();
                            timerremote.purge();
                            new PlayLive().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                        }
                    });
                }
            };
            timerremote.scheduleAtFixedRate(timerTaskRemote,3000,10000);
        }
    }

    public void PullRemote(){
        if(timerstart==false) {
            timerstart = true;
            timer = new Timer();
            TimerTask timerTask = new TimerTask() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @RequiresApi(api = Build.VERSION_CODES.M)
                        @Override
                        public void run() {
                            timerstart = false;
                            timer.cancel();
                            timer.purge();
                            remotenumber = remote.getText().toString();
                            remote.setText("");
                            PullRemoteSend();
                        }
                    });
                }
            };
            timer.scheduleAtFixedRate(timerTask,3000,10000);
        }
    }

    public void PullRemoteSend(){
        Gson gson = new GsonBuilder().create();
        JsonArray myCustomArray = gson.toJsonTree(data).getAsJsonArray();
        boolean c = false;
        for(int i=0;i<data.size();i++){
            c = myCustomArray.get(i).toString().contains("\"remotetv\":\""+remotenumber+"\"");
            if(c == true){
                progressBar.setVisibility(View.VISIBLE);
                item_selected = i;
                idLink = data.get(i).getId();
                new PlayLive().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                break;
            }
        }
    }
}