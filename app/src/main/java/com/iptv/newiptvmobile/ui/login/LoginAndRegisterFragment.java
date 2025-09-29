package com.iptv.newiptvmobile.ui.login;

import static android.content.Context.MODE_PRIVATE;

import static java.lang.Integer.parseInt;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.iptv.newiptvmobile.HomeActivity;
import com.iptv.newiptvmobile.R;
import com.iptv.newiptvmobile.adapter.CustomViewPagerScroller;
import com.iptv.newiptvmobile.adapter.InfinitePagerAdapter;
import com.iptv.newiptvmobile.model.Api;
import com.iptv.newiptvmobile.model.Data;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
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

public class LoginAndRegisterFragment extends Fragment {
    TextView text_login,text_register;
    ViewPager vp_img_slide;
    InfinitePagerAdapter adapter;
    String type_screen;
    SharedPreferences db;
    String apiurl;
    private Timer swipeTimer = new Timer();
    TimerTask timerTask;
    private Handler handler = new Handler();
    final long delaySlide = 5000;
    int maxSlide = 20000;

    public LoginAndRegisterFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        assert getArguments() != null;
        db = getActivity().getApplicationContext().getSharedPreferences("iptv", MODE_PRIVATE);
        apiurl=db.getString("apiurl", getString(R.string.api_main));
        type_screen = getArguments().getString("type");
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        if (type_screen.equals("login")){
            transaction.replace(R.id.frame_layout, new LoginFragment()).commit();
        }else if (type_screen.equals("register")){
            transaction.replace(R.id.frame_layout, new RegisterFragment()).commit();
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login_and_register, container, false);

        text_login = view.findViewById(R.id.text_login);
        text_register = view.findViewById(R.id.text_register);
        vp_img_slide = view.findViewById(R.id.vp_img_slide);

        text_login.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus){
                text_login.setTextColor(ContextCompat.getColor(requireActivity(), R.color.orange_main));
            }else{
                text_login.setTextColor(ContextCompat.getColor(requireActivity(), R.color.white));
            }
        });

        text_register.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus){
                text_register.setTextColor(ContextCompat.getColor(requireActivity(), R.color.orange_main));
            }else{
                text_register.setTextColor(ContextCompat.getColor(requireActivity(), R.color.white));
            }
        });


        text_login.setOnClickListener(v -> {
            text_register.setBackground(null);
            text_login.setBackground(ContextCompat.getDrawable(requireActivity(), R.drawable.bg_underline_tv));
            text_login.setPadding(0, 0, 0, 0);
            FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
            transaction.replace(R.id.frame_layout, new LoginFragment()).commit();
            vp_img_slide.setVisibility(View.VISIBLE);
            if (timerTask != null){
                timerTask.cancel();
            }
            swipeTimer.cancel();
            handler = null;
            new LoadSlide().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        });


        text_register.setOnClickListener(v -> {
            text_login.setBackground(null);
            text_register.setBackground(ContextCompat.getDrawable(requireActivity(), R.drawable.bg_underline_tv));
            text_register.setPadding(0, 0, 0, 0);
            FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
            transaction.replace(R.id.frame_layout, new RegisterFragment()).commit();
            vp_img_slide.setVisibility(View.GONE);
            if (timerTask != null){
                timerTask.cancel();
            }
            swipeTimer.cancel();
            handler = null;
        });

        if (type_screen.equals("login")){
            text_login.requestFocus();
            text_register.setBackground(null);
            text_login.setBackground(ContextCompat.getDrawable(requireActivity(), R.drawable.bg_underline_tv));
            text_login.setPadding(0, 0, 0, 0);
            new LoadSlide().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }else if (type_screen.equals("register")){
            text_register.requestFocus();
            text_login.setBackground(null);
            text_register.setBackground(ContextCompat.getDrawable(requireActivity(), R.drawable.bg_underline_tv));
            text_register.setPadding(0, 0, 0, 0);
            vp_img_slide.setVisibility(View.GONE);
            timerTask.cancel();
            swipeTimer.cancel();
            handler = null;
        }

        // Inflate the layout for this fragment
        return view;
    }

    private class LoadSlide extends AsyncTask<String, Integer, String> {

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
                    .url(apiurl + "slides")
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
                showData(string);
            }
        }

        @SuppressLint("ClickableViewAccessibility")
        private void showData(String jsonString) {
            Gson gson = new Gson();
            final Api blog = gson.fromJson(jsonString, Api.class);
            final List<Data> data = blog.getData();
            if (blog.getStatus().equals("success")){

                CustomViewPagerScroller scroller = new CustomViewPagerScroller(requireContext());
                scroller.setScrollDuration(1000);

                try {
                    Field scrollerField = ViewPager.class.getDeclaredField("mScroller");
                    scrollerField.setAccessible(true);
                    scrollerField.set(vp_img_slide, scroller);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                adapter = new InfinitePagerAdapter(data, getContext());
                vp_img_slide.setAdapter(adapter);
                vp_img_slide.setCurrentItem(0);
                vp_img_slide.setOnTouchListener(new View.OnTouchListener() {
                    public boolean onTouch(View arg0, MotionEvent arg1) {
                        return true;
                    }
                });
                handler = new Handler();
                timerTask = new TimerTask() {
                    @Override
                    public void run() {
                        handler.post(() -> {
                            int currentItem = vp_img_slide.getCurrentItem();
                            if (currentItem >= maxSlide - 1) {
                                vp_img_slide.setAdapter(null);
                                vp_img_slide.removeAllViews();
                                vp_img_slide.setAdapter(adapter);
                                vp_img_slide.setCurrentItem(maxSlide/2);
                                Log.d("TAG", "run: new Adapter");
                            } else {
                                vp_img_slide.setCurrentItem(currentItem + 1, true);
                            }
                        });
                    }
                };
                swipeTimer = new Timer();
                swipeTimer.schedule(timerTask, 1000, delaySlide);
            }
        }
    }

    @Override
    public void onStop() {
        if (timerTask !=null){
            timerTask.cancel();
        }
        if (swipeTimer != null){
            swipeTimer.cancel();
        }
        if (handler != null){
            handler = null;
        }

        super.onStop();
    }
}