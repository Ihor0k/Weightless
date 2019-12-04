package com.motorminds.weightless.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatImageButton;

import com.motorminds.weightless.R;

public class IconButton extends AppCompatImageButton {
    private Paint paint;
    private int cornerRadius;
    private int minPadding;
    private Rect bounds;
    private RectF boundsF;
    private boolean sizeByWidth;

    public IconButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public IconButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.IconButton);
        this.cornerRadius = attributes.getDimensionPixelSize(R.styleable.IconButton_cornerRadius, 0);
        this.minPadding = attributes.getDimensionPixelSize(R.styleable.IconButton_minPadding, 0);
        int sizeBy = attributes.getInt(R.styleable.IconButton_sizeBy, 0);
        this.sizeByWidth = sizeBy != 1;
        int backgroundColor = attributes.getColor(R.styleable.IconButton_backgroundColor, 0);
        this.paint = new Paint();
        this.bounds = new Rect();
        this.boundsF = new RectF();
        paint.setColor(backgroundColor);
        setScaleType(ScaleType.FIT_CENTER);
        attributes.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        if (sizeByWidth) {
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(width, heightMeasureSpec);
        } else {
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(height, widthMeasureSpec);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        width = getMeasuredWidth();
        height = getMeasuredHeight();
        if (width > height) {
            int diff = (width - height) / 2;
            setPadding(minPadding, minPadding + diff, minPadding, minPadding + diff);
        } else {
            int diff = (height - width) / 2;
            setPadding(minPadding + diff, minPadding, minPadding + diff, minPadding);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.getClipBounds(bounds);
        boundsF.set(bounds);
        canvas.drawRoundRect(boundsF, cornerRadius, cornerRadius, paint);
        super.onDraw(canvas);
    }
}
