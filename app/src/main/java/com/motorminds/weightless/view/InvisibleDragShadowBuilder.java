package com.motorminds.weightless.view;


import android.graphics.Point;
import android.view.View;

class InvisibleDragShadowBuilder extends View.DragShadowBuilder {
    @Override
    public void onProvideShadowMetrics(Point outShadowSize, Point outShadowTouchPoint) {
        outShadowSize.set(1, 1);
        outShadowTouchPoint.set(1, 1);
    }
}