package com.example.takeanote.adapter;

import android.os.Build;
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
import com.google.android.gms.maps.MapView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class NotesAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private List<NoteListItem> mdata;
    private List<NoteListItem> original;
    private final OnNoteTypeClickListener listener;
    private final List<MapView> mapViewList;

    public NotesAdapter(List<NoteListItem> mdata, OnNoteTypeClickListener listener) {
        this.mdata = mdata;
        this.original = new ArrayList<>();
        this.original.addAll(mdata);
        this.listener = listener;
        this.mapViewList = new ArrayList<>();
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d("NAda", "OnCreateViewHolder");
        View view;
        switch (viewType) {
            case (Constant.ITEM_TEXT_NOTE_VIEWTYPE):
                Log.d("NAda", "TEXT NOTE");
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_card_layout, parent, false);
                return new TextNoteViewHolder(view, listener);
            case (Constant.ITEM_PAINT_NOTE_VIEWTYPE):
                Log.d("NAda", "PAINT NOTE");
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.paint_card_layout, parent, false);
                return new PaintNoteViewHolder(view, listener);
            case (Constant.ITEM_AUDIO_NOTE_VIEWTYPE):
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.audio_card_layout, parent, false);
                return new AudioNoteViewHolder(view, listener);
            case (Constant.ITEM_IMAGE_NOTE_VIEWTYPE):
                Log.d("NAda", "IMAGE NOTE");
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_card_layout, parent, false);
                return new ImageNoteViewHolder(view, listener);
            case (Constant.ITEM_MAP_NOTE_VIEWTYPE):
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.map_card_layout, parent, false);
                MapsViewHolder map = new MapsViewHolder(view, listener);
                mapViewList.add(map.getMapView());
                return map;

            default:
                throw new IllegalArgumentException();
        }
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        holder.setData(mdata.get(position));
    }

    @Override
    public int getItemViewType(int position) {
        return mdata.get(position).getViewType();
    }

    @Override
    public int getItemCount() {
        if (mdata != null) {
            return mdata.size();
        } else {
            Log.d("SIZE", "List Empty");
            return 0;
        }
    }

    public List<MapView> getMapViewList() {
        return mapViewList;
    }

    public void filter(String s) {
        final String search = s.toLowerCase();
        original.size();
        mdata.size();
        if (search.length() == 0) {
            mdata.clear();
            mdata.addAll(original);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                mdata.clear();
                List<NoteListItem> collect = original.stream()
                        .filter(i -> {
                            switch (i.getViewType()) {
                                case 0:
                                    return i.getTextNoteItem().getTitle().toLowerCase().contains(search);
                                case 1:
                                    return i.getPaintInfo().getTitle().toLowerCase().contains(search);
                                case 3:
                                    return i.getImageInfo().getTitle().toLowerCase().contains(search);
                                case 2:
                                    return i.getAudioNoteItem().getTitle().toLowerCase().contains(search);
                                case 4:
                                    return i.getMaps().getTitle().toLowerCase().contains(search);
                                default:
                                    return Boolean.parseBoolean(null);
                            }
                        })
                        .collect(Collectors.toList());
                mdata.addAll(collect);
            } else {
                mdata.clear();
                for (NoteListItem i : original) {
                    switch (i.getViewType()) {
                        case 0:
                            if (i.getTextNoteItem().getTitle().toLowerCase().contains(search)) {
                                mdata.add(i);
                            }
                            break;
                        case 1:
                            if (i.getPaintInfo().getTitle().toLowerCase().contains(search)) {
                                mdata.add(i);
                            }
                            break;
                        case 3:
                            if (i.getImageInfo().getTitle().toLowerCase().contains(search)) {
                                mdata.add(i);
                            }
                            break;
                        case 2:
                            if (i.getAudioNoteItem().getTitle().toLowerCase().contains(search)) {
                                mdata.add(i);
                            }
                            break;
                        case 4:
                            if (i.getMaps().getTitle().toLowerCase().contains(search)) {
                                mdata.add(i);
                            }
                            break;
                        default:
                            break;
                    }
                }
            }
        }
        notifyDataSetChanged();
    }

    public void orderRecyclerView(int option) {
        List<NoteListItem> temp = new ArrayList<>();
        switch (option) {
            case 0:
                order(temp);
                mdata.clear();
                mdata.addAll(temp);
                notifyDataSetChanged();
                break;
            case 1:
                order(temp);
                Collections.reverse(temp);
                mdata.clear();
                mdata.addAll(temp);
                notifyDataSetChanged();
                break;

            case 2:
                Collections.sort(mdata, (o1, o2) -> String.valueOf(o1.getViewType()).compareTo(String.valueOf(o2.getViewType())));
                notifyDataSetChanged();
                break;
            default:
                break;
        }

    }

    private void order(List<NoteListItem> temp) {
        temp.clear();
        temp.addAll(mdata);
        Collections.sort(temp, (o1, o2) -> {
            String title1 = null, title2 = null;
            switch (o1.getViewType()) {
                case 0:
                    title1 = o1.getTextNoteItem().getTitle();
                    break;
                case 1:
                    title1 = o1.getPaintInfo().getTitle();
                    break;
                case 3:
                    title1 = o1.getImageInfo().getTitle();
                    break;
                case 2:
                    title1 = o1.getAudioNoteItem().getTitle();
                    break;
                case 4:
                    title1 = o1.getMaps().getTitle();
                    break;
                default:
                    break;
            }
            switch (o2.getViewType()) {
                case 0:
                    title2 = o2.getTextNoteItem().getTitle();
                    break;
                case 1:
                    title2 = o2.getPaintInfo().getTitle();
                    break;
                case 3:
                    title2 = o2.getImageInfo().getTitle();
                    break;
                case 2:
                    title2 = o2.getAudioNoteItem().getTitle();
                    break;
                case 4:
                    title2 = o2.getMaps().getTitle();
                    break;
                default:
                    break;
            }

            return title1.compareTo(title2);
        });
    }

    public List<NoteListItem> getMdata() {
        return mdata;
    }

    public List<NoteListItem> getOriginal() {
        return original;
    }

    public void setMdata(List<NoteListItem> mdata) {
        this.mdata = mdata;
    }

    public void setOriginal(List<NoteListItem> original) {
        this.original = original;
    }
}
