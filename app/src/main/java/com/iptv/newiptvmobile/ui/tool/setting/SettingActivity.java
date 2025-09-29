package com.iptv.newiptvmobile.ui.tool.setting;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.app.FragmentManager;
import android.content.DialogInterface;
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
import com.iptv.newiptvmobile.HomeActivity;
import com.iptv.newiptvmobile.LoginActivity;
import com.iptv.newiptvmobile.R;
import com.iptv.newiptvmobile.adapter.ActivateLineDialog;
import com.iptv.newiptvmobile.adapter.AlertDialogMain;
import com.iptv.newiptvmobile.helper.HelperApp;
import com.iptv.newiptvmobile.model.Api;
import com.iptv.newiptvmobile.ui.tool.history_favorite.HistoryActivity;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Objects;

import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.dnsoverhttps.DnsOverHttps;

public class SettingActivity extends AppCompatActivity {

    LinearLayout layout_progress;
    TextView device,text_menu_change_password,text_menu_clear_history,text_menu_clear_fav,text_menu_activate_line,text_menu_main_setting,text_live_player_setting;
    View view_menu_change_password,view_menu_main_setting,view_live_player_setting;
    String deviceId;
    String text_activate_code;
    String dbusername,dbpassword,apiurl;
    SharedPreferences db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = getApplicationContext().getSharedPreferences("iptv", MODE_PRIVATE);
        deviceId = android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
        Application.getInstance().initAppLanguage(this);
        setContentView(R.layout.activity_setting);
        dbusername = db.getString("user", "");
        dbpassword = db.getString("pass", "");
        apiurl=db.getString("apiurl",getString(R.string.api_main));
        text_menu_change_password = findViewById(R.id.text_menu_change_password);
        text_menu_clear_history = findViewById(R.id.text_menu_clear_history);
        text_menu_clear_fav = findViewById(R.id.text_menu_clear_fav);
        text_menu_activate_line = findViewById(R.id.text_menu_activate_line);
        text_menu_main_setting = findViewById(R.id.text_menu_main_setting);
        view_menu_change_password = findViewById(R.id.view_menu_change_password);
        view_menu_main_setting = findViewById(R.id.view_menu_main_setting);
        text_live_player_setting = findViewById(R.id.text_live_player_setting);
        view_live_player_setting = findViewById(R.id.view_live_player_setting);
        device = findViewById(R.id.device);
        layout_progress = findViewById(R.id.layout_progress);
        ImageView logo = findViewById(R.id.logo);
        Picasso.get().load(db.getString("brandlogo", "")).into(logo);
        HelperApp helperApp = new HelperApp(this);
        logo.setOnClickListener(v -> {
            helperApp.intentHome();
        });
        device.setText("("+deviceId+")");
        loadFragment(new ChangePasswordFragment());

        text_menu_change_password.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus){
                text_menu_change_password.setTextColor(getResources().getColor(R.color.orange_main));
            }else{
                text_menu_change_password.setTextColor(getResources().getColor(R.color.white));
            }
        });

        text_menu_clear_history.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus){
                text_menu_clear_history.setTextColor(getResources().getColor(R.color.orange_main));
            }else{
                text_menu_clear_history.setTextColor(getResources().getColor(R.color.white));
            }
        });

        text_menu_clear_fav.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus){
                text_menu_clear_fav.setTextColor(getResources().getColor(R.color.orange_main));
            }else{
                text_menu_clear_fav.setTextColor(getResources().getColor(R.color.white));
            }
        });

        text_menu_activate_line.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus){
                text_menu_activate_line.setTextColor(getResources().getColor(R.color.orange_main));
            }else{
                text_menu_activate_line.setTextColor(getResources().getColor(R.color.white));
            }
        });

        text_menu_main_setting.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus){
                text_menu_main_setting.setTextColor(getResources().getColor(R.color.orange_main));
            }else{
                text_menu_main_setting.setTextColor(getResources().getColor(R.color.white));
            }
        });

        text_live_player_setting.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus){
                text_live_player_setting.setTextColor(getResources().getColor(R.color.orange_main));
            }else{
                text_live_player_setting.setTextColor(getResources().getColor(R.color.white));
            }
        });

        text_menu_change_password.setOnClickListener(v -> {
            loadFragment(new ChangePasswordFragment());
            view_menu_change_password.setVisibility(View.VISIBLE);
            view_menu_main_setting.setVisibility(View.GONE);
            view_live_player_setting.setVisibility(View.GONE);
        });

        text_menu_clear_history.setOnClickListener(v -> {
            AlertDialogMain alertDialogMain = new AlertDialogMain(this, getString(R.string.title_clear_history));
            alertDialogMain.show();
            alertDialogMain.setBt_yes(R.string.bt_yes, () -> new Cleardata().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR));
            alertDialogMain.setBt_no(R.string.bt_no, alertDialogMain::remove);
        });

        text_menu_clear_fav.setOnClickListener(v -> {
            AlertDialogMain alertDialogMain = new AlertDialogMain(this, getString(R.string.title_clear_fav));
            alertDialogMain.show();
            alertDialogMain.setBt_yes(R.string.bt_yes, () -> new Clearfav().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR));
            alertDialogMain.setBt_no(R.string.bt_no, alertDialogMain::remove);
        });

        text_menu_activate_line.setOnClickListener(v -> {
            ActivateLineDialog dialog = new ActivateLineDialog(this, code -> {
                //TODO
                Toast.makeText(SettingActivity.this, code, Toast.LENGTH_SHORT).show();
            });
            dialog.show();
        });

        text_menu_main_setting.setOnClickListener(v -> {
            loadFragment(new SettingMainMenuFragment());
            view_menu_change_password.setVisibility(View.GONE);
            view_live_player_setting.setVisibility(View.GONE);
            view_menu_main_setting.setVisibility(View.VISIBLE);
        });

        text_live_player_setting.setOnClickListener(v -> {
            loadFragment(new LivePlayerFragment());
            view_menu_change_password.setVisibility(View.GONE);
            view_menu_main_setting.setVisibility(View.GONE);
            view_live_player_setting.setVisibility(View.VISIBLE);
        });

    }

    private void loadFragment(Fragment fragment) {
        androidx.fragment.app.FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame_layout, fragment)
                .commit();

        if (getSupportFragmentManager().findFragmentById(R.id.frame_layout) != null) {
            getSupportFragmentManager().popBackStack();
        }
    }

    void ShowProgress(){
        layout_progress.setVisibility(View.VISIBLE);
    }

    void HideProgress(){
        layout_progress.setVisibility(View.GONE);
    }

    private class Cleardata extends AsyncTask<String, Integer, String> {

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
                    .url(apiurl + "clearhistory")
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
                Toast.makeText(SettingActivity.this, "Please check your internet!", Toast.LENGTH_SHORT).show();
            } else if (string.contains("ServerError")) {
                Toast.makeText(SettingActivity.this, "Server Error!", Toast.LENGTH_SHORT).show();
            } else {
                showData(string);
            }
        }
        private void showData(String jsonString) {
            Gson gson = new Gson();
            final Api blog = gson.fromJson(jsonString, Api.class);
            if (blog.getStatus().equals("success")) {
                Toast.makeText(SettingActivity.this, "Success", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(SettingActivity.this, blog.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class Clearfav extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {
            OkHttpClient okHttpClient2 = new OkHttpClient();

            RequestBody formBody = new FormBody.Builder()
                    .add("username", dbusername)
                    .add("password", dbpassword)
                    .build();

            Request request = new Request.Builder()
                    .url(apiurl + "clearfavorite")
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
                Toast.makeText(SettingActivity.this, "Please check your internet!", Toast.LENGTH_SHORT).show();
            } else if (string.contains("ServerError")) {
                Toast.makeText(SettingActivity.this, "Server Error!", Toast.LENGTH_SHORT).show();
            } else {
                showData(string);
            }
        }
        private void showData(String jsonString) {
            Gson gson = new Gson();
            final Api blog = gson.fromJson(jsonString, Api.class);
            if (blog.getStatus().equals("success")) {
                Toast.makeText(SettingActivity.this, "Success", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(SettingActivity.this, blog.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public interface ConfirmActivateLine {
        void confirmActivateLine(String code);
    }
}