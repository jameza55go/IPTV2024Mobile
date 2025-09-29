package com.iptv.newiptvmobile.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.iptv.newiptvmobile.MovieDetailActivity;
import com.iptv.newiptvmobile.R;
import com.iptv.newiptvmobile.listener.OnClickMovieCollection;
import com.iptv.newiptvmobile.listener.OnFocusMovieCollection;
import com.iptv.newiptvmobile.model.Data;
import com.iptv.newiptvmobile.widgets.ItemAnimator;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

public class RvMovieCollectionAdapter extends RecyclerView.Adapter<RvMovieCollectionAdapter.MyViewHolder> {

    Context mContext;
    List<Data> list;
    OnFocusMovieCollection focusListener;
    OnClickMovieCollection clickListener;

    private int focusedPosition;

    public RvMovieCollectionAdapter(Context mContext, List<Data> list, OnFocusMovieCollection focusListener, OnClickMovieCollection clickListener) {
        this.mContext = mContext;
        this.list = list;
        this.focusListener = focusListener;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public RvMovieCollectionAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.item_rv_movie_collection,parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RvMovieCollectionAdapter.MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        final Data lists = list.get(position);

        holder.title_movie.setText(lists.getTitle());


        Picasso.get().load(lists.getLogo()).into(holder.cover_movie, new Callback() {
            @Override
            public void onSuccess() {
                ItemAnimator animator = new ItemAnimator(holder.bt_cv);
                holder.bt.setOnFocusChangeListener((v, hasFocus) -> {
                    if (hasFocus){
                        focusListener.onFocusMovieCollection(position);
                        animator.startFocusGainAnimation();
                    }else{
                        animator.startFocusLossAnimation();
                    }
                    focusedPosition = position;
                });

                if (focusedPosition == position) holder.bt.requestFocus();

                holder.bt.setOnClickListener(v -> {
                    clickListener.onFocusMovieCollection(lists.getId());
                });
            }

            @Override
            public void onError(Exception e) {

            }
        });


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        ConstraintLayout bt;
        CardView bt_cv;
        TextView title_movie;
        RoundedImageView cover_movie;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            bt = itemView.findViewById(R.id.bt);
            bt_cv = itemView.findViewById(R.id.bt_cv);
            title_movie = itemView.findViewById(R.id.title_movie);
            cover_movie = itemView.findViewById(R.id.cover_movie);
        }

    }



}
