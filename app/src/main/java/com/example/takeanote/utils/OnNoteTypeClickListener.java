package com.example.takeanote.utils;

import android.view.View;

import com.example.takeanote.model.NoteListItem;

public interface OnNoteTypeClickListener {
    void onNoteClick(NoteListItem noteUI);

    void onNoteMenuClick(NoteListItem noteUI, View view);

    void onPlayClick(NoteListItem noteUI, View view);

}

