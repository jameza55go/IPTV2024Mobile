package com.iptv.newiptvmobile.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;


import com.iptv.newiptvmobile.R;
import com.iptv.newiptvmobile.model.Data;
import com.iptv.newiptvmobile.ui.login.RvDemoMenuListener;

import java.util.List;

public class RvDemoMenuAdapter extends RecyclerView.Adapter<RvDemoMenuAdapter.MyViewHolder> {

    List<Data> list;
    Context context;
    RvDemoMenuListener listener;
    int selected_position = 0;

    public RvDemoMenuAdapter(List<Data> list, Context context, RvDemoMenuListener listener) {
        this.list = list;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_demo_text_menu,parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        final Data lists = list.get(position);

        holder.textView.setText(lists.getTitle());
        holder.itemView.setSelected(selected_position == position);

        holder.textView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus){
                    holder.textView.setTextColor(ContextCompat.getColor(context, R.color.orange_main));
                }else{
                    holder.textView.setTextColor(ContextCompat.getColor(context, R.color.white));
                }
            }
        });

        holder.textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                listener.rvDemoMenuListener(position,lists.getTitle(),lists.getId());
                notifyItemChanged(selected_position);
                selected_position = position;
                notifyItemChanged(selected_position);
            }
        });

        // Updating old as well as new positions


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.textView6);
        }
    }
}
