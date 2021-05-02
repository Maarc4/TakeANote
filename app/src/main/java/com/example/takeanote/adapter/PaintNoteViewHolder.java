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

    private TextView paintTitle;
    private ImageView paintContent, paintMenuItem;
    private View view;
    private OnNoteTypeClickListener listener;

    public PaintNoteViewHolder(@NonNull View itemView, OnNoteTypeClickListener listener) {
        super( itemView );
        paintTitle = itemView.findViewById( R.id.paintNoteTitle );
        paintContent = itemView.findViewById( R.id.paintNoteContent );
        view = itemView; // Aixo es per manejar el click, pero amb material card potser es diferent
        paintMenuItem = itemView.findViewById( R.id.paintMenuIcon );
        this.listener = listener;
    }

    @Override
    void setData(NoteListItem item) {
        PaintInfo paintInfo = item.getPaintInfo();
        //PaintView paintNote = item.getPaintNoteItem();
        paintTitle.setText( paintInfo.getTitle() );
        //paintContent.setImageBitmap(paintInfo.getBmp());
        //Glide.with(itemView.getContext()).load(paintInfo.getBmp()).into(paintContent);
        Glide.with( itemView.getContext() ).load( paintInfo.getUri() ).into( paintContent );
        view.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onNoteClick( item );
            }
        } );
        paintMenuItem.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onNoteMenuClick( item, v );
            }
        } );

    }
}
