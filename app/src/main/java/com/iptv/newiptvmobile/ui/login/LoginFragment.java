package com.iptv.newiptvmobile.ui.login;

import static android.content.Context.MODE_PRIVATE;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.iptv.newiptvmobile.HomeActivity;
import com.iptv.newiptvmobile.R;
import com.iptv.newiptvmobile.model.Api;
import com.iptv.newiptvmobile.model.Data;

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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginFragment extends Fragment {

    LinearLayout bt_login;
    TextView text_login;
    EditText username,password;
    CheckBox remember;
    SharedPreferences db;
    SharedPreferences.Editor editor;
    String getusername,getpassword;
    String dbusername,dbpassword,apiurl;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    String deviceId;
    ProgressBar progress;

    public LoginFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LoginFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LoginFragment newInstance(String param1, String param2) {
        LoginFragment fragment = new LoginFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = getActivity().getApplicationContext().getSharedPreferences("iptv", MODE_PRIVATE);
        deviceId = android.provider.Settings.Secure.getString(this.getActivity().getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
        editor = getActivity().getSharedPreferences("iptv",MODE_PRIVATE).edit();
        dbusername=db.getString("username", "");
        dbpassword=db.getString("password", "");
        apiurl=db.getString("apiurl", getString(R.string.api_main));
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
//        Toast.makeText(getActivity(),apiurl,Toast.LENGTH_LONG).show();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        // Inflate the layout for this fragment
        bt_login = view.findViewById(R.id.bt_login);
        text_login = view.findViewById(R.id.text_login);
        username = view.findViewById(R.id.username);
        password = view.findViewById(R.id.password);
        remember = view.findViewById(R.id.checkbox);
        progress = view.findViewById(R.id.progressbar);
        if(!dbusername.equals("")){
            username.setText(dbusername);
            password.setText(dbpassword);
        }
        bt_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getusername = String.valueOf(username.getText());
                getpassword = String.valueOf(password.getText());
                progress.setVisibility(View.VISIBLE);
                new MyUpdate().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        });
        bt_login.requestFocus();

        bt_login.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus){
                    text_login.setTextColor(getResources().getColor(R.color.black));
                }else{
                    text_login.setTextColor(getResources().getColor(R.color.white));
                }
            }
        });
        return view;
    }

    private class MyUpdate extends AsyncTask<String, Integer, String> {

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
                    .add("username", getusername)
                    .add("password", getpassword)
                    .add("device", deviceId)
                    .build();
            Request request = new Request.Builder()
                    .url(apiurl + "login")
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
                Toast.makeText(getActivity(), "Please check your internet!", Toast.LENGTH_SHORT).show();
            } else if (string.contains("ServerError")) {
                Toast.makeText(getActivity(), "Server Error!", Toast.LENGTH_SHORT).show();
            } else {
                progress.setVisibility(View.GONE);
                showData(string);
            }
        }

        private void showData(String jsonString) {
            Gson gson = new Gson();
            final Api blog = gson.fromJson(jsonString, Api.class);
            final List<Data> data = blog.getData();
            if (blog.getStatus().equals("success")){
                if(remember.isChecked()){
                    editor.putString("username",getusername);
                    editor.putString("password",getpassword);
                }else{
                    editor.putString("username","");
                    editor.putString("password","");
                }
                editor.putString("user",getusername);
                editor.putString("pass",getpassword);
                editor.putString("uid",data.get(0).getUid());
                editor.putString("vipday",data.get(0).getVipday());
                editor.putString("point",data.get(0).getPoint());
                editor.putBoolean("expire",data.get(0).getExpire());
                editor.putBoolean("login",true);
                editor.apply();
                Intent intent = new Intent(getActivity(), HomeActivity.class);
                startActivity(intent);
                getActivity().finish();
                }else{
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage(blog.getMessage())
                        .setCancelable(false)
                        .setPositiveButton(R.string.bt_yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
                }
            }
    }


}