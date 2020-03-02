package com.motorminds.weightless.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.content.ContextCompat;

import com.motorminds.weightless.R;
import com.motorminds.weightless.Tile;
import com.motorminds.weightless.TileInfoAndView;

public class BottomPanelView extends ViewGroup {
    private static int MAX_CELLS = 7;

    public BottomPanelView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initTileView(context);
    }

    private void initTileView(Context context) {
        createTileView(context, ContextCompat.getColor(context, R.color.cellYellow), Tile.Type.SIMPLE, 0);
        createTileView(context, ContextCompat.getColor(context, R.color.cellRed), Tile.Type.SIMPLE, 1);
        createTileView(context, ContextCompat.getColor(context, R.color.cellWhite), Tile.Type.SIMPLE, 2);
        createTileView(context, ContextCompat.getColor(context, R.color.cellWhite), Tile.Type.SIMPLE, 3);
        createTileView(context, ContextCompat.getColor(context, R.color.cellYellow), Tile.Type.SIMPLE, 4);
        createTileView(context, ContextCompat.getColor(context, R.color.cellRed), Tile.Type.SIMPLE, 5);
        createTileView(context, ContextCompat.getColor(context, R.color.cellGreen), Tile.Type.SIMPLE, 6);
    }

    private void createTileView(Context context, int color, Tile.Type type, int index) {
        TileView tileView = new TileView(context, color, type);
        addView(tileView);

        tileView.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN: {
                    TileInfoAndView tileInfoAndView = new TileInfoAndView(color, type, (TileView) v);
                    return v.startDrag(null, new InvisibleDragShadowBuilder(), tileInfoAndView, 0);
                }
            }
            return false;
        });
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int cellSize = Math.min(width / MAX_CELLS, height);
        int childMeasureSpec = MeasureSpec.makeMeasureSpec(cellSize, MeasureSpec.EXACTLY);
        for (int i = 0; i < getChildCount(); i++) {
            View tileView = getChildAt(i);
            tileView.measure(childMeasureSpec, childMeasureSpec);
        }
        widthMeasureSpec = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY);
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(cellSize, MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int n = getChildCount();
        int width = getWidth();
        for (int i = 0; i < n; i++) {
            View tileView = getChildAt(i);
            onCellLayout(tileView, i, n, width);
        }
    }

    private void onCellLayout(View view, int i, int n, int width) {
        int cellWidth = view.getMeasuredWidth();
        int cellHeight = view.getMeasuredHeight();
        int offset = (width - cellWidth * n) / 2;
        int cellLeft = i * cellWidth + offset;
        int cellTop = 0;
        int cellRight = cellLeft + cellWidth;
        int cellBottom = cellTop + cellHeight;
        view.layout(cellLeft, cellTop, cellRight, cellBottom);
    }
}
