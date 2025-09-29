package com.iptv.newiptvmobile.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.iptv.newiptvmobile.R;


public class MorePagerPage3Adapter extends RecyclerView.Adapter<MorePagerPage3Adapter.MyViewHolder> {

    Context context;

    public MorePagerPage3Adapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_rv_more_page3,parent, false);

        return new MyViewHolder(view, viewType);
    }

    @Override
    public int getItemViewType(int position) {
        // Just as an example, return 0 or 2 depending on position
        // Note that unlike in ListView adapters, types don't have to be contiguous
        return position;
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {

        if (position == 0){
            holder.image.setImageDrawable(context.getResources().getDrawable(R.drawable.promtpay));
        }else if (position == 1){
            holder.image.setImageDrawable(context.getResources().getDrawable(R.drawable.truemoney));
        }

        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return 2;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        public MyViewHolder(@NonNull View itemView, int viewType) {
            super(itemView);
                image = itemView.findViewById(R.id.image);
        }
    }
}

