package com.iptv.newiptvmobile.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.iptv.newiptvmobile.R;
import com.iptv.newiptvmobile.listener.Refresh;
import com.iptv.newiptvmobile.model.Data;
import com.iptv.newiptvmobile.model.Row;

import java.util.List;

public class CustomArraySettingMainMenuAdapter extends ArrayAdapter<String> {
    private final List<String> list;
    private final Context context;
    int currentIndex = 0;

    public CustomArraySettingMainMenuAdapter(@NonNull Context context, int resource, List<String> list) {
        super(context, resource, list);
        this.list = list;
        this.context = context;
    }

    @Override
    public View getDropDownView(int position, View convertView,
                                ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    public View getCustomView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater=(LayoutInflater) context.getSystemService(  Context.LAYOUT_INFLATER_SERVICE );
        View row=inflater.inflate(R.layout.item_spiner, parent, false);
        TextView label=(TextView)row.findViewById(R.id.textView);
        ImageView image = (ImageView) row.findViewById(R.id.image);
        label.setText(list.get(position));

            image.setVisibility(View.GONE);
            if (position == getCurrentIndex()) {
                label.setTextColor(context.getResources().getColor(R.color.orange_main));
            }else{
                label.setTextColor(context.getResources().getColor(R.color.white));
            }

        return row;
    }

    public int getCurrentIndex() {
        return currentIndex;
    }

    public void setCurrentIndex(int currentIndex) {
        this.currentIndex = currentIndex;
    }
}
