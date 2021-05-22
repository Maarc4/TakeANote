package com.example.takeanote.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.takeanote.R;
import com.example.takeanote.model.NoteListItem;
import com.example.takeanote.utils.Constant;
import com.example.takeanote.utils.OnNoteTypeClickListener;

import java.util.List;

public class NotesAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private List<NoteListItem> mdata;
    private OnNoteTypeClickListener listener;


    public NotesAdapter(List<NoteListItem> mdata, OnNoteTypeClickListener listener) {
        this.mdata = mdata;
        this.listener = listener;
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d( "NAda", "OnCreateViewHolder" );
        View view;
        switch (viewType) {
            case (Constant.ITEM_TEXT_NOTE_VIEWTYPE):
                Log.d( "NAda", "TEXT NOTE" );
                view = LayoutInflater.from( parent.getContext() ).inflate( R.layout.note_card_layout, parent, false );
                return new TextNoteViewHolder( view, listener );
            case (Constant.ITEM_PAINT_NOTE_VIEWTYPE):
                Log.d( "NAda", "PAINT NOTE" );
                view = LayoutInflater.from( parent.getContext() ).inflate( R.layout.paint_card_layout, parent, false );
                return new PaintNoteViewHolder( view, listener );
            case (Constant.ITEM_AUDIO_NOTE_VIEWTYPE):
                view = LayoutInflater.from( parent.getContext() ).inflate( R.layout.audio_card_layout, parent, false );
                return new AudioNoteViewHolder( view, listener );
            case (Constant.ITEM_IMAGE_NOTE_VIEWTYPE):
                Log.d( "NAda", "IMAGE NOTE" );
                view = LayoutInflater.from( parent.getContext() ).inflate( R.layout.image_card_layout, parent, false );
                return new ImageNoteViewHolder( view, listener );

            default:
                throw new IllegalArgumentException();
        }
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        holder.setData( mdata.get( position ) );
    }

    @Override
    public int getItemViewType(int position) {
        return mdata.get( position ).getViewType();
    }

    @Override
    public int getItemCount() {
        if (mdata != null) {
            return mdata.size();
        } else {
            Log.d( "SIZE", "List Empty" );
            return 0;
        }
    }
}
