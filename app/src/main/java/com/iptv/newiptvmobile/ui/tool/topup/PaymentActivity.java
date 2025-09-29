package com.iptv.newiptvmobile.ui.tool.topup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.iptv.newiptvmobile.Application;
import com.iptv.newiptvmobile.R;
import com.iptv.newiptvmobile.adapter.RvPaymentAdapter;
import com.iptv.newiptvmobile.adapter.RvPointAdapter;
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

public class PaymentActivity extends AppCompatActivity {

    RecyclerView rv_payment;
    RvPaymentAdapter adapter;
    RvPointAdapter adapter2;
    SharedPreferences db;
    String username,password,apiurl,type;
    TextView text_type,text_before,text_after,text_receive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Application.getInstance().initAppLanguage(this);
        setContentView(R.layout.activity_payment);
        db = getApplicationContext().getSharedPreferences("iptv", MODE_PRIVATE);
        username = db.getString("user", "");
        password = db.getString("pass", "");
        apiurl=db.getString("apiurl",getString(R.string.api_main));
        rv_payment = findViewById(R.id.rv_payment);
        text_type = findViewById(R.id.text_type);
        text_before = findViewById(R.id.text_before);
        text_after = findViewById(R.id.text_after);
        text_receive = findViewById(R.id.text_receive);
        ImageView logo = findViewById(R.id.logo);
        getIntentData();
        Picasso.get().load(db.getString("brandlogo", "")).into(logo);
        HelperApp helperApp = new HelperApp(this);
        logo.setOnClickListener(v -> {
            helperApp.intentHome();
        });

        new GetPayment().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @SuppressLint("StaticFieldLeak")
    private class GetPayment extends AsyncTask<String, Integer, String> {

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
                    .build();
            Request request = new Request.Builder()
                    .url(apiurl + type)
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
                Toast.makeText(PaymentActivity.this, "Please check your internet!", Toast.LENGTH_SHORT).show();
            } else if (string.contains("ServerError")) {
                Toast.makeText(PaymentActivity.this, "Server Error!", Toast.LENGTH_SHORT).show();
            } else {
                showData(string);
            }
        }

        private void showData(String jsonString) {
            Gson gson = new Gson();
            final Api blog = gson.fromJson(jsonString, Api.class);
            List<Data> data = blog.getData();

            if (blog.getStatus().equals("success")){
                if (type.equals("paymenthistory")){
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(PaymentActivity.this,RecyclerView.VERTICAL,false);
                    rv_payment.setLayoutManager(linearLayoutManager);
                    adapter = new RvPaymentAdapter(PaymentActivity.this,data);
                    rv_payment.setAdapter(adapter);
                }else {
                    text_type.setText(getResources().getString(R.string.point_history));
                    text_before.setText(getResources().getString(R.string.txt_before_point));
                    text_receive.setText(getResources().getString(R.string.txt_point_recive));
                    text_after.setText(getResources().getString(R.string.txt_point_left));
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(PaymentActivity.this,RecyclerView.VERTICAL,false);
                    rv_payment.setLayoutManager(linearLayoutManager);
                    adapter2 = new RvPointAdapter(PaymentActivity.this,data);
                    rv_payment.setAdapter(adapter2);
                }
            }else{
                Toast.makeText(PaymentActivity.this, blog.getStatus(), Toast.LENGTH_SHORT).show();
            }

        }
    }

    void getIntentData() {
        if (getIntent().getExtras().getString("type") == null){
            type = "paymenthistory";
        }else {
            type = Objects.requireNonNull(getIntent().getExtras()).getString("type");
        }

    }
}