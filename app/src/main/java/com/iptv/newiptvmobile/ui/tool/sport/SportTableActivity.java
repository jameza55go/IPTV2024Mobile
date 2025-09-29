package com.iptv.newiptvmobile.ui.tool.sport;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.iptv.newiptvmobile.Application;
import com.iptv.newiptvmobile.R;
import com.iptv.newiptvmobile.adapter.RvMenuSportTableAdapter;
import com.iptv.newiptvmobile.adapter.RvSportTableAdapter;
import com.iptv.newiptvmobile.helper.HelperApp;
import com.iptv.newiptvmobile.listener.OnClickMenuSportTable;
import com.iptv.newiptvmobile.model.Api;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.dnsoverhttps.DnsOverHttps;

public class SportTableActivity extends AppCompatActivity {

    RecyclerView rv_sport_table;
    RecyclerView rv_menu_sport_table;
    LinearLayout layout_manu, bt_open_manu;
    TextView text_sport;
    RvSportTableAdapter adapter;
    RvMenuSportTableAdapter manuAdapter;
    LinearLayoutManager linearLayoutManager;
    SharedPreferences db;
    String apiurl;
    String dbusername, dbpassword;
    String sport_type = "all";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Application.getInstance().initAppLanguage(this);
        setContentView(R.layout.activity_sport_table);
        db = getApplicationContext().getSharedPreferences("iptv", MODE_PRIVATE);
        apiurl = db.getString("apiurl", getString(R.string.api_main));
        dbusername = db.getString("user", "");
        dbpassword = db.getString("pass", "");
        rv_sport_table = findViewById(R.id.rv_sport_table);
        rv_menu_sport_table = findViewById(R.id.rv_menu_sport_table);
        layout_manu = findViewById(R.id.layout_manu);
        bt_open_manu = findViewById(R.id.bt_open_manu);
        text_sport = findViewById(R.id.textView10);
        ImageView logo = findViewById(R.id.logo);
        Picasso.get().load(db.getString("brandlogo", "")).into(logo);
        HelperApp helperApp = new HelperApp(this);
        logo.setOnClickListener(v -> {
            helperApp.intentHome();
        });
        new LoadDataCategory().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @SuppressLint("StaticFieldLeak")
    private class LoadDataCategory extends AsyncTask<String, Integer, String> {

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
                    .add("username", dbusername)
                    .add("password", dbpassword)
                    .add("id", sport_type)
                    .build();
            Request request = new Request.Builder()
                    .url(apiurl + "fixtures")
                    .post(formBody)
                    .build();
            try {
                Response response = okHttpClient2.newCall(request).execute();
                if (response.isSuccessful()) {
                    assert response.body() != null;
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
                Toast.makeText(SportTableActivity.this, "Please check your internet!", Toast.LENGTH_SHORT).show();
            } else if (string.contains("ServerError")) {
                Toast.makeText(SportTableActivity.this, "Server Error!", Toast.LENGTH_SHORT).show();
            } else {
                showData(string);
            }
        }

        @SuppressLint("NotifyDataSetChanged")
        private void showData(String jsonString) {
            Gson gson = new Gson();
            final Api blog = gson.fromJson(jsonString, Api.class);
            linearLayoutManager = new LinearLayoutManager(SportTableActivity.this, LinearLayoutManager.VERTICAL, false);
            rv_sport_table.setLayoutManager(linearLayoutManager);
            adapter = new RvSportTableAdapter(SportTableActivity.this, blog.getData());
            rv_sport_table.setAdapter(adapter);
            adapter.notifyDataSetChanged();

            rv_menu_sport_table.setLayoutManager(new LinearLayoutManager(SportTableActivity.this, LinearLayoutManager.VERTICAL, false));
            manuAdapter = new RvMenuSportTableAdapter(SportTableActivity.this, blog.getLists(), new OnClickMenuSportTable() {
                @Override
                public void OnClick(String id, String title) {
//                    if (sport_type.equals("0")) {
//                        sport_type = "all";
//                    } else {
//                        sport_type = id;
//                    }
                    sport_type = id;
                    if (title.equals("All")) {
                        text_sport.setText(R.string.txt_sport);
                    } else {
                        text_sport.setText(title);
                    }
                    layout_manu.setVisibility(View.GONE);
                    new LoadDataCategory().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }
            });
            rv_menu_sport_table.setAdapter(manuAdapter);
            bt_open_manu.setOnClickListener(v -> {
                if (layout_manu.getVisibility() == View.GONE) {
                    layout_manu.setVisibility(View.VISIBLE);
                    rv_menu_sport_table.requestFocus();
                } else {
                    layout_manu.setVisibility(View.GONE);
                }

            });

            bt_open_manu.setOnFocusChangeListener((v, hasFocus) -> {
                if (hasFocus) {
                    bt_open_manu.setBackgroundResource(R.drawable.bg_white_coner_20);
                } else {
                    bt_open_manu.setBackgroundResource(R.drawable.bg_orange_corner_20);
                }
            });


        }
    }

    public void RvScrollToPosition(int position) {
        RecyclerView.SmoothScroller smoothScroller = new LinearSmoothScroller(SportTableActivity.this) {
            @Override
            protected int getVerticalSnapPreference() {
                return LinearSmoothScroller.SNAP_TO_START;
            }

            @Override
            protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
                return 100f / displayMetrics.densityDpi;
            }
        };
        smoothScroller.setTargetPosition(position);
        linearLayoutManager.startSmoothScroll(smoothScroller);
    }

    @Override
    public void onBackPressed() {
        if (layout_manu.getVisibility() == View.VISIBLE) {
            layout_manu.setVisibility(View.GONE);
        } else {
            super.onBackPressed();
        }
    }
}