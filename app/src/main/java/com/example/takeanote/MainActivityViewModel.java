package com.example.takeanote;

import android.app.Application;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.takeanote.model.ImageInfo;
import com.example.takeanote.model.MapsInfo;
import com.example.takeanote.model.AudioInfo;
import com.example.takeanote.model.NoteListItem;
import com.example.takeanote.model.NoteUI;
import com.example.takeanote.model.PaintInfo;
import com.example.takeanote.utils.Constant;
import com.google.android.gms.common.util.ScopeUtil;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.common.collect.Maps;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class MainActivityViewModel extends AndroidViewModel {

    FirebaseFirestore db;
    FirebaseUser user;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private MutableLiveData<List<NoteListItem>> notesData;
    final long ONE_MEGABYTE = 1024 * 1024;
    private final String STORAGE_URL = "gs://takeanote-a0e9a.appspot.com/";


    public MainActivityViewModel(@NonNull Application application) {
        super( application );
        notesData = new MutableLiveData<>();
        this.db = FirebaseFirestore.getInstance();
    }


    public LiveData<List<NoteListItem>> init() {

        this.user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseUser finalUser = user;
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReferenceFromUrl( STORAGE_URL );
        List<NoteListItem> notes = new ArrayList<>();
        boolean succes = false;


        //IMAGE NOTES
       db.collection( "notes" ).document( user.getUid() ).collection( "imageNotes" )
                .get()
                .addOnSuccessListener( new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (queryDocumentSnapshots.isEmpty()) {
                            Log.d( "MAVM", "List Empty" );
                            return;
                        } else {
                            Log.d( "Entra", "Entra" );
                            for (DocumentSnapshot q : queryDocumentSnapshots) {
                                storageReference.child( q.getString( "url" ) )
                                        .getDownloadUrl().
                                        addOnSuccessListener( new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                ImageInfo ii = q.toObject( ImageInfo.class );

                                                ii.setUri( uri );
                                                ii.setTitle( q.getString( "title" ) );
                                                ii.setId( q.getId() );

                                                ///pi.setTitle(q.getString("title")); //No hi ha re guardat al title
                                                /// PaintView pv = q.toObject(PaintView.class);
                                                NoteListItem noteListItem = new NoteListItem( ii );

                                                notes.add( noteListItem );
                                            }
                                        } ).addOnFailureListener( new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d( "MAVM ->ERROR", "PRINGAT" + e.getMessage() );
                                    }
                                } );
                            }

                        }
                        notesData.setValue( notes );
                    }
                } ).addOnFailureListener( new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText( getApplication().getApplicationContext(), "Error loading IMAGE notes!", Toast.LENGTH_SHORT ).show();
            }
        } );

        //PAINT NOTES
        db.collection( "notes" ).document( user.getUid() ).collection( "paintNotes" )
                .get()
                .addOnSuccessListener( new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (queryDocumentSnapshots.isEmpty()) {
                            Log.d( "MAVM", "List Empty" );
                            return;
                        } else {
                            Log.d( "Entra", "Entra" );
                            for (DocumentSnapshot q : queryDocumentSnapshots) {
                                storageReference.child( q.getString( "url" ) )
                                        .getDownloadUrl().
                                        addOnSuccessListener( new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {

                                                PaintInfo pi = q.toObject( PaintInfo.class );
                                                pi.setUri( uri );
                                                pi.setTitle( q.getString( "title" ) );

                                                pi.setId( q.getId() );
                                                pi.setUriPath(uri.getPath());

                                                //pi.setTitle(q.getString("title")); //No hi ha re guardat al title

                                                // PaintView pv = q.toObject(PaintView.class);
                                                NoteListItem noteListItem = new NoteListItem( pi );
                                                notes.add( noteListItem );
                                            }
                                        } ).addOnFailureListener( new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d( "MAVM ->ERROR", "PRINGAT" + e.getMessage() );
                                    }
                                } );
                            }

                        }
                        notesData.setValue( notes );
                    }
                } ).addOnFailureListener( new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText( getApplication().getApplicationContext(), "Error loading PAINT notes!", Toast.LENGTH_SHORT ).show();
                    }
                } );

            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText( getApplication().getApplicationContext(), "Error loading PAINT notes!", Toast.LENGTH_SHORT ).show();
            }
        } );

        //TEXT NOTES
         db.collection( "notes" ).document( user.getUid() ).collection( "myNotes" )
                .get()
                .addOnSuccessListener( new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (queryDocumentSnapshots.isEmpty()) {
                            Log.d( "MAVM", "List Empty" );
                            return;
                        } else {
                            for (DocumentSnapshot q : queryDocumentSnapshots) {
                                NoteUI note = q.toObject( NoteUI.class );
                                String id = q.getId();
                                note.setId( id );
                                note.setUser( finalUser.getUid() );
                                NoteListItem noteListItem = new NoteListItem( note );
                                notes.add( noteListItem );
                            }
                            //notesData.postValue(notes);
                        }
                        notesData.setValue( notes );
                        Log.d( "MAVM", "notes amb text? " + notes.size() );
                    }
                } ).addOnFailureListener( new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText( getApplication().getApplicationContext(), "Error loading TEXT notes!", Toast.LENGTH_SHORT ).show();
            }
        } );

         //MAPS NOTES
        db.collection( "notes" ).document( user.getUid() ).collection( "myMaps" )
                .get()
                .addOnSuccessListener( new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (queryDocumentSnapshots.isEmpty()) {
                            Log.d( "MAVM", "List Empty" );
                            return;
                        } else {
                            for (DocumentSnapshot q : queryDocumentSnapshots) {
                                MapsInfo mapsInfo = q.toObject( MapsInfo.class );
                                String id = q.getId();
                                mapsInfo.setId( id );
                                mapsInfo.setLatLng( new LatLng((double) q.get( "lat" ), (double) q.get("lng")) );
                                mapsInfo.setAddress( q.get("address").toString() );
                                NoteListItem noteListItem = new NoteListItem( mapsInfo );
                                notes.add( noteListItem );
                            }
                            //notesData.postValue(notes);
                        }
                        notesData.setValue( notes );
                        Log.d( "MAVM", "notes amb text? " + notes.size() );
                    }
                } ).addOnFailureListener( new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText( getApplication().getApplicationContext(), "Error loading TEXT notes!", Toast.LENGTH_SHORT ).show();
            }
        } );
        //Audio NOTES
        db.collection( "notes" ).document( user.getUid() ).collection( "AudioNotes" )
                .get()
                .addOnSuccessListener( new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (queryDocumentSnapshots.isEmpty()) {
                            Log.d( "MAVM", "List Empty" );
                            return;
                        } else {
                            Log.d("Entra", "Entra" );
                            for (DocumentSnapshot q : queryDocumentSnapshots) {
                                storageReference.child( q.getString( "url" ) )
                                        .getDownloadUrl().
                                        addOnSuccessListener( new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {

                                                AudioInfo pi = q.toObject( AudioInfo.class );
                                                pi.setUri(uri);
                                                pi.setTitle( q.getString( "title" ) );
                                                pi.setId( q.getId());
                                                pi.setPath(q.getString("url"));
                                                //pi.setTitle(q.getString("title")); //No hi ha re guardat al title

                                                // PaintView pv = q.toObject(PaintView.class);
                                                NoteListItem noteListItem = new NoteListItem( pi );
                                                notes.add( noteListItem );
                                            }
                                        } ).addOnFailureListener( new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d( "MAVM ->ERROR", "PRINGAT" + e.getMessage() );
                                    }
                                } );
                            }

                        }
                        notesData.setValue( notes );
                    }
                } ).addOnFailureListener( new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText( getApplication().getApplicationContext(), "Error loading PAINT notes!", Toast.LENGTH_SHORT ).show();
            }
        } );

        return notesData;
    }

    public void checkUserNav(NavigationView nav) {
        if (!user.isAnonymous()) {
            nav.getMenu().clear();
            nav.inflateMenu( R.menu.nav_menu_loged_user );
        } else {
            nav.getMenu().clear();
            nav.inflateMenu( R.menu.nav_menu );
        }
    }

    /*public List<NoteUI> getData() {
        return notesData.getValue();
    }*/

    public void deleteNote(NoteListItem noteListItem, int viewType) {
        //Log.d("docId ->", docId);
        List<NoteListItem> oldData = notesData.getValue();
        List<NoteListItem> newData = new ArrayList<NoteListItem>();
        for (NoteListItem note : oldData) {
            //Borrar nota text
            if (note.getViewType() == viewType) {
                if (note.getViewType() == Constant.ITEM_TEXT_NOTE_VIEWTYPE) {
                    NoteUI noteUI = note.getTextNoteItem();
                    NoteUI noteToDelete = noteListItem.getTextNoteItem();
                    if (!noteUI.getId().equals( noteToDelete.getId() )) {
                        newData.add( note );
                    }
                }
                //Borrar nota paint
                else if (note.getViewType() == Constant.ITEM_PAINT_NOTE_VIEWTYPE) {
                    PaintInfo paintNote = note.getPaintInfo();
                    PaintInfo paintToDelete = note.getPaintInfo();
                    if (!paintNote.getUri().toString().equals( paintToDelete.getUri().toString() )) {
                        newData.add( note );
                    }
                } else if (note.getViewType() == Constant.ITEM_MAP_NOTE_VIEWTYPE) {
                    MapsInfo map = note.getMaps();
                    MapsInfo mapToDelete = noteListItem.getMaps();
                    if (!map.getTitle().equals( mapToDelete.getTitle() )) {
                        newData.add( note );
                    }
                }
                //Borrar nota Image
                else if (note.getViewType() == Constant.ITEM_IMAGE_NOTE_VIEWTYPE) {
                    ImageInfo imageNote = note.getImageInfo();
                    ImageInfo imageToDelete = note.getImageInfo();
                    if (!imageNote.getUri().toString().equals( imageToDelete.getUri().toString() )) {
                        newData.add( note );
                    }
                }
                //Borrar nota Audio
                else if (note.getViewType() == Constant.ITEM_AUDIO_NOTE_VIEWTYPE) {
                    AudioInfo AudioNote = note.getAudioNoteItem();
                    AudioInfo AudioToDelete = noteListItem.getAudioNoteItem();
                    if (!AudioNote.getUri().toString().equals( AudioToDelete.getUri().toString() )) {
                        newData.add( note );
                    }
                }
            } else {
                newData.add( note );
            }
        }
        DocumentReference docRef;
        switch (viewType) {
            case Constant.ITEM_TEXT_NOTE_VIEWTYPE:
                NoteUI textNote = noteListItem.getTextNoteItem();
                docRef = db.collection( "notes" ).document( user.getUid() ).collection( "myNotes" ).document( textNote.getId() );
                docRef.delete().addOnSuccessListener( new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText( getApplication().getApplicationContext(), "NoteUI deleted.", Toast.LENGTH_SHORT ).show();
                        notesData.setValue( newData );
                    }
                } ).addOnFailureListener( new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText( getApplication().getApplicationContext(), "FAILED to delete the note.", Toast.LENGTH_SHORT ).show();
                    }
                } );
                break;
            case Constant.ITEM_PAINT_NOTE_VIEWTYPE:
                PaintInfo pinfo = noteListItem.getPaintInfo();

                db.collection( "notes" ).document( user.getUid() ).collection( "paintNotes" ).document( pinfo.getId() )
                        .get()
                        .addOnSuccessListener( new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                storageReference.child( documentSnapshot.getString( "url" ) ).delete();
                                notesData.setValue( newData );
                                DocumentReference docRef = db.collection( "notes" ).document( user.getUid() ).collection( "paintNotes" ).document( pinfo.getId() );
                                docRef.delete().addOnSuccessListener( new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText( getApplication().getApplicationContext(), "PaintNote deleted.", Toast.LENGTH_SHORT ).show();
                                        notesData.setValue( newData );
                                    }
                                } ).addOnFailureListener( new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText( getApplication().getApplicationContext(), "FAILED to delete the note.", Toast.LENGTH_SHORT ).show();
                                    }
                                } );
                            }
                        } ).addOnFailureListener( new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText( getApplication().getApplicationContext(), "Error Deleting PAINT note!", Toast.LENGTH_SHORT ).show();
                    }
                } );

                break;
            case Constant.ITEM_MAP_NOTE_VIEWTYPE:
                MapsInfo mapsInfo = noteListItem.getMaps();
                docRef = db.collection( "notes" ).document( user.getUid() ).collection( "myMaps" ).document( mapsInfo.getId() );
                docRef.delete().addOnSuccessListener( new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText( getApplication().getApplicationContext(), "Map deleted.", Toast.LENGTH_SHORT ).show();
                        notesData.setValue( newData );
                    }
                } ).addOnFailureListener( new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText( getApplication().getApplicationContext(), "FAILED to delete the map.", Toast.LENGTH_SHORT ).show();
                    }
                } );
                break;
            case Constant.ITEM_IMAGE_NOTE_VIEWTYPE:
                ImageInfo iinfo = noteListItem.getImageInfo();
                db.collection( "notes" ).document( user.getUid() ).collection( "imageNotes" ).document( iinfo.getId() )
                        .get()
                        .addOnSuccessListener( new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                storageReference.child( documentSnapshot.getString( "url" ) ).delete();
                                notesData.setValue( newData );
                            }
                        } ).addOnFailureListener( new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText( getApplication().getApplicationContext(), "Error Deleting PAINT note!", Toast.LENGTH_SHORT ).show();
                    }
                } );
            case Constant.ITEM_AUDIO_NOTE_VIEWTYPE:
                AudioInfo aud = noteListItem.getAudioNoteItem();
                db.collection( "notes" ).document( user.getUid() ).collection( "AudioNotes" ).document( aud.getId() )
                        .get()
                        .addOnSuccessListener( new OnSuccessListener<DocumentSnapshot>() {

                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                storageReference.child( documentSnapshot.getString( "url" ) ).delete();
                                notesData.setValue( newData );
                                DocumentReference docRef = db.collection( "notes" ).document( user.getUid() ).collection( "AudioNotes" ).document( aud.getId() );
                                docRef.delete().addOnSuccessListener( new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText( getApplication().getApplicationContext(), "PaintNote deleted.", Toast.LENGTH_SHORT ).show();
                                        notesData.setValue( newData );
                                    }
                                } ).addOnFailureListener( new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText( getApplication().getApplicationContext(), "FAILED to delete the note.", Toast.LENGTH_SHORT ).show();
                                    }
                                } );
                            }
                        } ).addOnFailureListener( new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText( getApplication().getApplicationContext(), "Error Deleting Audio note!", Toast.LENGTH_SHORT ).show();
                    }
                } );
                break;
            default:
                throw new IllegalArgumentException();
        }

    }

    public boolean sync() {
        if (user.isAnonymous()) {
            return true;
        } else {
            Toast.makeText( getApplication().getApplicationContext(), "You are connected", Toast.LENGTH_SHORT ).show();
            return false;
        }
    }

    public boolean userAnonymous() {
        // if user is real or not
        if (user.isAnonymous()) {
            return true;
        } else {
            FirebaseAuth.getInstance().signOut();
            return false;
        }
    }


    public void menuConf(TextView email, TextView username) {
        if (user.isAnonymous()) {
            email.setVisibility( View.INVISIBLE );
            username.setText( "Temporal account" );
        } else {
            email.setText( user.getEmail() );
            username.setText( user.getDisplayName() );
        }
    }


    public FirebaseUser getUser() {
        return user;
    }

}
