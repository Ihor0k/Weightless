package com.motorminds.weightless.view;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.content.ContextCompat;

import com.motorminds.weightless.R;
import com.motorminds.weightless.ColorAndView;

public class BottomPanelView extends ViewGroup {
    private static int MAX_CELLS = 7;

    public BottomPanelView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initTileView(context);
    }

    private void initTileView(Context context) {
        createTileView(context, ContextCompat.getColor(context, R.color.cell1), 0);
        createTileView(context, ContextCompat.getColor(context, R.color.cell2), 1);
        createTileView(context, ContextCompat.getColor(context, R.color.cell4), 2);
        createTileView(context, ContextCompat.getColor(context, R.color.cell4), 3);
        createTileView(context, ContextCompat.getColor(context, R.color.cell1), 4);
        createTileView(context, ContextCompat.getColor(context, R.color.cell2), 5);
        createTileView(context, ContextCompat.getColor(context, R.color.cell3), 6);
    }

    private void createTileView(Context context, int color, int index) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View cellLayout = inflater.inflate(R.layout.cell_layout, this, false);
        addView(cellLayout);
        View cellView = cellLayout.findViewById(R.id.cell_view);
        ((GradientDrawable) cellView.getBackground()).setColor(color);

        cellLayout.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN: {
                    ColorAndView colorAndView = new ColorAndView(color, v);
                    return v.startDrag(null, new DragShadowBuilder(v), colorAndView, 0);
                }
                case MotionEvent.ACTION_UP: {
                    return v.performClick();
                }
                default: {
                    return false;
                }
            }
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
