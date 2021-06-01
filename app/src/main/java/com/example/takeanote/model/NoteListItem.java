package com.example.takeanote.model;

import com.example.takeanote.PaintView;
import com.example.takeanote.utils.Constant;

public class NoteListItem {

    private NoteUI textNoteItem;
    private PaintView paintNoteItem;
    private AudioInfo audioNoteItem;
    private ImageInfo imageInfo;
    private PaintInfo paintInfo;
    private MapsInfo maps;
    private int viewType;

    public NoteListItem(NoteUI textNoteItem) {
        this.textNoteItem = textNoteItem;
        this.viewType = Constant.ITEM_TEXT_NOTE_VIEWTYPE;
    }

    public NoteListItem(PaintView paintNoteItem) {
        this.paintNoteItem = paintNoteItem;
        this.viewType = Constant.ITEM_PAINT_NOTE_VIEWTYPE;
    }

    public NoteListItem(PaintInfo paintInfo) {
        this.paintInfo = paintInfo;
        this.viewType = Constant.ITEM_PAINT_NOTE_VIEWTYPE;
    }

    public NoteListItem(MapsInfo mapsItem) {
        this.maps = mapsItem;
        this.viewType = Constant.ITEM_MAP_NOTE_VIEWTYPE;
    }

    public NoteListItem(AudioInfo audioNoteItem) {
        this.audioNoteItem = audioNoteItem;
        this.viewType = Constant.ITEM_AUDIO_NOTE_VIEWTYPE;
    }

    public NoteListItem(ImageInfo imageInfo) {
        this.imageInfo = imageInfo;
        this.viewType = Constant.ITEM_IMAGE_NOTE_VIEWTYPE;
    }

    public MapsInfo getMaps() {
        return maps;
    }

    public void setMaps(MapsInfo maps) {
        this.maps = maps;
    }

    public NoteUI getTextNoteItem() {
        return textNoteItem;
    }

    public void setTextNoteItem(NoteUI textNoteItem) {
        this.textNoteItem = textNoteItem;
    }

    public PaintView getPaintNoteItem() {
        return paintNoteItem;
    }

    public void setPaintNoteItem(PaintView paintNoteItem) {
        this.paintNoteItem = paintNoteItem;
    }

    public AudioInfo getAudioNoteItem() {
        return audioNoteItem;
    }

    public void setAudioNoteItem(AudioInfo audioNoteItem) {
        this.audioNoteItem = audioNoteItem;
    }

    public ImageInfo getImageInfo() {
        return imageInfo;
    }

    public void setImageInfo(ImageInfo imageInfo) {
        this.imageInfo = imageInfo;
    }

    public PaintInfo getPaintInfo() {
        return paintInfo;
    }

    public void setPaintInfo(PaintInfo paintInfo) {
        this.paintInfo = paintInfo;
    }

    public int getViewType() {
        return viewType;
    }

    public void setViewType(int viewType) {
        this.viewType = viewType;
    }
}
