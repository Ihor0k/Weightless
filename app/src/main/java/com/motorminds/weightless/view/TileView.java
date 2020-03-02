package com.motorminds.weightless.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;

import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;

import com.motorminds.weightless.R;
import com.motorminds.weightless.Tile;

public class TileView extends AbstractCellView {
    private final int color;
    private final Tile.Type type;
    private final int highlightColor;

    public TileView(Context context, int color, Tile.Type type) {
        super(context);
        this.color = color;
        this.type = type;
        int strokeWidth = getResources().getDimensionPixelSize(R.dimen.cell_highlight_width);
        background.setStroke(strokeWidth, color);
        background.setColor(color);
        int alpha = Color.alpha(color);
        this.highlightColor = ColorUtils.setAlphaComponent(color, alpha / 2);
        Drawable icon = getIcon(context, type);
        setContent(icon);
    }

    private Drawable getIcon(Context context, Tile.Type type) {
        int resource = 0;
        switch (type) {
            case SIMPLE:
                return null;
            case VERTICAL:
                resource = R.drawable.cell_vertical;
                break;
            case HORIZONTAL:
                resource = R.drawable.cell_horizontal;
                break;
            case VERTICAL_HORIZONTAL:
                resource = R.drawable.cell_vertical_horizontal;
                break;
            case BOMB:
                resource = R.drawable.cell_bomb;
                break;
        }
        return ContextCompat.getDrawable(context, resource);
    }

    public int getColor() {
        return color;
    }

    public Tile.Type getType() {
        return type;
    }

    @Override
    public void highlight() {
        setColor(highlightColor);
    }

    @Override
    public void unhighlight() {
        setColor(color);
    }

    private void setColor(int color) {
        background.setColor(color);
        invalidate();
    }
}
