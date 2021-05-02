package com.example.takeanote.model;

import android.graphics.Bitmap;
import android.net.Uri;

public class PaintInfo {

    Uri uri;
    String title;
    Bitmap bmp;

    public PaintInfo() {
    }

    public PaintInfo(Uri uri, String title, Bitmap bmp) {
        this.uri = uri;
        this.title = title;
        this.bmp = bmp;
    }

    public Bitmap getBmp() {
        return bmp;
    }

    public void setBmp(Bitmap bmp) {
        this.bmp = bmp;
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
