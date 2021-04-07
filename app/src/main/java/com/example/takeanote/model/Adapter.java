package com.example.takeanote.model;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.takeanote.NoteDetails;
import com.example.takeanote.R;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {
    List<String> titles;
    List<String> content;
    public Adapter(List<String> titles, List<String> content){
        this.titles = titles;
        this.content = content;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_card_layout,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (titles != null && position < getItemCount()) {
            holder.noteTitle.setText(titles.get(position));
            holder.noteContent.setText(content.get(position));
            //holder.mCardView.setCardBackgroundColor(holder.view.getResources().getRandomColor(),null);

            holder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Al clicar una nota, es mou a una activity nova (details)
                    Intent intent = new Intent(v.getContext(), NoteDetails.class);
                    intent.putExtra("title", titles.get(position));
                    intent.putExtra("content", content.get(position));
                    v.getContext().startActivity(intent);
                }
            });
        }
    }
/*
    private int getRandomColor() {
        List<Integer> colorCode = new ArrayList<>();
        colorCode.add(R.color.blue);
        colorCode.add(R.color.yellow);
        colorCode.add(R.color.skyblue);
        colorCode.add(R.color.lightPurple);
        colorCode.add(R.color.lightGreen);
        colorCode.add(R.color.gray);
        colorCode.add(R.color.pink);
        colorCode.add(R.color.red);
        colorCode.add(R.color.greenlight);
        colorCode.add(R.color.notgreen);

        Random rColor = new Random();
        int n = rColor.nextInt(colorCode.size());
        return colorCode.get(n);
    }*/

    @Override
    public int getItemCount() {
        return titles.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView noteTitle, noteContent;
        View view;
        //Per cambiar els colors random
        //MaterialCardView mCardView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            noteTitle = itemView.findViewById(R.id.noteTitle);
            noteContent = itemView.findViewById(R.id.noteContent);
            view = itemView; // Aixo es per manejar el click, pero amb material card potser es diferent
            //mCardView = itemView.findViewById(R.id.cardViewContent);
        }
    }
}
