package com.iptv.newiptvmobile.helper;

import android.content.Context;
import android.content.Intent;

import com.iptv.newiptvmobile.HomeActivity;

public class HelperApp {
    Context context;

    public HelperApp(Context context) {
        this.context = context;
    }

    public void intentHome(){
        Intent intent = new Intent(context, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }
}
