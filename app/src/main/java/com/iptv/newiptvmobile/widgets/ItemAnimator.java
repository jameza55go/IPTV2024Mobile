package com.iptv.newiptvmobile.widgets;

import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

public class ItemAnimator {

    private static final long FOCUS_GAIN_DURATION = 350L;
    private static final long FOCUS_LOSS_DURATION = 200L;
    private static final AccelerateInterpolator FOCUS_GAIN_INTERPOLATOR = new AccelerateInterpolator();
    private static final DecelerateInterpolator FOCUS_LOSS_INTERPOLATOR = new DecelerateInterpolator();


    private final View itemView;
    private final Handler handler = new Handler(Looper.getMainLooper());

    private final Runnable focusGainRunnable = new Runnable() {
        @Override
        public void run() {
            itemView.animate()
                    .scaleX(1.1f)
                    .scaleY(1.1f)
                    .setInterpolator(FOCUS_GAIN_INTERPOLATOR)
                    .setDuration(FOCUS_GAIN_DURATION);
        }
    };

    private final Runnable focusLossRunnable = new Runnable() {
        @Override
        public void run() {
            itemView.setScaleX(1.05f);
            itemView.setScaleY(1.05f);
            itemView.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .setInterpolator(FOCUS_LOSS_INTERPOLATOR)
                    .setDuration(FOCUS_LOSS_DURATION);
        }
    };

    public ItemAnimator(View itemView) {
        this.itemView = itemView;
    }

    public void startFocusGainAnimation() {
        cancelPendingAnimations();
        handler.post(focusGainRunnable);
    }

    public void startFocusLossAnimation() {
        cancelPendingAnimations();
        handler.post(focusLossRunnable);
    }

    public void cancel() {
        cancelPendingAnimations();
        itemView.setScaleX(1f);
        itemView.setScaleY(1f);
    }

    private void cancelPendingAnimations() {
        handler.removeCallbacks(focusGainRunnable);
        handler.removeCallbacks(focusLossRunnable);
        itemView.animate().cancel();
    }
}
