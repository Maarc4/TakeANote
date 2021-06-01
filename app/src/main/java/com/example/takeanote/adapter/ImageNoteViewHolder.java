package com.example.takeanote.adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.example.takeanote.R;
import com.example.takeanote.model.ImageInfo;
import com.example.takeanote.model.NoteListItem;
import com.example.takeanote.utils.OnNoteTypeClickListener;


public class ImageNoteViewHolder extends BaseViewHolder {


    private final TextView imageTitle;
    private final ImageView imageContent;
    private final ImageView imageMenuItem;
    private final View view;
    private final OnNoteTypeClickListener listener;


    public ImageNoteViewHolder(@NonNull View itemView, OnNoteTypeClickListener listener) {
        super(itemView);
        imageTitle = itemView.findViewById(R.id.imageNoteTitle);
        imageContent = itemView.findViewById(R.id.imageNoteContent);
        view = itemView; // Aixo es per manejar el click, pero amb material card potser es diferent
        imageMenuItem = itemView.findViewById(R.id.imageMenuIcon);
        this.listener = listener;
    }

    @Override
    void setData(NoteListItem item) {
        ImageInfo imageInfo = item.getImageInfo();
        imageTitle.setText(imageInfo.getTitle());
        Glide.with(itemView.getContext()).load(imageInfo.getUri()).into(imageContent);
        view.setOnClickListener(v -> listener.onNoteClick(item));
        imageMenuItem.setOnClickListener(v -> listener.onNoteMenuClick(item, v));
    }
}
