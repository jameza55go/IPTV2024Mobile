package com.iptv.newiptvmobile.ui.login;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import com.iptv.newiptvmobile.R;
import com.iptv.newiptvmobile.adapter.MorePagerAdapter;

import ru.tinkoff.scrollingpagerindicator.ScrollingPagerIndicator;

public class MoreActivity extends FragmentActivity {

    RecyclerView rv_more;
    MorePagerAdapter adapter;
    int position = 0;
    MorePageListenerToActivity listener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more);

        rv_more= findViewById(R.id.rv_more);

        rv_more.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false));
        adapter = new MorePagerAdapter(this, new MorePageListener() {
            @Override
            public void morePageListener(String type) {
//                listener.morePageListenerToActivity(type);
                Intent intent=new Intent();
                intent.putExtra("type",type);
                setResult(10,intent);
                finish();
            }
        });
        rv_more.setAdapter(adapter);
        ScrollingPagerIndicator indicator = findViewById(R.id.indicator);
        indicator.setOrientation(LinearLayoutManager.VERTICAL);
        indicator.setDotColor(getResources().getColor(R.color.white));
        indicator.setSelectedDotColor(getResources().getColor(R.color.orange_main));
        indicator.attachToRecyclerView(rv_more);
//        new PagerSnapHelper().attachToRecyclerView(rv_more);

    }



}