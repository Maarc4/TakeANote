package com.example.takeanote.model;

import android.graphics.Bitmap;
import android.net.Uri;

public class PaintInfo {

    Uri uri;
    String title;
    Bitmap bmp;
    String id;
    String uriPath;
    byte[] bytes;

    public PaintInfo() {
    }

    public PaintInfo(Uri uri, String title, Bitmap bmp) {
        this.uri = uri;
        this.title = title;
        this.bmp = bmp;
        this.uriPath = uri.getPath();
    }

    public byte[] getBytes() {
        return this.bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
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

    public void setUriPath(String path) {
        this.uriPath = path;
    }

    public String getUriPath() {
        return this.uriPath;
    }


    public void setTitle(String title) {
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
