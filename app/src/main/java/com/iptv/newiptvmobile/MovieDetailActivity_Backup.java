//package com.iptv.newiptvapplication;
//
//import android.animation.Animator;
//import android.animation.AnimatorListenerAdapter;
//import android.animation.ValueAnimator;
//import android.annotation.SuppressLint;
//import android.content.Context;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.content.res.Resources;
//import android.graphics.drawable.Drawable;
//import android.net.Uri;
//import android.os.AsyncTask;
//import android.os.Bundle;
//import android.text.TextUtils;
//import android.util.DisplayMetrics;
//import android.view.KeyEvent;
//import android.view.View;
//import android.view.ViewGroup;
//import android.view.ViewTreeObserver;
//import android.widget.AdapterView;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.Spinner;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.appcompat.app.AlertDialog;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.media3.common.MimeTypes;
//import androidx.media3.common.util.UnstableApi;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.google.gson.Gson;
//import com.iptv.newiptvapplication.adapter.CustomArrayAdapter;
//import com.iptv.newiptvapplication.adapter.CustomGridLayoutManager;
//import com.iptv.newiptvapplication.adapter.EndlessRecyclerViewScrollListener;
//import com.iptv.newiptvapplication.adapter.RvMovieCollectionAdapter;
//import com.iptv.newiptvapplication.adapter.RvMovieDetailAdapter;
//import com.iptv.newiptvapplication.adapter.RvMovieDetailAdviceAdapter;
//import com.iptv.newiptvapplication.listener.Refresh;
//import com.iptv.newiptvapplication.model.Api;
//import com.iptv.newiptvapplication.model.Data;
//import com.iptv.newiptvapplication.model.Lists;
//import com.iptv.newiptvapplication.ui.tool.topup.TopUpActivity;
//import com.squareup.picasso.Picasso;
//
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.List;
//import java.util.Objects;
//
//import okhttp3.FormBody;
//import okhttp3.OkHttpClient;
//import okhttp3.Request;
//import okhttp3.RequestBody;
//import okhttp3.Response;
//
//@UnstableApi public class MovieDetailActivity_Backup extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
//
//    TextView tv_title,tv_actor,tv_year,tv_type,tv_imdb,tv_detail,tv_read_more,text_filter;
//    ImageView img_wallpaper,fav_icon,img_imdb,arrow1,arrow2,img_slide;
//    LinearLayout linearLayout,bt_order,bt_slide;
//    RecyclerView rv_ep,rv_movie_advice;
//    RvMovieDetailAdviceAdapter adviceAdapter;
//    RvMovieDetailAdapter adapter;
//    RvMovieCollectionAdapter adapterCollection;
//    private Spinner spinner;
//
//    String wallpaper;
//    String title;
//    String actor;
//    String writer;
//    String year;
//    String country;
//    String genre;
//    String rating;
//    String runtime;
//    String released;
//    String detail;
//    String id;
//    String tid;
//    String type,uid,username,password,apiurl;
//    Integer playerid;
//    SharedPreferences db;
//    SharedPreferences.Editor editor;
//    List<String> paths = new ArrayList<>();
//    List<Lists> lists2;
//    boolean fav_status = false;
//    Integer spanCount = 6;
//    boolean moving = false;
//    Integer count = 0;
//    Integer direction = 0;
//    CustomArrayAdapter customArrayAdapter;
//    private EndlessRecyclerViewScrollListener scrollListener;
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        Application.getInstance().initAppLanguage(this);
//        setContentView(R.layout.activity_movie_detail);
//        db = getApplicationContext().getSharedPreferences("iptv", MODE_PRIVATE);
//        editor = getSharedPreferences("iptv",MODE_PRIVATE).edit();
//        playerid=db.getInt("playerid",0);
//        uid=db.getString("uid", "");
//        username=db.getString("user", "");
//        password=db.getString("pass", "");
//        apiurl=db.getString("apiurl",getString(R.string.api_main));
//        tv_title = findViewById(R.id.title);
//        tv_actor = findViewById(R.id.actor);
//        tv_year = findViewById(R.id.year);
//        tv_type = findViewById(R.id.type);
//        tv_imdb = findViewById(R.id.imdb);
//        img_imdb = findViewById(R.id.imageView4);
//        tv_detail = findViewById(R.id.detail);
//        tv_read_more = findViewById(R.id.read_more);
//        img_wallpaper = (ImageView) findViewById(R.id.wallpaper);
//        rv_ep = findViewById(R.id.rv_ep);
//        rv_movie_advice = findViewById(R.id.rv_movie_advice);
//        spinner = (Spinner) findViewById(R.id.spinner);
//        fav_icon = findViewById(R.id.fav_icon);
//        linearLayout = findViewById(R.id.linearLayout);
//        bt_order = findViewById(R.id.bt_order);
//        arrow1 = findViewById(R.id.arrow1);
//        arrow2 = findViewById(R.id.arrow2);
//        bt_slide = findViewById(R.id.bt_slide);
//        img_slide = findViewById(R.id.img_slide);
//        text_filter = findViewById(R.id.text_filter);
//
//
//        paths.add("FW Player");
//        paths.add("Android Player");
//        paths.add("VLC Player");
//        paths.add("MX Player");
//
//        customArrayAdapter = new CustomArrayAdapter(MovieDetailActivity_Backup.this,
//                R.layout.item_spiner, paths, "player", playerid, new Refresh() {
//            @Override
//            public void refresh() {
//                playerid=db.getInt("playerid",0);
//                customArrayAdapter.notifyDataSetChanged();
//            }
//        });
//        customArrayAdapter.setDropDownViewResource(R.layout.item_spiner_dropdown);
//        spinner.setAdapter(customArrayAdapter);
//        spinner.setSelection(playerid);
//        spinner.setOnItemSelectedListener(this);
//
//
//
//        if (!getIntent().getExtras().isEmpty()){
//
//            getIntentData();
//
//            if (type.equals("adult")){
//                fav_icon.setVisibility(View.GONE);
//            }
//
//            if (type.equals("collection")){
//                new LoadDataCollection().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//            }else{
//                tv_title.setText(title);
//                if (actor.equals("") && writer.equals("")){
//                    tv_actor.setVisibility(View.GONE);
//                }else{
//                    tv_actor.setVisibility(View.VISIBLE);
//                    tv_actor.setText(actor + " " + writer);
//                }
//                if ((year.equals("0") || year.equals("")) && country.equals("")){
//                    tv_year.setVisibility(View.GONE);
//                }else{
//                    tv_year.setVisibility(View.VISIBLE);
//                    tv_year.setText(year + " " + country);
//                }
//                if (genre.equals("")){
//                    tv_type.setVisibility(View.GONE);
//                }else{
//                    tv_type.setVisibility(View.VISIBLE);
//                    tv_type.setText(genre);
//                }
//
//                String text = "";
//                if (rating.equals("/10") || rating.equals("")){
//                    img_imdb.setVisibility(View.GONE);
//                }else{
//                    img_imdb.setVisibility(View.VISIBLE);
//                    text += rating;
//                }
//
//                if (!(runtime.equals("0") || runtime.equals(""))){
//                    text += " : " + runtime + getString(R.string.txt_minute);
//                }
//
//                if (!released.equals("")){
//                    text += getString(R.string.txt_released) + released;
//                }
//
//                if (text.equals("")){
//                    tv_imdb.setVisibility(View.GONE);
//                }else{
//                    tv_imdb.setVisibility(View.VISIBLE);
//                    tv_imdb.setText(text);
//                }
//
//                tv_detail.setText(detail);
//
//                Picasso.get().load(wallpaper).into(img_wallpaper);
//
//                new LoadDataEp().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//            }
//
//
//        }
//
//    }
//
//    @Override
//    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//        if(playerid!=position) {
//            playerid = position;
//            editor.putInt("playerid", position);
//            editor.apply();
//            Toast.makeText(this, customArrayAdapter.getItem(position), Toast.LENGTH_SHORT).show();
//        }
//        customArrayAdapter.setCurrentIndex(position);
//    }
//
//    @Override
//    public void onNothingSelected(AdapterView<?> parent) {
//
//    }
//
//    private class LoadDataEp extends AsyncTask<String, Integer, String> {
//
//        @Override
//        protected String doInBackground(String... params) {
//            OkHttpClient okHttpClient2 = new OkHttpClient();
//
//            RequestBody formBody = new FormBody.Builder()
//                    .add("id", tid)
//                    .add("lang", LocaleUtils.getSelectedLanguageId(MovieDetailActivity_Backup.this))
//                    .add("uid",uid)
//                    .add("username",username)
//                    .add("type",type)
//                    .build();
//            Request request = new Request.Builder()
//                    .url(apiurl + "viewepisode")
//                    .post(formBody)
//                    .build();
//
//            try {
//                Response response = okHttpClient2.newCall(request).execute();
//                if (response.isSuccessful()) {
//                    return Objects.requireNonNull(response.body()).string();
//                } else {
//                    return "ServerError";
//                }
//            } catch (IOException e) {
//                return "InternetError";
//            }
//        }
//
//        @Override
//        protected void onPostExecute(String string) {
//            if (string.contains("InternetError")) {
//                Toast.makeText(MovieDetailActivity_Backup.this, "Please check your internet!", Toast.LENGTH_SHORT).show();
//            } else if (string.contains("ServerError")) {
//                Toast.makeText(MovieDetailActivity_Backup.this, "Server Error!", Toast.LENGTH_SHORT).show();
//            } else {
//                showData(string);
//            }
//        }
//
//        @SuppressLint({"NotifyDataSetChanged", "UseCompatLoadingForDrawables"})
//        private void showData(String jsonString) {
//            Gson gson = new Gson();
//            final Api blog = gson.fromJson(jsonString, Api.class);
//            List<Data> data = blog.getData();
//            List<Lists> lists = blog.getLists();
////            if (data.size() < spanCount){
////                rv_ep.setLayoutManager(new LinearLayoutManager(MovieDetailActivity.this,RecyclerView.HORIZONTAL,false));
////            }else{
////                rv_ep.setLayoutManager(new LinearLayoutManager(MovieDetailActivity.this,RecyclerView.HORIZONTAL,false));
////            }
//
//            CustomGridLayoutManager gridLayoutManager = new CustomGridLayoutManager(MovieDetailActivity_Backup.this,spanCount,LinearLayoutManager.VERTICAL,false);
//            rv_ep.setLayoutManager(gridLayoutManager);
//            adapter = new RvMovieDetailAdapter(data, MovieDetailActivity_Backup.this, (position, title, id2) -> {
//                id = id2;
//                new LoadDataPlayEp().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//            }, focus -> {
////                if (focus){
////                    if (data.size() > spanCount*2){
////                        int newHeight = (int) convertDpToPixel(150,MovieDetailActivity.this);
////                        animateHeight(linearLayout,newHeight,100);
////                    } else if (data.size() > spanCount) {
////                        int newHeight = (int) convertDpToPixel(100,MovieDetailActivity.this);
////                        animateHeight(linearLayout,newHeight,100);
////                    }
////                }else{
////                    int newHeight = (int) convertDpToPixel(40,MovieDetailActivity.this);
////                    animateHeight(linearLayout,newHeight,100);
////                }
//            });
//            rv_ep.setAdapter(adapter);
//            rv_ep.requestFocus();
//
//            int heightPixels = Resources.getSystem().getDisplayMetrics().heightPixels;
//
////           if (!type.equals("movie")){
//                bt_slide.setVisibility(View.VISIBLE);
//                int newHeight2 = (int) convertDpToPixel(100, MovieDetailActivity_Backup.this);
//                animateHeight(linearLayout,newHeight2,100);
//                bt_slide.setOnClickListener(v -> {
//                    if (direction == 0){
//                        detailGone();
//                        animateHeight(linearLayout, (int) (heightPixels - (heightPixels * 0.32)),100);
//                        img_slide.setImageResource(R.drawable.arrow_down_w);
//                        direction = 1;
//                        if (lists.size() > 0){
//                            rv_movie_advice.setVisibility(View.GONE);
//                        }
//                    }else{
//                            int newHeight = (int) convertDpToPixel(80, MovieDetailActivity_Backup.this);
//                            animateHeight(linearLayout,newHeight,100);
//                        detailVisible();
//                        img_slide.setImageResource(R.drawable.arrow_up_w);
//                        linearLayout.setBackground(null);
//                        direction = 0;
//                        if (lists.size() > 0){
//                            rv_movie_advice.setVisibility(View.VISIBLE);
//                        }
//                    }
//
//                });
////            }else{
////                bt_slide.setVisibility(View.VISIBLE);
////            }
//
//            Drawable[] res_up = {getResources().getDrawable(R.drawable.up_r),getResources().getDrawable(R.drawable.down_r)};
//            Drawable[] res_down = {getResources().getDrawable(R.drawable.down_w),getResources().getDrawable(R.drawable.up_w)};
//            bt_order.setOnClickListener(v -> {
//                if (count == 0){
//                    text_filter.setText(R.string.text_new);
//                    Collections.reverse(data);
//                    adapter.notifyDataSetChanged();
//                    count = 1;
//                }else {
//                    text_filter.setText(R.string.text_last);
//                    Collections.reverse(data);
//                    adapter.notifyDataSetChanged();
//                    count = 0;
//                }
//                arrow1.setImageDrawable(res_up[count]);
//                arrow2.setImageDrawable(res_down[count]);
//            });
//
//            if (lists.size() > 0){
//                rv_movie_advice.setVisibility(View.VISIBLE);
//                rv_movie_advice.setLayoutManager(new LinearLayoutManager(MovieDetailActivity_Backup.this,RecyclerView.HORIZONTAL,false));
//                adviceAdapter = new RvMovieDetailAdviceAdapter(MovieDetailActivity_Backup.this,lists,type);
//                rv_movie_advice.setAdapter(adviceAdapter);
//            }else{
//                rv_movie_advice.setVisibility(View.GONE);
//            }
//
//            fav_status = blog.isFavorite();
//            if (fav_status){
//                fav_icon.setImageDrawable(getDrawable(R.drawable.favorite));
//            }else{
//                fav_icon.setImageDrawable(getDrawable(R.drawable.un_favorite));
//            }
//
//            fav_icon.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (fav_status){
//                        fav_icon.setImageDrawable(getDrawable(R.drawable.un_favorite));
//                        fav_status = false;
//                    }else{
//                        fav_icon.setImageDrawable(getDrawable(R.drawable.favorite));
//                        fav_status = true;
//
//                    }
//                    new FavoriteData().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//                }
//            });
//
//            tv_detail.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//                @Override
//                public void onGlobalLayout() {
//                    tv_detail.getViewTreeObserver().removeOnGlobalLayoutListener(this);
//                    int noOfLinesVisible = (tv_detail.getHeight() / tv_detail.getLineHeight());
//                    tv_detail.setText(detail);
//                    if(noOfLinesVisible == 0){
//                        noOfLinesVisible = 1;
//                    }
//                    tv_detail.setMaxLines(noOfLinesVisible);
//                    tv_detail.setEllipsize(TextUtils.TruncateAt.END);
//                }
//            });
//
//            readMore();
//
//        }
//    }
//
//    private class LoadDataCollection extends AsyncTask<String, Integer, String> {
//        @Override
//        protected String doInBackground(String... params) {
//            OkHttpClient okHttpClient2 = new OkHttpClient();
//
//            RequestBody formBody = new FormBody.Builder()
//                    .add("id", tid)
//                    .add("lang", LocaleUtils.getSelectedLanguageId(MovieDetailActivity_Backup.this))
//                    .build();
//            Request request = new Request.Builder()
//                    .url(apiurl + "viewcollection")
//                    .post(formBody)
//                    .build();
//
//            try {
//                Response response = okHttpClient2.newCall(request).execute();
//                if (response.isSuccessful()) {
//                    return Objects.requireNonNull(response.body()).string();
//                } else {
//                    return "ServerError";
//                }
//            } catch (IOException e) {
//                return "InternetError";
//            }
//        }
//
//        @Override
//        protected void onPostExecute(String string) {
//            if (string.contains("InternetError")) {
//                Toast.makeText(MovieDetailActivity_Backup.this, "Please check your internet!", Toast.LENGTH_SHORT).show();
//            } else if (string.contains("ServerError")) {
//                Toast.makeText(MovieDetailActivity_Backup.this, "Server Error!", Toast.LENGTH_SHORT).show();
//            } else {
//                showData(string);
//            }
//        }
//
//        @SuppressLint("NotifyDataSetChanged")
//        private void showData(String jsonString) {
//            Gson gson = new Gson();
//            final Api blog = gson.fromJson(jsonString, Api.class);
//            List<Data> data = blog.getData();
//            type = blog.getType();
//            rv_ep.setLayoutManager(new LinearLayoutManager(MovieDetailActivity_Backup.this,RecyclerView.HORIZONTAL,false));
//            adapterCollection = new RvMovieCollectionAdapter(MovieDetailActivity_Backup.this, data,
//                    position -> {
//                        setDetailData(data,position);
//                        tv_detail.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//                            @Override
//                            public void onGlobalLayout() {
//                                tv_detail.getViewTreeObserver().removeOnGlobalLayoutListener(this);
//                                int noOfLinesVisible = (tv_detail.getHeight() / tv_detail.getLineHeight());
//                                tv_detail.setText(detail);
//                                if(noOfLinesVisible == 0){
//                                    noOfLinesVisible = 1;
//                                }
//                                tv_detail.setMaxLines(noOfLinesVisible);
//                                tv_detail.setEllipsize(TextUtils.TruncateAt.END);
//                                readMore();
//                            }
//                        });
//            },
//                    idCollection -> {
//                        tid = idCollection;
//                        new LoadDataEp().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//            });
//            int newHeight2 = (int) convertDpToPixel(200, MovieDetailActivity_Backup.this);
//            animateHeightListener(linearLayout, newHeight2, 50, new OnAnimateSuccess() {
//                @Override
//                public void successAnim() {
//                    rv_ep.setAdapter(adapterCollection);
//                    rv_ep.requestFocus();
//                }
//            });
//
//
//
//
//        }
//    }
//
//    private class LoadDataPlayEp extends AsyncTask<String, Integer, String> {
//
//        @Override
//        protected String doInBackground(String... params) {
//            OkHttpClient okHttpClient2 = new OkHttpClient();
//
//            RequestBody formBody = new FormBody.Builder()
//                    .add("id", id)
//                    .add("type", type)
//                    .add("tid", tid)
//                    .add("uid", uid)
//                    .add("username", username)
//                    .add("password", password)
//                    .build();
//            Request request = new Request.Builder()
//                    .url(apiurl + "play")
//                    .post(formBody)
//                    .build();
//
//            try {
//                Response response = okHttpClient2.newCall(request).execute();
//                if (response.isSuccessful()) {
//                    return Objects.requireNonNull(response.body()).string();
//                } else {
//                    return "ServerError";
//                }
//            } catch (IOException e) {
//                return "InternetError";
//            }
//        }
//
//        @Override
//        protected void onPostExecute(String string) {
//            if (string.contains("InternetError")) {
//                Toast.makeText(MovieDetailActivity_Backup.this, "Please check your internet!", Toast.LENGTH_SHORT).show();
//            } else if (string.contains("ServerError")) {
//                Toast.makeText(MovieDetailActivity_Backup.this, "Server Error!", Toast.LENGTH_SHORT).show();
//            } else {
//                showData(string);
//            }
//        }
//
//        @SuppressLint("NotifyDataSetChanged")
//        private void showData(String jsonString) {
//            Gson gson = new Gson();
//            final Api blog = gson.fromJson(jsonString, Api.class);
//            if (blog.getStatus().equals("success")){
//                ChoosePlayer(blog.getUrl());
//            }else {
//                AlertDialog.Builder builder = new AlertDialog.Builder(MovieDetailActivity_Backup.this);
//                builder.setMessage(blog.getMessage())
//                        .setCancelable(false)
//                        .setPositiveButton(R.string.bt_yes, new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int id) {
//                                dialog.dismiss();
//                                Intent intent = new Intent(MovieDetailActivity_Backup.this, TopUpActivity.class);
//                                startActivity(intent);
//                                finish();
//                            }
//                        });
//                AlertDialog alert = builder.create();
//                alert.show();
//            }
//        }
//    }
//
//    public void ChoosePlayer(String url){
//        switch(playerid){
//            case 1:
//                Bundle bundle = new Bundle();
//                bundle.putString("url_media", url);
//                bundle.putString("id", id);
//                bundle.putString("tid", tid);
//                bundle.putString("type", type);
//                Intent intent = new Intent(MovieDetailActivity_Backup.this,AndroidPlayer.class);
//                intent.putExtras(bundle);
//                startActivity(intent);
//                break;
//            case 2:
//                try {
//                    Intent vlc = new Intent("android.intent.action.VIEW");
//                    vlc.setDataAndType(Uri.parse(url), "video/*");
//                    vlc.setPackage("org.videolan.vlc");
//                    vlc.putExtra("title", title);
//                    startActivity(vlc);
//                } catch (Exception e) {
//                    Intent vlc2 = new Intent("android.intent.action.VIEW");
//                    vlc2.setData(Uri.parse("market://details?id=org.videolan.vlc"));
//                    startActivity(vlc2);
//                }
//                break;
//            case 3:
//                try {
//                    Intent mx = new Intent("android.intent.action.VIEW");
//                    mx.setDataAndType(Uri.parse(url), MimeTypes.APPLICATION_M3U8);
//                    mx.setPackage("com.mxtech.videoplayer.ad");
//                    mx.putExtra("title", title);
//                    startActivity(mx);
//                } catch (Exception e) {
//                    Intent mx2 = new Intent("android.intent.action.VIEW");
//                    mx2.setDataAndType(Uri.parse(url), MimeTypes.APPLICATION_M3U8);
//                    mx2.setPackage("com.mxtech.videoplayer.pro");
//                    mx2.putExtra("title", title);
//                    startActivity(mx2);
//                }
//                break;
//            default:
//                Bundle bundle2 = new Bundle();
//                bundle2.putString("url_media", url);
//                bundle2.putString("id", id);
//                bundle2.putString("tid", tid);
//                bundle2.putString("type", type);
//                Intent intent2 = new Intent(MovieDetailActivity_Backup.this,PlayerExoPlayerActivity.class);
//                intent2.putExtras(bundle2);
//                startActivity(intent2);
//                break;
//        }
//    }
//
//    private class FavoriteData extends AsyncTask<String, Integer, String> {
//
//        @Override
//        protected String doInBackground(String... params) {
//            OkHttpClient okHttpClient2 = new OkHttpClient();
//
//            RequestBody formBody = new FormBody.Builder()
//                    .add("id", tid)
//                    .add("type", type)
//                    .add("username", username)
//                    .build();
//            Request request = new Request.Builder()
//                    .url(apiurl + "favorite")
//                    .post(formBody)
//                    .build();
//
//            try {
//                Response response = okHttpClient2.newCall(request).execute();
//                if (response.isSuccessful()) {
//                    return Objects.requireNonNull(response.body()).string();
//                } else {
//                    return "ServerError";
//                }
//            } catch (IOException e) {
//                return "InternetError";
//            }
//        }
//
//        @Override
//        protected void onPostExecute(String string) {
//            if (string.contains("InternetError")) {
//                Toast.makeText(MovieDetailActivity_Backup.this, "Please check your internet!", Toast.LENGTH_SHORT).show();
//            } else if (string.contains("ServerError")) {
//                Toast.makeText(MovieDetailActivity_Backup.this, "Server Error!", Toast.LENGTH_SHORT).show();
//            } else {
//                showData(string);
//            }
//        }
//
//        @SuppressLint("NotifyDataSetChanged")
//        private void showData(String jsonString) {
//            Gson gson = new Gson();
//            final Api blog = gson.fromJson(jsonString, Api.class);
//            if (blog.getStatus().equals("success")){
//                Toast.makeText(MovieDetailActivity_Backup.this, blog.getMessage(), Toast.LENGTH_SHORT).show();
//            }else {
//                Toast.makeText(MovieDetailActivity_Backup.this, blog.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        }
//    }
//
//    private void onPositiveButtonClicked(){
//        new LoadDataEp().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//    }
//
//    void getIntentData(){
//        type = getIntent().getExtras().getString("type");
//        wallpaper = getIntent().getExtras().getString("wallpaper");
//        title = getIntent().getExtras().getString("title");
//        actor = getIntent().getExtras().getString("actor");
//        writer = getIntent().getExtras().getString("writer");
//        year = getIntent().getExtras().getString("year");
//        country = getIntent().getExtras().getString("country");
//        genre = getIntent().getExtras().getString("genre");
//        rating = getIntent().getExtras().getString("rating");
//        runtime = getIntent().getExtras().getString("runtime");
//        released = getIntent().getExtras().getString("released");
//        detail = getIntent().getExtras().getString("detail");
//        tid = getIntent().getExtras().getString("id");
//    }
//
//    void setDetailData(List<Data> data,int position){
//        Data listsCollection = data.get(position);
////        type = listsCollection.getType();
//        wallpaper = listsCollection.getWallpaper();
//        title = listsCollection.getTitle();
//        actor = listsCollection.getActor();
//        writer = listsCollection.getWriter();
//        year = listsCollection.getYear();
//        country = listsCollection.getCountry();
//        genre = listsCollection.getGenre();
//        rating = listsCollection.getRating();
//        runtime = listsCollection.getRuntime();
//        released = listsCollection.getReleased();
//        detail = listsCollection.getDetail();
//        tid = listsCollection.getId();
//
//        tv_title.setText(title);
//        tv_actor.setText(actor + " " + writer);
//        tv_year.setText(year + " " + country);
//        tv_type.setText(genre);
//        tv_imdb.setText(rating + " " + runtime + " " + released);
//        tv_detail.setText(detail);
//        Picasso.get().load(wallpaper).into(img_wallpaper);
//    }
//
//    void detailGone(){
//        tv_actor.setVisibility(View.GONE);
//        tv_year.setVisibility(View.GONE);
//        tv_type.setVisibility(View.GONE);
//        tv_imdb.setVisibility(View.GONE);
//        tv_detail.setVisibility(View.GONE);
//    }
//
//    void detailVisible(){
//        tv_detail.setVisibility(View.VISIBLE);
//        if (actor.equals("") && writer.equals("")){
//            tv_actor.setVisibility(View.GONE);
//        }else{
//            tv_actor.setVisibility(View.VISIBLE);
//        }
//        if ((year.equals("0") || year.equals("")) && country.equals("")){
//            tv_year.setVisibility(View.GONE);
//        }else{
//            tv_year.setVisibility(View.VISIBLE);
//        }
//        if (genre.equals("")){
//            tv_type.setVisibility(View.GONE);
//        }else{
//            tv_type.setVisibility(View.VISIBLE);
//        }
//
//        String text = "";
//        if (rating.equals("/10") || rating.equals("")){
//            img_imdb.setVisibility(View.GONE);
//        }else{
//            img_imdb.setVisibility(View.VISIBLE);
//        }
//
//        if (text.equals("")){
//            tv_imdb.setVisibility(View.GONE);
//        }else{
//            tv_imdb.setVisibility(View.VISIBLE);
//        }
//
//    }
//
//    void readMore(){
//        tv_detail.setOnClickListener(v -> {
//            AlertDialog.Builder builder = new AlertDialog.Builder(MovieDetailActivity_Backup.this);
//            builder.setMessage(detail);
//            builder.setNegativeButton("OK", (dialog, which) -> dialog.dismiss());
//            builder.setCancelable(false);
//            builder.show();
//        });
//
//        tv_detail.setOnFocusChangeListener((v, hasFocus) -> {
//            if (hasFocus){
//                tv_read_more.setVisibility(View.VISIBLE);
//            }else{
//                tv_read_more.setVisibility(View.GONE);
//            }
//        });
//    }
//    public static float convertDpToPixel(float dp, Context context){
//        return dp * ((float) context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
//    }
//
//    void animateHeight(View view,int height,int DURATION){
//        moving = true;
//        ValueAnimator anim = ValueAnimator.ofInt(view.getMeasuredHeight(), height);
//        anim.addUpdateListener(valueAnimator -> {
//            int val = (Integer) valueAnimator.getAnimatedValue();
//            ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
//            layoutParams.height = val;
//            view.setLayoutParams(layoutParams);
//        });
//        anim.setDuration(DURATION);
//        anim.addListener(new AnimatorListenerAdapter() {
//            @Override
//            public void onAnimationEnd(Animator animation) {
//                // At the end of animation, set the height to wrap content
//                // This fix is for long views that are not shown on screen
//                ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
//                layoutParams.height = view.getMeasuredHeight();
//                moving = false;
////                view.getLayoutParams().height = height;
////                view.setLayoutParams(new GridLayoutManager.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,height));
//            }
//        });
//
//        anim.start();
//    }
//
//    void animateHeightListener(View view,int height,int DURATION,OnAnimateSuccess listener){
//        moving = true;
//        ValueAnimator anim = ValueAnimator.ofInt(view.getMeasuredHeight(), height);
//        anim.addUpdateListener(valueAnimator -> {
//            int val = (Integer) valueAnimator.getAnimatedValue();
//            ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
//            layoutParams.height = val;
//            view.setLayoutParams(layoutParams);
//            listener.successAnim();
//        });
//        anim.setDuration(DURATION);
//        anim.addListener(new AnimatorListenerAdapter() {
//            @Override
//            public void onAnimationEnd(Animator animation) {
//                // At the end of animation, set the height to wrap content
//                // This fix is for long views that are not shown on screen
//                ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
//                layoutParams.height = view.getMeasuredHeight();
//                moving = false;
////                view.getLayoutParams().height = height;
////                view.setLayoutParams(new GridLayoutManager.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,height));
//            }
//        });
//
//        anim.start();
//    }
//
//    interface OnAnimateSuccess{
//        void successAnim();
//    }
//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (moving)
//            return true;
//        else
//            return super.onKeyDown(keyCode, event);
//    }
//}