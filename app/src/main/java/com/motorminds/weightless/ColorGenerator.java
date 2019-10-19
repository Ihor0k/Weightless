package com.motorminds.weightless;

import android.content.Context;
import android.graphics.Color;

import androidx.core.content.ContextCompat;

public class ColorGenerator {
    private int[] palette;
    private int i;

    public ColorGenerator(Context context) {
        // https://mycolor.space/
//        int[] baseColors = new int[]{
//                Color.parseColor("#D9FF00"),
//                Color.parseColor("#FFE335"),
//                Color.parseColor("#FFCA66"),
//                Color.parseColor("#FFBA91"),
//                Color.parseColor("#FFCA66"),
//                Color.parseColor("#FFE335"),
//        };
        this.i = 0;
        this.palette = new int[] {
                ContextCompat.getColor(context, R.color.cell1),
                ContextCompat.getColor(context, R.color.cell2),
                ContextCompat.getColor(context, R.color.cell3),
                ContextCompat.getColor(context, R.color.cell4)
        };
//        this.palette = generatePalette(baseColors, 3);
    }

//    private int[] generatePalette(int[] baseColors, int subColors) {
//        int numBaseColors = baseColors.length;
//        int[] reds = new int[numBaseColors];
//        int[] greens = new int[numBaseColors];
//        int[] blues = new int[numBaseColors];
//        for (int i = 0; i < numBaseColors; i++) {
//            reds[i] = Color.red(baseColors[i]);
//            greens[i] = Color.green(baseColors[i]);
//            blues[i] = Color.blue(baseColors[i]);
//        }
//        int numColors = subColors * numBaseColors;
//        int[] result = new int[numColors];
//        for (int i = 0; i < numColors; i++) {
//            int baseColorIdx = i / subColors;
//            int subColorIdx = i % subColors;
//            int nextBaseColorIdx = baseColorIdx + 1;
//            if (nextBaseColorIdx == numBaseColors) {
//                nextBaseColorIdx = 0;
//            }
//            int red = reds[baseColorIdx] + (reds[nextBaseColorIdx] - reds[baseColorIdx]) / subColors * subColorIdx;
//            int green = greens[baseColorIdx] + (greens[nextBaseColorIdx] - greens[baseColorIdx]) / subColors * subColorIdx;
//            int blue = blues[baseColorIdx] + (blues[nextBaseColorIdx] - blues[baseColorIdx]) / subColors * subColorIdx;
//            result[i] = Color.rgb(red, green, blue);
//        }
//        return result;
//    }

    public int nextColor() {
        if (i >= palette.length) i = 0;
        return palette[i++];
    }
}
