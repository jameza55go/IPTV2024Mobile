package com.iptv.newiptvmobile.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.annotation.NonNull;

import com.iptv.newiptvmobile.R;
import com.iptv.newiptvmobile.listener.Refresh;

import java.util.List;

public class CustomArrayAdapter extends ArrayAdapter<String> {
    private List<String> objects;
    private Context context;
    String type = "0";
    int fPosition = 0;
    Refresh refresh;
    int currentIndex = 0;

    public CustomArrayAdapter(Context context, int resourceId,
                              List<String> objects) {
        super(context, resourceId, objects);
        this.objects = objects;
        this.context = context;
    }
    public CustomArrayAdapter(Context context, int resourceId,
                              List<String> objects,String type,int fPosition,Refresh refresh) {
        super(context, resourceId, objects);
        this.objects = objects;
        this.context = context;
        this.type = type;
        this.fPosition = fPosition;
        this.refresh = refresh;
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
        label.setText(objects.get(position));

        if (type.equals("player")){
            image.setVisibility(View.GONE);
            if (position == getCurrentIndex()) {
                label.setTextColor(context.getResources().getColor(R.color.orange_main));
            }else{
                label.setTextColor(context.getResources().getColor(R.color.white));
            }
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
