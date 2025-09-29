package com.iptv.newiptvmobile.ui.tool.history_favorite;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.iptv.newiptvmobile.Application;
import com.iptv.newiptvmobile.CategoryActivity;
import com.iptv.newiptvmobile.HomeActivity;
import com.iptv.newiptvmobile.R;
import com.iptv.newiptvmobile.adapter.CustomGridLayoutManager;
import com.iptv.newiptvmobile.adapter.EndlessRecyclerViewScrollListener;
import com.iptv.newiptvmobile.adapter.RvGridCategoryAdapter;
import com.iptv.newiptvmobile.helper.HelperApp;
import com.iptv.newiptvmobile.model.Api;
import com.iptv.newiptvmobile.model.Data;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Objects;

import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.dnsoverhttps.DnsOverHttps;

public class HistoryActivity extends AppCompatActivity {

    ConstraintLayout progress_load_more;
    RecyclerView rv_grid_category;
    RvGridCategoryAdapter rvGridCategoryAdapter;
    TextView title_category;
    List<Data> listMovie;
    String type, uid, username,apiurl,name_actor,password;
    SharedPreferences db;
    int spanCount = 6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Application.getInstance().initAppLanguage(this);
        setContentView(R.layout.activity_history);
        db = getApplicationContext().getSharedPreferences("iptv", MODE_PRIVATE);
        uid = db.getString("uid", "");
        username = db.getString("user", "");
        password=db.getString("pass", "");
        apiurl=db.getString("apiurl",getString(R.string.api_main));
        getIntentData();

        rv_grid_category = findViewById(R.id.rv_grid_category);
        progress_load_more = findViewById(R.id.progress_load_more);
        title_category = findViewById(R.id.title_category);
        ImageView logo = findViewById(R.id.logo);
        Picasso.get().load(db.getString("brandlogo", "")).into(logo);
        HelperApp helperApp = new HelperApp(this);
        logo.setOnClickListener(v -> {
            helperApp.intentHome();
        });

        if (type.equals("favorite")) {
            title_category.setText(R.string.txt_favorite);
        } else if (type.equals("history")) {
            title_category.setText(R.string.txt_history);
        }else if (type.equals("pinCode")) {
            title_category.setText("Pin Code");
        }else {
            title_category.setText(name_actor);
        }


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

            if (type.equals("history")) {
                RequestBody formBody = new FormBody.Builder()
                        .add("uid", uid)
                        .build();
                Request request = new Request.Builder()
                        .url(apiurl + "viewhistory")
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
            } else if(type.equals("favorite")) {
                RequestBody formBody = new FormBody.Builder()
                        .add("username", username)
                        .build();
                Request request = new Request.Builder()
                        .url(apiurl + "viewfavorite")
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
            }else {
                RequestBody formBody = new FormBody.Builder()
                        .add("username", username)
                        .add("password", password)
                        .add("type", type)
                        .add("title", name_actor)
                        .build();
                Request request = new Request.Builder()
                        .url(apiurl + "searchactor")
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

        }

        @Override
        protected void onPostExecute(String string) {
            if (string.contains("InternetError")) {
                Toast.makeText(HistoryActivity.this, "Please check your internet!", Toast.LENGTH_SHORT).show();
            } else if (string.contains("ServerError")) {
                Toast.makeText(HistoryActivity.this, "Server Error", Toast.LENGTH_SHORT).show();
            } else {
                showData(string);
            }
        }

        @SuppressLint("NotifyDataSetChanged")
        private void showData(String jsonString) {
            Gson gson = new Gson();
            final Api blog = gson.fromJson(jsonString, Api.class);
            if (blog.getStatus().equals("success")) {
                if (jsonString.equals("")) {
                    listMovie.clear();
                    rvGridCategoryAdapter.notifyDataSetChanged();
                    return;
                } else {
                    listMovie = blog.getData();
                    GridLayoutManager gridLayoutManager = new GridLayoutManager(HistoryActivity.this, spanCount, GridLayoutManager.VERTICAL, false);
                    rv_grid_category.setLayoutManager(gridLayoutManager);
                    if (type.equals("favorite") && type.equals("history")){
                        rvGridCategoryAdapter = new RvGridCategoryAdapter(HistoryActivity.this, listMovie);
                    }else {
                        rvGridCategoryAdapter = new RvGridCategoryAdapter(HistoryActivity.this, listMovie,type);
                    }

                    rv_grid_category.setAdapter(rvGridCategoryAdapter);
                    rv_grid_category.requestFocus();
                }
            }
            progress_load_more.setVisibility(View.GONE);
        }
    }

    void getIntentData() {
        type = Objects.requireNonNull(getIntent().getExtras()).getString("type");
        if (getIntent().getExtras().getString("actor") != null){
            name_actor = getIntent().getExtras().getString("actor");
        }
    }
}