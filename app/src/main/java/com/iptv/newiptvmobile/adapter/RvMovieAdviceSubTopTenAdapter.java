package com.iptv.newiptvmobile.adapter;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.iptv.newiptvmobile.HomeActivity;
import com.iptv.newiptvmobile.MovieDetailActivity;
import com.iptv.newiptvmobile.R;
import com.iptv.newiptvmobile.model.SubData;
import com.iptv.newiptvmobile.widgets.ItemAnimator;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

public class RvMovieAdviceSubTopTenAdapter extends RecyclerView.Adapter<RvMovieAdviceSubTopTenAdapter.MyViewHolder> {
    HomeActivity mContext;
    List<SubData> list;
    int rvPosition;
    String type;
    private final int[] pic_number = {
            R.drawable.number_1,
            R.drawable.number_2,
            R.drawable.number_3,
            R.drawable.number_4,
            R.drawable.number_5,
            R.drawable.number_6,
            R.drawable.number_7,
            R.drawable.number_8,
            R.drawable.number_9,
            R.drawable.number_10,
    };
    private final String[] movie_type = {
            null,
            "ZOOM",
            "Ultra-HD"
    };
    public RvMovieAdviceSubTopTenAdapter(HomeActivity mContext, List<SubData> list, int rvPosition) {
        this.mContext = mContext;
        this.list = list;
        this.rvPosition = rvPosition;
    }
    @NonNull
    @Override
    public RvMovieAdviceSubTopTenAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.item_rv_top_ten, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RvMovieAdviceSubTopTenAdapter.MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        SubData lists = list.get(position);
        holder.setIsRecyclable(false);
        holder.title_movie.setText(lists.getTitle());
        if (position <= 10){
            holder.img_number.setImageResource(pic_number[position]);
            holder.img_number.setVisibility(View.VISIBLE);
        }else{
            holder.img_number.setVisibility(View.GONE);
        }

         if (lists.getLabel() > 0 && lists.getLabel() <= 2) {
             holder.text_type.setVisibility(View.VISIBLE);
            holder.text_type.setText(movie_type[lists.getLabel()]);
        }else {
             holder.text_type.setVisibility(View.GONE);
        }

        if (lists.isUpdate()) {
            holder.text_update.setVisibility(View.VISIBLE);
            holder.setIsRecyclable(false);
        }
        Picasso.get().load(lists.getLogo()).into(holder.cover_movie, new Callback() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public void onSuccess() {
                ItemAnimator animator = new ItemAnimator(holder.bt);
                holder.bt.setOnFocusChangeListener((v, hasFocus) -> {
                    if (hasFocus) {
                        holder.title_movie.setTextColor(ContextCompat.getColor(mContext, R.color.black));
                        holder.title_movie.setBackgroundResource(R.color.orange_main);
                        animator.startFocusGainAnimation();
                        mContext.SendDataToHomeActivity(rvPosition);
                        if (position == 0){
                            mContext.focusToFirstItem();
                        } else if (position == list.size()-1) {
                            mContext.focusToLastItem();
                        }else {
                            mContext.enableFocus();
                        }
                    } else {
                        holder.title_movie.setTextColor(ContextCompat.getColor(mContext, R.color.white));
                        holder.title_movie.setBackgroundResource(R.color.black_transparent2);
                        animator.startFocusLossAnimation();
                        mContext.enableFocus();
                    }

                });

                holder.bt.setTag(1000+position);

                holder.bt.setOnClickListener(v -> {
                    Gson gson = new Gson();
                    String actorList = gson.toJson(lists.getActorlist());
                    Bundle bundle = new Bundle();
                    bundle.putString("type", lists.getType());
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
                    if (lists.getType().equals("adult")) {
                        bundle.putString("cover", lists.getLogo());
                    }
                    @SuppressLint("UnsafeOptInUsageError") Intent intent = new Intent(mContext, MovieDetailActivity.class);
                    intent.putExtras(bundle);
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
        TextView title_movie, text_update,text_type;
        RoundedImageView cover_movie;
        ImageView img_number;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            bt = itemView.findViewById(R.id.bt);
            bt_cv = itemView.findViewById(R.id.bt_cv);
            title_movie = itemView.findViewById(R.id.title_movie);
            text_update = itemView.findViewById(R.id.text_update);
            text_type = itemView.findViewById(R.id.text_type);
            cover_movie = itemView.findViewById(R.id.cover_movie);
            img_number = itemView.findViewById(R.id.img_number);

        }

    }



}
