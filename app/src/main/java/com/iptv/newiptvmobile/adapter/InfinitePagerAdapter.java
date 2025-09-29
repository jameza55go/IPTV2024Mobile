package com.iptv.newiptvmobile.adapter;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.iptv.newiptvmobile.model.Data;
import com.makeramen.roundedimageview.RoundedImageView;

import java.io.File;
import java.util.List;

public class InfinitePagerAdapter extends PagerAdapter {
    List<Data> list;
    Context context;

    public InfinitePagerAdapter(List<Data> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public int getCount() {
        return 20000;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        int imagePosition = position % list.size();
        Log.d("TAG", "imagePosition: " + String.valueOf(position));
        RoundedImageView imageView = new RoundedImageView(container.getContext());
        imageView.setCornerRadius(20);
        imageView.setAdjustViewBounds(true);
        imageView.setFocusableInTouchMode(false);
        imageView.setFocusable(false);
        imageView.setOnFocusChangeListener(null);
        imageView.setOnClickListener(null);
        imageView.clearFocus();
        /* switch (scaleType) {
            case "fit" :
                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                break;
            case "centercrop" :
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                break;
            case "original" :
                imageView.setAdjustViewBounds(true);
            default:
                break;
        }
         */
        Glide.with(context)
                .load(list.get(imagePosition).getLogo())
                .into((ImageView) imageView);

        container.addView(imageView);
        return imageView;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}
