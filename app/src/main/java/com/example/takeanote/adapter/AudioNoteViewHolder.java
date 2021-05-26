package com.example.takeanote.adapter;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.takeanote.AddAudio;
import com.example.takeanote.R;
import com.example.takeanote.model.AudioInfo;
import com.example.takeanote.model.NoteListItem;
import com.example.takeanote.utils.OnNoteTypeClickListener;

import java.io.IOException;

public class AudioNoteViewHolder extends BaseViewHolder {

    ImageButton audioPlayButton;
    ImageView audioMenuItem;
    TextView audioTitle;
    View view;
    OnNoteTypeClickListener listener;
    private MediaPlayer mediaplayer;

    public AudioNoteViewHolder(@NonNull View itemView, OnNoteTypeClickListener listener) {
        super( itemView );
        audioTitle = itemView.findViewById( R.id.audioNoteTitle );
        audioPlayButton = itemView.findViewById( R.id.audioPlayButton );
        view = itemView; // Aixo es per manejar el click, pero amb material card potser es diferent
        audioMenuItem = itemView.findViewById( R.id.menuIcon );
        this.listener = listener;
    }

    @Override
    void setData(NoteListItem item) {
        AudioInfo audioNote = item.getAudioNoteItem();
        audioTitle.setText(audioNote.getTitle());

        audioPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onPlayClick(item,v);
            }
        });
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(v.getContext(), "Coming soon AudioNote", Toast.LENGTH_SHORT).show();
                listener.onNoteClick(item);
            }
        });
        audioMenuItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(v.getContext(), "Coming soon AudioNote MenuOptions", Toast.LENGTH_SHORT).show();
                listener.onNoteMenuClick(item, v);
            }
        });
    }
    }

