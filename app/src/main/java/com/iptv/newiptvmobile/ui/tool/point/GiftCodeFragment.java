package com.iptv.newiptvmobile.ui.tool.point;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.iptv.newiptvmobile.R;
import com.iptv.newiptvmobile.adapter.RvExchangePointAdapter;
import com.iptv.newiptvmobile.model.Api;
import com.iptv.newiptvmobile.model.Data;
import com.iptv.newiptvmobile.ui.tool.topup.SuccessActivity;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.dnsoverhttps.DnsOverHttps;

public class GiftCodeFragment extends Fragment {

    LinearLayout bt_confirm_gift_card;
    EditText gift_code;
    SharedPreferences db;
    String username,password,apiurl;

    PointActivity activity;

    public GiftCodeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = requireActivity().getApplicationContext().getSharedPreferences("iptv", MODE_PRIVATE);
        username = db.getString("user", "");
        password = db.getString("pass", "");
        apiurl=db.getString("apiurl",getString(R.string.api_main));
        activity = (PointActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gift_code, container, false);
        bt_confirm_gift_card = view.findViewById(R.id.bt_confirm_gift_card);
        gift_code = view.findViewById(R.id.gift_code);

        bt_confirm_gift_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.ShowProgress();
                new GetGiftCode().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        });

        gift_code.requestFocus();
        // Inflate the layout for this fragment
        return view;
    }

    @SuppressLint("StaticFieldLeak")
    private class GetGiftCode extends AsyncTask<String, Integer, String> {

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
                    .add("giftcode", String.valueOf(gift_code.getText()))
                    .build();

            Request request = new Request.Builder()
                    .url(apiurl + "viewgift")
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
                activity.HideProgress();
                Toast.makeText(getContext(), "Please check your internet!", Toast.LENGTH_SHORT).show();
            } else if (string.contains("ServerError")) {
                activity.HideProgress();
                Toast.makeText(getContext(), "Server Error!", Toast.LENGTH_SHORT).show();
            } else {
                showData(string);
            }
        }
        private void showData(String jsonString) {
            Gson gson = new Gson();
            final Api blog = gson.fromJson(jsonString, Api.class);

            if (blog.getStatus().equals("success")){
                activity.HideProgress();
                Bundle bundle = new Bundle();
                bundle.putString("text", "Gift Code");
                Intent intent = new Intent(activity, SuccessActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }else{
                activity.HideProgress();
                Toast.makeText(activity, "Gift Code ผิดพลาด", Toast.LENGTH_SHORT).show();
            }

        }
    }
}