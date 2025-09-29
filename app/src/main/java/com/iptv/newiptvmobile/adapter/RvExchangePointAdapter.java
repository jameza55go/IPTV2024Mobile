package com.iptv.newiptvmobile.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
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

import com.iptv.newiptvmobile.R;
import com.iptv.newiptvmobile.listener.OnClickRvHomeCategory;
import com.iptv.newiptvmobile.model.Data;
import com.iptv.newiptvmobile.ui.tool.point.ExchangePointFragment;
import com.iptv.newiptvmobile.widgets.ItemAnimator;
import com.squareup.picasso.Picasso;

import java.util.List;

public class RvExchangePointAdapter extends RecyclerView.Adapter<RvExchangePointAdapter.MyViewHolder> {

    private Context context;
    private List<Data> list;
    ExchangePointFragment.OnClickExchangePoint listener;

    public RvExchangePointAdapter(Context context, List<Data> list, ExchangePointFragment.OnClickExchangePoint listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RvExchangePointAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_rv_exchange_point,parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RvExchangePointAdapter.MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Data lists = list.get(position);

        holder.text_price_point.setText(lists.getPoint());
        holder.text_count_days.setText(context.getString(R.string.txt_day,lists.getVipday()));

        holder.bt_exchange_point.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClickExchangePoint(lists.getId());
            }
        });

        holder.bt_exchange_point.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    holder.text_price_point.setTextColor(ContextCompat.getColor(context,R.color.orange_main));
                    holder.text_fw_point.setTextColor(ContextCompat.getColor(context,R.color.orange_main));
                    holder.text_exchange_free.setTextColor(ContextCompat.getColor(context,R.color.orange_main));
                    holder.text_count_days.setTextColor(ContextCompat.getColor(context,R.color.white));
                } else {
                    holder.text_price_point.setTextColor(ContextCompat.getColor(context,R.color.white));
                    holder.text_fw_point.setTextColor(ContextCompat.getColor(context,R.color.white));
                    holder.text_exchange_free.setTextColor(ContextCompat.getColor(context,R.color.white));
                    holder.text_count_days.setTextColor(ContextCompat.getColor(context,R.color.orange_main));
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ConstraintLayout bt_exchange_point;
        TextView text_price_point,text_fw_point,text_exchange_free,text_count_days;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            bt_exchange_point = itemView.findViewById(R.id.bt_exchange_point);
            text_price_point = itemView.findViewById(R.id.text_price_point);
            text_fw_point = itemView.findViewById(R.id.text_fw_point);
            text_exchange_free = itemView.findViewById(R.id.text_exchange_free);
            text_count_days = itemView.findViewById(R.id.text_count_days);
        }
    }
}
