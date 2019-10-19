package com.motorminds.weightless;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import java.util.HashMap;
import java.util.Map;

public class BoardView extends ViewGroup implements GameContract.View {
    private GameContract.Presenter presenter;

    private Map<Cell, View> tileViews;

    private OnTouchListenerBuilder listenerBuilder;
    private int rowsCount;
    private int columnsCount;
    private int cellSize;
    private boolean enabled;

    public BoardView(Context context, AttributeSet attrs) {
        super(context, attrs);

        this.tileViews = new HashMap<>();
        this.listenerBuilder = new OnTouchListenerBuilder();
        this.enabled = false;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        this.cellSize = Math.min(width / columnsCount, height / rowsCount);
        int childMeasureSpec = MeasureSpec.makeMeasureSpec(cellSize, MeasureSpec.EXACTLY);
        for (View view : tileViews.values()) {
            view.measure(childMeasureSpec, childMeasureSpec);
        }
        int newWidth = cellSize * columnsCount;
        int newHeight = cellSize * rowsCount;
        System.out.printf("width: %d -> %d; height: %d -> %d; cellSize: %d\n", width, newWidth, height, newHeight, cellSize);
        widthMeasureSpec = MeasureSpec.makeMeasureSpec(newWidth, MeasureSpec.getMode(widthMeasureSpec));
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(newHeight, MeasureSpec.getMode(heightMeasureSpec));
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        for (Map.Entry<Cell, View> cellViewEntry : tileViews.entrySet()) {
            Cell cell = cellViewEntry.getKey();
            View view = cellViewEntry.getValue();
            onCellLayout(cell, view);
        }
    }

    private void onCellLayout(Cell cell, View view) {
        if (view.isLayoutRequested()) {
            int cellWidth = view.getMeasuredWidth();
            int cellHeight = view.getMeasuredHeight();
            int cellLeft = cell.x * cellWidth;
            int cellTop = cell.y * cellHeight;
            int cellRight = cellLeft + cellWidth;
            int cellBottom = cellTop + cellHeight;
            view.setTranslationX(0);
            view.setTranslationY(0);
            view.layout(cellLeft, cellTop, cellRight, cellBottom);
        }
    }

    @Override
    public void init(Tile[][] field) {
        for (View view : this.tileViews.values()) {
            removeView(view);
        }
        this.tileViews.clear();
        this.rowsCount = field.length;
        this.columnsCount = field[0].length;

        for (int i = 0; i < field.length; i++) {
            for (int j = 0; j < field[i].length; j++) {
                Tile tile = field[i][j];
                if (tile == null) continue;
                Cell cell = new Cell(j, i);
                View tileView = createTileView(cell, tile.getColor());
                addTileView(cell, tileView);
            }
        }

        this.enabled = true;
    }

    private View createTileView(Cell cell, int color) {
        OnTouchListener onTouchListener = listenerBuilder.buildListener(cell);
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View tileLayout = inflater.inflate(R.layout.cell_layout, this, false);
        getTileBackground(tileLayout).setColor(color);
        tileLayout.setOnTouchListener(onTouchListener);
        return tileLayout;
    }

    private void addTileView(Cell cell, View tileView) {
        tileViews.put(cell, tileView);
        addView(tileView);
    }

    @Override
    public Animator createTile(Cell cell, int color) {
        View tileView = createTileView(cell, color);
        tileView.setAlpha(0);
        addTileView(cell, tileView);
        return ObjectAnimator.ofFloat(tileView, "alpha", 1);
    }

    @Override
    public Animator moveTile(Cell from, Cell to) {
        View tileView = tileViews.remove(from);
        tileViews.put(to, tileView);
        tileView.setOnTouchListener(listenerBuilder.buildListener(to));
        ObjectAnimator animator = new ObjectAnimator();
        animator.setTarget(tileView);
        if (from.x != to.x) {
            float realFromX = from.x * cellSize;
            float realToX = to.x * cellSize;
            animator.setPropertyName("x");
            animator.setFloatValues(realFromX, realToX);
        } else if (from.y != to.y) {
            float realFromY = from.y * cellSize;
            float realToY = to.y * cellSize;
            animator.setPropertyName("y");
            animator.setFloatValues(realFromY, realToY);
        }
        return animator;
    }

    @Override
    public Animator removeTile(Cell cell) {
        final View tileView = tileViews.remove(cell);
        Animator animator = ObjectAnimator.ofFloat(tileView, "alpha", 0);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                removeView(tileView);
            }
        });
        return animator;
    }

    @Override
    public void setPresenter(GameContract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void enable() {
        this.enabled = true;
    }

    @Override
    public void disable() {
        this.enabled = false;
    }

    private GradientDrawable getTileBackground(View tileLayout) {
        return (GradientDrawable) tileLayout.findViewById(R.id.cell_view).getBackground();
    }

    private void setBackgroundAlpha(View tileLayout, int alpha) {
        getTileBackground(tileLayout).setAlpha(alpha);
    }

    class OnTouchListenerBuilder {
        OnTouchListener buildListener(Cell cell) {
            return (v, event) -> {
                if (!enabled) return true;
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        setBackgroundAlpha(v, 127);
                        return true;
                    }
                    case MotionEvent.ACTION_MOVE: {
                        float dx = event.getX();
                        int x = (int) (cell.x + dx / cellSize);
//                        MoveToCell moveTo = presenter.wantToMove(cell, x);
                        return true;
                    }
                    case MotionEvent.ACTION_UP: {
                        setBackgroundAlpha(v, 255);
                        float dx = event.getX();
                        int toColumn = (int) (cell.x + dx / cellSize);
                        toColumn = Math.min(Math.max(toColumn, 0), columnsCount - 1);
                        Cell toCell = presenter.wantToMove(cell, toColumn);
                        if (toCell != null) {
                            presenter.moveTile(cell, toCell);
                        }
                        v.performClick();
                        return true;
                    }
                    default: {
                        return false;
                    }
                }
            };
        }
    }
}
