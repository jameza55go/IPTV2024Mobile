package com.iptv.newiptvmobile.ui.tool.topup;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.iptv.newiptvmobile.Application;
import com.iptv.newiptvmobile.HomeActivity;
import com.iptv.newiptvmobile.R;
import com.iptv.newiptvmobile.helper.HelperApp;
import com.squareup.picasso.Picasso;

public class SuccessActivity extends AppCompatActivity {

    LinearLayout bt_back_to_home;
    TextView type_success;
    SharedPreferences db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Application.getInstance().initAppLanguage(this);
        setContentView(R.layout.activity_success);
        db = getApplicationContext().getSharedPreferences("iptv", MODE_PRIVATE);
        bt_back_to_home = findViewById(R.id.bt_back_to_home);
        type_success = findViewById(R.id.type_success);
        ImageView logo = findViewById(R.id.logo);
        Picasso.get().load(db.getString("brandlogo", "")).into(logo);
        HelperApp helperApp = new HelperApp(this);
        logo.setOnClickListener(v -> {
            helperApp.intentHome();
            finish();
        });
        bt_back_to_home.setOnClickListener(v -> {
            helperApp.intentHome();
            finish();
        });
    }
}