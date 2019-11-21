package com.motorminds.weightless.view;

import android.content.Context;

public class DropZoneView extends AbstractCellView {
    private int color;

    public DropZoneView(Context context) {
        super(context);
        unhighlight();
    }

    public void setColor(int color) {
        this.color = color;
    }

    public void highlight() {
        drawable.setColor(color);
        drawable.setAlpha(127);
        invalidate();
    }

    public void unhighlight() {
        drawable.setAlpha(0);
        invalidate();
    }
}
