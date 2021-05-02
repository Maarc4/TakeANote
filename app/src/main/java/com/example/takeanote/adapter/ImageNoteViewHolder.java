package com.example.takeanote.adapter;

import android.view.View;

import androidx.annotation.NonNull;

import com.example.takeanote.model.NoteListItem;
import com.example.takeanote.utils.OnNoteTypeClickListener;

public class ImageNoteViewHolder extends BaseViewHolder {

    public ImageNoteViewHolder(@NonNull View itemView, OnNoteTypeClickListener listener) {
        super( itemView );
    }

    @Override
    void setData(NoteListItem item) {

    }
}
