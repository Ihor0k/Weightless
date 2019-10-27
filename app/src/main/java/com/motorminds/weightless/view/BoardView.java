package com.motorminds.weightless.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.motorminds.weightless.Cell;
import com.motorminds.weightless.ColorAndView;
import com.motorminds.weightless.GameContract;
import com.motorminds.weightless.R;
import com.motorminds.weightless.Tile;

import java.util.HashMap;
import java.util.Map;

public class BoardView extends ViewGroup implements GameContract.View {
    private GameContract.Presenter presenter;

    private Map<Cell, View> tileViews;
    private Map<View, Cell> dropZones;

    private int rowsCount;
    private int columnsCount;
    private int cellSize;
    private boolean enabled;

    public BoardView(Context context, AttributeSet attrs) {
        super(context, attrs);

        this.tileViews = new HashMap<>();
        this.dropZones = new HashMap<>();
        this.enabled = false;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        this.cellSize = Math.min(width / columnsCount, height / rowsCount);
        int childMeasureSpec = MeasureSpec.makeMeasureSpec(cellSize, MeasureSpec.EXACTLY);
        for (View view : dropZones.keySet()) {
            view.measure(childMeasureSpec, childMeasureSpec);
        }
        for (View view : tileViews.values()) {
            view.measure(childMeasureSpec, childMeasureSpec);
        }
        int newWidth = cellSize * columnsCount;
        int newHeight = cellSize * rowsCount;
        widthMeasureSpec = MeasureSpec.makeMeasureSpec(newWidth, MeasureSpec.getMode(widthMeasureSpec));
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(newHeight, MeasureSpec.getMode(heightMeasureSpec));
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        for (Map.Entry<View, Cell> viewCellEntry : dropZones.entrySet()) {
            Cell cell = viewCellEntry.getValue();
            View view = viewCellEntry.getKey();
            onCellLayout(cell, view);
        }
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
        this.tileViews.clear();
        this.rowsCount = field.length;
        this.columnsCount = rowsCount > 0 ? field[0].length : 0;

        for (int i = 0; i < field.length; i++) {
            for (int j = 0; j < field[i].length; j++) {
                Cell cell = new Cell(j, i);
                createDropZone(cell);
                Tile tile = field[i][j];
                if (tile == null) continue;
                View tileView = createTileView(tile.getColor());
                addTileView(cell, tileView);
            }
        }

        this.enabled = true;
    }

    private void createDropZone(Cell cell) {
        View view = new View(getContext());
        dropZones.put(view, cell);
        view.setOnDragListener((v, event) -> {
            switch (event.getAction()) {
                case DragEvent.ACTION_DROP: {
                    ColorAndView colorAndView = (ColorAndView) event.getLocalState();
                    View tileView = colorAndView.view;
                    ViewGroup viewParent = (ViewGroup) tileView.getParent();
                    viewParent.removeView(tileView);
                    presenter.createTile(cell, colorAndView.color);
                }
            }
            return true;
        });
        addView(view);
    }

    private View createTileView(int color) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View tileLayout = inflater.inflate(R.layout.cell_layout, this, false);
        setCellBackground(tileLayout, color);
        return tileLayout;
    }

    private void addTileView(Cell cell, View tileView) {
        OnTouchListener onTouchListener = buildListener(cell);
        tileView.setOnTouchListener(onTouchListener);
        tileViews.put(cell, tileView);
        addView(tileView);
    }

    @Override
    public Animator createTile(Cell cell, int color) {
        View tileView = createTileView(color);
        tileView.setAlpha(0);
        addTileView(cell, tileView);
        return ObjectAnimator.ofFloat(tileView, "alpha", 1);
    }

    @Override
    public Animator moveTile(Cell from, Cell to) {
        View tileView = tileViews.remove(from);
        tileViews.put(to, tileView);
        OnTouchListener onTouchListener = buildListener(to);
        tileView.setOnTouchListener(onTouchListener);
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

    private void setCellBackground(View cellLayout, int color) {
        View cellView = cellLayout.findViewById(R.id.cell_view);
        GradientDrawable background = (GradientDrawable) cellView.getBackground();
        background.setColor(color);
    }

    private OnTouchListener buildListener(Cell cell) {
        return (v, event) -> {
            if (!enabled) return true;
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN: {
                    v.setAlpha(0.5F);
                    return true;
                }
                case MotionEvent.ACTION_MOVE: {
                    float dx = event.getX();
                    int x = (int) (cell.x + dx / cellSize);
//                        MoveToCell moveTo = presenter.wantToMove(cell, x);
                    return true;
                }
                case MotionEvent.ACTION_UP: {
                    v.setAlpha(1.0F);
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
