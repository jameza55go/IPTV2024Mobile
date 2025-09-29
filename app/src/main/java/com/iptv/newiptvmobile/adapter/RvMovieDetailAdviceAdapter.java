package com.iptv.newiptvmobile.adapter;

import android.annotation.SuppressLint;
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

import com.google.gson.Gson;
import com.iptv.newiptvmobile.MovieDetailActivity;
import com.iptv.newiptvmobile.R;
import com.iptv.newiptvmobile.model.Lists;
import com.iptv.newiptvmobile.widgets.ItemAnimator;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

public class RvMovieDetailAdviceAdapter extends RecyclerView.Adapter<RvMovieDetailAdviceAdapter.MyViewHolder> {

    @SuppressLint("UnsafeOptInUsageError")
    MovieDetailActivity mContext;
    List<Lists> list;
    String type;

    public RvMovieDetailAdviceAdapter(@SuppressLint("UnsafeOptInUsageError") MovieDetailActivity context, List<Lists> list, String type) {
        this.mContext = context;
        this.list = list;
        this.type = type;
    }

    @NonNull
    @Override
    public RvMovieDetailAdviceAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.item_rv_detail_advice,parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RvMovieDetailAdviceAdapter.MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        final Lists lists = list.get(position);

        holder.title_movie.setText(lists.getTitle());

        Picasso.get().load(lists.getLogo()).into(holder.cover_movie, new Callback() {
            @Override
            public void onSuccess() {
                ItemAnimator animator = new ItemAnimator(holder.bt_cv);
                holder.bt.setOnFocusChangeListener((v, hasFocus) -> {
                    if (hasFocus){
                        holder.title_movie.setTextColor(ContextCompat.getColor(mContext,R.color.white));
                        holder.title_movie.setVisibility(View.VISIBLE);
                        animator.startFocusGainAnimation();
                    }else{
                        holder.title_movie.setTextColor(ContextCompat.getColor(mContext,R.color.white));
                        holder.title_movie.setVisibility(View.INVISIBLE);
                        animator.startFocusLossAnimation();
                    }

                });

                holder.bt.setOnClickListener(v -> {
                    Gson gson = new Gson();
                    String actorList = gson.toJson(lists.getActorlist());
                    Bundle bundle = new Bundle();
                    bundle.putString("type", type);
                    bundle.putString("wallpaper", lists.getWallpaper());
                    bundle.putString("title", lists.getTitle());
                    bundle.putString("actor", lists.getActor());
                    bundle.putString("actor_list", actorList);
                    bundle.putString("writer", lists.getWriter());
                    bundle.putString("year", lists.getYear());
                    bundle.putString("country", lists.getCountry());
                    bundle.putString("genre", lists.getGenre());
                    bundle.putString("rating", lists.getRating());
                    bundle.putString("runtime", lists.getRuntime());
                    bundle.putString("released", lists.getReleased());
                    bundle.putString("detail", lists.getDetail());
                    bundle.putString("id", lists.getId());
                    Intent intent = new Intent(mContext, MovieDetailActivity.class);
                    intent.putExtras(bundle);
                    mContext.finish();
                    mContext.startActivity(intent);


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

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
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
