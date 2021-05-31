package com.example.takeanote.adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.takeanote.R;
import com.example.takeanote.model.MapsInfo;
import com.example.takeanote.model.NoteListItem;
import com.example.takeanote.utils.OnNoteTypeClickListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsViewHolder extends BaseViewHolder implements OnMapReadyCallback {

    private final TextView text;
    private final TextView address;
    private final MapView mapView;
    private GoogleMap map;
    private final View view;
    private MapsInfo mapsInfo;
    private final ImageView menuIcon;
    private final OnNoteTypeClickListener listener;
    private NoteListItem item;

    public MapsViewHolder(@NonNull View itemView, OnNoteTypeClickListener listener) {
        super(itemView);
        text = itemView.findViewById(R.id.mapNoteTitle);
        address = itemView.findViewById(R.id.mapNoteAddress);
        mapView = itemView.findViewById(R.id.map_view);
        this.listener = listener;
        view = itemView;
        menuIcon = itemView.findViewById(R.id.mapMenuIcon);
    }

    @Override
    void setData(NoteListItem item) {
        this.item = item;
        mapsInfo = item.getMaps();
        text.setText(mapsInfo.getTitle());
        address.setText(String.format("%s%s", view.getResources().getString(R.string.maps_address), mapsInfo.getAddress()));
        if (mapView != null) {
            mapView.onCreate(null);
            mapView.getMapAsync(this);
        }
        view.setOnClickListener(v -> listener.onNoteClick(item));
        menuIcon.setOnClickListener(v -> listener.onNoteMenuClick(item, v));

    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        MapsInitializer.initialize(view.getContext());
        map = googleMap;
        setUpMap();
    }

    private void setUpMap() {
        if (map == null) return;
        map.setOnMapClickListener(v -> listener.onNoteClick(item));
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(mapsInfo.getLatLng(), 10f));
        map.addMarker(new MarkerOptions().position(mapsInfo.getLatLng()));
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        map.getUiSettings().setScrollGesturesEnabled(false);
        map.getUiSettings().setZoomGesturesEnabled(false);
        mapView.onResume();
        mapView.onEnterAmbient(null);
    }

    public MapView getMapView() {
        return mapView;
    }
}
