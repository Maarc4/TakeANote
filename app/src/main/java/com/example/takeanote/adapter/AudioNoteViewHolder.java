package com.example.takeanote.adapter;

import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.takeanote.R;
import com.example.takeanote.model.AudioInfo;
import com.example.takeanote.model.NoteListItem;
import com.example.takeanote.utils.OnNoteTypeClickListener;

public class AudioNoteViewHolder extends BaseViewHolder {

    ImageButton audioPlayButton;
    ImageView audioMenuItem;
    TextView audioTitle;
    View view;
    OnNoteTypeClickListener listener;


    public AudioNoteViewHolder(@NonNull View itemView, OnNoteTypeClickListener listener) {
        super(itemView);
        audioTitle = itemView.findViewById(R.id.audioNoteTitle);
        audioPlayButton = itemView.findViewById(R.id.audioPlayButton);
        view = itemView; // Aixo es per manejar el click, pero amb material card potser es diferent
        audioMenuItem = itemView.findViewById(R.id.menuIcon);
        this.listener = listener;
    }

    @Override
    void setData(NoteListItem item) {
        AudioInfo audioNote = item.getAudioNoteItem();
        audioTitle.setText(audioNote.getTitle());

        audioPlayButton.setOnClickListener(v -> listener.onPlayClick(item, v));
        view.setOnClickListener(v -> listener.onNoteClick(item));
        audioMenuItem.setOnClickListener(v -> listener.onNoteMenuClick(item, v));
    }
}

