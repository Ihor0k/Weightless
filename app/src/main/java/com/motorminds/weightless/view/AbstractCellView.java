package com.motorminds.weightless.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.view.View;

import androidx.core.content.ContextCompat;

import com.motorminds.weightless.R;

public abstract class AbstractCellView extends View implements Highlightable {
    protected final GradientDrawable background;
    private final int margin;
    private final int padding;

    private Drawable content;

    public AbstractCellView(Context context) {
        super(context);
        this.background = (GradientDrawable) ContextCompat.getDrawable(context, R.drawable.cell_background);
        Resources resources = getResources();
        this.margin = resources.getDimensionPixelSize(R.dimen.cell_margin);
        this.padding = resources.getDimensionPixelSize(R.dimen.cell_padding);
        background.setStroke(-1, 0);
    }

    public void setContent(Drawable content) {
        this.content = content;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        background.draw(canvas);
        if (content != null) {
            content.draw(canvas);
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int width = right - left;
        int height = bottom - top;
        if (content != null) {
            int contentMargin = margin + padding;
            int contentAvailWidth = width - 2 * contentMargin;
            int contentAvailHeight = height - 2 * contentMargin;
            int contentWidth = content.getIntrinsicWidth();
            int contentHeight = content.getIntrinsicHeight();
            float scale = Math.min((float) contentAvailWidth / contentWidth, (float) contentAvailHeight / contentHeight);
            float newContentWidth = contentWidth * scale;
            float newContentHeight = contentHeight * scale;
            int horizontalMargin = (int) ((width - newContentWidth) / 2);
            int verticalMargin = (int) ((height - newContentHeight) / 2);
            content.setBounds(horizontalMargin, verticalMargin, width - horizontalMargin, height - verticalMargin);
        }
        background.setBounds(margin, margin, width - margin, height - margin);
    }
}
