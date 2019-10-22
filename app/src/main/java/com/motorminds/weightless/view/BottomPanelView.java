package com.motorminds.weightless.view;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.content.ContextCompat;

import com.motorminds.weightless.R;

import java.util.ArrayList;
import java.util.List;

public class BottomPanelView extends ViewGroup {
    private static int MAX_CELLS = 7;

    private List<View> cellViews;

    public BottomPanelView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initTileView(context);
    }

    private void initTileView(Context context) {
        cellViews = new ArrayList<>(MAX_CELLS);

        createTileView(context, ContextCompat.getColor(context, R.color.cell1));
        createTileView(context, ContextCompat.getColor(context, R.color.cell2));
        createTileView(context, ContextCompat.getColor(context, R.color.cell4));
        createTileView(context, ContextCompat.getColor(context, R.color.cell4));
        createTileView(context, ContextCompat.getColor(context, R.color.cell1));
        createTileView(context, ContextCompat.getColor(context, R.color.cell2));
        createTileView(context, ContextCompat.getColor(context, R.color.cell3));

        for (int i = 0; i < getChildCount(); i++) {
            cellViews.add(getChildAt(i));
        }
    }

    private void createTileView(Context context, int color) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View cellLayout = inflater.inflate(R.layout.cell_layout, this);
        View cellView = cellLayout.findViewById(R.id.cell_view);
        ((GradientDrawable) cellView.getBackground()).setColor(color);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int cellSize = Math.min(width / MAX_CELLS, height);
        int childMeasureSpec = MeasureSpec.makeMeasureSpec(cellSize, MeasureSpec.EXACTLY);
        for (View cellView : cellViews) {
            cellView.measure(childMeasureSpec, childMeasureSpec);
        }
        widthMeasureSpec = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY);
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(cellSize, MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int n = cellViews.size();
        int width = getWidth();
        for (int i = 0; i < n; i++) {
            View tileView = cellViews.get(i);
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
