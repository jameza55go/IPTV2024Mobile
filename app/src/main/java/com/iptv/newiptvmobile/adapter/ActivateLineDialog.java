package com.iptv.newiptvmobile.adapter;

import static android.content.Context.MODE_PRIVATE;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.iptv.newiptvmobile.R;
import com.iptv.newiptvmobile.ui.tool.setting.SettingActivity;
import com.squareup.picasso.Picasso;

import java.nio.charset.StandardCharsets;
import java.util.zip.CRC32;


public class ActivateLineDialog extends Dialog{

    SettingActivity activity;
    LinearLayout bt_confirm_activate_code;
    TextView activate_code,activate_code2,linetexts;
    TextView text_confirm;
    SettingActivity.ConfirmActivateLine listener;
    SharedPreferences db;
    String dbusername,dbuid,lineurl,linename;
    ImageView line;

    public ActivateLineDialog(SettingActivity activity, SettingActivity.ConfirmActivateLine listener) {
        super(activity, android.R.style.Theme_Translucent_NoTitleBar);
        // TODO Auto-generated constructor stub
        this.activity = activity;
        this.listener = listener;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activate_line_dialog);
        db = activity.getApplicationContext().getSharedPreferences("iptv", MODE_PRIVATE);
        View v = getWindow().getDecorView();
        v.setBackgroundResource(android.R.color.transparent);
        dbusername = db.getString("user", "");
        dbuid = db.getString("uid", "");
        linename = db.getString("linename", "@fwiptvuhd");
        lineurl = db.getString("lineid", "https://dev.inwmanga.com/tv/images/line.png");
        CRC32 crc = new CRC32();
        crc.update(dbuid.getBytes(StandardCharsets.UTF_8));
        long crcValue = crc.getValue();
        String crcHexString = Long.toHexString(crcValue);
        bt_confirm_activate_code = v.findViewById(R.id.bt_confirm_activate_code);
        activate_code = v.findViewById(R.id.activate_code);
        activate_code2 = v.findViewById(R.id.activate_code2);
        text_confirm = v.findViewById(R.id.text_confirm);
        line = v.findViewById(R.id.lineadd);
        linetexts = v.findViewById(R.id.linetext);
        activate_code.setText(dbusername);
        activate_code2.setText(crcHexString);
        //Picasso.get().load(lineurl).into(line);
        linetexts.setText(activity.getString(R.string.lineadd,linename));
        bt_confirm_activate_code.setOnClickListener(v12 -> {
//            listener.confirmActivateLine(String.valueOf(activate_code.getText()));
            dismiss();
        });

        bt_confirm_activate_code.setOnFocusChangeListener((v1, hasFocus) -> {
            if (hasFocus){
                text_confirm.setTextColor(ContextCompat.getColor(activity,R.color.black));
            }else{
                text_confirm.setTextColor(ContextCompat.getColor(activity,R.color.white));
            }
        });

    }

}