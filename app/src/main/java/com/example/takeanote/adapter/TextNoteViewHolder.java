package com.example.takeanote.adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.takeanote.R;
import com.example.takeanote.model.NoteListItem;
import com.example.takeanote.model.NoteUI;
import com.example.takeanote.utils.OnNoteTypeClickListener;

public class TextNoteViewHolder extends BaseViewHolder {
    private TextView noteTitle, noteContent;
    private View view;
    private ImageView menuIcon;
    private OnNoteTypeClickListener listener;

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

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(v.getContext(), "Coming soon TextNote", Toast.LENGTH_SHORT).show();
                listener.onNoteClick(item);
            }
        });
        menuIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(v.getContext(), "Coming soon TextNote MenuOptions", Toast.LENGTH_SHORT).show();
                listener.onNoteMenuClick(item, v);
            }
        });
    }

    private String modifyContent(String content) {
        if (content.length() > 20 | content.contains("\n")) {
            String newContent = "";
            char salto = '\n';
            for (int i = 0; i < 20; i++) {
                if (content.charAt(i) == salto) {
                    break;
                }
                newContent += content.charAt(i);
            }
            newContent += "...";
            content = newContent;
        }
        return content;
    }
}
