package com.iptv.newiptvmobile.ui.login;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.app.FragmentManager;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.iptv.newiptvmobile.R;
import com.iptv.newiptvmobile.model.Api;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.dnsoverhttps.DnsOverHttps;

public class RegisterFragment extends Fragment {
    String deviceId,apiurl,lineid,linename;
    String token="";
    LinearLayout bt_register,bt_receive_otp;
    TextView text_register,text_receive_otp,text_qrcode;
    EditText username,password,phone,otp;
    SharedPreferences db;
    ProgressBar progressBar;
    ImageView imageView;
    private CountDownTimer countDownTimer;
    public RegisterFragment() {
        // Required empty public constructor
    }

    @SuppressLint("HardwareIds")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = requireActivity().getApplicationContext().getSharedPreferences("iptv", MODE_PRIVATE);
        deviceId = android.provider.Settings.Secure.getString(this.requireActivity().getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
        apiurl=db.getString("apiurl", getString(R.string.api_main));
        lineid=db.getString("lineid", "https://dev.inwmanga.com/tv/images/line.png");
        linename=db.getString("linename", "@fwiptvuhd");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register2, container, false);
        bt_register = view.findViewById(R.id.bt_register);
        text_register = view.findViewById(R.id.text_register);
        username = view.findViewById(R.id.user);
        password = view.findViewById(R.id.pass);
        text_qrcode = view.findViewById(R.id.textqrcode);
        phone = view.findViewById(R.id.phonenumber);
        otp = view.findViewById(R.id.otp);
        text_receive_otp = view.findViewById(R.id.text_receive_otp);
        bt_receive_otp = view.findViewById(R.id.linearLayout_receive_otp);
        progressBar = view.findViewById(R.id.progress);
        imageView = view.findViewById(R.id.qr_code);
        //Picasso.get().load(lineid).into(imageView);
        text_qrcode.setText(getString(R.string.qr_code_activate_user_fwiptvudh,linename));
        bt_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(username.getText().length()>0 && password.getText().length()>0 && phone.getText().length()>0 && otp.getText().length()>0) {
                    progressBar.setVisibility(View.VISIBLE);
                    new MyUpdate().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }else{
                    Toast.makeText(getActivity(), "Please fill in the form. กรุณากรอกข้อมูลให้ครบถ้วน", Toast.LENGTH_SHORT).show();
                }
            }
        });
        bt_register.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus){
                text_register.setTextColor(getResources().getColor(R.color.black));
            }else{
                text_register.setTextColor(getResources().getColor(R.color.white));
            }
        });

        enableOTP();
        return view;
    }

    @SuppressLint("StaticFieldLeak")
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
                    .add("username", String.valueOf(username.getText()))
                    .add("password", String.valueOf(password.getText()))
                    .add("tel", String.valueOf(phone.getText()))
                    .add("device", deviceId)
                    .add("otp", String.valueOf(otp.getText()))
                    .add("token", token)
                    .build();
            Request request = new Request.Builder()
                    .url(apiurl + "register")
                    .post(formBody)
                    .build();

            try {
                Response response = okHttpClient2.newCall(request).execute();
                if (response.isSuccessful()) {
                    assert response.body() != null;
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
                showData(string);
            }
        }

        private void showData(String jsonString) {
            progressBar.setVisibility(View.GONE);
            Gson gson = new Gson();
            final Api blog = gson.fromJson(jsonString, Api.class);
            if (blog.getStatus().equals("success")){
                loadFragment(new LoginAndRegisterFragment());
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
            builder.setMessage(blog.getMessage())
                    .setCancelable(false)
                    .setPositiveButton(R.string.bt_yes, (dialog, id) -> dialog.dismiss());
            AlertDialog alert = builder.create();
            alert.show();
        }
    }

    private void loadFragment(Fragment fragment) {
        androidx.fragment.app.FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        Bundle bundle = new Bundle();
        bundle.putString("type", "login");
        fragment.setArguments(bundle);

        fragmentManager.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frameLayout, fragment, "login")
                .commit();
    }
    void enableOTP(){
        bt_receive_otp.setBackgroundResource(R.drawable.bt_select_otp);
        text_receive_otp.setTextColor(ContextCompat.getColor(requireContext(),R.color.black));
        bt_receive_otp.setOnClickListener(v -> {
            otp.requestFocus();
            text_receive_otp.setTextColor(ContextCompat.getColor(requireContext(),R.color.white));
            disableOTP();
            new MyOTP().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            //TODO send OTP
            countDownTimer  = new CountDownTimer(60000, 1000) {
                @SuppressLint("SetTextI18n")
                public void onTick(long millisUntilFinished) {
                    text_receive_otp.setText(" " + millisUntilFinished / 1000);
                }
                public void onFinish() {
                    text_receive_otp.setText(R.string.send_otp);
                    enableOTP();
                }
            }.start();
        });
    }

    void disableOTP(){
        bt_receive_otp.setBackgroundResource(R.drawable.bg_disable_otp);
        bt_receive_otp.setOnClickListener(null);
    }

    private class MyOTP extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {
            OkHttpClient okHttpClient2 = new OkHttpClient();

            RequestBody formBody = new FormBody.Builder()
                    .add("username", String.valueOf(username.getText()))
                    .add("password", String.valueOf(password.getText()))
                    .add("tel", String.valueOf(phone.getText()))
                    .add("device", deviceId)
                    .build();
            Request request = new Request.Builder()
                    .url(apiurl + "getotp")
                    .post(formBody)
                    .build();

            try {
                Response response = okHttpClient2.newCall(request).execute();
                if (response.isSuccessful()) {
                    assert response.body() != null;
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
                showData(string);
            }
        }

        private void showData(String jsonString) {
            progressBar.setVisibility(View.GONE);
            Gson gson = new Gson();
            final Api blog = gson.fromJson(jsonString, Api.class);
            if (blog.getStatus().equals("success")){
                phone.setEnabled(false);
                token = blog.getToken();
                Toast.makeText(getActivity(), "Send SMS finish.", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(getActivity(), blog.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}