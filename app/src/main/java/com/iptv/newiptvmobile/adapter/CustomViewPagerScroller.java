package com.iptv.newiptvmobile.adapter;

import android.content.Context;
import android.view.animation.Interpolator;
import android.widget.Scroller;

public class CustomViewPagerScroller extends Scroller {
    private int scrollDuration = 1000; // Set the desired duration for the slide animation

    public CustomViewPagerScroller(Context context) {
        super(context);
    }

    public CustomViewPagerScroller(Context context, Interpolator interpolator) {
        super(context, interpolator);
    }

    public void setScrollDuration(int scrollDuration) {
        this.scrollDuration = scrollDuration;
    }

    @Override
    public void startScroll(int startX, int startY, int dx, int dy, int duration) {
        // Override the duration with your desired scroll duration
        super.startScroll(startX, startY, dx, dy, scrollDuration);
    }
}
