package com.iptv.newiptvmobile.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.iptv.newiptvmobile.R;
import com.iptv.newiptvmobile.listener.OnClickListLiveSub;
import com.iptv.newiptvmobile.model.SubData;
import com.iptv.newiptvmobile.widgets.ItemAnimator;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ListLiveSubAdapter extends BaseAdapter {

    LayoutInflater mInflater;

    Context context;
    List<SubData> list;
    OnClickListLiveSub OnClickListLiveSub;

    public ListLiveSubAdapter(Context context, List<SubData> list, OnClickListLiveSub OnClickListLiveSub) {
        this.context = context;
        this.list = list;
        this.OnClickListLiveSub = OnClickListLiveSub;
        mInflater = (LayoutInflater) context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("ViewHolder")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = mInflater.inflate(R.layout.item_list_live_main, parent,false);
        CardView card_live = convertView.findViewById(R.id.card_live);
        ConstraintLayout bt_live = convertView.findViewById(R.id.bt_live);
        ImageView chanel_logo = convertView.findViewById(R.id.chanel_logo);
        TextView chanel_number = convertView.findViewById(R.id.chanel_number);
        TextView chanel_title = convertView.findViewById(R.id.chanel_title);

        final SubData lists = list.get(position);

        Picasso.get().load(lists.getLogo()).into(chanel_logo);
        chanel_number.setText(lists.getRemotetv());
        chanel_title.setText(lists.getTitle());

        bt_live.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OnClickListLiveSub.onClickListLiveSub(position,lists.getId(),lists.getTitle());
            }
        });

        bt_live.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                ItemAnimator animator = new ItemAnimator(bt_live);
                if (hasFocus){
                    chanel_title.setSelected(true);
                    chanel_number.setTextColor(ContextCompat.getColor(context, R.color.black));
                    chanel_title.setTextColor(ContextCompat.getColor(context, R.color.black));
                    animator.startFocusGainAnimation();
                }else{
                    chanel_title.setSelected(false);
                    chanel_number.setTextColor(ContextCompat.getColor(context, R.color.white));
                    chanel_title.setTextColor(ContextCompat.getColor(context, R.color.white));
                    animator.startFocusLossAnimation();
                }

            }
        });
        return convertView;
    }

}
