package com.iptv.newiptvmobile.ui.tool.topup;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.iptv.newiptvmobile.Application;
import com.iptv.newiptvmobile.PlayerExoPlayerActivity;
import com.iptv.newiptvmobile.R;
import com.iptv.newiptvmobile.adapter.AlertDialogMain;
import com.iptv.newiptvmobile.adapter.AlertDialogPaymentSuccess;
import com.iptv.newiptvmobile.adapter.RvPaymentAdapter;
import com.iptv.newiptvmobile.helper.HelperApp;
import com.iptv.newiptvmobile.model.Api;
import com.iptv.newiptvmobile.model.Data;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;
import java.util.Objects;

import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.dnsoverhttps.DnsOverHttps;

public class TopUpQRCodeActivity extends AppCompatActivity {

    TextView text_price,text_number,text_count_down,text_top_up_history;
    ImageView img_qr_code;
    LinearLayout bt_cancel,bt_accept,bt_top_up_history;
    String username,password,apiurl;
    private CountDownTimer countDownTimer;
    SharedPreferences db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Application.getInstance().initAppLanguage(this);
        setContentView(R.layout.activity_top_up_qrcode);
        db = getApplicationContext().getSharedPreferences("iptv", MODE_PRIVATE);
        username = db.getString("user", "");
        password = db.getString("pass", "");
        apiurl=db.getString("apiurl",getString(R.string.api_main));
        text_price = findViewById(R.id.text_price);
        text_number = findViewById(R.id.text_number);
        text_count_down = findViewById(R.id.text_count_down);
        img_qr_code = findViewById(R.id.img_qr_code);
        bt_cancel = findViewById(R.id.bt_cancel);
        bt_accept = findViewById(R.id.bt_accept);
        bt_top_up_history = findViewById(R.id.bt_top_up_history);
        text_top_up_history = findViewById(R.id.text_top_up_history);
        ImageView logo = findViewById(R.id.logo);
        Picasso.get().load(db.getString("brandlogo", "")).into(logo);
        HelperApp helperApp = new HelperApp(this);
        logo.setOnClickListener(v -> {
            helperApp.intentHome();
        });

        if (!getIntent().getExtras().isEmpty()){
            Gson gson = new Gson();
            final Api blog = gson.fromJson(getIntent().getExtras().getString("json"), Api.class);

            text_price.setText(blog.getPrice());
            text_number.setText(blog.getAccount());
            Glide.with(this)
                    .asBitmap()
                    .load(Base64.decode(blog.getImage().substring(blog.getImage().indexOf(",") + 1), Base64.DEFAULT))
                    .into(img_qr_code);
            countDownTimer = new CountDownTimer(blog.getExtime()* 1000L, 1000) {
                @SuppressLint("SetTextI18n")
                public void onTick(long millisUntilFinished) {
                    // Used for formatting digit to be in 2 digits only
                    NumberFormat f = new DecimalFormat("00");
                    long min = (millisUntilFinished / 60000) % 60;
                    long sec = (millisUntilFinished / 1000) % 60;
                    text_count_down.setText(f.format(min) + ":" + f.format(sec));
                }
                // When the task is over it will print 00:00:00 there
                @SuppressLint("SetTextI18n")
                public void onFinish() {
                    text_count_down.setText("00:00");
                }
            }.start();

            bt_accept.setOnClickListener(v -> {
                new CheckPayment().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            });

            bt_cancel.setOnClickListener(v -> finish());

        }

        bt_top_up_history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TopUpQRCodeActivity.this, PaymentActivity.class);
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
    private class CheckPayment extends AsyncTask<String, Integer, String> {

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
                    .url(apiurl + "checkcupong")
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
                Toast.makeText(TopUpQRCodeActivity.this, "Please check your internet!", Toast.LENGTH_SHORT).show();
            } else if (string.contains("ServerError")) {
                Toast.makeText(TopUpQRCodeActivity.this, "Server Error!", Toast.LENGTH_SHORT).show();
            } else {
                showData(string);
            }
        }

        private void showData(String jsonString) {
            Gson gson = new Gson();
            final Api blog = gson.fromJson(jsonString, Api.class);
            boolean status;
            status = blog.getStatus().equals("success");

            AlertDialogPaymentSuccess alertDialogMain = new AlertDialogPaymentSuccess(
                    TopUpQRCodeActivity.this,
                    status,
                    () -> {
                        HelperApp helperApp = new HelperApp(TopUpQRCodeActivity.this);
                        helperApp.intentHome();
                    });
            alertDialogMain.show();

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    public interface OnDialogClick {
        void onClick();
    }
}