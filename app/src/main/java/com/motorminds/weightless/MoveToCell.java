package com.motorminds.weightless;

import androidx.annotation.Nullable;

public class MoveToCell {
    public final Cell moveToCell;

    @Nullable
    public final Cell bumpCell;

    public MoveToCell(Cell moveToCell, @Nullable Cell bumpCell) {
        this.moveToCell = moveToCell;
        this.bumpCell = bumpCell;
    }
}
