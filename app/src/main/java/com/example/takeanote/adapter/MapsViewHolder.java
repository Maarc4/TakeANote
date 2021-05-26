package com.example.takeanote.adapter;

import android.content.ClipData;
import android.location.Address;
import android.location.Geocoder;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.takeanote.MapsActivity;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MapsViewHolder extends BaseViewHolder implements OnMapReadyCallback {

    private TextView text, address;
    private MapView mapView;
    private GoogleMap map;
    private View view;
    private MapsInfo mapsInfo;
    private ImageView menuIcon;
    private OnNoteTypeClickListener listener;
    private NoteListItem item;

    public MapsViewHolder(@NonNull View itemView, OnNoteTypeClickListener listener) {
        super( itemView );
        text = itemView.findViewById( R.id.mapNoteTitle );
        address = itemView.findViewById( R.id.mapNoteAddress );
        mapView = itemView.findViewById( R.id.map_view );
        this.listener = listener;
        view = itemView;
        menuIcon = itemView.findViewById( R.id.mapMenuIcon );
    }

    @Override
    void setData(NoteListItem item) {
        this.item = item;
        mapsInfo = item.getMaps();
        text.setText( mapsInfo.getTitle() );
        address.setText( "Address: " + mapsInfo.getAddress() );
        if (mapView!=null){
            mapView.onCreate( null );
            mapView.getMapAsync( this );
        }
        view.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onNoteClick( item );
            }
        } );
        menuIcon.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onNoteMenuClick( item, v );
            }
        } );

    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        MapsInitializer.initialize( view.getContext() );
        map = googleMap;
        setUpMap();
    }

    private void setUpMap() {
        if (map == null) return;
        map.setOnMapClickListener( v -> listener.onNoteClick( item ) );
        map.moveCamera( CameraUpdateFactory.newLatLngZoom(mapsInfo.getLatLng(), 10f));
        map.addMarker(new MarkerOptions().position(mapsInfo.getLatLng()));
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        map.getUiSettings().setScrollGesturesEnabled( false );
        map.getUiSettings().setZoomGesturesEnabled( false );
        mapView.onResume();
        mapView.onEnterAmbient( null );
    }

    public MapView getMapView() {
        return mapView;
    }
}
