package com.iptv.newiptvmobile.ui.tool.topup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.iptv.newiptvmobile.Application;
import com.iptv.newiptvmobile.R;
import com.iptv.newiptvmobile.adapter.RvTopUpAdapter;
import com.iptv.newiptvmobile.helper.HelperApp;
import com.iptv.newiptvmobile.model.Api;
import com.iptv.newiptvmobile.model.TopUpModel;
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

public class TopUpActivity extends AppCompatActivity {

    RecyclerView rv_top_up;
    RvTopUpAdapter adapter;
    LinearLayout layout_progress,bt_top_up_history;
    TextView text_top_up_history;
    List<TopUpModel> list;
    String username,apiurl;
    int type;
    SharedPreferences db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Application.getInstance().initAppLanguage(this);
        db = getApplicationContext().getSharedPreferences("iptv", MODE_PRIVATE);
        setContentView(R.layout.activity_top_up);
        username=db.getString("user", "");
        apiurl=db.getString("apiurl",getString(R.string.api_main));
        rv_top_up = findViewById(R.id.rv_top_up);
        bt_top_up_history = findViewById(R.id.bt_top_up_history);
        text_top_up_history = findViewById(R.id.text_top_up_history);
        layout_progress = findViewById(R.id.layout_progress);
        ImageView logo = findViewById(R.id.logo);
        Picasso.get().load(db.getString("brandlogo", "")).into(logo);
        HelperApp helperApp = new HelperApp(this);
        logo.setOnClickListener(v -> helperApp.intentHome());

        list = new ArrayList<>();
        CreateList();
        GridLayoutManager gridLayoutManager = new GridLayoutManager(TopUpActivity.this,3,LinearLayoutManager.VERTICAL,false);
        rv_top_up.setLayoutManager(gridLayoutManager);
        adapter = new RvTopUpAdapter(TopUpActivity.this, list, id -> {
            type = Integer.parseInt(id);
            layout_progress.setVisibility(View.VISIBLE);
            new GetCuPong().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        });
        rv_top_up.setAdapter(adapter);

        bt_top_up_history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                Intent intent = new Intent(TopUpActivity.this, PaymentActivity.class);
                bundle.putString("type", "paymenthistory");
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        bt_top_up_history.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus){
                    text_top_up_history.setTextColor(getResources().getColor(R.color.black));
                }else {
                    text_top_up_history.setTextColor(getResources().getColor(R.color.white));
                }

            }
        });
    }

    @SuppressLint("StaticFieldLeak")
    private class GetCuPong extends AsyncTask<String, Integer, String> {

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
                    .add("type", String.valueOf(type))
                    .build();
            Request request = new Request.Builder()
                    .url(apiurl + "viewcupong")
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
                Toast.makeText(TopUpActivity.this, "Please check your internet!", Toast.LENGTH_SHORT).show();
            } else if (string.contains("ServerError")) {
                Toast.makeText(TopUpActivity.this, "Server Error!", Toast.LENGTH_SHORT).show();
            } else {
                showData(string);
            }
        }

        private void showData(String jsonString) {
            Gson gson = new Gson();
            final Api blog = gson.fromJson(jsonString, Api.class);

            if (blog.getStatus().equals("success")){
                Bundle bundle = new Bundle();
                bundle.putString("json",jsonString);
                Intent intent = new Intent(TopUpActivity.this,TopUpQRCodeActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
                layout_progress.setVisibility(View.GONE);
            }else{
                Toast.makeText(TopUpActivity.this, blog.getStatus(), Toast.LENGTH_SHORT).show();
            }

        }
    }

    void CreateList(){
            list.add(new TopUpModel("16",getString(R.string.txt_day,"2")));
            list.add(new TopUpModel("15",getString(R.string.txt_day,"14")));
            list.add(new TopUpModel("1",getString(R.string.txt_day,"30")));
            list.add(new TopUpModel("3",getString(R.string.txt_day,"90")));
            list.add(new TopUpModel("6",getString(R.string.txt_day,"180")));
            list.add(new TopUpModel("12",getString(R.string.txt_day,"365")));
    }
}