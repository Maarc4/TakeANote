package com.example.takeanote.adapter;

import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.takeanote.R;
import com.example.takeanote.model.NoteListItem;
import com.example.takeanote.utils.OnNoteTypeClickListener;

public class AudioNoteViewHolder extends BaseViewHolder {

    ImageButton audioPlayButton;
    ImageView audioMenuItem;
    TextView audioTitle;
    View view;
    OnNoteTypeClickListener listener;

    public AudioNoteViewHolder(@NonNull View itemView, OnNoteTypeClickListener listener) {
        super( itemView );
        audioTitle = itemView.findViewById( R.id.audioNoteTitle );
        audioPlayButton = itemView.findViewById( R.id.audioPlayButton );
        view = itemView; // Aixo es per manejar el click, pero amb material card potser es diferent
        audioMenuItem = itemView.findViewById( R.id.audioMenuIcon );
        this.listener = listener;
    }

    @Override
    void setData(NoteListItem item) {
        /*Audio audioNote = item.getAudioNoteItem();
        audioTitle.setText(audioNote.getTitle());

        audioPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), "Coming soon. Audio Play Button.", Toast.LENGTH_SHORT).show();
            }
        });
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), "Coming soon AudioNote", Toast.LENGTH_SHORT).show();
                //listener.onNoteClick(item);
            }
        });
        audioMenuItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), "Coming soon AudioNote MenuOptions", Toast.LENGTH_SHORT).show();
                //listener.onNoteMenuClick(item, v);
            }
        });
    }*/
    }
}