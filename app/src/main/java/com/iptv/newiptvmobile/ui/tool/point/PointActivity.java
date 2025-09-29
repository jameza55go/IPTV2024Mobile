package com.iptv.newiptvmobile.ui.tool.point;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.iptv.newiptvmobile.Application;
import com.iptv.newiptvmobile.R;
import com.iptv.newiptvmobile.helper.HelperApp;
import com.iptv.newiptvmobile.ui.tool.topup.PaymentActivity;
import com.squareup.picasso.Picasso;

public class PointActivity extends AppCompatActivity {

    FrameLayout frame_layout;
    TextView text_menu_exchange_point,text_menu_gift_code,text_menu_detail,text_menu_point_history;
    View view_menu_exchange_point,view_menu_gift_code,view_menu_detail,view_menu_point_history;
    LinearLayout layout_progress;
    ImageView logo;
    SharedPreferences db;

    void ClearLine(){
        view_menu_exchange_point.setVisibility(View.GONE);
        view_menu_gift_code.setVisibility(View.GONE);
        view_menu_detail.setVisibility(View.GONE);
        view_menu_point_history.setVisibility(View.GONE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Application.getInstance().initAppLanguage(this);
        setContentView(R.layout.activity_point);
        db = getApplicationContext().getSharedPreferences("iptv", MODE_PRIVATE);
        frame_layout = findViewById(R.id.frame_layout);
        text_menu_exchange_point = findViewById(R.id.text_menu_exchange_point);
        view_menu_exchange_point = findViewById(R.id.view_menu_exchange_point);
        text_menu_gift_code = findViewById(R.id.text_menu_gift_code);
        view_menu_gift_code = findViewById(R.id.view_menu_gift_code);
        text_menu_detail = findViewById(R.id.text_menu_detail);
        view_menu_detail = findViewById(R.id.view_menu_detail);
        layout_progress = findViewById(R.id.layout_progress);
        text_menu_point_history = findViewById(R.id.text_menu_point_history);
        view_menu_point_history = findViewById(R.id.view_menu_point_history);
        logo = findViewById(R.id.logo);
        Picasso.get().load(db.getString("brandlogo", "")).into(logo);
        HelperApp helperApp = new HelperApp(this);
        logo.setOnClickListener(v -> {
            helperApp.intentHome();
        });

        text_menu_detail.setOnClickListener(v -> {
            ClearLine();
            view_menu_detail.setVisibility(View.VISIBLE);
            loadFragment(new PointMainFragment(),"exchange points");
        });

        text_menu_detail.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus){
                text_menu_detail.setTextColor(getResources().getColor(R.color.orange_main));
            }else{
                text_menu_detail.setTextColor(getResources().getColor(R.color.white));
            }
        });

        text_menu_exchange_point.setOnClickListener(v -> {
            ClearLine();
            view_menu_exchange_point.setVisibility(View.VISIBLE);
            loadFragment(new ExchangePointFragment(),"exchange points");
        });

        text_menu_exchange_point.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus){
                text_menu_exchange_point.setTextColor(getResources().getColor(R.color.orange_main));
            }else{
                text_menu_exchange_point.setTextColor(getResources().getColor(R.color.white));
            }
        });

        text_menu_gift_code.setOnClickListener(v -> {
            ClearLine();
            view_menu_gift_code.setVisibility(View.VISIBLE);
            loadFragment(new GiftCodeFragment(),"exchange points");
        });

        text_menu_gift_code.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus){
                text_menu_gift_code.setTextColor(getResources().getColor(R.color.orange_main));
            }else{
                text_menu_gift_code.setTextColor(getResources().getColor(R.color.white));
            }
        });

        text_menu_point_history.setOnClickListener(v -> {
            ClearLine();
            view_menu_point_history.setVisibility(View.VISIBLE);
            Bundle bundle = new Bundle();
            Intent intent = new Intent(PointActivity.this, PaymentActivity.class);
            bundle.putString("type", "pointhistory");
            intent.putExtras(bundle);
            startActivity(intent);
        });

        text_menu_point_history.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus){
                text_menu_point_history.setTextColor(getResources().getColor(R.color.orange_main));
            }else{
                text_menu_point_history.setTextColor(getResources().getColor(R.color.white));
            }
        });

        loadFragment(new PointMainFragment(),"main points");
    }

    private void loadFragment(Fragment fragment,String tag) {
        animateFragment();
        androidx.fragment.app.FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame_layout, fragment)
                .commit();

        if (getSupportFragmentManager().findFragmentById(R.id.frame_layout) != null) {
            getSupportFragmentManager().popBackStack();
        }
    }

    private void animateFragment() {
        frame_layout.animate().alpha(0.0f)
                .setDuration(0)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        frame_layout.setVisibility(View.VISIBLE);
                        frame_layout.animate()
                                .alpha(1.0f)
                                .setDuration(1000);
                    }
                });
    }

    public interface OnClickMenuPoint {
        void onClickMenuPoint(String type);
    }

    void ShowProgress(){
        layout_progress.setVisibility(View.VISIBLE);
    }

    void HideProgress(){
        layout_progress.setVisibility(View.GONE);
    }
}