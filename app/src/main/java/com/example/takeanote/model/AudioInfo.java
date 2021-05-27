package com.example.takeanote.model;

import android.net.Uri;

public class AudioInfo {

    Uri uri;
    String title;
    String id;
    boolean repro;
    String path;

    public AudioInfo() {
    }

    public AudioInfo(Uri uri, String title) {
        this.uri = uri;
        this.title = title;
        this.repro = false;
        this.path = null;

    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean isRepro() {
        return repro;
    }

    public void setRepro(boolean repro) {
        this.repro = repro;
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
