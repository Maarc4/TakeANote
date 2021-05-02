package com.example.takeanote.adapter;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.takeanote.model.NoteListItem;

public abstract class BaseViewHolder extends RecyclerView.ViewHolder {

    abstract void setData(NoteListItem item);


    public BaseViewHolder(@NonNull View itemView) {
        super(itemView);
    }
}
