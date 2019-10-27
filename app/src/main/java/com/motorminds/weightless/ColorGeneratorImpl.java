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
