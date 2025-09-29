package com.iptv.newiptvmobile;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.media3.common.MimeTypes;
import androidx.media3.common.util.UnstableApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.iptv.newiptvmobile.adapter.CustomArrayAdapter;
import com.iptv.newiptvmobile.adapter.CustomGridLayoutManager;
import com.iptv.newiptvmobile.adapter.RvMovieCollectionAdapter;
import com.iptv.newiptvmobile.adapter.RvMovieDetailActorAdapter;
import com.iptv.newiptvmobile.adapter.RvMovieDetailAdapter;
import com.iptv.newiptvmobile.adapter.RvMovieDetailAdviceAdapter;
import com.iptv.newiptvmobile.listener.Refresh;
import com.iptv.newiptvmobile.model.Actorlist;
import com.iptv.newiptvmobile.model.Api;
import com.iptv.newiptvmobile.model.Data;
import com.iptv.newiptvmobile.model.ListSound;
import com.iptv.newiptvmobile.model.Lists;
import com.iptv.newiptvmobile.ui.tool.history_favorite.HistoryActivity;
import com.iptv.newiptvmobile.ui.tool.topup.TopUpActivity;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.dnsoverhttps.DnsOverHttps;

@UnstableApi
public class MovieDetailActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    TextView tv_title, tv_actor, tv_director, tv_year, tv_country, tv_time, tv_type, imdb_title, tv_imdb, tv_on_air, tv_detail, tv_read_more, text_filter,soundtrack,subtitle;
    ImageView img_wallpaper, fav_icon, img_imdb, arrow1, arrow2, img_slide;
    LinearLayout linearLayout, bt_order, bt_slide;
    RecyclerView rv_ep, rv_movie_advice, rv_actor;
    RvMovieDetailAdviceAdapter adviceAdapter;
    RvMovieDetailAdapter adapter;
    RvMovieCollectionAdapter adapterCollection;
    RvMovieDetailActorAdapter actorAdapter;
    RoundedImageView cover_image;
    private Spinner spinner;

    String wallpaper;
    String title;
    String actor;
    String actor_list;
    String writer;
    String year;
    String country;
    String genre;
    String rating;
    String runtime;
    String released;
    String detail;
    String id;
    String tid;
    String type, uid, username, password, apiurl;
    Integer playerid;
    SharedPreferences db;
    SharedPreferences.Editor editor;
    List<String> paths = new ArrayList<>();
    List<Actorlist> actorList;
    boolean fav_status = false;
    Integer spanCount = 6;
    boolean moving = false;
    Integer count = 0;
    Integer direction = 0;
    CustomArrayAdapter customArrayAdapter;
    boolean login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Application.getInstance().initAppLanguage(this);
        setContentView(R.layout.activity_movie_detail);
        db = getApplicationContext().getSharedPreferences("iptv", MODE_PRIVATE);
        editor = getSharedPreferences("iptv", MODE_PRIVATE).edit();
        playerid = db.getInt("playerid", 0);
        uid = db.getString("uid", "");
        username = db.getString("user", "");
        password = db.getString("pass", "");
        apiurl = db.getString("apiurl", getString(R.string.api_main));
        login = db.getBoolean("login", false);
        tv_title = findViewById(R.id.title);
        tv_actor = findViewById(R.id.actor);
        tv_director = findViewById(R.id.director);
        tv_year = findViewById(R.id.year);
        tv_type = findViewById(R.id.type);
        tv_imdb = findViewById(R.id.imdb);
        tv_time = findViewById(R.id.time);
        tv_country = findViewById(R.id.country);
        img_imdb = findViewById(R.id.imageView4);
        tv_on_air = findViewById(R.id.on_air);
        tv_detail = findViewById(R.id.detail);
        imdb_title = findViewById(R.id.imdb_title);
        tv_read_more = findViewById(R.id.read_more);
        img_wallpaper = (ImageView) findViewById(R.id.wallpaper);
        rv_ep = findViewById(R.id.rv_ep);
        rv_movie_advice = findViewById(R.id.rv_movie_advice);
        rv_actor = findViewById(R.id.rv_actor);
        spinner = (Spinner) findViewById(R.id.spinner);
        fav_icon = findViewById(R.id.fav_icon);
        linearLayout = findViewById(R.id.linearLayout);
        bt_order = findViewById(R.id.bt_order);
        arrow1 = findViewById(R.id.arrow1);
        arrow2 = findViewById(R.id.arrow2);
        bt_slide = findViewById(R.id.bt_slide);
        img_slide = findViewById(R.id.img_slide);
        text_filter = findViewById(R.id.text_filter);
        cover_image = findViewById(R.id.cover_image);
        soundtrack = findViewById(R.id.soundtrack);
        subtitle = findViewById(R.id.subtitle);


        paths.add("FW Player");
        paths.add("Android Player");
        paths.add("VLC Player");
        paths.add("MX Player");

        customArrayAdapter = new CustomArrayAdapter(MovieDetailActivity.this,
                R.layout.item_spiner, paths, "player", playerid, new Refresh() {
            @Override
            public void refresh() {
                playerid = db.getInt("playerid", 0);
                customArrayAdapter.notifyDataSetChanged();
            }
        });
        customArrayAdapter.setDropDownViewResource(R.layout.item_spiner_dropdown);
        spinner.setAdapter(customArrayAdapter);
        spinner.setSelection(playerid);
        spinner.setOnItemSelectedListener(this);


        if (!Objects.requireNonNull(getIntent().getExtras()).isEmpty()) {

            getIntentData();

            if (type.equals("adult")) {
                fav_icon.setVisibility(View.GONE);
                cover_image.setVisibility(View.VISIBLE);
                Picasso.get().load(getIntent().getExtras().getString("cover")).into(cover_image);
            } else {
                cover_image.setVisibility(View.GONE);
            }

            if (type.equals("collection")) {
                new LoadDataCollection().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } else {
                SetTextData();
                detailVisible();
                Picasso.get().load(wallpaper).into(img_wallpaper);

                new LoadDataEp().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        }
        if (!login) {
            Intent intent2 = new Intent(MovieDetailActivity.this, LoginActivity.class);
            startActivity(intent2);
            finish();
        }
    }
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (playerid != position) {
            playerid = position;
            editor.putInt("playerid", position);
            editor.apply();
            Toast.makeText(this, customArrayAdapter.getItem(position), Toast.LENGTH_SHORT).show();
        }
        customArrayAdapter.setCurrentIndex(position);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private class LoadDataEp extends AsyncTask<String, Integer, String> {

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
                    .add("id", tid)
                    .add("lang", LocaleUtils.getSelectedLanguageId(MovieDetailActivity.this))
                    .add("uid", uid)
                    .add("username", username)
                    .add("type", type)
                    .build();
            Request request = new Request.Builder()
                    .url(apiurl + "viewepisode")
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
                Toast.makeText(MovieDetailActivity.this, "Please check your internet!", Toast.LENGTH_SHORT).show();
            } else if (string.contains("ServerError")) {
                Toast.makeText(MovieDetailActivity.this, "Server Error!", Toast.LENGTH_SHORT).show();
            } else {
                showData(string);
            }
        }

        @SuppressLint({"NotifyDataSetChanged", "UseCompatLoadingForDrawables"})
        private void showData(String jsonString) {
            Gson gson = new Gson();
            final Api blog = gson.fromJson(jsonString, Api.class);
            List<Data> data = blog.getData();
            List<Lists> lists = blog.getLists();
            List<ListSound> listSounds = blog.getListsound();

            if (listSounds.size() == 0){
                soundtrack.setVisibility(View.INVISIBLE);
                subtitle.setVisibility(View.INVISIBLE);
            }else if (listSounds.size() == 1){
                if (listSounds.get(0).isSound_th()){
                    soundtrack.setVisibility(View.VISIBLE);
                    soundtrack.setBackgroundResource(R.drawable.bt_select_style_1_5dp_select);
                    soundtrack.setTextColor(getResources().getColor(R.color.orange_main));
                }else {
                    subtitle.setVisibility(View.VISIBLE);
                    subtitle.setBackgroundResource(R.drawable.bt_select_style_1_5dp_select);
                    subtitle.setTextColor(getResources().getColor(R.color.orange_main));
                }
            }else if (listSounds.size() == 2){
                soundtrack.setVisibility(View.VISIBLE);
                subtitle.setVisibility(View.VISIBLE);
                if (listSounds.get(0).getId().equals(tid)){
                    soundtrack.setBackgroundResource(R.drawable.bt_select_style_1_5dp_select);
                    soundtrack.setTextColor(getResources().getColor(R.color.orange_main));
                    subtitle.setOnClickListener(v -> {
                        Bundle bundle = new Bundle();
                        bundle.putString("type", type);
                        bundle.putString("wallpaper", listSounds.get(1).getWallpaper());
                        bundle.putString("title", listSounds.get(1).getTitle());
                        bundle.putString("actor", listSounds.get(1).getActor());
                        Gson gson12 = new Gson();
                        String actorList = gson12.toJson(listSounds.get(1).getActorlist());
                        bundle.putString("actor_list", actorList);
                        bundle.putString("writer", listSounds.get(1).getWriter());
                        bundle.putString("year", listSounds.get(1).getYear());
                        bundle.putString("country", listSounds.get(1).getCountry());
                        bundle.putString("genre", listSounds.get(1).getGenre());
                        bundle.putString("rating", listSounds.get(1).getRating());
                        bundle.putString("runtime", listSounds.get(1).getRuntime());
                        bundle.putString("released", listSounds.get(1).getReleased());
                        bundle.putString("detail", listSounds.get(1).getDetail());
                        bundle.putString("id", listSounds.get(1).getId());
                        Intent intent = new Intent(MovieDetailActivity.this, MovieDetailActivity.class);
                        intent.putExtras(bundle);
                        finish();
                        startActivity(intent);
                    });
                    subtitle.setOnFocusChangeListener((v, hasFocus) -> {
                        if (hasFocus){
                            subtitle.setTextColor(getResources().getColor(R.color.black));
                        }else {
                            subtitle.setTextColor(getResources().getColor(R.color.white));
                        }

                    });
                }else {
                    subtitle.setBackgroundResource(R.drawable.bt_select_style_1_5dp_select);
                    subtitle.setTextColor(getResources().getColor(R.color.orange_main));
                    soundtrack.setOnClickListener(v -> {
                        Bundle bundle = new Bundle();
                        bundle.putString("type", type);
                        bundle.putString("wallpaper", listSounds.get(0).getWallpaper());
                        bundle.putString("title", listSounds.get(0).getTitle());
                        bundle.putString("actor", listSounds.get(0).getActor());
                        Gson gson1 = new Gson();
                        String actorList = gson1.toJson(listSounds.get(0).getActorlist());
                        bundle.putString("actor_list", actorList);
                        bundle.putString("writer", listSounds.get(0).getWriter());
                        bundle.putString("year", listSounds.get(0).getYear());
                        bundle.putString("country", listSounds.get(0).getCountry());
                        bundle.putString("genre", listSounds.get(0).getGenre());
                        bundle.putString("rating", listSounds.get(0).getRating());
                        bundle.putString("runtime", listSounds.get(0).getRuntime());
                        bundle.putString("released", listSounds.get(0).getReleased());
                        bundle.putString("detail", listSounds.get(0).getDetail());
                        bundle.putString("id", listSounds.get(0).getId());
                        Intent intent = new Intent(MovieDetailActivity.this, MovieDetailActivity.class);
                        intent.putExtras(bundle);
                        finish();
                        startActivity(intent);
                    });

                    soundtrack.setOnFocusChangeListener((v, hasFocus) -> {
                        if (hasFocus){
                            soundtrack.setTextColor(getResources().getColor(R.color.black));
                        }else {
                            soundtrack.setTextColor(getResources().getColor(R.color.white));
                        }

                    });
                }
            }
            CustomGridLayoutManager gridLayoutManager = new CustomGridLayoutManager(MovieDetailActivity.this, spanCount, LinearLayoutManager.VERTICAL, false);
            rv_ep.setLayoutManager(gridLayoutManager);
            adapter = new RvMovieDetailAdapter(data, MovieDetailActivity.this, (position, title, id2) -> {
                id = id2;
                new LoadDataPlayEp().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }, focus -> {

            });
            rv_ep.setAdapter(adapter);
            rv_ep.requestFocus();


            SetRvActor(actorList);

            int heightPixels = Resources.getSystem().getDisplayMetrics().heightPixels;

            bt_slide.setVisibility(View.VISIBLE);
            int newHeight2 = (int) convertDpToPixel(100, MovieDetailActivity.this);
            animateHeight(linearLayout, newHeight2, 700);
            bt_slide.setOnClickListener(v -> {
                if (direction == 0) {
                    detailGone();
                    animateHeight(linearLayout, (int) (heightPixels - (heightPixels * 0.32)), 700);
                    img_slide.setImageResource(R.drawable.arrow_down_w);
                    direction = 1;
                    if (lists.size() > 0) {
                        rv_movie_advice.setVisibility(View.GONE);
                    }
                } else {
                    int newHeight = (int) convertDpToPixel(100, MovieDetailActivity.this);
                    animateHeight(linearLayout, newHeight, 700);
                    detailVisible();
                    img_slide.setImageResource(R.drawable.arrow_up_w);
                    linearLayout.setBackground(null);
                    direction = 0;
                    if (lists.size() > 0) {
                        rv_movie_advice.setVisibility(View.VISIBLE);
                    }
                }

            });

            Drawable[] res_up = {getResources().getDrawable(R.drawable.up_r), getResources().getDrawable(R.drawable.down_r)};
            Drawable[] res_down = {getResources().getDrawable(R.drawable.down_w), getResources().getDrawable(R.drawable.up_w)};
            bt_order.setOnClickListener(v -> {
                if (count == 0) {
                    text_filter.setText(R.string.text_new);
                    Collections.reverse(data);
                    adapter.notifyDataSetChanged();
                    count = 1;
                } else {
                    text_filter.setText(R.string.text_last);
                    Collections.reverse(data);
                    adapter.notifyDataSetChanged();
                    count = 0;
                }
                arrow1.setImageDrawable(res_up[count]);
                arrow2.setImageDrawable(res_down[count]);
            });

            if (lists.size() > 0) {
                rv_movie_advice.setVisibility(View.VISIBLE);
                rv_movie_advice.setLayoutManager(new LinearLayoutManager(MovieDetailActivity.this, RecyclerView.HORIZONTAL, false));
                adviceAdapter = new RvMovieDetailAdviceAdapter(MovieDetailActivity.this, lists, type);
                rv_movie_advice.setAdapter(adviceAdapter);
            } else {
                rv_movie_advice.setVisibility(View.GONE);
            }

            fav_status = blog.isFavorite();
            if (fav_status) {
                fav_icon.setImageDrawable(getDrawable(R.drawable.favorite));
            } else {
                fav_icon.setImageDrawable(getDrawable(R.drawable.un_favorite));
            }

            fav_icon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (fav_status) {
                        fav_icon.setImageDrawable(getDrawable(R.drawable.un_favorite));
                        fav_status = false;
                    } else {
                        fav_icon.setImageDrawable(getDrawable(R.drawable.favorite));
                        fav_status = true;

                    }
                    new FavoriteData().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }
            });

            tv_detail.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    tv_detail.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    int noOfLinesVisible = (tv_detail.getHeight() / tv_detail.getLineHeight());
                    tv_detail.setText(detail);
                    if (noOfLinesVisible == 0) {
                        noOfLinesVisible = 1;
                    }
                    tv_detail.setMaxLines(noOfLinesVisible);
                    tv_detail.setEllipsize(TextUtils.TruncateAt.END);
                }
            });

            readMore();

        }


    }

    @SuppressLint("StaticFieldLeak")
    private class LoadDataCollection extends AsyncTask<String, Integer, String> {
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
                    .add("id", tid)
                    .add("lang", LocaleUtils.getSelectedLanguageId(MovieDetailActivity.this))
                    .build();
            Request request = new Request.Builder()
                    .url(apiurl + "viewcollection")
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
                Toast.makeText(MovieDetailActivity.this, "Please check your internet!", Toast.LENGTH_SHORT).show();
            } else if (string.contains("ServerError")) {
                Toast.makeText(MovieDetailActivity.this, "Server Error!", Toast.LENGTH_SHORT).show();
            } else {
                showData(string);
            }
        }

        @SuppressLint("NotifyDataSetChanged")
        private void showData(String jsonString) {
            Gson gson = new Gson();
            final Api blog = gson.fromJson(jsonString, Api.class);
            List<Data> data = blog.getData();
            type = blog.getType();
            rv_ep.setLayoutManager(new LinearLayoutManager(MovieDetailActivity.this, RecyclerView.HORIZONTAL, false));
            adapterCollection = new RvMovieCollectionAdapter(MovieDetailActivity.this, data,
                    position -> {
                        setDetailData(data, position);
                        tv_detail.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                            @Override
                            public void onGlobalLayout() {
                                tv_detail.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                                int noOfLinesVisible = (tv_detail.getHeight() / tv_detail.getLineHeight());
                                tv_detail.setText(detail);
                                if (noOfLinesVisible == 0) {
                                    noOfLinesVisible = 1;
                                }
                                tv_detail.setMaxLines(noOfLinesVisible);
                                tv_detail.setEllipsize(TextUtils.TruncateAt.END);
                                readMore();
                            }
                        });
                    },
                    idCollection -> {
                        tid = idCollection;
                        new LoadDataEp().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    });
            rv_ep.setAdapter(adapterCollection);
            rv_ep.requestFocus();

            ViewGroup.LayoutParams params = linearLayout.getLayoutParams();
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            linearLayout.setLayoutParams(params);


        }
    }

    private class LoadDataPlayEp extends AsyncTask<String, Integer, String> {

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
                    .add("id", id)
                    .add("type", type)
                    .add("tid", tid)
                    .add("uid", uid)
                    .add("username", username)
                    .add("password", password)
                    .build();
            Request request = new Request.Builder()
                    .url(apiurl + "play")
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
                Toast.makeText(MovieDetailActivity.this, "Please check your internet!", Toast.LENGTH_SHORT).show();
            } else if (string.contains("ServerError")) {
                Toast.makeText(MovieDetailActivity.this, "Server Error!", Toast.LENGTH_SHORT).show();
            } else {
                showData(string);
            }
        }

        @SuppressLint("NotifyDataSetChanged")
        private void showData(String jsonString) {
            Gson gson = new Gson();
            final Api blog = gson.fromJson(jsonString, Api.class);
            if (blog.getStatus().equals("success")) {
                ChoosePlayer(blog.getUrl());
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(MovieDetailActivity.this);
                builder.setMessage(blog.getMessage())
                        .setCancelable(false)
                        .setPositiveButton(R.string.bt_yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                                Intent intent = new Intent(MovieDetailActivity.this, TopUpActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        }
    }

    public void ChoosePlayer(String url) {
        switch (playerid) {
            case 1:
                Bundle bundle = new Bundle();
                bundle.putString("url_media", url);
                bundle.putString("id", id);
                bundle.putString("tid", tid);
                bundle.putString("type", type);
                Intent intent = new Intent(MovieDetailActivity.this, AndroidPlayer.class);
                intent.putExtras(bundle);
                startActivity(intent);
                break;
            case 2:
                try {
                    Intent vlc = new Intent("android.intent.action.VIEW");
                    vlc.setDataAndType(Uri.parse(url), "video/*");
                    vlc.setPackage("org.videolan.vlc");
                    vlc.putExtra("title", title);
                    startActivity(vlc);
                } catch (Exception e) {
                    Intent vlc2 = new Intent("android.intent.action.VIEW");
                    vlc2.setData(Uri.parse("market://details?id=org.videolan.vlc"));
                    startActivity(vlc2);
                }
                break;
            case 3:
                try {
                    Intent mx = new Intent("android.intent.action.VIEW");
                    mx.setDataAndType(Uri.parse(url), MimeTypes.APPLICATION_M3U8);
                    mx.setPackage("com.mxtech.videoplayer.ad");
                    mx.putExtra("title", title);
                    startActivity(mx);
                } catch (Exception e) {
                    Intent mx2 = new Intent("android.intent.action.VIEW");
                    mx2.setDataAndType(Uri.parse(url), MimeTypes.APPLICATION_M3U8);
                    mx2.setPackage("com.mxtech.videoplayer.pro");
                    mx2.putExtra("title", title);
                    startActivity(mx2);
                }
                break;
            default:
                Bundle bundle2 = new Bundle();
                bundle2.putString("url_media", url);
                bundle2.putString("id", id);
                bundle2.putString("tid", tid);
                bundle2.putString("type", type);
                Intent intent2 = new Intent(MovieDetailActivity.this, PlayerExoPlayerActivity.class);
                intent2.putExtras(bundle2);
                startActivity(intent2);
                break;
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class FavoriteData extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {
            OkHttpClient okHttpClient2 = new OkHttpClient();

            RequestBody formBody = new FormBody.Builder()
                    .add("id", tid)
                    .add("type", type)
                    .add("username", username)
                    .build();
            Request request = new Request.Builder()
                    .url(apiurl + "favorite")
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
                Toast.makeText(MovieDetailActivity.this, "Please check your internet!", Toast.LENGTH_SHORT).show();
            } else if (string.contains("ServerError")) {
                Toast.makeText(MovieDetailActivity.this, "Server Error!", Toast.LENGTH_SHORT).show();
            } else {
                showData(string);
            }
        }

        @SuppressLint("NotifyDataSetChanged")
        private void showData(String jsonString) {
            Gson gson = new Gson();
            final Api blog = gson.fromJson(jsonString, Api.class);
            if (blog.getStatus().equals("success")) {
                Toast.makeText(MovieDetailActivity.this, blog.getMessage(), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MovieDetailActivity.this, blog.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    void getIntentData() {
        type = Objects.requireNonNull(getIntent().getExtras()).getString("type");
        wallpaper = getIntent().getExtras().getString("wallpaper");
        title = getIntent().getExtras().getString("title");
        actor = getIntent().getExtras().getString("actor");
        actor_list = getIntent().getExtras().getString("actor_list");
        JsonArrayToActorList(actor_list);

        writer = getIntent().getExtras().getString("writer");
        year = getIntent().getExtras().getString("year");
        country = getIntent().getExtras().getString("country");
        genre = getIntent().getExtras().getString("genre");
        rating = getIntent().getExtras().getString("rating");
        runtime = getIntent().getExtras().getString("runtime");
        released = getIntent().getExtras().getString("released");
        detail = getIntent().getExtras().getString("detail");
        tid = getIntent().getExtras().getString("id");

    }

    private void JsonArrayToActorList(String jsonArray) {
        Gson gson = new Gson();
        Type type = new TypeToken<List<Actorlist>>() {
        }.getType();
        actorList = gson.fromJson(jsonArray, type);
    }

    @SuppressLint("SetTextI18n")
    void setDetailData(List<Data> data, int position) {
        Data listsCollection = data.get(position);
//        type = listsCollection.getType();
        wallpaper = listsCollection.getWallpaper();
        title = listsCollection.getTitle();
        actor = listsCollection.getActor();
        writer = listsCollection.getWriter();
        year = listsCollection.getYear();
        country = listsCollection.getCountry();
        genre = listsCollection.getGenre();
        rating = listsCollection.getRating();
        runtime = listsCollection.getRuntime();
        released = listsCollection.getReleased();
        detail = listsCollection.getDetail();
        tid = listsCollection.getId();
        actorList = listsCollection.getActorlist();
        SetTextData();
        detailVisible();
        Picasso.get().load(wallpaper).into(img_wallpaper);
    }

    @SuppressLint("SetTextI18n")
    private void SetTextData() {
        tv_title.setText(title);
        tv_detail.setText(detail);
        tv_actor.setText(getString(R.string.actor));
        SetRvActor(actorList);
        tv_director.setText(getString(R.string.director) + " " + writer);
        tv_year.setText(getString(R.string.year) + " " + year);
        tv_country.setText(getString(R.string.country) + " " + country);
        tv_time.setText(getString(R.string.time) + " " + runtime);
        tv_type.setText(getString(R.string.type) + " " + genre);
        tv_imdb.setText(rating);
        tv_on_air.setText(getString(R.string.on_air) + " " + released);
    }

    void detailGone() {
        tv_actor.setVisibility(View.GONE);
        rv_actor.setVisibility(View.GONE);
        tv_director.setVisibility(View.GONE);
        tv_year.setVisibility(View.GONE);
        tv_country.setVisibility(View.GONE);
        tv_time.setVisibility(View.GONE);
        tv_type.setVisibility(View.GONE);
        tv_imdb.setVisibility(View.GONE);
        imdb_title.setVisibility(View.GONE);
        tv_on_air.setVisibility(View.GONE);
        tv_detail.setVisibility(View.GONE);
    }

    void detailVisible() {
        tv_detail.setVisibility(View.VISIBLE);
        tv_actor.setVisibility(CheckNull(actor));
        rv_actor.setVisibility(CheckNull(actor));
        tv_director.setVisibility(CheckNull(writer));
        tv_year.setVisibility(CheckNull(year));
        tv_country.setVisibility(CheckNull(country));
        tv_time.setVisibility(CheckNull(runtime));
        tv_type.setVisibility(CheckNull(genre));
        tv_imdb.setVisibility(CheckNull(rating));
        imdb_title.setVisibility(CheckNull(rating));
        img_imdb.setVisibility(CheckNull(rating));
        tv_on_air.setVisibility(CheckNull(released));
    }

    void SetRvActor(List<Actorlist> listActor) {
        if (listActor != null) {
            rv_actor.setLayoutManager(new LinearLayoutManager(MovieDetailActivity.this, LinearLayoutManager.HORIZONTAL, false));
            actorAdapter = new RvMovieDetailActorAdapter(MovieDetailActivity.this, listActor, new RvActorClick() {
                @Override
                public void click(String nameActor) {

                    Bundle bundle = new Bundle();
                    bundle.putString("type", type);
                    bundle.putString("actor", nameActor);
                    Intent intent = new Intent(MovieDetailActivity.this, HistoryActivity.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            });
            rv_actor.setAdapter(actorAdapter);
        } else {
            actorAdapter = null;
        }

    }

    void readMore() {
        tv_detail.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(MovieDetailActivity.this);
            builder.setMessage(detail);
            builder.setNegativeButton("OK", (dialog, which) -> dialog.dismiss());
            builder.setCancelable(false);
            builder.show();
        });

        tv_detail.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                tv_read_more.setVisibility(View.VISIBLE);
            } else {
                tv_read_more.setVisibility(View.GONE);
            }
        });
    }

    public static float convertDpToPixel(float dp, Context context) {
        return dp * ((float) context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

    void animateHeight(View view, int height, int DURATION) {
        moving = true;
        ValueAnimator anim = ValueAnimator.ofInt(view.getMeasuredHeight(), height);
        anim.addUpdateListener(valueAnimator -> {
            int val = (Integer) valueAnimator.getAnimatedValue();
            ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
            layoutParams.height = val;
            view.setLayoutParams(layoutParams);
        });
        anim.setDuration(DURATION);
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
                layoutParams.height = view.getMeasuredHeight();
                moving = false;
            }
        });

        anim.start();
    }

    void animateHeightListener(View view, int height, int DURATION, OnAnimateSuccess listener) {
        moving = true;
        ValueAnimator anim = ValueAnimator.ofInt(view.getMeasuredHeight(), height);
        anim.addUpdateListener(valueAnimator -> {
            int val = (Integer) valueAnimator.getAnimatedValue();
            ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
            layoutParams.height = val;
            view.setLayoutParams(layoutParams);
            listener.successAnim();
        });
        anim.setDuration(DURATION);
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
                layoutParams.height = view.getMeasuredHeight();
                moving = false;
            }
        });

        anim.start();
    }

    interface OnAnimateSuccess {
        void successAnim();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (moving)
            return true;
        else
            return super.onKeyDown(keyCode, event);
    }

    int CheckNull(String text) {
        if (text.equals("") || text.equals("/10") || text.equals("0") || text.equals(" ")) {
            return View.GONE;
        } else {
            return View.VISIBLE;
        }
    }

    public interface RvActorClick {
        void click(String title);
    }
}