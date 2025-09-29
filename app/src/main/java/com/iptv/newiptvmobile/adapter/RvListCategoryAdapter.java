package com.iptv.newiptvmobile.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.iptv.newiptvmobile.CategoryActivity;
import com.iptv.newiptvmobile.R;
import com.iptv.newiptvmobile.listener.OnClickRvListCategory;
import com.iptv.newiptvmobile.listener.OnFocusChangeScrolltoMiddle;
import com.iptv.newiptvmobile.model.SubData;
import com.iptv.newiptvmobile.widgets.ItemAnimator;

import java.util.List;

public class RvListCategoryAdapter extends RecyclerView.Adapter<RvListCategoryAdapter.MyViewHolder> {

    Context context;
    List<SubData> list;
    OnClickRvListCategory clickListener;

    public RvListCategoryAdapter(Context context, List<SubData> list, OnClickRvListCategory clickListener) {
        this.context = context;
        this.list = list;
        this.clickListener = clickListener;
    }


    @NonNull
    @Override
    public RvListCategoryAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_text_view_list,parent, false);
        return new MyViewHolder(view);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onBindViewHolder(@NonNull RvListCategoryAdapter.MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        final SubData lists = list.get(position);
        holder.setIsRecyclable(false);

        if (list.size() <= 1){
            holder.textView.setText("ทั้งหมด");
            holder.textView.setOnClickListener(v -> {
                Log.d("TAG", "ทั้งหมด");
            });
        }else{
            if (position == 0){
                holder.ic_search.setVisibility(View.VISIBLE);
            }
            holder.textView.setText(lists.getTitle());
            holder.bt_list.setOnClickListener(v -> {
                clickListener.onClickRvListCategory(lists.getTitle(),lists.getId(),lists.getSubid());
            });
            holder.bt_list.setOnFocusChangeListener((v, hasFocus) -> {
                ItemAnimator animator = new ItemAnimator(holder.bt_list);
                if (hasFocus){
                    holder.textView.setSelected(true);
                    holder.textView.setTextColor(ContextCompat.getColor(context, R.color.black));
                    holder.ic_search.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_search2_b));
                    animator.startFocusGainAnimation();
                }else{
                    holder.textView.setSelected(false);
                    holder.textView.setTextColor(ContextCompat.getColor(context, R.color.white));
                    holder.ic_search.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_search2_w));
                    animator.startFocusLossAnimation();
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        if (list.size() <= 0){
            return 1;
        }else{
            return list.size();
        }

    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        ImageView ic_search;
        ConstraintLayout bt_list;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.text_list);
            ic_search = itemView.findViewById(R.id.ic_search);
            bt_list = itemView.findViewById(R.id.bt_list);
        }
    }
}
