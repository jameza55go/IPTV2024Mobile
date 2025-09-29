package com.iptv.newiptvmobile.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.iptv.newiptvmobile.R;
import com.iptv.newiptvmobile.ui.login.MorePageListener;

public class MorePagerAdapter extends RecyclerView.Adapter<MorePagerAdapter.MyViewHolder> {

    Context context;
    MorePagerPage3Adapter adapter;
    MorePageListener listener;

    public MorePagerAdapter(Context context, MorePageListener listener) {
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MorePagerAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = null;
        if (viewType == 0){
            view = inflater.inflate(R.layout.fragment_page1,parent, false);
        }else if (viewType == 1){
            view = inflater.inflate(R.layout.fragment_page2,parent, false);
        }else if (viewType == 2){
            view = inflater.inflate(R.layout.fragment_page3,parent, false);
        }else if (viewType == 3){
            view = inflater.inflate(R.layout.fragment_page4,parent, false);
        }

        assert view != null;
        return new MyViewHolder(view, viewType);
    }

    @Override
    public int getItemViewType(int position) {
        // Just as an example, return 0 or 2 depending on position
        // Note that unlike in ListView adapters, types don't have to be contiguous
        return position;
    }

    @Override
    public void onBindViewHolder(@NonNull MorePagerAdapter.MyViewHolder holder, @SuppressLint("RecyclerView") int position) {

        if (holder.getItemViewType() == 0){
            holder.bt_register.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.morePageListener("register");
                }
            });

            holder.bt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.bt_register.requestFocus();
                }
            });
        }else if (holder.getItemViewType() == 1){
            holder.image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context, String.valueOf(position), Toast.LENGTH_SHORT).show();
                }
            });
        }else if (holder.getItemViewType() == 2){

            holder.bt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.bt_register.requestFocus();
                }
            });

//            holder.rv_more_page3.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL,false));
//            adapter = new MorePagerPage3Adapter(context);
//            holder.rv_more_page3.setAdapter(adapter);

        }else if (holder.getItemViewType() == 3){
            holder.bt_register.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.morePageListener("register");
                }
            });

            holder.bt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.bt_register.requestFocus();
                }
            });
        }


    }

    public int getUniqueId(){
        return (int) SystemClock.currentThreadTimeMillis();
    }

    @Override
    public int getItemCount() {
        return 4;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        LinearLayout bt_register;
        FrameLayout bt;
        RecyclerView rv_more_page3;
        ImageView image;
        public MyViewHolder(@NonNull View itemView, int viewType) {
            super(itemView);
            if (viewType == 0){
                bt_register = itemView.findViewById(R.id.bt_register);
                bt = itemView.findViewById(R.id.bt);
            }else if (viewType == 1){
                image = itemView.findViewById(R.id.image);
            }else if (viewType == 2){
//                rv_more_page3 = itemView.findViewById(R.id.rv_more_page3);
                bt = itemView.findViewById(R.id.bt);
            }else if (viewType == 3){
                bt_register = itemView.findViewById(R.id.bt_register);
                bt = itemView.findViewById(R.id.bt);
            }




        }
    }
}
