package com.iptv.newiptvmobile.adapter;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.iptv.newiptvmobile.R;
import com.iptv.newiptvmobile.model.Data;
import com.iptv.newiptvmobile.ui.liveTV.LiveActivity;
import com.iptv.newiptvmobile.ui.tool.sport.SportTableActivity;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Objects;
import java.util.StringTokenizer;

public class RvSportTableAdapter extends RecyclerView.Adapter<RvSportTableAdapter.MyViewHolder> {

    SportTableActivity activity;
    List<Data> list;

    int lastPosition = -1;

    public RvSportTableAdapter(SportTableActivity activity, List<Data> list) {
        this.activity = (SportTableActivity) activity;
        this.list = list;
    }

    @NonNull
    @Override
    public RvSportTableAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(activity);
        View view = inflater.inflate(R.layout.item_rv_sport_table,parent, false);

        return new MyViewHolder(view);
    }

    @SuppressLint({"CheckResult", "UseCompatLoadingForDrawables"})
    @Override
    public void onBindViewHolder(@NonNull RvSportTableAdapter.MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Data lists = list.get(position);
        holder.setIsRecyclable(false);
        RoundedImageView[] chanel_live = {holder.ch_1,holder.ch_2,holder.ch_3,holder.ch_4,holder.ch_5,holder.ch_6,holder.ch_7,holder.ch_8};
        RoundedImageView[] logo_language = {holder.logo_lang_ch1,holder.logo_lang_ch2,holder.logo_lang_ch3,holder.logo_lang_ch4,holder.logo_lang_ch5,holder.logo_lang_ch6,holder.logo_lang_ch7,holder.logo_lang_ch8};
        ConstraintLayout[] layouts = {holder.layout_ch1,holder.layout_ch2,holder.layout_ch3,holder.layout_ch4,holder.layout_ch5,holder.layout_ch6,holder.layout_ch7,holder.layout_ch8};
        LinearLayout[] bg_lang = {holder.bg_logo_ch1,holder.bg_logo_ch2,holder.bg_logo_ch3,holder.bg_logo_ch4,holder.bg_logo_ch5,holder.bg_logo_ch6,holder.bg_logo_ch7,holder.bg_logo_ch8};
        LinearLayout[] bg_ch = {holder.bg_ch1,holder.bg_ch2,holder.bg_ch3,holder.bg_ch4,holder.bg_ch5,holder.bg_ch6,holder.bg_ch7,holder.bg_ch8};

        StringTokenizer tokens = new StringTokenizer(lists.getDatetime(), " ");
        String date = tokens.nextToken();
        String time = tokens.nextToken();

        Picasso.get().load(lists.getSport_logo()).into(holder.logo_sport);
        Picasso.get().load(lists.getTeam_a()).into(holder.logo_team_a);
        Picasso.get().load(lists.getTeam_b()).into(holder.logo_team_b);
        if (lists.isLive()){
            holder.text_status_live.setVisibility(View.VISIBLE);
            holder.img_status_live.setVisibility(View.GONE);
        }else {
            holder.text_status_live.setVisibility(View.GONE);
            holder.img_status_live.setVisibility(View.VISIBLE);
        }
        holder.name_sport.setText(lists.getName());
        holder.text_date.setText(date);
        holder.text_time.setText(time);
        holder.text_vs.setText(lists.getEvent());

        holder.bt_table.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int countItem = lists.getData().size();

                if (countItem != 0){
                    holder.chanel_layout.setVisibility(View.VISIBLE);
                    activity.RvScrollToPosition(position);
                    holder.ch_1.requestFocus();
                }

                if (lastPosition != getItemViewType(position)){
                    notifyItemChanged(lastPosition);
                    lastPosition = getItemViewType(position);
                }

            }
        });

        if (lastPosition == getItemViewType(position)){
//            holder.chanel_layout.setVisibility(View.GONE);
        }

        holder.bt_table.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus){
//                    holder.chanel_layout.setVisibility(View.GONE);
                }

            }
        });

        for (int i=0;i<lists.getData().size();i++){
            int finalI = i;
            if (Objects.equals(lists.getData().get(i).getType(), "livethai")){
                logo_language[i].setImageResource(R.drawable.sport_th);
            }else {
                logo_language[i].setImageResource(R.drawable.sport_en);
            }
            Picasso.get().load(lists.getData().get(i).getLogo()).resize(150,150).centerCrop().into(chanel_live[i]);
            layouts[i].setVisibility(View.VISIBLE);
            layouts[i].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Bundle bundle = new Bundle();
                        bundle.putString("id", lists.getData().get(finalI).getId());
                        bundle.putString("type", lists.getData().get(finalI).getType());
                        bundle.putString("form", "sporttable");
                        Intent intent = new Intent(activity, LiveActivity.class);
                        intent.putExtras(bundle);
                        activity.startActivity(intent);
                    }
                });

            layouts[i].setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus){
                        bg_lang[finalI].setBackgroundResource(R.drawable.bg_orange_rounded);
                        bg_ch[finalI].setBackgroundResource(R.drawable.bg_orange_corner_10);
                    }else{
                        bg_lang[finalI].setBackgroundResource(R.drawable.bg_white_rounded);
                        bg_ch[finalI].setBackgroundResource(R.drawable.bg_white_coner_10);
                    }

                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView logo_sport,logo_team_a,logo_team_b,img_status_live;
        RoundedImageView ch_1,ch_2,ch_3,ch_4,ch_5,ch_6,ch_7,ch_8;
        RoundedImageView logo_lang_ch1,logo_lang_ch2,logo_lang_ch3,logo_lang_ch4,logo_lang_ch5,logo_lang_ch6,logo_lang_ch7,logo_lang_ch8;
        TextView name_sport,text_date,text_time,text_vs,text_status_live;
        LinearLayout bt_channel,bt_table,chanel_layout;
        ConstraintLayout layout_ch1,layout_ch2,layout_ch3,layout_ch4,layout_ch5,layout_ch6,layout_ch7,layout_ch8;
        LinearLayout bg_logo_ch1,bg_logo_ch2,bg_logo_ch3,bg_logo_ch4,bg_logo_ch5,bg_logo_ch6,bg_logo_ch7,bg_logo_ch8;
        LinearLayout bg_ch1,bg_ch2,bg_ch3,bg_ch4,bg_ch5,bg_ch6,bg_ch7,bg_ch8;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            logo_sport = itemView.findViewById(R.id.logo_sport);
            img_status_live = itemView.findViewById(R.id.img_status_live);
            text_status_live = itemView.findViewById(R.id.text_status_live);
            name_sport = itemView.findViewById(R.id.name_sport);
            logo_team_a = itemView.findViewById(R.id.logo_team_a);
            text_date = itemView.findViewById(R.id.text_date);
            text_time = itemView.findViewById(R.id.text_time);
            text_vs = itemView.findViewById(R.id.text_vs);
            logo_team_b = itemView.findViewById(R.id.logo_team_b);
            bt_channel = itemView.findViewById(R.id.bt_channel);
            bt_table = itemView.findViewById(R.id.bt_table);
            chanel_layout = itemView.findViewById(R.id.chanel_layout);
            ch_1 = itemView.findViewById(R.id.ch_1);
            ch_2 = itemView.findViewById(R.id.ch_2);
            ch_3 = itemView.findViewById(R.id.ch_3);
            ch_4 = itemView.findViewById(R.id.ch_4);
            ch_5 = itemView.findViewById(R.id.ch_5);
            ch_6 = itemView.findViewById(R.id.ch_6);
            ch_7 = itemView.findViewById(R.id.ch_7);
            ch_8 = itemView.findViewById(R.id.ch_8);
            layout_ch1 = itemView.findViewById(R.id.layout_ch1);
            layout_ch2 = itemView.findViewById(R.id.layout_ch2);
            layout_ch3 = itemView.findViewById(R.id.layout_ch3);
            layout_ch4 = itemView.findViewById(R.id.layout_ch4);
            layout_ch5 = itemView.findViewById(R.id.layout_ch5);
            layout_ch6 = itemView.findViewById(R.id.layout_ch6);
            layout_ch7 = itemView.findViewById(R.id.layout_ch7);
            layout_ch8 = itemView.findViewById(R.id.layout_ch8);
            bg_logo_ch1 = itemView.findViewById(R.id.bg_logo_ch1);
            bg_logo_ch2 = itemView.findViewById(R.id.bg_logo_ch2);
            bg_logo_ch3 = itemView.findViewById(R.id.bg_logo_ch3);
            bg_logo_ch4 = itemView.findViewById(R.id.bg_logo_ch4);
            bg_logo_ch5 = itemView.findViewById(R.id.bg_logo_ch5);
            bg_logo_ch6 = itemView.findViewById(R.id.bg_logo_ch6);
            bg_logo_ch7 = itemView.findViewById(R.id.bg_logo_ch7);
            bg_logo_ch8 = itemView.findViewById(R.id.bg_logo_ch8);
            logo_lang_ch1 = itemView.findViewById(R.id.logo_lang_ch1);
            logo_lang_ch2 = itemView.findViewById(R.id.logo_lang_ch2);
            logo_lang_ch3 = itemView.findViewById(R.id.logo_lang_ch3);
            logo_lang_ch4 = itemView.findViewById(R.id.logo_lang_ch4);
            logo_lang_ch5 = itemView.findViewById(R.id.logo_lang_ch5);
            logo_lang_ch6 = itemView.findViewById(R.id.logo_lang_ch6);
            logo_lang_ch7 = itemView.findViewById(R.id.logo_lang_ch7);
            logo_lang_ch8 = itemView.findViewById(R.id.logo_lang_ch8);
            bg_ch1 = itemView.findViewById(R.id.bg_ch1);
            bg_ch2 = itemView.findViewById(R.id.bg_ch2);
            bg_ch3 = itemView.findViewById(R.id.bg_ch3);
            bg_ch4 = itemView.findViewById(R.id.bg_ch4);
            bg_ch5 = itemView.findViewById(R.id.bg_ch5);
            bg_ch6 = itemView.findViewById(R.id.bg_ch6);
            bg_ch7 = itemView.findViewById(R.id.bg_ch7);
            bg_ch8 = itemView.findViewById(R.id.bg_ch8);
        }
    }
}
