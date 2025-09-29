package com.iptv.newiptvmobile.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.iptv.newiptvmobile.R;
import com.iptv.newiptvmobile.listener.OnClickMenuSportTable;
import com.iptv.newiptvmobile.model.Lists;

import java.util.List;

public class RvMenuSportTableAdapter extends RecyclerView.Adapter<RvMenuSportTableAdapter.MyViewHolder> {
    Context context;
    List<Lists> list;
    OnClickMenuSportTable listener;

    public RvMenuSportTableAdapter(Context context, List<Lists> list, OnClickMenuSportTable listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RvMenuSportTableAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_rv_sport_menu,parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RvMenuSportTableAdapter.MyViewHolder holder, int position) {
        Lists lists = list.get(position);
        holder.textView.setText(lists.getTitle());

        holder.textView.setOnClickListener(v -> listener.OnClick(lists.getId(),lists.getTitle()));

        holder.textView.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus){
                holder.textView.setBackgroundResource(R.drawable.bg_orange_shadow);
            }else{
                holder.textView.setBackgroundColor(context.getResources().getColor(R.color.white));
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.textView);
        }
    }
}
