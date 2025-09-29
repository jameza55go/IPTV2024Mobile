package com.iptv.newiptvmobile.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.iptv.newiptvmobile.HomeActivity;
import com.iptv.newiptvmobile.R;
import com.iptv.newiptvmobile.model.Data;

import java.util.List;

public class RvMovieAdviceAdapter extends RecyclerView.Adapter<RvMovieAdviceAdapter.MyViewHolder> {
    HomeActivity context;
    List<Data> list;
    public RvMovieAdviceAdapter(HomeActivity context, List<Data> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public RvMovieAdviceAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_rv_movie_advice, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RvMovieAdviceAdapter.MyViewHolder holder, int position) {
        Data lists = list.get(position);
        holder.setIsRecyclable(false);
        holder.text_topic.setText(lists.getTitle());

        if (lists.isTopten()){
            LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
            holder.recyclerView.setLayoutManager(layoutManager);
            RvMovieAdviceSubTopTenAdapter subAdapter = new RvMovieAdviceSubTopTenAdapter(context,lists.getData(),position);
            holder.recyclerView.setAdapter(subAdapter);
        }else {
            LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
            holder.recyclerView.setLayoutManager(layoutManager);
            RvMovieAdviceSubAdapter subAdapter = new RvMovieAdviceSubAdapter(context,lists.getData(),position);
            holder.recyclerView.setAdapter(subAdapter);

        }
        holder.arrow_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //scrollRecyclerViewTopTen(holder.recyclerView,layoutManager,subAdapter,false);
                holder.recyclerView.smoothScrollToPosition(0);
            }
        });

        holder.arrow_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //scrollRecyclerViewTopTen(holder.recyclerView,layoutManager,subAdapter,true);
                holder.recyclerView.smoothScrollToPosition(lists.getData().size()-1);
            }
        });

        loadAllItems(holder.recyclerView);

        holder.layout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return false;
            }
        });
        holder.layout.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                holder.recyclerView.requestFocus();
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView text_topic;
        RecyclerView recyclerView;
        ConstraintLayout layout;
        ImageView arrow_left,arrow_right;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            text_topic = itemView.findViewById(R.id.text_topic);
            recyclerView = itemView.findViewById(R.id.recyclerView);
            layout = itemView.findViewById(R.id.layout);
            arrow_left = itemView.findViewById(R.id.arrow_left);
            arrow_right = itemView.findViewById(R.id.arrow_right);
        }
    }

    private void loadAllItems(RecyclerView recyclerView) {
        RecyclerView.Adapter adapter = recyclerView.getAdapter();
        if (adapter == null) {
            return;
        }

        // Notify the adapter of changes
        adapter.notifyDataSetChanged();

        // Measure and layout all children
        int itemCount = adapter.getItemCount();
        for (int i = 0; i < itemCount; i++) {
            RecyclerView.ViewHolder holder = adapter.createViewHolder(recyclerView, adapter.getItemViewType(i));
            adapter.bindViewHolder(holder, i);

            // Measure and layout the view
            View view = holder.itemView;
            view.measure(
                    View.MeasureSpec.makeMeasureSpec(recyclerView.getWidth(), View.MeasureSpec.EXACTLY),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            );
            view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        }
    }

    private void scrollRecyclerViewByOnePage(RecyclerView recyclerView,
                                             LinearLayoutManager layoutManager,
                                             RvMovieAdviceSubAdapter adapter,
                                             boolean forward) {
        int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
        int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
        int visibleItemCount = lastVisibleItemPosition - firstVisibleItemPosition;

        // Calculate the target position
        int targetPosition = forward
                ? firstVisibleItemPosition + visibleItemCount
                : firstVisibleItemPosition - visibleItemCount;

        // Ensure the target position is within valid range
        if (targetPosition >= 0 && targetPosition < adapter.getItemCount()) {
            recyclerView.smoothScrollToPosition(targetPosition);
        }
    }

    private void scrollRecyclerViewTopTen(RecyclerView recyclerView,
                                             LinearLayoutManager layoutManager,
                                             RvMovieAdviceSubTopTenAdapter adapter,
                                             boolean forward) {
        int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
        int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
        int visibleItemCount = lastVisibleItemPosition - firstVisibleItemPosition;

        // Calculate the target position
        int targetPosition = forward
                ? firstVisibleItemPosition + visibleItemCount
                : firstVisibleItemPosition - visibleItemCount;

        // Ensure the target position is within valid range
        if (targetPosition >= 0 && targetPosition < adapter.getItemCount()) {
            recyclerView.smoothScrollToPosition(targetPosition);
        }
    }

}
