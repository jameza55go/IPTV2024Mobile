package com.iptv.newiptvmobile.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.iptv.newiptvmobile.R;
import com.iptv.newiptvmobile.listener.OnClickListLiveMain;
import com.iptv.newiptvmobile.listener.OnFocusRvLiveMain;
import com.iptv.newiptvmobile.listener.OnLongClickListLiveMain;
import com.iptv.newiptvmobile.model.Data;
import com.iptv.newiptvmobile.model.SubData;
import com.iptv.newiptvmobile.widgets.ItemAnimator;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ListLiveMainAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    Context context;
    List<Data> list;
    List<Boolean> status_fav = new ArrayList<>();
    OnClickListLiveMain onClickListLiveMain;
    OnLongClickListLiveMain onLongClickListLiveMain;
    OnFocusRvLiveMain onFocusRvLiveMain;

    public ListLiveMainAdapter(Context context, List<Data> list, OnClickListLiveMain onClickListLiveMain, OnFocusRvLiveMain onFocusRvLiveMain,OnLongClickListLiveMain onLongClickListLiveMain) {
        this.context = context;
        this.list = list;
        this.onClickListLiveMain = onClickListLiveMain;
        this.onFocusRvLiveMain = onFocusRvLiveMain;
        this.onLongClickListLiveMain = onLongClickListLiveMain;
        mInflater = (LayoutInflater) context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
        for (int i = 0; i < list.size(); i++) {
            status_fav.add(i, list.get(i).getData().get(0).isFavorite());
        }
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

        convertView = mInflater.inflate(R.layout.item_list_live_main, parent, false);
        ConstraintLayout bt_live = convertView.findViewById(R.id.bt_live);
        ImageView chanel_logo = convertView.findViewById(R.id.chanel_logo);
        ImageView ico_fav = convertView.findViewById(R.id.ico_fav);
        TextView chanel_number = convertView.findViewById(R.id.chanel_number);
        TextView chanel_title = convertView.findViewById(R.id.chanel_title);

        final SubData lists = list.get(position).getData().get(0);

        Picasso.get().load(lists.getLogo()).into(chanel_logo);
        chanel_number.setText(lists.getRemotetv());
        chanel_title.setText(lists.getTitle());

        bt_live.setOnClickListener(v -> onClickListLiveMain.onClickListLiveMain(position));

        bt_live.setOnLongClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage(status_fav.get(position) ?
                            context.getString(R.string.alert_unfav, lists.getTitle()) :
                            context.getString(R.string.alert_fav, lists.getTitle()))
                    .setCancelable(false)
                    .setPositiveButton(R.string.bt_yes, (dialog, id) -> {
                        if (status_fav.get(position)) {
                            ico_fav.setVisibility(View.GONE);
                            status_fav.set(position, false);
                            onLongClickListLiveMain.onLongClickListLiveMain(position,"unfav");
                        } else {
                            ico_fav.setVisibility(View.VISIBLE);
                            status_fav.set(position, true);
                            onLongClickListLiveMain.onLongClickListLiveMain(position,"fav");
                        }
                        notifyDataSetChanged();
                    })
                    .setNegativeButton(R.string.bt_no, (dialog, id) -> dialog.cancel());
            AlertDialog alert = builder.create();
            alert.show();
            return false;
        });

        bt_live.setOnFocusChangeListener((v, hasFocus) -> {
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

        });

        if (status_fav.get(position)) {
            ico_fav.setVisibility(View.VISIBLE);
        } else {
            ico_fav.setVisibility(View.GONE);
        }
        return convertView;
    }

}
