package com.iptv.newiptvmobile.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.iptv.newiptvmobile.MovieDetailActivity;
import com.iptv.newiptvmobile.R;
import com.iptv.newiptvmobile.model.Actorlist;

import java.util.List;

public class RvMovieDetailActorAdapter extends RecyclerView.Adapter<RvMovieDetailActorAdapter.MyViewHolder> {

    Context context;
    List<Actorlist> list;
    @SuppressLint("UnsafeOptInUsageError")
    MovieDetailActivity.RvActorClick listener;

    public RvMovieDetailActorAdapter(Context context, List<Actorlist> list, @SuppressLint("UnsafeOptInUsageError") MovieDetailActivity.RvActorClick listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RvMovieDetailActorAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_rv_actor,parent, false);
        return new MyViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RvMovieDetailActorAdapter.MyViewHolder holder, int position) {
        Actorlist lists = list.get(position);

        if (position != 0){
            holder.text.setText(", "+lists.getTitle());
        }else {
            holder.text.setText(" " + lists.getTitle());
        }

        holder.text.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("UnsafeOptInUsageError")
            @Override
            public void onClick(View v) {
                listener.click(lists.getTitle());
            }
        });

        holder.text.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus){
                    holder.text.setTextColor(ContextCompat.getColor(context, R.color.orange_main));
                }else {
                    holder.text.setTextColor(ContextCompat.getColor(context, R.color.white));
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView text;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            text = itemView.findViewById(R.id.text);
        }
    }
}
