package com.example.takeanote;

import android.app.Application;
import android.location.Address;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapsViewModel extends AndroidViewModel {

    FirebaseFirestore db;
    FirebaseUser user;
    private MutableLiveData<Map<String, Object>> map;

    public MapsViewModel(@NonNull Application application) {
        super( application );
        map = new MutableLiveData<>();
        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
    }

    public LiveData<Map<String, Object>> updateMaps(String title, LatLng latLng, String address, String id) {
        if (title.isEmpty()) {
            Toast.makeText( getApplication().getApplicationContext(), "Cannot SAVE with an empty field.", Toast.LENGTH_SHORT ).show();
            return map;
        }
        DocumentReference docref = db.collection( "notes" ).document( user.getUid() ).collection( "myMaps" ).document(id);
        Map<String, Object> newNote = new HashMap<>();
        newNote.put( "title", title );
        newNote.put( "lat", latLng.latitude );
        newNote.put( "lng", latLng.longitude );
        newNote.put( "address", address);

        docref.update( newNote ).addOnSuccessListener( new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText( getApplication().getApplicationContext(), "Map updated in database.", Toast.LENGTH_SHORT ).show();
                map.setValue( newNote );
                //progressBarSave.setVisibility( View.INVISIBLE );
                //onBackPressed();
            }
        } ).addOnFailureListener( new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText( getApplication().getApplicationContext(), "FAILED to update map to database.", Toast.LENGTH_SHORT ).show();
                //progressBarSave.setVisibility( View.VISIBLE );
            }
        } );
        return map;
    }

    public LiveData<Map<String, Object>> saveMaps(String title, LatLng latLng, String address) {
        if (title.isEmpty()) {
            Toast.makeText( getApplication().getApplicationContext(), "Cannot SAVE with an empty field.", Toast.LENGTH_SHORT ).show();
            return map;
        }
        DocumentReference docref = db.collection( "notes" ).document( user.getUid() ).collection( "myMaps" ).document();
        Map<String, Object> newNote = new HashMap<>();
        newNote.put( "title", title );
        newNote.put( "lat", latLng.latitude );
        newNote.put( "lng", latLng.longitude );
        newNote.put( "address", address);
        newNote.put( "type", "mapNote" );

        docref.set( newNote ).addOnSuccessListener( new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText( getApplication().getApplicationContext(), "Map Added to database.", Toast.LENGTH_SHORT ).show();
                map.setValue( newNote );
                //progressBarSave.setVisibility( View.INVISIBLE );
                //onBackPressed();
            }
        } ).addOnFailureListener( new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText( getApplication().getApplicationContext(), "FAILED to add map to database.", Toast.LENGTH_SHORT ).show();
                //progressBarSave.setVisibility( View.VISIBLE );
            }
        } );
        return map;
    }

    public void confItems(PopupMenu pop, List<Address> addressList) {
        MenuItem item1, item2, item3, item4, item5;
        item1 = pop.getMenu().getItem( 0 );
        item2 = pop.getMenu().getItem( 1 );
        item3 = pop.getMenu().getItem( 2 );
        item4 = pop.getMenu().getItem( 3 );
        item5 = pop.getMenu().getItem( 4 );
        if (addressList == null || addressList.isEmpty()){
            item1.setVisible( false );
        } else {
            item1.setVisible( true );
            item1.setTitle( addressList.get(0).getAddressLine(0));
        }
        if (addressList == null || addressList.size() < 2){
            item2.setVisible( false );
        } else {
            item2.setVisible( true );
            item2.setTitle( addressList.get(1).getLocality());
        }
        if (addressList == null || addressList.size() < 3){
            item3.setVisible( false );
        } else {
            item3.setVisible( true );
            item3.setTitle( addressList.get(2).getLocality());
        }
        if (addressList == null || addressList.size() < 4){
            item4.setVisible( false );
        } else {
            item4.setVisible( true );
            item4.setTitle( addressList.get(3).getLocality());
        }

        if (addressList == null || addressList.size() < 5){
            item5.setVisible( false );
        } else {
            item5.setVisible( true );
            item5.setTitle( addressList.get(4).getLocality());
        }
    }

}
