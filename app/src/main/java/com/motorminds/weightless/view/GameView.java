package com.motorminds.weightless.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.motorminds.weightless.R;

public class GameView extends ViewGroup {
    private final int SIDE_LINE_VERTICAL_MARGIN;
    private final int SIDE_LINE_HORIZONTAL_MARGIN;

    private View topPanelView;
    private View boardView;
    private View bottomPanelView;
    private Drawable leftLine;
    private Drawable rightLine;

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        Resources resources = getResources();
        this.SIDE_LINE_VERTICAL_MARGIN = resources.getDimensionPixelSize(R.dimen.side_line_vertical_margin);
        this.SIDE_LINE_HORIZONTAL_MARGIN = resources.getDimensionPixelSize(R.dimen.side_line_horizontal_margin);
        setWillNotDraw(false);
        initChildren(context);
    }

    private void initChildren(Context context) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        this.topPanelView = inflater.inflate(R.layout.top_panel_layout, this, false);
        this.boardView = inflater.inflate(R.layout.board_layout, this, false);
        this.bottomPanelView = inflater.inflate(R.layout.bottom_panel_layout, this, false);
        addView(this.topPanelView);
        addView(this.boardView);
        addView(this.bottomPanelView);

        Resources resources = context.getResources();
        int sideLineColor = resources.getColor(R.color.boardBackground);
        int circleSize = resources.getDimensionPixelSize(R.dimen.side_line_circle_size);
        int lineWidth = resources.getDimensionPixelSize(R.dimen.side_line_line_width);

        this.leftLine = new SideLine(sideLineColor, circleSize, lineWidth);
        this.rightLine = new SideLine(sideLineColor, circleSize, lineWidth);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int availWidth = r - l;
        int availHeight = b - t;

        int topPanelHeight = topPanelView.getMeasuredHeight();
        int topPanelWidth = topPanelView.getMeasuredWidth();
        int boardWidth = boardView.getMeasuredWidth();
        int boardHeight = boardView.getMeasuredHeight();
        int bottomPanelHeight = bottomPanelView.getMeasuredHeight();

        int spaceSize = (availHeight - topPanelHeight - boardHeight - bottomPanelHeight) / 4;

        int topPanelLeft = (availWidth - topPanelWidth) / 2;
        int topPanelTop = spaceSize;
        int topPanelRight = topPanelLeft + topPanelWidth;
        int topPanelBottom = topPanelTop + topPanelHeight;
        topPanelView.layout(topPanelLeft, topPanelTop, topPanelRight, topPanelBottom);

        int boardLeft = (availWidth - boardWidth) / 2;
        int boardTop = topPanelBottom + spaceSize;
        int boardRight = boardLeft + boardWidth;
        int boardBottom = boardTop + boardHeight;
        boardView.layout(boardLeft, boardTop, boardRight, boardBottom);

        int bottomPanelTop = boardBottom + spaceSize;
        int bottomPanelBottom = bottomPanelTop + bottomPanelHeight;
        bottomPanelView.layout(boardLeft, bottomPanelTop, boardRight, bottomPanelBottom);

        int leftLineLeft = SIDE_LINE_HORIZONTAL_MARGIN;
        int leftLineTop = boardTop + SIDE_LINE_VERTICAL_MARGIN;
        int leftLineRight = boardLeft - SIDE_LINE_HORIZONTAL_MARGIN;
        int leftLineBottom = boardBottom - SIDE_LINE_VERTICAL_MARGIN;
        leftLine.setBounds(leftLineLeft, leftLineTop, leftLineRight, leftLineBottom);

        int rightLineLeft = boardRight + SIDE_LINE_HORIZONTAL_MARGIN;
        int rightLineTop = boardTop + SIDE_LINE_VERTICAL_MARGIN;
        int rightLineRight = availWidth - SIDE_LINE_HORIZONTAL_MARGIN;
        int rightLineBottom = boardBottom - SIDE_LINE_VERTICAL_MARGIN;
        rightLine.setBounds(rightLineLeft, rightLineTop, rightLineRight, rightLineBottom);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int spaceForSideLines = leftLine.getMinimumWidth() + rightLine.getMinimumWidth() + 4 * SIDE_LINE_HORIZONTAL_MARGIN;
        int availWidth = MeasureSpec.getSize(widthMeasureSpec) - spaceForSideLines;
        int availHeight = MeasureSpec.getSize(heightMeasureSpec);

        int topPanelWidthMeasureSpec = MeasureSpec.makeMeasureSpec(availWidth, MeasureSpec.EXACTLY);
        int topPanelHeightMeasureSpec = MeasureSpec.makeMeasureSpec(availHeight, MeasureSpec.AT_MOST);
        topPanelView.measure(topPanelWidthMeasureSpec, topPanelHeightMeasureSpec);
        int topPanelHeight = topPanelView.getMeasuredHeight();

        int bottomPanelWidthMeasureSpec = MeasureSpec.makeMeasureSpec(availWidth, MeasureSpec.AT_MOST);
        int bottomPanelHeightMeasureSpec = MeasureSpec.makeMeasureSpec(availHeight, MeasureSpec.AT_MOST);
        bottomPanelView.measure(bottomPanelWidthMeasureSpec, bottomPanelHeightMeasureSpec);
        int bottomPanelHeight = bottomPanelView.getMeasuredHeight();

        int boardHeight = availHeight - topPanelHeight - bottomPanelHeight;
        int boardWidthMeasureSpec = MeasureSpec.makeMeasureSpec(availWidth, MeasureSpec.AT_MOST);
        int boardHeightMeasureSpec = MeasureSpec.makeMeasureSpec(boardHeight, MeasureSpec.AT_MOST);
        boardView.measure(boardWidthMeasureSpec, boardHeightMeasureSpec);

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        leftLine.draw(canvas);
        rightLine.draw(canvas);
    }
}
