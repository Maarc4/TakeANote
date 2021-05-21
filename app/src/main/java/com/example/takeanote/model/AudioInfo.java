package com.example.takeanote.model;
import android.graphics.Bitmap;
import android.net.Uri;

public class AudioInfo {

    Uri uri;
    String title;
    String id;

    public AudioInfo() {
    }

    public AudioInfo(Uri uri, String title) {
        this.uri = uri;
        this.title = title;

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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
