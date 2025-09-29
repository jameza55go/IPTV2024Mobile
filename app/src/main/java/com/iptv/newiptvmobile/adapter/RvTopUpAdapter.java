package com.iptv.newiptvmobile.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.iptv.newiptvmobile.R;
import com.iptv.newiptvmobile.listener.OnClickRvTopUp;
import com.iptv.newiptvmobile.model.TopUpModel;
import com.iptv.newiptvmobile.widgets.ItemAnimator;

import java.util.List;

public class RvTopUpAdapter extends RecyclerView.Adapter<RvTopUpAdapter.MyViewHolder> {

    Context context;
    List<TopUpModel> list;
    OnClickRvTopUp listener;

    public RvTopUpAdapter(Context context, List<TopUpModel> list, OnClickRvTopUp listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RvTopUpAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_rv_top_up,parent, false);
        return new MyViewHolder(view);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onBindViewHolder(@NonNull RvTopUpAdapter.MyViewHolder holder, int position) {
        TopUpModel lists = list.get(position);

        holder.text_days.setText(lists.getTitle());

        holder.bt_top_up.setOnClickListener(v -> listener.onClickRvTopUp(lists.getId()));
        if (position == 2){
            holder.text_update.setTextColor(context.getResources().getColor(R.color.black));
            holder.text_update.setText(R.string.hot);
            holder.text_update.setTypeface(holder.text_update.getTypeface(), Typeface.BOLD);
            holder.text_update.setBackgroundColor(context.getResources().getColor(R.color.gold));
            holder.text_update.setVisibility(View.VISIBLE);
        }

        if (position == 5){
            holder.text_update.setText(R.string.value);
            holder.text_update.setTypeface(holder.text_update.getTypeface(), Typeface.BOLD);
            holder.text_update.setBackgroundColor(context.getResources().getColor(R.color.red));
            holder.text_update.setVisibility(View.VISIBLE);
        }

        holder.bt_top_up.setOnFocusChangeListener((v, hasFocus) -> {
            ItemAnimator animator = new ItemAnimator(holder.layout_top_up);
            if (hasFocus){
                holder.layout_top_up.setBackground(context.getResources().getDrawable(R.drawable.bg_orange_corner_15));
                holder.text_days.setTextColor(ContextCompat.getColor(context,R.color.black));
                animator.startFocusGainAnimation();
            }else{
                holder.layout_top_up.setBackground(context.getResources().getDrawable(R.drawable.bg_black_corner_15));
                holder.text_days.setTextColor(ContextCompat.getColor(context,R.color.white));
                animator.startFocusLossAnimation();
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        LinearLayout layout_top_up;
        ConstraintLayout bt_top_up;
        TextView text_days,text_update;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            bt_top_up = itemView.findViewById(R.id.bt_top_up);
            layout_top_up = itemView.findViewById(R.id.layout_top_up);
            text_days = itemView.findViewById(R.id.text_days);
            text_update = itemView.findViewById(R.id.text_update);
        }
    }
}
