package com.example.takeanote;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.takeanote.model.NoteUI;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class MainActivityViewModel extends ViewModel {

    FirebaseFirestore db;
    //FirestoreRecyclerAdapter<NoteUI, MainActivity.NoteViewHolder> noteAdapter;
    FirebaseUser user;
    FirebaseAuth auth;
    private Context context;

    private MutableLiveData<List<NoteUI>> notesData;

    public MainActivityViewModel() {
        notesData = new MutableLiveData<>();
    }

    public LiveData<List<NoteUI>> init(Context context, FirebaseAuth auth, FirebaseUser user, FirebaseFirestore db) {
        this.context = context;
        this.db = db;
        this.auth = auth;
        this.user = user;
        //user = auth.getCurrentUser();
        NoteUI note = new NoteUI();
        FirebaseUser finalUser = user;
        db.collection( "notes" ).document( user.getUid() ).collection( "myNotes" )
                .get()
                .addOnSuccessListener( new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (queryDocumentSnapshots.isEmpty()) {
                            Log.d( "TAG", "List Empty" );
                            return;
                        } else {
                            List<NoteUI> notes = new ArrayList<>();
                            for (DocumentSnapshot q : queryDocumentSnapshots) {
                                String id = q.getId();
                                NoteUI note = q.toObject( NoteUI.class );
                                note.setId( id );
                                note.setUser( finalUser.getUid() );
                                notes.add( note );
                            }
                            notesData.setValue( notes );
                            Log.d( "TAG", "List Loaded" );
                        }
                    }
                } ).addOnFailureListener( new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText( context, "Error loading notes!", Toast.LENGTH_SHORT ).show();
            }
        } );
        return notesData;
    }

    public List<NoteUI> getData() {
        return notesData.getValue();
    }

    public void deleteNote(NoteUI deleteNoteUI) {
        //Log.d("docId ->", docId);
        List<NoteUI> oldData = notesData.getValue();
        List<NoteUI> newData = new ArrayList<NoteUI>();
        for (NoteUI note : oldData) {
            if (!note.getId().equals( deleteNoteUI.getId() )) {
                newData.add( note );
            }
        }
        notesData.setValue( newData );

        DocumentReference docRef = db.collection( "notes" ).document( user.getUid() ).collection( "myNotes" ).document( deleteNoteUI.getId() );
        docRef.delete().addOnSuccessListener( new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText( context, "NoteUI deleted.", Toast.LENGTH_SHORT ).show();
            }
        } ).addOnFailureListener( new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText( context, "FAILED to delete the note.", Toast.LENGTH_SHORT ).show();
            }
        } );
    }


}
