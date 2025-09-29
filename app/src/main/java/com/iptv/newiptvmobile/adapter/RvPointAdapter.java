package com.iptv.newiptvmobile.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.iptv.newiptvmobile.R;
import com.iptv.newiptvmobile.model.Data;
import com.iptv.newiptvmobile.ui.tool.topup.PaymentActivity;

import java.util.List;

public class RvPointAdapter extends RecyclerView.Adapter<RvPointAdapter.MyViewHolder> {

    PaymentActivity activity;
    List<Data> list;


    public RvPointAdapter(PaymentActivity activity, List<Data> list) {
        this.activity = activity;
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(activity);
        View view = inflater.inflate(R.layout.item_rv_payment,parent, false);

        return new MyViewHolder(view);
    }

    @SuppressLint({"CheckResult", "UseCompatLoadingForDrawables"})
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Data lists = list.get(position);
        holder.setIsRecyclable(false);

        holder.date.setText(lists.getDatetime());
        holder.before_pay.setText(lists.getBefor_point());
        holder.add_days.setText(lists.getPoints());
        holder.after_pay.setText(lists.getAfter_point());
        holder.detail.setText(lists.getComment());
        holder.bt_select_payment.setOnClickListener(v -> {});

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView date,before_pay,add_days,after_pay,detail;
        LinearLayout bt_select_payment;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.date);
            before_pay = itemView.findViewById(R.id.before_pay);
            add_days = itemView.findViewById(R.id.add_days);
            after_pay = itemView.findViewById(R.id.after_pay);
            detail = itemView.findViewById(R.id.detail);
            bt_select_payment = itemView.findViewById(R.id.bt_select_payment);

        }
    }
}
