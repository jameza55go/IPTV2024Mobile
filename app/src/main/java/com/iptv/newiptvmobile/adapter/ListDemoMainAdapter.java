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
import com.iptv.newiptvmobile.listener.OnClickListLiveMain;
import com.iptv.newiptvmobile.listener.OnFocusRvLiveMain;
import com.iptv.newiptvmobile.model.Data;
import com.iptv.newiptvmobile.model.SubData;
import com.iptv.newiptvmobile.widgets.ItemAnimator;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ListDemoMainAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private ViewHolder mViewHolder;

    Context context;
    List<Data> mPosts;
    Data mPost;
    OnClickListLiveMain onClickListLiveMain;
    OnFocusRvLiveMain onFocusRvLiveMain;

    public ListDemoMainAdapter(Context context, List<Data> list, OnClickListLiveMain onClickListLiveMain, OnFocusRvLiveMain onFocusRvLiveMain) {
        this.context = context;
        this.mPosts = list;
        this.onClickListLiveMain = onClickListLiveMain;
        this.onFocusRvLiveMain = onFocusRvLiveMain;
        mInflater = (LayoutInflater) context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mPosts.size();
    }

    @Override
    public Object getItem(int position) {
        return mPosts.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("ViewHolder")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        convertView = mInflater.inflate(R.layout.item_list_live_main, parent, false);
        CardView card_live = convertView.findViewById(R.id.card_live);
        ConstraintLayout bt_live = convertView.findViewById(R.id.bt_live);
        ImageView chanel_logo = convertView.findViewById(R.id.chanel_logo);
        TextView chanel_number = convertView.findViewById(R.id.chanel_number);
        TextView chanel_title = convertView.findViewById(R.id.chanel_title);
        Picasso.get().load(mPosts.get(position).getLogo()).into(chanel_logo);
        chanel_number.setText(mPosts.get(position).getRemotetv());
        chanel_title.setText(mPosts.get(position).getTitle());

        bt_live.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickListLiveMain.onClickListLiveMain(position);
            }
        });

        bt_live.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                ItemAnimator animator = new ItemAnimator(bt_live);
                if (hasFocus) {
                    chanel_title.setSelected(true);
                    chanel_number.setTextColor(ContextCompat.getColor(context, R.color.black));
                    chanel_title.setTextColor(ContextCompat.getColor(context, R.color.black));
                    animator.startFocusGainAnimation();
                } else {
                    chanel_title.setSelected(false);
                    chanel_number.setTextColor(ContextCompat.getColor(context, R.color.white));
                    chanel_title.setTextColor(ContextCompat.getColor(context, R.color.white));
                    animator.startFocusLossAnimation();
                }
                notifyDataSetChanged();

            }
        });

        return convertView;
    }

    private static class ViewHolder {
        CardView card_live;
        ConstraintLayout bt_live;
        ImageView chanel_logo;
        TextView chanel_number;
        TextView chanel_title;
    }
}
