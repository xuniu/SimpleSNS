package me.xuneal.simplesns.app.ui.components;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

public class ResizeAnimation extends Animation {
    final int startHeight;
    final int targetHeight;
    View view;

    public ResizeAnimation(View view, int targetHeight) {
        this.view = view;
        this.targetHeight = targetHeight;
        startHeight = view.getHeight();
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        int height = (int) (startHeight + (targetHeight - startHeight) * interpolatedTime);
        view.getLayoutParams().height = height;
        view.requestLayout();
    }

    @Override
    public void initialize(int width, int height, int parentWidth, int parentHeight) {
        super.initialize(width, height, parentWidth, parentHeight);
    }

    @Override
    public boolean willChangeBounds() {
        return true;
    }
}