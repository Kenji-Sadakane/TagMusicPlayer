package com.keepingrack.tagmusicplayer.animation;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

public class HeightAnimation extends Animation {

    int targetHeight;
    int startHeight;

    View view;

    public HeightAnimation(View view, int startHeight, int targetHeight) {
        this.view = view;
        this.targetHeight = targetHeight;
        this.startHeight = startHeight;
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        int newHeight = (int) (startHeight + (targetHeight - startHeight) * interpolatedTime);
        view.getLayoutParams().height = newHeight;
        view.requestLayout();
    }

    @Override
    public void initialize(int width, int height, int parentWidth, int parentHeight) {
        super.initialize(width, height, ((View)view.getParent()).getWidth(), parentHeight);
    }

    @Override
    public boolean willChangeBounds() {
        return true;
    }
}
