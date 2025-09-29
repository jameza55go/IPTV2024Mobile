package com.iptv.newiptvmobile.adapter;

import static android.app.PendingIntent.getActivity;

import android.app.Activity;
import android.app.Dialog;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;

import com.iptv.newiptvmobile.R;
import com.iptv.newiptvmobile.listener.OnClickNoAlertDialog;
import com.iptv.newiptvmobile.listener.OnClickYesAlertDialog;
import com.iptv.newiptvmobile.ui.tool.topup.TopUpQRCodeActivity;


public class AlertDialogPaymentSuccess extends Dialog {
    LinearLayout bt_back_to_home;
    TopUpQRCodeActivity activity;
    Boolean status_success;
    TopUpQRCodeActivity.OnDialogClick listener;


    public AlertDialogPaymentSuccess(TopUpQRCodeActivity activity,Boolean status_success, TopUpQRCodeActivity.OnDialogClick listener) {
        super(activity,android.R.style.Theme_Translucent_NoTitleBar);
        this.activity = activity;
        this.status_success = status_success;
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (status_success){
            setContentView(R.layout.alert_dialog_payment_success);
        }else {
            setContentView(R.layout.alert_dialog_payment_failed);
        }

        bt_back_to_home = findViewById(R.id.bt_back_to_home);

        bt_back_to_home.setOnClickListener(v -> listener.onClick());

    }

    public void remove(){
       dismiss();
    }

}
