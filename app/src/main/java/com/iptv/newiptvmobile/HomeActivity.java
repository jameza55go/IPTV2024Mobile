package com.iptv.newiptvmobile;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.transition.Fade;
import android.transition.Slide;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.transition.TransitionSet;
import android.transition.TransitionValues;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.collect.Ordering;
import com.google.gson.Gson;
import com.iptv.newiptvmobile.adapter.AdultPasswordDialog;
import com.iptv.newiptvmobile.adapter.AlertDialogKaraoke;
import com.iptv.newiptvmobile.adapter.AlertDialogMain;
import com.iptv.newiptvmobile.adapter.RvHomeCategoryAdapter;
import com.iptv.newiptvmobile.adapter.RvHomeToolAdapter;
import com.iptv.newiptvmobile.adapter.RvMovieAdviceAdapter;
import com.iptv.newiptvmobile.adapter.TopSnapHelper;
import com.iptv.newiptvmobile.helper.HelperApp;
import com.iptv.newiptvmobile.listener.OnClickNoAlertDialog;
import com.iptv.newiptvmobile.listener.OnClickYesAlertDialog;
import com.iptv.newiptvmobile.model.Api;
import com.iptv.newiptvmobile.model.Data;
import com.iptv.newiptvmobile.model.ToolModel;
import com.iptv.newiptvmobile.ui.liveTV.FwplayerActivity;
import com.iptv.newiptvmobile.ui.liveTV.LiveActivity;
import com.iptv.newiptvmobile.ui.login.DemoFragment;
import com.iptv.newiptvmobile.ui.login.LoginAndRegisterFragment;
import com.iptv.newiptvmobile.ui.tool.history_favorite.HistoryActivity;
import com.iptv.newiptvmobile.ui.tool.point.PointActivity;
import com.iptv.newiptvmobile.ui.tool.search.SearchActivity;
import com.iptv.newiptvmobile.ui.tool.setting.SettingActivity;
import com.iptv.newiptvmobile.ui.tool.sport.SportTableActivity;
import com.iptv.newiptvmobile.ui.tool.topup.PaymentActivity;
import com.iptv.newiptvmobile.ui.tool.topup.TopUpActivity;
import com.iptv.newiptvmobile.ui.tool.topup.TopUpQRCodeActivity;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
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

public class HomeActivity extends AppCompatActivity {

    LinearLayout bt_user_payment, bt_to_movie_advice, layout_main_menu, layout_movie_advice, bt_back_to_main_menu,button_promotion;
    LinearLayoutManager linearLayoutManager;
    RvMovieAdviceAdapter movieAdviceAdapter;
    ConstraintLayout layout;
    ImageView img_to_movie_advice,icon_promotion;
    RecyclerView rv_home_category, rv_movie_advice;
    RecyclerView rv_toolbar;
    RvHomeCategoryAdapter rvHomeCategoryAdapter;
    RvHomeToolAdapter rvHomeToolAdapter;
    HomeActivity context = this;
    GridLayoutManager gridLayoutManager;
    List<ToolModel> toolList;
    TextView username, vipday, text_notice,text_to_movie_advice,title_promotion;
    SharedPreferences db;
    SharedPreferences.Editor db_edit;
    String dbusername, dbvipday, dbpassword, dblang, apiurl,playerlive;
    SimpleDateFormat dateFormat;
    Timer alive;
    String deviceId;
    boolean moveLeft = true;
    boolean moveRight = true;
    ImageView logo;
    int i=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Application.getInstance().initAppLanguage(this);
        setContentView(R.layout.activity_home);
        db = getApplicationContext().getSharedPreferences("iptv", MODE_PRIVATE);
        deviceId = android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
        dateFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        db_edit = getSharedPreferences("iptv", MODE_PRIVATE).edit();
        dbusername = db.getString("user", "");
        dbpassword = db.getString("pass", "");
        dbvipday = db.getString("vipday", "");
        dblang = db.getString("lang", Locale.getDefault().getLanguage());
        apiurl = db.getString("apiurl", getString(R.string.api_main));
        rv_home_category = findViewById(R.id.rv_home_category);
        bt_user_payment = findViewById(R.id.bt_user_payment);
        username = findViewById(R.id.username);
        vipday = findViewById(R.id.vipday);
        bt_to_movie_advice = findViewById(R.id.bt_to_movie_advice);
        bt_back_to_main_menu = findViewById(R.id.bt_back_to_main_menu);
        layout_main_menu = findViewById(R.id.layout_main_menu);
        layout_movie_advice = findViewById(R.id.layout_movie_advice);
        rv_movie_advice = findViewById(R.id.rv_movie_advice);
        text_notice = findViewById(R.id.text_notice);
        layout = findViewById(R.id.layout);
        img_to_movie_advice = findViewById(R.id.img_to_movie_advice);
        text_to_movie_advice = findViewById(R.id.text_to_movie_advice);
        button_promotion = findViewById(R.id.button_promotion);
        icon_promotion = findViewById(R.id.icon_promotion);
        title_promotion = findViewById(R.id.title_promotion);
        username.setText(dbusername);
        rv_toolbar = findViewById(R.id.rv_toolbar);
        logo = findViewById(R.id.logo);
        Picasso.get().load(db.getString("brandlogo", "")).into(logo);
        HelperApp helperApp = new HelperApp(this);
        logo.setOnClickListener(v -> {
            helperApp.intentHome();
        });

        bt_user_payment.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            Intent intent = new Intent(this, PaymentActivity.class);
            bundle.putString("type", "paymenthistory");
            intent.putExtras(bundle);
            startActivity(intent);
        });

        bt_to_movie_advice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GoToMovieAdvice();
            }
        });

        bt_to_movie_advice.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus){
                    bt_to_movie_advice.setBackground(getResources().getDrawable(R.color.orange_main));
                    img_to_movie_advice.setImageResource(R.drawable.arrow_up_b);
                    text_to_movie_advice.setTextColor(ContextCompat.getColor(HomeActivity.this,R.color.black));
                }else {
                    bt_to_movie_advice.setBackground(getResources().getDrawable(R.color.transparent));
                    img_to_movie_advice.setImageResource(R.drawable.arrow_up_w);
                    text_to_movie_advice.setTextColor(ContextCompat.getColor(HomeActivity.this,R.color.white));
                }
            }
        });

        bt_back_to_main_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GoToMainMenu();
            }
        });

        bt_back_to_main_menu.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus){
                    bt_back_to_main_menu.setBackground(getResources().getDrawable(R.drawable.bg_white_corner_5));

                }else {
                    bt_back_to_main_menu.setBackground(null);

                }
            }
        });
        


        createToolList();
        new MainMenuCategory().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void GoToMainMenu() {
        TransitionSet transitionSet = new TransitionSet();
        Fade transitionView1 = new Fade();
        transitionView1.addTarget(layout_movie_advice);
        transitionView1.setDuration(1000);

        Slide transitionView2 = new Slide(Gravity.BOTTOM);
        transitionView2.addTarget(layout_main_menu);
        transitionView2.setDuration(1000);

        transitionSet.addTransition(transitionView1);
        transitionSet.addTransition(transitionView2);
        transitionSet.setOrdering(TransitionSet.ORDERING_TOGETHER);
        TransitionManager.beginDelayedTransition(layout, transitionSet);
        layout_main_menu.setVisibility(View.VISIBLE);
        layout_movie_advice.setVisibility(View.GONE);
    }

    private void GoToMovieAdvice() {
        new GetMovieAdvice().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        TransitionSet transitionSet = new TransitionSet();
        Fade transitionView1 = new Fade();
        transitionView1.addTarget(layout_main_menu);
        transitionView1.setDuration(1000);

        Slide transitionView2 = new Slide(Gravity.BOTTOM);
        transitionView2.addTarget(layout_movie_advice);
        transitionView2.setDuration(1000);

        transitionSet.addTransition(transitionView1);
        transitionSet.addTransition(transitionView2);
        transitionSet.setOrdering(TransitionSet.ORDERING_TOGETHER);
        TransitionManager.beginDelayedTransition(layout, transitionSet);
        layout_main_menu.setVisibility(View.INVISIBLE);
        layout_movie_advice.setVisibility(View.VISIBLE);
    }

    public void AliveOnline() {
        alive = new Timer();
        alive.schedule(new TimerTask() {
            @Override
            public void run() {
                new AliveCheck().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }

        }, 300000, 300000);
    }

    private class AliveCheck extends AsyncTask<String, Integer, String> {

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
                    .add("device", deviceId)
                    .build();
            Request request = new Request.Builder()
                    .url(apiurl + "alive")
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
                ;
            } else if (string.contains("ServerError")) {
                ;
            } else {
                showData(string);
            }
        }

        private void showData(String jsonString) {
            Gson gson = new Gson();
            final Api blog = gson.fromJson(jsonString, Api.class);
            if (blog.getStatus().equals("error")) {
                if (alive != null) {
                    alive.cancel();
                    alive.purge();
                }
                db_edit.putBoolean("login", false);
                db_edit.apply();
                Intent intent = new Intent(HomeActivity.this, LiveActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("finish", true);
                startActivity(intent);
                Intent intent2 = new Intent(HomeActivity.this, LoginActivity.class);
                startActivity(intent2);
                finish();
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class MainMenuCategory extends AsyncTask<String, Integer, String> {

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
                    .add("lang", dblang)
                    .build();
            Request request = new Request.Builder()
                    .url(apiurl + "category")
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
                Toast.makeText(HomeActivity.this, "Please check your internet!", Toast.LENGTH_SHORT).show();
            } else if (string.contains("ServerError")) {
                Toast.makeText(HomeActivity.this, "Server Error!", Toast.LENGTH_SHORT).show();
            } else {
                showData(string);
            }
        }

        public MainMenuCategory() {
        }

        private void showData(String jsonString) {
            Gson gson = new Gson();
            final Api blog = gson.fromJson(jsonString, Api.class);
            final List<Data> data = blog.getData();

            if (blog.isPromotion()){
                button_promotion.setVisibility(View.VISIBLE);
                button_promotion.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(HomeActivity.this, WheelActivity.class);
                        startActivity(intent);
                    }
                });
            }else{
                button_promotion.setVisibility(View.GONE);
            }
            gridLayoutManager = new GridLayoutManager(context, 4, GridLayoutManager.VERTICAL, false);
            rv_home_category.setLayoutManager(gridLayoutManager);
            rvHomeCategoryAdapter = new RvHomeCategoryAdapter(context, data, position -> {
                if (data.get(Integer.parseInt(position)).getType().equals("adult")) {
                    AdultPasswordDialog adultPasswordDialog = new AdultPasswordDialog(HomeActivity.this, () -> {
                        Bundle bundle = new Bundle();
                        bundle.putString("json", jsonString);
                        bundle.putString("position", position);
                        Intent intent = new Intent(HomeActivity.this, CategoryActivity.class);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    });
                    adultPasswordDialog.show();
                } else if (data.get(Integer.parseInt(position)).getType().equals("live")) {
                    playerlive = db.getString("LivePlayer", "AndroidPlayer");
                    if(playerlive.contains("AndroidPlayer")){
                        Bundle bundle = new Bundle();
                        bundle.putString("id", data.get(Integer.parseInt(position)).getTid());
                        bundle.putString("type", data.get(Integer.parseInt(position)).getLivecat());
                        Intent intent = new Intent(HomeActivity.this, LiveActivity.class);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }else{
                        Bundle bundle = new Bundle();
                        bundle.putString("id", data.get(Integer.parseInt(position)).getTid());
                        bundle.putString("type", data.get(Integer.parseInt(position)).getLivecat());
                        Intent intent = new Intent(HomeActivity.this, FwplayerActivity.class);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                } else if (data.get(Integer.parseInt(position)).getType().equals("karaoke")) {
                    new AlertDialogKaraoke(HomeActivity.this).show();
                } else {
                    Bundle bundle = new Bundle();
                    bundle.putString("json", jsonString);
                    bundle.putString("position", position);
                    Intent intent = new Intent(HomeActivity.this, CategoryActivity.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            });
            rv_home_category.setAdapter(rvHomeCategoryAdapter);
            rv_home_category.requestFocus();

        }
    }

    @SuppressLint("StaticFieldLeak")
    private class GetInfo extends AsyncTask<String, Integer, String> {

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
                    .build();

            Request request = new Request.Builder()
                    .url(apiurl + "memberinfo")
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
                Toast.makeText(HomeActivity.this, "Please check your internet!", Toast.LENGTH_SHORT).show();
            } else if (string.contains("ServerError")) {
                Toast.makeText(HomeActivity.this, "Server Error!", Toast.LENGTH_SHORT).show();
            } else {
                showData(string);
            }
        }

        private void showData(String jsonString) {
            Gson gson = new Gson();
            final Api blog = gson.fromJson(jsonString, Api.class);
            final Data data = blog.getData().get(0);

            if (blog.getStatus().equals("success")) {
                if (dblang.equals("en")) {
                    vipday.setText(getString(R.string.vipday_en, data.getVipday()));
                } else {
                    vipday.setText(getString(R.string.vipday, data.getVipday()));
                }
                db_edit.putString("vipday", data.getVipday());
                db_edit.putString("point", data.getPoint());
                db_edit.putBoolean("expire", data.getExpire());
                db_edit.apply();
            } else {
                Toast.makeText(HomeActivity.this, blog.getMessage(), Toast.LENGTH_SHORT).show();
            }


        }
    }

    @SuppressLint("StaticFieldLeak")
    private class LogOut extends AsyncTask<String, Integer, String> {

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
                    .build();

            Request request = new Request.Builder()
                    .url(apiurl + "logout")
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
                Toast.makeText(HomeActivity.this, "Please check your internet!", Toast.LENGTH_SHORT).show();
            } else if (string.contains("ServerError")) {
                Toast.makeText(HomeActivity.this, "Server Error!", Toast.LENGTH_SHORT).show();
            } else {
                showData(string);
            }
        }

        private void showData(String jsonString) {
            Gson gson = new Gson();
            final Api blog = gson.fromJson(jsonString, Api.class);

            if (blog.getStatus().equals("success")) {
                if (alive != null) {
                    alive.cancel();
                    alive.purge();
                }
                Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(HomeActivity.this, blog.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class GetMovieAdvice extends AsyncTask<String, Integer, String> {

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
                    .add("lang", dblang)
                    .build();

            Request request = new Request.Builder()
                    .url(apiurl + "viewindex")
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
                Toast.makeText(HomeActivity.this, "Please check your internet!", Toast.LENGTH_SHORT).show();
            } else if (string.contains("ServerError")) {
                Toast.makeText(HomeActivity.this, "Server Error!", Toast.LENGTH_SHORT).show();
            } else {
                showData(string);
            }
        }

        private void showData(String jsonString) {
            Gson gson = new Gson();
            final Api blog = gson.fromJson(jsonString, Api.class);
            final List<Data> data = blog.getData();
//            Log.d("TAG", jsonString);
            if (blog.getStatus().equals("success")) {
                text_notice.setText(blog.getNotice());

                linearLayoutManager = new LinearLayoutManager(HomeActivity.this, LinearLayoutManager.VERTICAL, false);
                rv_movie_advice.setLayoutManager(linearLayoutManager);
                movieAdviceAdapter = new RvMovieAdviceAdapter(HomeActivity.this, data);
                rv_movie_advice.setAdapter(movieAdviceAdapter);

                rv_movie_advice.setOnKeyListener(new View.OnKeyListener() {
                    @Override
                    public boolean onKey(View v, int keyCode, KeyEvent event) {
                        if (event.getAction() == KeyEvent.ACTION_DOWN) {
                            if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                                moveFocus(true);
                                return true;
                            } else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                                moveFocus(false);
                                return true;
                            }
                        }
                        return false;
                    }
                });


            } else {
                Toast.makeText(HomeActivity.this, blog.getMessage(), Toast.LENGTH_SHORT).show();
            }

        }
    }

    private void moveFocus(boolean moveForward) {
        int currentPosition = linearLayoutManager.findFirstVisibleItemPosition();
        int itemCount = movieAdviceAdapter.getItemCount();

        int nextPosition = moveForward ? currentPosition + 1 : currentPosition - 1;

        if (nextPosition >= 0 && nextPosition < itemCount) {
            View nextView = linearLayoutManager.findViewByPosition(nextPosition);
            if (nextView != null) {
                nextView.requestFocus();
            }
        }
    }

    void createToolList() {
        toolList = new ArrayList<>();
        toolList.add(new ToolModel(R.drawable.ic_topup_w, R.drawable.ic_topup_w, getString(R.string.bt_topup), "top up", dblang, getString(R.string.bt_topup_en)));
        toolList.add(new ToolModel(R.drawable.ic_gift_w, R.drawable.ic_gift_w, getString(R.string.bt_point), "point", dblang, getString(R.string.bt_point_en)));
        toolList.add(new ToolModel(R.drawable.ic_lang_w, R.drawable.ic_lang_w, getString(R.string.bt_lang), "lang", dblang, getString(R.string.bt_lang_en)));
        toolList.add(new ToolModel(R.drawable.ic_table, R.drawable.ic_table, getString(R.string.bt_table), "tv guide", dblang, getString(R.string.bt_table_en)));
        toolList.add(new ToolModel(R.drawable.ic_search_w,R.drawable.ic_search_w,getString(R.string.bt_search),"search",dblang,getString(R.string.bt_search)));
        toolList.add(new ToolModel(R.drawable.star_2, R.drawable.star_2, getString(R.string.bt_fav), "favorite", dblang, getString(R.string.bt_fav_en)));
        toolList.add(new ToolModel(R.drawable.ic_history_w, R.drawable.ic_history_w, getString(R.string.bt_history), "history", dblang, getString(R.string.bt_history_en)));
        toolList.add(new ToolModel(R.drawable.ic_setting_w, R.drawable.ic_setting_w, getString(R.string.bt_setting), "setting", dblang, getString(R.string.bt_setting_en)));
        toolList.add(new ToolModel(R.drawable.ic_logout_w, R.drawable.ic_logout_w, getString(R.string.bt_logout), "logout", dblang, getString(R.string.bt_logout_en)));

        rv_toolbar.setLayoutManager(new LinearLayoutManager(HomeActivity.this, LinearLayoutManager.HORIZONTAL, false));
        rvHomeToolAdapter = new RvHomeToolAdapter(context, toolList, this::intentTool);
        rv_toolbar.setAdapter(rvHomeToolAdapter);
    }

    void intentTool(String type) {
        Intent intent;
        Bundle bundle;
        switch (type) {
            case "top up":
                bundle = new Bundle();
                bundle.putString("username", dbusername);
                intent = new Intent(HomeActivity.this, TopUpActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
                break;
            case "point":
                intent = new Intent(HomeActivity.this, PointActivity.class);
                startActivity(intent);
                break;
            case "tv guide":
                intent = new Intent(HomeActivity.this, SportTableActivity.class);
                startActivity(intent);
                break;
            case "history":
                bundle = new Bundle();
                bundle.putString("type", "history");
                intent = new Intent(HomeActivity.this, HistoryActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
                break;
            case "search":
                bundle = new Bundle();
                bundle.putString("username", dbusername);
                intent = new Intent(HomeActivity.this, SearchActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
                break;
            case "favorite":
                bundle = new Bundle();
                bundle.putString("type", "favorite");
                intent = new Intent(HomeActivity.this, HistoryActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
                break;
            case "setting":
                bundle = new Bundle();
                bundle.putString("username", dbusername);
                intent = new Intent(HomeActivity.this, SettingActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
                break;
            case "logout":
                AlertDialogMain alertDialogMain = new AlertDialogMain(context, getString(R.string.text_logout));
                alertDialogMain.show();
                alertDialogMain.setBt_yes(R.string.bt_yes, () -> new LogOut().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR));
                alertDialogMain.setBt_no(R.string.bt_no, alertDialogMain::remove);
                break;
            case "lang":
                if (dblang.equals("th")) {
                    db_edit.putString("lang", "en");
                    db_edit.apply();
                    LocaleUtils.setSelectedLanguageId("en", context);
                } else {
                    db_edit.putString("lang", "th");
                    db_edit.apply();
                    LocaleUtils.setSelectedLanguageId("th", context);
                }
                recreate();
                break;

            default:
                Toast.makeText(context, type + " Working", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        new GetInfo().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        AliveOnline();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    public interface ConfirmAdultPassword {
        void confirmAdultPassword();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
            if (moveLeft) {
                return super.onKeyDown(keyCode, event);
            } else {
                moveRight = true;
                return true;
            }
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
            if (moveRight) {
                return super.onKeyDown(keyCode, event);
            } else {
                moveLeft = true;
                return true;
            }
        } else if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (layout_movie_advice.getVisibility() == View.VISIBLE) {
                GoToMainMenu();
            } else {
                AlertDialogMain alertDialogMain = new AlertDialogMain(context, getString(R.string.text_logout));
                alertDialogMain.show();
                alertDialogMain.setBt_yes(R.string.bt_yes, () -> new LogOut().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR));
                alertDialogMain.setBt_no(R.string.bt_no, alertDialogMain::remove);
            }
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    public void SendDataToHomeActivity(int rvPosition) {
        rv_movie_advice.smoothScrollToPosition(rvPosition);
    }

    public void focusToLastItem() {
        moveRight = false;
    }

    public void focusToFirstItem() {
        moveLeft = false;
    }

    public void enableFocus(){
        moveLeft = true;
        moveRight = true;
    }
}