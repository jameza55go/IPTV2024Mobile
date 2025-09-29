package com.iptv.newiptvmobile.ui.tool.setting;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.iptv.newiptvmobile.LocaleUtils;
import com.iptv.newiptvmobile.MovieDetailActivity;
import com.iptv.newiptvmobile.R;
import com.iptv.newiptvmobile.adapter.CustomArrayAdapter;
import com.iptv.newiptvmobile.adapter.CustomArraySettingMainMenuAdapter;
import com.iptv.newiptvmobile.adapter.RvMovieCollectionAdapter;
import com.iptv.newiptvmobile.listener.Refresh;
import com.iptv.newiptvmobile.model.Api;
import com.iptv.newiptvmobile.model.Data;
import com.iptv.newiptvmobile.model.Row;

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

public class SettingMainMenuFragment extends Fragment {

    LinearLayout bt_confirm_change_setting_menu_main;
    TextView confirmtext;
    SharedPreferences db;
    SharedPreferences.Editor editor;
    String username, password, apiurl;
    CustomArraySettingMainMenuAdapter adapter_row1,adapter_row2,adapter_row3;
    Spinner spinner_row_1,spinner_row_2,spinner_row_3;
    List<String> paths_row1 = new ArrayList<>();
    List<String> paths_row2 = new ArrayList<>();
    List<String> paths_row3 = new ArrayList<>();
    int FPositionRow1 = 0;
    int FPositionRow2 = 0;
    int FPositionRow3 = 0;
    String selectRow1,selectRow2,selectRow3;
    public SettingMainMenuFragment() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_setting_main_menu, container, false);
        // Inflate the layout for this fragment
        db = requireContext().getApplicationContext().getSharedPreferences("iptv", MODE_PRIVATE);
        editor = requireContext().getSharedPreferences("iptv", MODE_PRIVATE).edit();
        username = db.getString("user", "");
        password = db.getString("pass", "");
        apiurl = db.getString("apiurl", getString(R.string.api_main));
        spinner_row_1 = view.findViewById(R.id.spinner_row_1);
        spinner_row_2 = view.findViewById(R.id.spinner_row_2);
        spinner_row_3 = view.findViewById(R.id.spinner_row_3);
        bt_confirm_change_setting_menu_main = view.findViewById(R.id.bt_confirm_change_setting_menu_main);
        confirmtext = view.findViewById(R.id.confirmtext);
        new LoadData().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        return view;
    }

    @SuppressLint("StaticFieldLeak")
    private class LoadData extends AsyncTask<String, Integer, String> {
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
                    .add("lang", LocaleUtils.getSelectedLanguageId(requireContext()))
                    .build();
            Request request = new Request.Builder()
                    .url(apiurl + "indextheme")
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
                Toast.makeText(requireContext(), "Please check your internet!", Toast.LENGTH_SHORT).show();
            } else if (string.contains("ServerError")) {
                Toast.makeText(requireContext(), "Server Error!", Toast.LENGTH_SHORT).show();
            } else {
                showData(string);
            }
        }

        @SuppressLint("NotifyDataSetChanged")
        private void showData(String jsonString) {
            Gson gson = new Gson();
            final Api blog = gson.fromJson(jsonString, Api.class);
            List<Row> dataRow1 = blog.getRowone();
            List<Row> dataRow2 = blog.getRowtwo();
            List<Row> dataRow3 = blog.getRowthree();

            Log.d("TAG", jsonString);
            for (int i = 0; i < dataRow1.size(); i++){
                paths_row1.add(dataRow1.get(i).getTitle());
                if (dataRow1.get(i).isStatus()){
                    FPositionRow1 = i;
                    Log.d("FPositionRow1", "FPositionRow1: " + i);
                }
            }

            for (int i = 0; i < dataRow2.size(); i++){
                paths_row2.add(dataRow2.get(i).getTitle());
                if (dataRow2.get(i).isStatus()){
                    FPositionRow2 = i;
                    Log.d("FPositionRow2", "FPositionRow2: " + i);
                }
            }

            for (int i = 0; i < dataRow3.size(); i++){
                paths_row3.add(dataRow3.get(i).getTitle());
                if (dataRow3.get(i).isStatus()){
                    FPositionRow3 = i;
                    Log.d("FPositionRow3", "FPositionRow3: " + i);
                }
            }

            adapter_row1 = new CustomArraySettingMainMenuAdapter(requireContext(),
                    R.layout.item_spiner, paths_row1);
            adapter_row1.setDropDownViewResource(R.layout.item_spiner_dropdown);
            spinner_row_1.setAdapter(adapter_row1);
            spinner_row_1.setSelection(FPositionRow1);
            spinner_row_1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    adapter_row1.setCurrentIndex(position);
                    adapter_row1.notifyDataSetChanged();
                    selectRow1 = dataRow1.get(position).getId();
                    Log.d("onItemSelected1", "onItemSelected: " + position);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            adapter_row2 = new CustomArraySettingMainMenuAdapter(requireContext(),
                    R.layout.item_spiner, paths_row2);
            adapter_row2.setDropDownViewResource(R.layout.item_spiner_dropdown);
            spinner_row_2.setAdapter(adapter_row2);
            spinner_row_2.setSelection(FPositionRow2);
            spinner_row_2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    adapter_row2.setCurrentIndex(position);
                    adapter_row2.notifyDataSetChanged();
                    selectRow2 = dataRow2.get(position).getId();
                    Log.d("onItemSelected2", "onItemSelected: " + position);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            adapter_row3 = new CustomArraySettingMainMenuAdapter(requireContext(),
                    R.layout.item_spiner, paths_row3);
            adapter_row3.setDropDownViewResource(R.layout.item_spiner_dropdown);
            spinner_row_3.setAdapter(adapter_row3);
            spinner_row_3.setSelection(FPositionRow3);
            spinner_row_3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    adapter_row3.setCurrentIndex(position);
                    adapter_row3.notifyDataSetChanged();
                    selectRow3 = dataRow3.get(position).getId();
                    Log.d("onItemSelected3", "onItemSelected: " + position);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            bt_confirm_change_setting_menu_main.setOnClickListener(v -> {
                new SendData().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            });
            bt_confirm_change_setting_menu_main.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus){
                        confirmtext.setTextColor(getResources().getColor(R.color.black));
                    }else{
                        confirmtext.setTextColor(getResources().getColor(R.color.white));
                    }
                }
            });

        }
    }

    @SuppressLint("StaticFieldLeak")
    private class SendData extends AsyncTask<String, Integer, String> {
        @Override
        protected String doInBackground(String... params) {
            OkHttpClient okHttpClient2 = new OkHttpClient();

            RequestBody formBody = new FormBody.Builder()
                    .add("username", username)
                    .add("password", password)
                    .add("rowone", selectRow1)
                    .add("rowtwo", selectRow2)
                    .add("rowthree", selectRow3)
                    .build();
            Request request = new Request.Builder()
                    .url(apiurl + "indextheme/change")
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
                Toast.makeText(requireContext(), "Please check your internet!", Toast.LENGTH_SHORT).show();
            } else if (string.contains("ServerError")) {
                Toast.makeText(requireContext(), "Server Error!", Toast.LENGTH_SHORT).show();
            } else {
                showData(string);
            }
        }

        @SuppressLint("NotifyDataSetChanged")
        private void showData(String jsonString) {
            Gson gson = new Gson();
            final Api blog = gson.fromJson(jsonString, Api.class);
            Toast.makeText(requireContext(),blog.getMessage(),Toast.LENGTH_LONG).show();

        }
    }
}