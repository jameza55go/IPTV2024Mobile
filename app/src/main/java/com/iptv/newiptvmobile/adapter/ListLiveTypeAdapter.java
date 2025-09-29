package com.iptv.newiptvmobile.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.iptv.newiptvmobile.R;
import com.iptv.newiptvmobile.listener.OnClickListLiveSub;
import com.iptv.newiptvmobile.listener.OnClickListLiveType;
import com.iptv.newiptvmobile.model.Lists;
import com.iptv.newiptvmobile.model.SubData;
import com.iptv.newiptvmobile.widgets.ItemAnimator;

import java.util.List;

public class ListLiveTypeAdapter extends BaseAdapter {

    LayoutInflater mInflater;

    Context context;
    List<Lists> list;
    OnClickListLiveType OnClickListLiveType;

    public ListLiveTypeAdapter(Context context, List<Lists> list, OnClickListLiveType OnClickListLiveType) {
        this.context = context;
        this.list = list;
        this.OnClickListLiveType = OnClickListLiveType;
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
        convertView = mInflater.inflate(R.layout.item_list_live_type, parent,false);
        ConstraintLayout bt_live = convertView.findViewById(R.id.bt_live);
        TextView chanel_title = convertView.findViewById(R.id.chanel_title);

        final Lists lists = list.get(position);

        chanel_title.setText(lists.getTitle());

        bt_live.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OnClickListLiveType.onClickRvLiveSub(lists.getId());
            }
        });

        bt_live.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                ItemAnimator animator = new ItemAnimator(bt_live);
                if (hasFocus){
                    chanel_title.setSelected(true);
                    chanel_title.setTextColor(ContextCompat.getColor(context, R.color.black));
                    animator.startFocusGainAnimation();
                }else{
                    chanel_title.setSelected(false);
                    chanel_title.setTextColor(ContextCompat.getColor(context, R.color.white));
                    animator.startFocusLossAnimation();
                }

            }
        });
        return convertView;
    }

}
