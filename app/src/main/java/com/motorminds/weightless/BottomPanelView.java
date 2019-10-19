package com.motorminds.weightless;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

public class BottomPanelView extends ViewGroup {
    private static int MAX_TILES = 7;

    private BoardView boardView;

    private List<View> cellViews;

    public BottomPanelView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initTileView(context);
    }

    private void initTileView(Context context) {
        cellViews = new ArrayList<>(MAX_TILES);

        createTileView(context, ContextCompat.getColor(context, R.color.cell1));
        createTileView(context, ContextCompat.getColor(context, R.color.cell2));
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
        int width = boardView.getMeasuredWidth();
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int cellSize = Math.min(width / MAX_TILES, height);
        int childMeasureSpec = MeasureSpec.makeMeasureSpec(cellSize, MeasureSpec.EXACTLY);
        for (View cellView : cellViews) {
            cellView.measure(childMeasureSpec, childMeasureSpec);
        }
        widthMeasureSpec = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY);
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
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
        int offset = (width - cellWidth * n) / 2;
        int cellHeight = view.getMeasuredHeight();
        int cellLeft = i * cellWidth + offset;
        int cellTop = 0;
        int cellRight = cellLeft + cellWidth;
        int cellBottom = cellTop + cellHeight;
//        System.out.printf("cell %d: %d %d -> %d %d\n", i, cellLeft, cellTop, cellRight, cellBottom);
        view.layout(cellLeft, cellTop, cellRight, cellBottom);
    }

    public void setBoardView(BoardView boardView) {
        this.boardView = boardView;
    }
}
