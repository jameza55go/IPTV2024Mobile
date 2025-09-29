package com.iptv.newiptvmobile.ui.liveTV;

import static android.widget.Toast.LENGTH_SHORT;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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
import com.iptv.newiptvmobile.AdsPlayer;
import com.iptv.newiptvmobile.CategoryActivity;
import com.iptv.newiptvmobile.HomeActivity;
import com.iptv.newiptvmobile.LocaleUtils;
import com.iptv.newiptvmobile.LoginActivity;
import com.iptv.newiptvmobile.R;
import com.iptv.newiptvmobile.adapter.ListLiveMainAdapter;
import com.iptv.newiptvmobile.adapter.ListLiveSubAdapter;
import com.iptv.newiptvmobile.adapter.ListLiveTypeAdapter;
import com.iptv.newiptvmobile.listener.OnClickListLiveMain;
import com.iptv.newiptvmobile.listener.OnClickListLiveSub;
import com.iptv.newiptvmobile.listener.OnClickListLiveType;
import com.iptv.newiptvmobile.listener.OnFocusRvLiveMain;
import com.iptv.newiptvmobile.listener.OnLongClickListLiveMain;
import com.iptv.newiptvmobile.model.Api;
import com.iptv.newiptvmobile.model.Data;
import com.iptv.newiptvmobile.model.SubData;
import com.iptv.newiptvmobile.ui.tool.topup.TopUpActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okhttp3.dnsoverhttps.DnsOverHttps;
import okio.ByteString;

public class LiveActivity extends AppCompatActivity {

    ListView listView_live_main,listView_live_sub,list_live_type;
    ListLiveMainAdapter adapterMain;
    ListLiveSubAdapter adapterSub;
    ListLiveTypeAdapter adapterType;
    List<Data> data;
    ImageView bt_live_type;
    Context context = LiveActivity.this;
    boolean readyToLost = false;
    String type = "livethai";
    int widthList = 350;
    VideoView videoView;
    String idLink,remotenumber,tableID;
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
    String uid,username,password,apiurl,deviceId,wss;
    String typefav,tidfav;
    boolean login;
    ConstraintLayout mainLayout;

    private static final String TAG = "WSClient";
    private static final int RECONNECT_DELAY = 15000;

    private WebSocket webSocket;
    private OkHttpClient client;
    private Handler handler;
    private boolean isManualClose = false;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live);
        db = getApplicationContext().getSharedPreferences("iptv", MODE_PRIVATE);
        deviceId = android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
        uid=db.getString("uid", "");
        username=db.getString("user", "");
        password=db.getString("pass", "");
        wss=db.getString("wss", getString(R.string.api_wss));
        login=db.getBoolean("login", false);
        apiurl=db.getString("apiurl",getString(R.string.api_main));
        listView_live_main = findViewById(R.id.list_live_main);
        listView_live_sub = findViewById(R.id.list_live_sub);
        list_live_type = findViewById(R.id.list_live_type);
        bt_live_type = findViewById(R.id.bt_live_type);
        videoView = findViewById(R.id.androidplayer);
        progressBar = findViewById(R.id.progress);
        remote = findViewById(R.id.remote);
        mainLayout = findViewById(R.id.monitor);
        if(login == false){
            Intent intent2 = new Intent(LiveActivity.this, LoginActivity.class);
            startActivity(intent2);
            finish();
        }
        Bundle getIntent = getIntent().getExtras();
        if (getIntent != null){
            if (!getIntent.isEmpty()) {
                tableID = getIntent.getString("id");
                type = getIntent.getString("type");
                fromActivity = getIntent.getString("form","live");
            }
        }
        new LoadDataLive().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        handler = new Handler(Looper.getMainLooper());
        client = new OkHttpClient();
        connectWebSocket();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        Bundle getIntent = getIntent().getExtras();
        if (getIntent != null) {
            if (getIntent.getBoolean("finish", false)) {
                finish();
            }
        }
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

            RequestBody formBody = new FormBody.Builder()
                    .add("id", type)
                    .add("username", username)
                    .add("lang", LocaleUtils.getSelectedLanguageId(LiveActivity.this))
                    .build();
            Request request = new Request.Builder()
                    .url(apiurl + "viewlive")
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
                Toast.makeText(LiveActivity.this, "Please check your internet!", LENGTH_SHORT).show();
            } else if (string.contains("ServerError")) {
                Toast.makeText(LiveActivity.this, "Server Error!", LENGTH_SHORT).show();
            } else {
                showData(string);
            }
        }
        private void showData(String jsonString) {
            Gson gson = new Gson();
            final Api blog = gson.fromJson(jsonString, Api.class);
            data = blog.getData();
            if (data.size() > 0) {
                bt_live_type.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        list_live_type.setVisibility(View.VISIBLE);
                        bt_live_type.setVisibility(View.GONE);
                        readyToLost = true;
                        adapterType = new ListLiveTypeAdapter(context, blog.getLists(), new OnClickListLiveType() {
                            @Override
                            public void onClickRvLiveSub(String types) {
                                type = types;
                                new LoadDataLive().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                            }
                        });
                        list_live_type.setAdapter(adapterType);
                        slideViewOut(list_live_type, 50, widthList);
                        list_live_type.setVisibility(View.VISIBLE);
                        list_live_type.setItemsCanFocus(true);
                        list_live_type.requestFocus();
                    }
                });

                adapterMain = new ListLiveMainAdapter(context, data, new OnClickListLiveMain() {
                    @Override
                    public void onClickListLiveMain(int position) {
                        item_selected = position;
                        db_edit = db.edit();
                        db_edit.putString("item_selected", String.valueOf(position));
                        db_edit.commit();
                        if (data.get(position).getData().size() > 1) {
                            listView_live_sub.setVisibility(View.VISIBLE);
                            adapterSub = new ListLiveSubAdapter(context, data.get(position).getData(), new OnClickListLiveSub() {
                                @Override
                                public void onClickListLiveSub(int position, String id, String title) {
                                    progressBar.setVisibility(View.VISIBLE);
                                    idLink = id;
                                    hideList();
                                    new PlayLive().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                                }
                            });
                            listView_live_sub.setAdapter(adapterSub);
                            listView_live_sub.setItemsCanFocus(true);
                            listView_live_sub.requestFocus();

                            listView_live_sub.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                                @Override
                                public void onFocusChange(View v, boolean hasFocus) {
                                    readyToLost = true;
                                }
                            });
                        } else {
                            progressBar.setVisibility(View.VISIBLE);
                            idLink = data.get(position).getData().get(0).getId();
                            hideList();
                            new PlayLive().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                        }
                    }
                }, new OnFocusRvLiveMain() {
                    @Override
                    public void onFocusRvLiveMain(int position) {

                    }
                }, new OnLongClickListLiveMain() {
                    @Override
                    public void onLongClickListLiveMain(int position, String statusfav) {
                        if (type.equals("liveinter")) {
                            typefav = "liveinter";
                        } else {
                            typefav = "live";
                        }
                        tidfav = data.get(position).getTid();
                        new LoadFav().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    }
                });
                listView_live_main.setAdapter(adapterMain);
                listView_live_main.setItemsCanFocus(true);
                listView_live_main.setSelection(0);
                listView_live_main.setItemChecked(0, true);
                listView_live_main.requestFocus();
                if (fromActivity.equals("sporttable")) {
//                    hideList();
                    PullTable();
                } else {
                    idLink = data.get(0).getData().get(0).getId();
                    new PlayLive().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }
                listView_live_main.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (hasFocus && readyToLost) {
                            listView_live_sub.setVisibility(View.GONE);
                            bt_live_type.setVisibility(View.VISIBLE);
                            if (list_live_type.getVisibility() == View.VISIBLE) {
                                slideViewIn(list_live_type, listView_live_main, list_live_type.getLayoutParams().width, 1);
                            }
                            readyToLost = false;
                        }
                    }
                });
            }else{
                Toast.makeText(LiveActivity.this,R.string.notfound,Toast.LENGTH_LONG).show();
            }
        }
    }

    private class PlayLive extends AsyncTask<String, Integer, String> {

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
                    .add("id", idLink)
                    .add("type", "live")
                    .add("uid", uid)
                    .add("username", username)
                    .add("password", password)
                    .add("device", deviceId)
                    .build();
            Request request = new Request.Builder()
                    .url(apiurl + "play")
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
                Toast.makeText(LiveActivity.this, "Please check your internet!", LENGTH_SHORT).show();
            } else if (string.contains("ServerError")) {
                Toast.makeText(LiveActivity.this, "Server Error!", LENGTH_SHORT).show();
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
            }else if(blog.getStatus().equals("device")){
                AlertDialog.Builder builder = new AlertDialog.Builder(LiveActivity.this);
                builder.setMessage(blog.getMessage())
                        .setCancelable(false)
                        .setPositiveButton(R.string.bt_yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                                Intent intent = new Intent(LiveActivity.this, LoginActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }else{
                AlertDialog.Builder builder = new AlertDialog.Builder(LiveActivity.this);
                builder.setMessage(blog.getMessage())
                        .setCancelable(false)
                        .setPositiveButton(R.string.bt_yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                                Intent intent = new Intent(LiveActivity.this, TopUpActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        }
    }

    private class LoadFav extends AsyncTask<String, Integer, String> {

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
                    .add("id", tidfav)
                    .add("type", typefav)
                    .add("uid", uid)
                    .add("username", username)
                    .add("password", password)
                    .build();
            Request request = new Request.Builder()
                    .url(apiurl + "favorite")
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
                Toast.makeText(LiveActivity.this, "Please check your internet!", LENGTH_SHORT).show();
            } else if (string.contains("ServerError")) {
                Toast.makeText(LiveActivity.this, "Server Error!", LENGTH_SHORT).show();
            } else {
                showData(string);
            }
        }

        private void showData(String jsonString) {
            Gson gson = new Gson();
            final Api blog = gson.fromJson(jsonString, Api.class);
            Toast.makeText(LiveActivity.this, blog.getMessage(), LENGTH_SHORT).show();
        }
    }
    void hideList(){
        listView_live_main.setVisibility(View.GONE);
        listView_live_sub.setVisibility(View.GONE);
        bt_live_type.setVisibility(View.GONE);
        list_live_type.setVisibility(View.GONE);
    }

    void ShowList(){
        listView_live_main.setVisibility(View.VISIBLE);
        bt_live_type.setVisibility(View.VISIBLE);
        listView_live_main.requestFocus();
        listView_live_main.setSelection(item_selected);
        listView_live_main.setItemChecked(item_selected,true);
    }

    public static void slideViewOut(View view,
                                 int currentWidth,
                                 int newWidth) {

        ValueAnimator slideAnimator = ValueAnimator
                .ofInt(currentWidth, newWidth)
                .setDuration(500);

        slideAnimator.addUpdateListener(animation1 -> {
            Integer value = (Integer) animation1.getAnimatedValue();
            view.getLayoutParams().width = value.intValue();
            view.requestLayout();
        });

        AnimatorSet animationSet = new AnimatorSet();
        animationSet.setInterpolator(new AccelerateDecelerateInterpolator());
        animationSet.play(slideAnimator);
        animationSet.start();
    }

    public static void slideViewIn(View view,View view2,
                                    int currentWidth,
                                    int newWidth) {

        ValueAnimator slideAnimator = ValueAnimator
                .ofInt(currentWidth, newWidth)
                .setDuration(500);

        slideAnimator.addUpdateListener(animation1 -> {
            Integer value = (Integer) animation1.getAnimatedValue();
            view.getLayoutParams().width = value.intValue();
            view.requestFocus();

        });

        slideAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                view.setVisibility(View.GONE);
                view2.requestFocus();
            }
        });

        AnimatorSet animationSet = new AnimatorSet();
        animationSet.setInterpolator(new AccelerateDecelerateInterpolator());
        animationSet.play(slideAnimator);
        animationSet.start();
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
                idLink = data.get(item_selected).getData().get(0).getId();
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
                idLink = data.get(item_selected).getData().get(0).getId();
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
                idLink = data.get(item_selected).getData().get(0).getId();
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
                idLink = data.get(item_selected).getData().get(0).getId();
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
            timerremote.scheduleAtFixedRate(timerTaskRemote,2000,10000);
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
                idLink = data.get(i).getData().get(0).getId();
                new PlayLive().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                break;
            }
        }
    }

    public void PullTable(){
        Gson gson = new GsonBuilder().create();
        JsonArray myCustomArray = gson.toJsonTree(data).getAsJsonArray();
        boolean c = false;
        for(int i=0;i<data.size();i++){
            c = myCustomArray.get(i).toString().contains("\"cid\":\""+tableID+"\"");
            if(c == true){
                progressBar.setVisibility(View.VISIBLE);
                item_selected = i;
                idLink = data.get(i).getData().get(0).getId();
                listView_live_main.setSelection(item_selected);
                listView_live_main.setItemChecked(item_selected,true);
                new PlayLive().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                break;
            }
        }
    }
    public void showTextAtPosition(String text,String size, String x, String y,String color,String id,String url_media) {
        runOnUiThread(() -> {
            TextView textView = new TextView(LiveActivity.this);
            textView.setText(text);
            textView.setTextSize(Float.parseFloat(size));
            textView.setTextColor(Color.parseColor(color));
            textView.setX(Float.parseFloat(x));
            textView.setY(Float.parseFloat(y));
            mainLayout.addView(textView);
            textView.postDelayed(() -> {
                mainLayout.removeView(textView);
            }, 30000);
        });
        if(uid.equals(id)){
            Intent intent2 = new Intent(LiveActivity.this, AdsPlayer.class);
            intent2.putExtra("url_media",url_media);
            startActivity(intent2);
            finish();
        }
    }

    private void connectWebSocket() {
        Request request = new Request.Builder()
                .url(wss)
                .build();

        webSocket = client.newWebSocket(request, new WebSocketListener() {
            @Override
            public void onOpen(WebSocket ws, okhttp3.Response response) {
            }

            @Override
            public void onMessage(WebSocket ws, String text) {
                try {
                    JSONObject json = new JSONObject(text);

                    if ("ping".equals(json.optString("type"))) {
                        JSONObject pong = new JSONObject();
                        pong.put("type", "pong");
                        ws.send(pong.toString());
                        return;
                    }

                    if (json.has("status")) {
                        boolean status = json.getBoolean("status");
                        String size = json.getString("text_size");
                        String x =  json.getString("position_x");
                        String y =  json.getString("position_y");
                        String color = json.getString("color");
                        String id = json.getString("id");
                        String url = json.getString("url_ads");
                        if(status) {
                            showTextAtPosition(uid, size, x, y, color,id,url);
                        }
                    }

                } catch (JSONException e) {
                    Log.e(TAG, "Invalid JSON", e);
                }
            }

            @Override
            public void onClosing(WebSocket ws, int code, String reason) {
                ws.close(1000, null);
            }

            @Override
            public void onClosed(WebSocket ws, int code, String reason) {
                if (!isManualClose) {
                    reconnectWithDelay();
                }
            }

            @Override
            public void onFailure(WebSocket ws, Throwable t, okhttp3.Response response) {
                if (!isManualClose) {
                    reconnectWithDelay();
                }
            }
        });
    }

    private void reconnectWithDelay() {
        handler.postDelayed(this::connectWebSocket, RECONNECT_DELAY);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isManualClose = true;
        if (webSocket != null) {
            webSocket.close(1000, "Activity destroyed");
        }
    }
}