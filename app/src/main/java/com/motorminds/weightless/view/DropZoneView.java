package com.motorminds.weightless.view;

import android.content.Context;

public class DropZoneView extends AbstractCellView {
    public DropZoneView(Context context) {
        super(context);
        unhighlight();
    }

    public void highlight(int color) {
        drawable.setColor(color);
        drawable.setAlpha(127);
        invalidate();
    }

    public void unhighlight() {
        drawable.setAlpha(0);
        invalidate();
    }
}
