package com.motorminds.weightless;

import android.content.Context;

import androidx.core.content.ContextCompat;

import java.util.Random;

public class ColorGenerator {
    private int[] palette;
    private Random random;

    public ColorGenerator(Context context) {
        this.palette = new int[]{
                ContextCompat.getColor(context, R.color.cell1),
                ContextCompat.getColor(context, R.color.cell2),
                ContextCompat.getColor(context, R.color.cell3),
                ContextCompat.getColor(context, R.color.cell4)
        };
        this.random = new Random();
    }

    public int nextColor() {
        return palette[random.nextInt(palette.length)];
    }
}
