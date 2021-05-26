package com.example.takeanote.model;

import com.example.takeanote.AddAudio;
import com.example.takeanote.PaintView;
import com.example.takeanote.utils.Constant;

public class NoteListItem {

    private NoteUI textNoteItem;
    private PaintView paintNoteItem;
    private AudioInfo audioNoteItem;
    //private Image imageNoteItem;
    private PaintInfo paintInfo;
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

    public NoteListItem(AudioInfo audioNoteItem) {
        this.audioNoteItem = audioNoteItem;
        this.viewType = Constant.ITEM_AUDIO_NOTE_VIEWTYPE;
    }

    /*public NoteListItem(Image imageNoteItem) {
        this.imageNoteItem = imageNoteItem;
        this.viewType = Constant.ITEM_IMAGE_NOTE_VIEWTYPE;
    }*/

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

    /*public Image getImageNoteItem() {
        return imageNoteItem;
    }

    public void setImageNoteItem(Image imageNoteItem) {
        this.imageNoteItem = imageNoteItem;
    }*/

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
