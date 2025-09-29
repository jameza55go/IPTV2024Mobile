package com.iptv.newiptvmobile.adapter;

import android.support.annotation.NonNull;
import android.view.View;

import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

public class TopSnapHelper extends LinearSnapHelper {
    @Override
    public int[] calculateDistanceToFinalSnap(@NonNull RecyclerView.LayoutManager layoutManager, @NonNull View targetView) {
        int[] out = new int[2];
        out[0] = layoutManager.getDecoratedLeft(targetView) - layoutManager.getPaddingLeft();
        out[1] = layoutManager.getDecoratedTop(targetView) - layoutManager.getPaddingTop();
        return out;
    }

    @Override
    public View findSnapView(RecyclerView.LayoutManager layoutManager) {
        if (layoutManager instanceof RecyclerView.SmoothScroller.ScrollVectorProvider) {
            return super.findSnapView(layoutManager);
        }
        return null;
    }
}
