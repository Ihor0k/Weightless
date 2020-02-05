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

    @Override
    public void highlight() {
        background.setColor(color);
        background.setAlpha(127);
        invalidate();
    }

    @Override
    public void unhighlight() {
        background.setAlpha(0);
        invalidate();
    }
}
