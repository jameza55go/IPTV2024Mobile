package com.iptv.newiptvmobile.adapter;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

import com.iptv.newiptvmobile.CategoryActivity;
import com.iptv.newiptvmobile.R;
import com.iptv.newiptvmobile.listener.OnClickNoAlertDialog;
import com.iptv.newiptvmobile.listener.OnClickRvListCategory;
import com.iptv.newiptvmobile.listener.OnClickYesAlertDialog;
import com.iptv.newiptvmobile.model.SubData;

import java.util.List;
import java.util.Objects;


public class AlertDialogMain extends Dialog {

    Context context;
    String text;
    OnClickYesAlertDialog yes_listener;
    OnClickNoAlertDialog no_listener;
    LinearLayout bt_yes,bt_no;
    TextView text_alert,text_yes,text_no;
    Dialog dialog = AlertDialogMain.this;

    public AlertDialogMain(Context context,String text) {
        super(context,android.R.style.Theme_Translucent_NoTitleBar);
        this.context = context;
        this.text = text;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.alert_dialog_main);

        text_alert = findViewById(R.id.text_alert);
        bt_yes = findViewById(R.id.bt_yes);
        bt_no = findViewById(R.id.bt_no);
        text_yes = findViewById(R.id.text_yes);
        text_no = findViewById(R.id.text_no);

        text_alert.setText(text);

    }

    public void setBt_yes(@StringRes int textId, OnClickYesAlertDialog yes_listener) {
        this.yes_listener = yes_listener;
        bt_yes.setVisibility(View.VISIBLE);
        text_yes.setText(textId);
        bt_yes.setOnClickListener(v -> {
            yes_listener.Click();
            this.dismiss();
        });
    }

    public void setBt_no(@StringRes int textId,OnClickNoAlertDialog no_listener){
        this.no_listener = no_listener;
        bt_no.setVisibility(View.VISIBLE);
        text_no.setText(textId);
        bt_no.setOnClickListener(v -> {
            no_listener.Click();

        });
    }
    public void remove(){
       dismiss();
    }

}
