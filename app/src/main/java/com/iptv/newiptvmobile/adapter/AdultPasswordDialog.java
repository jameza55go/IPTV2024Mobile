package com.iptv.newiptvmobile.adapter;

import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import com.iptv.newiptvmobile.HomeActivity;
import com.iptv.newiptvmobile.R;


public class AdultPasswordDialog extends Dialog{

    HomeActivity activity;
    LinearLayout bt_confirm_adult_password;
    EditText adult_password;
    TextView text_confirm;
    HomeActivity.ConfirmAdultPassword listener;

    public AdultPasswordDialog(HomeActivity activity, HomeActivity.ConfirmAdultPassword listener) {
        super(activity, android.R.style.Theme_Translucent_NoTitleBar);
        // TODO Auto-generated constructor stub
        this.activity = activity;
        this.listener = listener;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.adult_password_dialog);
        View v = getWindow().getDecorView();
        v.setBackgroundResource(android.R.color.transparent);

        bt_confirm_adult_password = v.findViewById(R.id.bt_confirm_adult_password);
        adult_password = v.findViewById(R.id.adult_password);
        text_confirm = v.findViewById(R.id.text_confirm);

        bt_confirm_adult_password.setOnClickListener(v12 -> {
            if (String.valueOf(adult_password.getText()).equals("fwok")){
                listener.confirmAdultPassword();
                dismiss();
            }else{
                Toast.makeText(activity, "พาสเวิร์ดไม่ถูกต้อง", Toast.LENGTH_SHORT).show();
            }
        });

        bt_confirm_adult_password.setOnFocusChangeListener((v1, hasFocus) -> {
            if (hasFocus){
                text_confirm.setTextColor(ContextCompat.getColor(activity,R.color.black));
            }else{
                text_confirm.setTextColor(ContextCompat.getColor(activity,R.color.white));
            }
        });

        adult_password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (String.valueOf(adult_password.getText()).equals("fwok")){
                    listener.confirmAdultPassword();
                    dismiss();
                }
            }
        });

    }

}