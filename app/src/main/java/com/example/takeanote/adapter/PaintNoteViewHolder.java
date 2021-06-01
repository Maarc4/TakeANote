package com.example.takeanote.adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.example.takeanote.R;
import com.example.takeanote.model.NoteListItem;
import com.example.takeanote.model.PaintInfo;
import com.example.takeanote.utils.OnNoteTypeClickListener;

public class PaintNoteViewHolder extends BaseViewHolder {

    private final TextView paintTitle;
    private final ImageView paintContent;
    private final ImageView paintMenuItem;
    private final View view;
    private final OnNoteTypeClickListener listener;

    public PaintNoteViewHolder(@NonNull View itemView, OnNoteTypeClickListener listener) {
        super(itemView);
        paintTitle = itemView.findViewById(R.id.paintNoteTitle);
        paintContent = itemView.findViewById(R.id.paintNoteContent);
        view = itemView; // Aixo es per manejar el click, pero amb material card potser es diferent
        paintMenuItem = itemView.findViewById(R.id.paintMenuIcon);
        this.listener = listener;
    }

    @Override
    void setData(NoteListItem item) {
        PaintInfo paintInfo = item.getPaintInfo();
        paintTitle.setText(paintInfo.getTitle());
        Glide.with(itemView.getContext()).load(paintInfo.getUri()).into(paintContent);
        view.setOnClickListener(v -> listener.onNoteClick(item));
        paintMenuItem.setOnClickListener(v -> listener.onNoteMenuClick(item, v));

    }
}
