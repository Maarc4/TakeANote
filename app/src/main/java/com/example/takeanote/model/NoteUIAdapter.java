package com.example.takeanote.model;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.takeanote.R;

import java.util.List;

public class NoteUIAdapter extends RecyclerView.Adapter<NoteUIAdapter.NoteUIViewHolder> {

    private List<NoteUI> notes;

    private OnNoteClickListener listener;

    public interface OnNoteClickListener {
        void onNoteClick(NoteUI noteUI);

        void onNoteMenuClick(NoteUI noteUI, View view);
    }

    public NoteUIAdapter(List<NoteUI> notes, OnNoteClickListener listener) {
        this.listener = listener;
        this.notes = notes;
    }

    @NonNull
    @Override
    public NoteUIViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_card_layout, parent, false);
        return new NoteUIViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteUIViewHolder holder, int position) {
        NoteUI note = this.notes.get(position);
        holder.noteTitle.setText(note.getTitle());
        String content = note.getContent();
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
        holder.noteContent.setText(content);
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onNoteClick(note);
            }
        });
        holder.menuIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onNoteMenuClick(note, v);
            }
        });

    }

    @Override
    public int getItemCount() {
        return this.notes.size();
    }

    public class NoteUIViewHolder extends RecyclerView.ViewHolder {
        TextView noteTitle, noteContent;
        View view;
        ImageView menuIcon;

        public NoteUIViewHolder(@NonNull View itemView) {
            super(itemView);
            noteTitle = itemView.findViewById(R.id.noteTitle);
            noteContent = itemView.findViewById(R.id.noteContent);
            view = itemView; // Aixo es per manejar el click, pero amb material card potser es diferent
            //mCardView = itemView.findViewById(R.id.cardViewContent);
            menuIcon = itemView.findViewById(R.id.menuIcon);
        }
    }


}
