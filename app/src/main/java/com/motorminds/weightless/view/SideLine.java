package com.motorminds.weightless.view;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class SideLine extends Drawable {
    private final Paint paint;
    private final int circleSize;
    private final float circleRadius;

    private float circleX;
    private float circle1Y;
    private float circle2Y;


    public SideLine(int color, int circleSize, int lineWidth) {
        this.paint = new Paint();
        this.circleSize = circleSize;
        this.circleRadius = circleSize / 2F;

        paint.setColor(color);
        paint.setStrokeWidth(lineWidth);
    }

    @Override
    public int getIntrinsicWidth() {
        return circleSize;
    }

    @Override
    public void setBounds(int left, int top, int right, int bottom) {
        super.setBounds(left, top, right, bottom);
        this.circleX = left + (right - left) / 2F;
        this.circle1Y = top + circleRadius;
        this.circle2Y = bottom - circleRadius;
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        canvas.drawCircle(circleX, circle1Y, circleRadius, paint);
        canvas.drawCircle(circleX, circle2Y, circleRadius, paint);
        canvas.drawLine(circleX, circle1Y, circleX, circle2Y, paint);
    }

    @Override
    public void setAlpha(int alpha) {
        paint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {
        paint.setColorFilter(colorFilter);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }
}
