package com.motorminds.weightless.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.motorminds.weightless.Cell;
import com.motorminds.weightless.ColorAndView;
import com.motorminds.weightless.GameContract;
import com.motorminds.weightless.Tile;
import com.motorminds.weightless.TileAndView;
import com.motorminds.weightless.game.GameField;

import java.util.HashMap;
import java.util.Map;

public class BoardView extends ViewGroup implements GameContract.View {
    private GameContract.Presenter presenter;

    private Map<Cell, TileView> tileViews;
    private Map<Cell, DropZoneView> dropZones;

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
        for (View view : dropZones.values()) {
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
        for (Map.Entry<Cell, DropZoneView> cellViewEntry : dropZones.entrySet()) {
            Cell cell = cellViewEntry.getKey();
            View view = cellViewEntry.getValue();
            onCellLayout(cell, view);
        }
        for (Map.Entry<Cell, TileView> cellViewEntry : tileViews.entrySet()) {
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
    public void init(GameField field) {
        removeAllViews();
        this.tileViews.clear();
        this.dropZones.clear();
        this.rowsCount = field.ROWS_COUNT;
        this.columnsCount = field.COLUMNS_COUNT;

        for (int x = 0; x < columnsCount; x++) {
            for (int y = 0; y < rowsCount; y++) {
                Cell cell = new Cell(x, y);
                createDropZone(cell);
                Tile tile = field.getTile(x, y);
                if (tile == null) continue;
                createTileView(tile);
            }
        }

        this.enabled = true;

        setOnDragListener(new BoardOnDragListener());
    }

    private void createDropZone(Cell cell) {
        DropZoneView view = new DropZoneView(getContext());
        dropZones.put(cell, view);
        addView(view);
    }

    private void createTileView(Tile tile) {
        TileView tileView = new TileView(getContext(), tile.color);
        OnTouchListener onTouchListener = buildOnTouchListener(tile);
        tileView.setOnTouchListener(onTouchListener);
        tileViews.put(tile.cell, tileView);
        addView(tileView);
    }

    @Override
    public Animator createTile(Tile tile) {
        createTileView(tile);
        TileView tileView = tileViews.get(tile.cell);
        tileView.setAlpha(0);
        return ObjectAnimator.ofFloat(tileView, "alpha", 1);
    }

    @Override
    public Animator moveTile(Cell from, Cell to) {
        TileView tileView = tileViews.remove(from);
        tileViews.put(to, tileView);
        int color = tileView.getColor();
        Tile tile = new Tile(to, color);
        OnTouchListener onTouchListener = buildOnTouchListener(tile);
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

    private OnTouchListener buildOnTouchListener(Tile tile) {
        return (v, event) -> {
            if (!enabled) return true;
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                TileAndView colorAndView = new TileAndView(tile, (TileView) v);
                return v.startDrag(null, new InvisibleDragShadowBuilder(), colorAndView, 0);
            }
            return false;
        };
    }

    private class BoardOnDragListener implements View.OnDragListener {
        private Highlightable highlightedTile;

        private void unhighlight() {
            if (highlightedTile != null) {
                highlightedTile.unhighlight();
                highlightedTile = null;
            }
        }

        private void highlight(Cell cell, int color) {
            if (cell == null) {
                unhighlight();
            } else {
                Highlightable view = tileViews.get(cell);
                if (view == null) {
                    DropZoneView dropZoneView = dropZones.get(cell);
                    if (dropZoneView != null) {
                        dropZoneView.setColor(color);
                    }
                    view = dropZoneView;

                }
                if (highlightedTile != view) {
                    unhighlight();
                    highlightedTile = view;
                    highlightedTile.highlight();
                }
            }
        }

        @Override
        public boolean onDrag(View v, DragEvent event) {
            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_STARTED: {
                    Object localState = event.getLocalState();
                    Highlightable view = null;
                    if (localState instanceof TileAndView) {
                        view = ((TileAndView) localState).view;
                    } else if (localState instanceof ColorAndView) {
                        view = ((ColorAndView) localState).view;
                    }
                    if (view != null) {
                        view.highlight();
                    }
                    break;
                }
                case DragEvent.ACTION_DRAG_LOCATION: {
                    int x = (int) event.getX() / cellSize;
                    Object localState = event.getLocalState();
                    if (localState instanceof TileAndView) {
                        TileAndView tileAndView = (TileAndView) localState;
                        Cell toCell = presenter.wantToMove(tileAndView.tile.cell, x);
                        highlight(toCell, tileAndView.tile.color);
                    } else if (localState instanceof ColorAndView) {
                        ColorAndView colorAndView = (ColorAndView) localState;
                        Cell cell = presenter.wantToCreate(x);
                        highlight(cell, colorAndView.color);
                    }
                    break;
                }
                case DragEvent.ACTION_DROP: {
                    int x = (int) event.getX() / cellSize;
                    Object localState = event.getLocalState();
                    if (localState instanceof TileAndView) {
                        TileAndView tileAndView = (TileAndView) localState;
                        tileAndView.view.unhighlight();
                        Cell toCell = presenter.wantToMove(tileAndView.tile.cell, x);
                        if (toCell != null) {
                            presenter.moveTile(tileAndView.tile.cell, toCell);
                        }
                    } else if (localState instanceof ColorAndView) {
                        ColorAndView colorAndView = (ColorAndView) localState;
                        colorAndView.view.unhighlight();
                        Cell cell = presenter.wantToCreate(x);
                        if (cell != null) {
                            Tile tile = new Tile(cell, colorAndView.color);
                            presenter.createTile(tile);
                        }
                    }
                    break;
                }
                case DragEvent.ACTION_DRAG_EXITED: {
                    unhighlight();
                    break;
                }
                case DragEvent.ACTION_DRAG_ENDED: {
                    Object localState = event.getLocalState();
                    if (localState instanceof TileAndView) {
                        TileAndView tileAndView = (TileAndView) localState;
                        tileAndView.view.unhighlight();
                    } else if (localState instanceof ColorAndView) {
                        ColorAndView colorAndView = (ColorAndView) localState;
                        colorAndView.view.unhighlight();
                    }
                    unhighlight();
                    break;
                }
            }
            return true;
        }
    }
}
