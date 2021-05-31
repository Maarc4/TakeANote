package com.example.takeanote.adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.takeanote.R;
import com.example.takeanote.model.NoteListItem;
import com.example.takeanote.model.NoteUI;
import com.example.takeanote.utils.OnNoteTypeClickListener;

public class TextNoteViewHolder extends BaseViewHolder {
    private final TextView noteTitle;
    private final TextView noteContent;
    private final View view;
    private final ImageView menuIcon;
    private final OnNoteTypeClickListener listener;

    public TextNoteViewHolder(@NonNull View itemView, OnNoteTypeClickListener listener) {
        super(itemView);
        noteTitle = itemView.findViewById(R.id.noteTitle);
        noteContent = itemView.findViewById(R.id.noteContent);
        view = itemView; // Aixo es per manejar el click, pero amb material card potser es diferent
        menuIcon = itemView.findViewById(R.id.menuIcon);
        this.listener = listener;
    }

    @Override
    void setData(NoteListItem item) {
        NoteUI textNote = item.getTextNoteItem();
        noteTitle.setText(textNote.getTitle());
        String content = modifyContent(textNote.getContent());
        noteContent.setText(content);

        view.setOnClickListener(v -> listener.onNoteClick(item));
        menuIcon.setOnClickListener(v -> listener.onNoteMenuClick(item, v));
    }

    private String modifyContent(String content) {
        if (content.length() > 20 | content.contains("\n")) {
            StringBuilder newContent = new StringBuilder();
            char salto = '\n';
            for (int i = 0; i < 20; i++) {
                if (content.charAt(i) == salto) {
                    break;
                }
                newContent.append(content.charAt(i));
            }
            newContent.append("...");
            content = newContent.toString();
        }
        return content;
    }
}
