package com.iptv.newiptvmobile.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.iptv.newiptvmobile.R;
import com.iptv.newiptvmobile.listener.OnClickRvHomeTool;
import com.iptv.newiptvmobile.model.ToolModel;

import java.util.ArrayList;
import java.util.List;

public class RvHomeToolAdapter extends RecyclerView.Adapter<RvHomeToolAdapter.MyViewHolder> {

    Context context;
    List<ToolModel> list = new ArrayList<>();
    OnClickRvHomeTool listener;

    public RvHomeToolAdapter(Context context, List<ToolModel> list, OnClickRvHomeTool listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RvHomeToolAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_rv_home_tool,parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RvHomeToolAdapter.MyViewHolder holder, int position) {
        ToolModel lists = list.get(position);
        holder.icon.setImageResource(lists.getIc_w());
        if (lists.getType().equals("favorite")){
            holder.icon.setPadding(10,10,10,10);
        }
        if (lists.getType().equals("logout")){
            holder.icon.setPadding(7,7,7,7);
        }
        if (lists.getLang().contains("en")){
            holder.title.setText(lists.getTitleEN());
        }else{
            holder.title.setText(lists.getTitleTH());
        }

        holder.button.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

            }
        });

        holder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClickRvHomeTool(lists.getType());
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        LinearLayout button;
        ImageView icon;
        TextView title;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            button = itemView.findViewById(R.id.button);
            icon = itemView.findViewById(R.id.icon);
            title = itemView.findViewById(R.id.title);
        }
    }
}
