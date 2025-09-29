package com.iptv.newiptvmobile.adapter;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import com.iptv.newiptvmobile.R;
import com.iptv.newiptvmobile.ui.tool.topup.TopUpQRCodeActivity;


public class AlertDialogKaraoke extends Dialog {

    Context context;

    public AlertDialogKaraoke(@NonNull Context context) {
        super(context);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.alert_dialog_karaoke);
        View v = getWindow().getDecorView();
        v.setBackgroundResource(android.R.color.transparent);

    }

    public void remove(){
       dismiss();
    }

}
