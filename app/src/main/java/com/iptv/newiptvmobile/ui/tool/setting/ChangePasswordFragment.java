package com.iptv.newiptvmobile.ui.tool.setting;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.iptv.newiptvmobile.R;
import com.iptv.newiptvmobile.model.Api;
import com.iptv.newiptvmobile.ui.tool.topup.SuccessActivity;

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

public class ChangePasswordFragment extends Fragment {

    SharedPreferences db;
    SharedPreferences.Editor db_edit;
    String username,apiurl;
    EditText current_password,new_password,re_new_password;
    LinearLayout bt_confirm_change_password;

    SettingActivity settingActivity;
    TextView text_confirm;

    public ChangePasswordFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = requireActivity().getApplicationContext().getSharedPreferences("iptv", MODE_PRIVATE);
        db_edit = requireActivity().getSharedPreferences("iptv", MODE_PRIVATE).edit();
        username = db.getString("user", "");
        apiurl=db.getString("apiurl",getString(R.string.api_main));
        settingActivity = (SettingActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_change_password, container, false);
        current_password = view.findViewById(R.id.current_password);
        new_password = view.findViewById(R.id.new_password);
        re_new_password = view.findViewById(R.id.re_new_password);
        text_confirm = view.findViewById(R.id.confirmtext);
        bt_confirm_change_password = view.findViewById(R.id.bt_confirm_change_password);

        bt_confirm_change_password.setOnClickListener(v -> {
            if(new_password.getText().length() > 5 && re_new_password.getText().length() > 5){
                if (String.valueOf(new_password.getText()).equals(String.valueOf(re_new_password.getText()))) {
                    settingActivity.ShowProgress();
                    new ResetPassword().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }else{
                    Toast.makeText(getContext(), "กรุณากรอกพาสเวิร์ดให้ตรงกัน", Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(getContext(), "ต้องใช้พาสเวิร์ดที่มีอักขระอย่างน้อย 6 ตัวอักษรขึ้นไป", Toast.LENGTH_SHORT).show();
            }

        });
        bt_confirm_change_password.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus){
                    text_confirm.setTextColor(getResources().getColor(R.color.black));
                }else{
                    text_confirm.setTextColor(getResources().getColor(R.color.white));
                }
            }
        });

        return view;
    }

    @SuppressLint("StaticFieldLeak")
    private class ResetPassword extends AsyncTask<String, Integer, String> {

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
                    .add("oldpassword", String.valueOf(current_password.getText()))
                    .add("newpassword", String.valueOf(new_password.getText()))
                    .build();

            Request request = new Request.Builder()
                    .url(apiurl + "changpassword")
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
                settingActivity.HideProgress();
                Toast.makeText(getContext(), "Please check your internet!", Toast.LENGTH_SHORT).show();
            } else if (string.contains("ServerError")) {
                settingActivity.HideProgress();
                Toast.makeText(getContext(), "Server Error!", Toast.LENGTH_SHORT).show();
            } else {
                showData(string);
            }
        }
        private void showData(String jsonString) {
            Gson gson = new Gson();
            final Api blog = gson.fromJson(jsonString, Api.class);

            if (blog.getStatus().equals("success")) {
                db_edit.putString("pass", String.valueOf(new_password.getText()));
                db_edit.apply();
                settingActivity.HideProgress();
                settingActivity.finish();
                Bundle bundle = new Bundle();
                bundle.putString("text", getString(R.string.txt_change_password));
                Intent intent = new Intent(settingActivity, SuccessActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }else{
                settingActivity.HideProgress();
                Toast.makeText(getContext(), blog.getMessage(), Toast.LENGTH_SHORT).show();
            }


        }
    }
}