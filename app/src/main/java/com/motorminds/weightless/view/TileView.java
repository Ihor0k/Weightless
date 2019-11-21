package com.motorminds.weightless.view;

import android.content.Context;
import android.graphics.Color;

import androidx.core.graphics.ColorUtils;

import com.motorminds.weightless.R;

public class TileView extends AbstractCellView {
    private final int color;
    private final int highlightColor;

    public TileView(Context context, int color) {
        super(context);
        this.color = color;
        int strokeWidth = getResources().getDimensionPixelSize(R.dimen.cell_highlight_width);
        drawable.setStroke(strokeWidth, color);
        drawable.setColor(color);
        int alpha = Color.alpha(color);
        this.highlightColor = ColorUtils.setAlphaComponent(color, alpha / 2);
    }

    public int getColor() {
        return color;
    }

    public void highlight() {
        setColor(highlightColor);
    }

    public void unhighlight() {
        setColor(color);
    }

    private void setColor(int color) {
        drawable.setColor(color);
        invalidate();
    }
}
