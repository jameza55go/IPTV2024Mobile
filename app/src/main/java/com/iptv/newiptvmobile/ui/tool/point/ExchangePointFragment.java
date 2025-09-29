package com.iptv.newiptvmobile.ui.tool.point;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.iptv.newiptvmobile.CategoryActivity;
import com.iptv.newiptvmobile.HomeActivity;
import com.iptv.newiptvmobile.R;
import com.iptv.newiptvmobile.adapter.RvExchangePointAdapter;
import com.iptv.newiptvmobile.adapter.RvHomeCategoryAdapter;
import com.iptv.newiptvmobile.listener.OnClickRvHomeCategory;
import com.iptv.newiptvmobile.model.Api;
import com.iptv.newiptvmobile.model.Data;
import com.iptv.newiptvmobile.ui.liveTV.LiveActivity;
import com.iptv.newiptvmobile.ui.tool.topup.SuccessActivity;
import com.iptv.newiptvmobile.ui.tool.topup.TopUpQRCodeActivity;

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

public class ExchangePointFragment extends Fragment {

    TextView text_point;
    RecyclerView rv_exchange_point;
    RvExchangePointAdapter adapter;

    SharedPreferences db;
    SharedPreferences.Editor db_edit;

    String point;
    String username;
    String password, apiurl;
    String id_exchange;

    PointActivity pointActivity;

    public ExchangePointFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = requireActivity().getApplicationContext().getSharedPreferences("iptv", MODE_PRIVATE);
        point = db.getString("point", "");
        username = db.getString("user", "");
        password = db.getString("pass", "");
        apiurl = db.getString("apiurl", getString(R.string.api_main));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_exchange_point, container, false);
        text_point = view.findViewById(R.id.text_point);
        rv_exchange_point = view.findViewById(R.id.rv_exchange_point);
        pointActivity = (PointActivity) getActivity();
        text_point.setText(" " + point + getString(R.string.txt_point));

        new GetExchangePoint().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        // Inflate the layout for this fragment
        return view;
    }

    @SuppressLint("StaticFieldLeak")
    private class GetExchangePoint extends AsyncTask<String, Integer, String> {

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

            Request request = new Request.Builder()
                    .url(apiurl + "viewreward")
                    .build();

            try {
                Response response = okHttpClient2.newCall(request).execute();
                if (response.isSuccessful()) {
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
                Toast.makeText(getContext(), "Please check your internet!", Toast.LENGTH_SHORT).show();
            } else if (string.contains("ServerError")) {
                Toast.makeText(getContext(), "Server Error!", Toast.LENGTH_SHORT).show();
            } else {
                showData(string);
            }
        }

        private void showData(String jsonString) {
            Gson gson = new Gson();
            final Api blog = gson.fromJson(jsonString, Api.class);
            final List<Data> data = blog.getData();

            GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 4, GridLayoutManager.VERTICAL, false);
            rv_exchange_point.setLayoutManager(gridLayoutManager);
            adapter = new RvExchangePointAdapter(getContext(), data, new OnClickExchangePoint() {
                @Override
                public void onClickExchangePoint(String id) {
                    id_exchange = id;
                    pointActivity.ShowProgress();
                    new PutExchangePoint().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }
            });
            rv_exchange_point.setAdapter(adapter);
            rv_exchange_point.requestFocus();
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class PutExchangePoint extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {
            OkHttpClient okHttpClient2 = new OkHttpClient();

            RequestBody formBody = new FormBody.Builder()
                    .add("username", username)
                    .add("password", password)
                    .add("id", id_exchange)
                    .build();

            Request request = new Request.Builder()
                    .url(apiurl + "viewreward/change")
                    .post(formBody)
                    .build();

            try {
                Response response = okHttpClient2.newCall(request).execute();
                if (response.isSuccessful()) {
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
                pointActivity.HideProgress();
                Toast.makeText(getContext(), "Please check your internet!", Toast.LENGTH_SHORT).show();
            } else if (string.contains("ServerError")) {
                pointActivity.HideProgress();
                Toast.makeText(getContext(), "Server Error!", Toast.LENGTH_SHORT).show();
            } else {
                showData(string);
            }
        }

        private void showData(String jsonString) {
            Gson gson = new Gson();
            final Api blog = gson.fromJson(jsonString, Api.class);

            if (blog.getStatus().equals("success")) {
                pointActivity.HideProgress();
                pointActivity.finish();
                Bundle bundle = new Bundle();
                bundle.putString("text", getString(R.string.txt_exchange_point));
                Intent intent = new Intent(pointActivity, SuccessActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            } else {
                pointActivity.HideProgress();
                Toast.makeText(getContext(), blog.getMessage(), Toast.LENGTH_SHORT).show();
            }


        }
    }

    public interface OnClickExchangePoint {
        void onClickExchangePoint(String id);
    }
}