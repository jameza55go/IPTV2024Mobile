package com.iptv.newiptvmobile.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.iptv.newiptvmobile.R;
import com.iptv.newiptvmobile.listener.RvMovieDetailFocusListener;
import com.iptv.newiptvmobile.listener.RvMovieDetailListener;
import com.iptv.newiptvmobile.model.Data;
import com.iptv.newiptvmobile.widgets.ItemAnimator;

import java.util.List;

public class RvMovieDetailAdapter extends RecyclerView.Adapter<RvMovieDetailAdapter.MyViewHolder> {

    List<Data> list;
    Context context;
    RvMovieDetailListener listener;
    RvMovieDetailFocusListener focus_listener;

    public RvMovieDetailAdapter(List<Data> list, Context context,RvMovieDetailListener listener,RvMovieDetailFocusListener focus_listener) {
        this.list = list;
        this.context = context;
        this.listener = listener;
        this.focus_listener = focus_listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_rv_movie_detail,parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        final Data lists = list.get(position);
        holder.setIsRecyclable(false);
        if(lists.isWatcheds()) {
            holder.textView.setText(lists.getTitle());
            holder.text_watched.setVisibility(View.VISIBLE);
        }else{
            holder.textView.setText(lists.getTitle());
        }
        holder.bt.setOnFocusChangeListener((v, hasFocus) -> {
            ItemAnimator animator = new ItemAnimator(holder.textView);
            focus_listener.rvMovieDetailListener(hasFocus);

            if (hasFocus){
                holder.textView.setTextColor(ContextCompat.getColor(context, R.color.black));
                animator.startFocusGainAnimation();
            }else{
                holder.textView.setTextColor(ContextCompat.getColor(context, R.color.white));
                animator.startFocusLossAnimation();
            }
        });

        holder.bt.setOnClickListener(v -> {
            holder.text_watched.setVisibility(View.VISIBLE);
            listener.rvMovieDetailListener(position,lists.getTitle(),lists.getId());
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView textView,text_watched;
        LinearLayout bt;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.text);
            bt = itemView.findViewById(R.id.bt);
            text_watched = itemView.findViewById(R.id.text_watched);
        }
    }
}
