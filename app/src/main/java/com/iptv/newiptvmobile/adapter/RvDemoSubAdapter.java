package com.iptv.newiptvmobile.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.iptv.newiptvmobile.R;
import com.iptv.newiptvmobile.model.SubData;
import com.iptv.newiptvmobile.ui.login.RvDemoSubListener;
import com.iptv.newiptvmobile.widgets.ItemAnimator;
import com.makeramen.roundedimageview.RoundedImageView;
import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.List;

public class RvDemoSubAdapter extends RecyclerView.Adapter<RvDemoSubAdapter.MyViewHolder> {

    List<SubData> list;
    Context context;
    RvDemoSubListener listener;

    public RvDemoSubAdapter(List<SubData> list, Context context, RvDemoSubListener listener) {
        this.list = list;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_rv_demo_data,parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        final SubData lists = list.get(position);

//        holder.img.setImageResource(R.drawable.img_live_popular);

        Picasso.get().load(lists.getLogo()).into(holder.img);

        holder.bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.rvDemoSubListener(position,lists.getTitle(),lists.getId());
            }
        });

        holder.bt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                ItemAnimator animator = new ItemAnimator(holder.img);
                if (hasFocus) {
                    animator.startFocusGainAnimation();
                } else {
                    animator.startFocusLossAnimation();
                }
            }
        });

    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        RoundedImageView img;
        LinearLayout bt;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.img);
            bt = itemView.findViewById(R.id.bt);
        }
    }
}
