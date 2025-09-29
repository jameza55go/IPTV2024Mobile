package com.iptv.newiptvmobile.ui.tool.search;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.iptv.newiptvmobile.Application;
import com.iptv.newiptvmobile.CategoryActivity;
import com.iptv.newiptvmobile.LocaleUtils;
import com.iptv.newiptvmobile.R;
import com.iptv.newiptvmobile.adapter.CategoryDialog;
import com.iptv.newiptvmobile.adapter.CustomGridLayoutManager;
import com.iptv.newiptvmobile.adapter.EndlessRecyclerViewScrollListener;
import com.iptv.newiptvmobile.adapter.RvGridCategoryAdapter;
import com.iptv.newiptvmobile.helper.HelperApp;
import com.iptv.newiptvmobile.model.Api;
import com.iptv.newiptvmobile.model.Data;
import com.iptv.newiptvmobile.model.SubData;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.dnsoverhttps.DnsOverHttps;

public class SearchActivity extends AppCompatActivity {

    String json, position_category, type_category,uid,apiurl,username,password;
    RecyclerView rv_list_category, rv_grid_category;
    RvGridCategoryAdapter rvGridCategoryAdapter;
    TextView title_category;
    ConstraintLayout progress_load_more;
    LinearLayout search_layout;
    EditText editText;

    List<Data> listMovie;
    String id_category = "all";
    String subid_category = "";
    int page_category;
    int spanCount = 6;
    String textSearch = "";

    private EndlessRecyclerViewScrollListener scrollListener;
    int totalItemsCountListener = 0;
    SharedPreferences db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        db = getApplicationContext().getSharedPreferences("iptv", MODE_PRIVATE);
        uid=db.getString("uid", "");
        username=db.getString("user", "");
        password=db.getString("pass", "");
        apiurl=db.getString("apiurl",getString(R.string.api_main));
        rv_list_category = findViewById(R.id.rv_list_category);
        rv_grid_category = findViewById(R.id.rv_grid_category);
        title_category = findViewById(R.id.title_category);
        progress_load_more = findViewById(R.id.progress_load_more);
        search_layout = findViewById(R.id.search_layout);
        editText = findViewById(R.id.text_search);
        ImageView logo = findViewById(R.id.logo);
        Picasso.get().load(db.getString("brandlogo", "")).into(logo);
        HelperApp helperApp = new HelperApp(SearchActivity.this);
        logo.setOnClickListener(v -> {
            helperApp.intentHome();
        });
        editText.requestFocus();
        editText.setText("");
        search_layout.setVisibility(View.VISIBLE);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                textSearch = String.valueOf(s);
                if (s.length() >= 3) {
                    page_category = 0;
                    progress_load_more.setVisibility(View.VISIBLE);
                    new LoadDataCategory().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }
            }
        });

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
                        .add("username", username)
                        .add("password", password)
                        .add("title", textSearch)
                        .add("page", String.valueOf(page_category))
                        .add("lang", LocaleUtils.getSelectedLanguageId(SearchActivity.this))
                        .build();
                Request request = new Request.Builder()
                        .url(apiurl + "searchall")
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
                Toast.makeText(SearchActivity.this, getString(R.string.alert_httpresponse), Toast.LENGTH_SHORT).show();
            } else if (string.contains("ServerError")) {
                Toast.makeText(SearchActivity.this, getString(R.string.alert_server_error), Toast.LENGTH_SHORT).show();
            } else {
                showData(string);
            }
        }

        @SuppressLint("NotifyDataSetChanged")
        private void showData(String jsonString) {
            Gson gson = new Gson();
            final Api blog = gson.fromJson(jsonString, Api.class);
            if (jsonString.equals("")) {
                listMovie.clear();
                rvGridCategoryAdapter.notifyDataSetChanged();
                return;
            } else {
                listMovie = blog.getData();
                CustomGridLayoutManager gridLayoutManager = new CustomGridLayoutManager(SearchActivity.this, spanCount, GridLayoutManager.VERTICAL, false);
                rv_grid_category.setLayoutManager(gridLayoutManager);
                rvGridCategoryAdapter = new RvGridCategoryAdapter(SearchActivity.this, listMovie, type_category);
                rv_grid_category.setAdapter(rvGridCategoryAdapter);
//                rv_grid_category.requestFocus();
//                rv_grid_category.setFocusable(false);

                scrollListener = new EndlessRecyclerViewScrollListener(gridLayoutManager) {
                    @Override
                    public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                        progress_load_more.setVisibility(View.VISIBLE);
                        page_category++;
                        totalItemsCountListener = totalItemsCount;
                        new LoadMoreDataCategory().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    }

                };
                rv_grid_category.addOnScrollListener(scrollListener);
            }
            progress_load_more.setVisibility(View.GONE);
        }
    }

    //
    @SuppressLint("StaticFieldLeak")
    private class LoadMoreDataCategory extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {
            OkHttpClient okHttpClient2 = new OkHttpClient();

                RequestBody formBody = new FormBody.Builder()
                        .add("title", editText.getText().toString())
                        .add("page", String.valueOf(page_category))
                        .add("lang", LocaleUtils.getSelectedLanguageId(SearchActivity.this))
                        .build();
                Request request = new Request.Builder()
                        .url(apiurl + "searchall")
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
                Toast.makeText(SearchActivity.this, getString(R.string.alert_httpresponse), Toast.LENGTH_SHORT).show();
            } else if (string.contains("ServerError")) {
                Toast.makeText(SearchActivity.this, getString(R.string.alert_server_error), Toast.LENGTH_SHORT).show();
            } else {
                showData(string);
            }
        }

        @SuppressLint("NotifyDataSetChanged")
        private void showData(String jsonString) {
            Gson gson = new Gson();
            final Api blog = gson.fromJson(jsonString, Api.class);
            List<Data> data = blog.getData();
            listMovie.addAll(data);

            rv_grid_category.post(new Runnable() {
                @Override
                public void run() {
                    rvGridCategoryAdapter.notifyItemRangeInserted(totalItemsCountListener, data.size() - 1);
                }
            });
            progress_load_more.setVisibility(View.GONE);
        }
    }

    private void onPositiveButtonClicked() {
        new LoadDataCategory().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

}