package com.example.takeanote.model;

import android.graphics.Path;

public class FingerPath {

    private final int color;
    private final boolean emboss;
    private final boolean blur;
    private final int strokeWidth;
    private final Path path;

    public FingerPath(int color, boolean emboss, boolean blur, int strokeWidth, Path path) {
        this.color = color;
        this.emboss = emboss;
        this.blur = blur;
        this.strokeWidth = strokeWidth;
        this.path = path;
    }

    public int getColor() {
        return color;
    }

    public boolean isEmboss() {
        return emboss;
    }

    public boolean isBlur() {
        return blur;
    }

    public int getStrokeWidth() {
        return strokeWidth;
    }

    public Path getPath() {
        return path;
    }

}
