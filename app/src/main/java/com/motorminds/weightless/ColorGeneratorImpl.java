package com.motorminds.weightless;

import android.content.Context;

import androidx.core.content.ContextCompat;

import com.motorminds.weightless.game.ColorGenerator;

import java.util.Random;

public class ColorGeneratorImpl implements ColorGenerator {
    private int[] palette;
    private Random random;

    public ColorGeneratorImpl(Context context) {
        this.palette = new int[]{
                ContextCompat.getColor(context, R.color.cellYellow),
                ContextCompat.getColor(context, R.color.cellRed),
                ContextCompat.getColor(context, R.color.cellGreen),
                ContextCompat.getColor(context, R.color.cellWhite)
        };
        this.random = new Random();
    }

    public int nextColor() {
        return palette[random.nextInt(palette.length)];
    }
}
