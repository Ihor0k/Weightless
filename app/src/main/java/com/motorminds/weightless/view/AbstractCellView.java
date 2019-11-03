package com.motorminds.weightless.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.GradientDrawable;
import android.view.View;

import androidx.core.content.ContextCompat;

import com.motorminds.weightless.R;

public abstract class AbstractCellView extends View implements Highlightable {
    protected final GradientDrawable drawable;
    private final int margin;

    public AbstractCellView(Context context) {
        super(context);
        this.drawable = (GradientDrawable) ContextCompat.getDrawable(context, R.drawable.cell_background);
        this.margin = getResources().getDimensionPixelSize(R.dimen.cell_margin);
        drawable.setStroke(-1, 0);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        drawable.draw(canvas);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int width = right - left;
        int height = bottom - top;
        drawable.setBounds(margin, margin, width - margin, height - margin);
    }
}
