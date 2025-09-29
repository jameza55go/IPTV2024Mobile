package com.iptv.newiptvmobile.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.Resource;
import com.iptv.newiptvmobile.HomeActivity;
import com.iptv.newiptvmobile.R;
import com.iptv.newiptvmobile.listener.OnClickRvHomeCategory;
import com.iptv.newiptvmobile.listener.OnClickRvHomeTool;
import com.iptv.newiptvmobile.model.Data;
import com.iptv.newiptvmobile.model.ToolModel;
import com.iptv.newiptvmobile.widgets.ItemAnimator;
import com.squareup.picasso.Picasso;

import java.util.List;

public class RvHomeCategoryAdapter extends RecyclerView.Adapter<RvHomeCategoryAdapter.MyViewHolder> {

    private Context context;
    private List<Data> list;
    private OnClickRvHomeCategory listener;

    public RvHomeCategoryAdapter(Context context, List<Data> list, OnClickRvHomeCategory listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }


    @NonNull
    @Override
    public RvHomeCategoryAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_rv_home_category,parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RvHomeCategoryAdapter.MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Data lists = list.get(position);
        holder.textView.setText(lists.getTitle());
        Picasso.get().load(lists.getBackground()).placeholder(R.drawable.bg_category).into(holder.bgCategory);
        holder.bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(context, lists.getTitle(), Toast.LENGTH_SHORT).show();
                listener.onClickRvHomeCategory(String.valueOf(position));
            }
        });

        holder.bt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                ItemAnimator animator = new ItemAnimator(holder.bt);
                if (hasFocus) {
                    holder.textView.setTextColor(ContextCompat.getColor(context,R.color.orange_main));
                    animator.startFocusGainAnimation();
                } else {
                    holder.textView.setTextColor(ContextCompat.getColor(context,R.color.white));
                    animator.startFocusLossAnimation();
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        CardView bt;
        TextView textView;
        ImageView bgCategory;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            bt = itemView.findViewById(R.id.bt_select);
            textView = itemView.findViewById(R.id.text);
            bgCategory = itemView.findViewById(R.id.bgCategory);
        }
    }
}
