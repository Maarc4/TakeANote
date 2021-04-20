package com.example.takeanote;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.takeanote.auth.Register;
import com.example.takeanote.model.NoteUI;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class MainActivityViewModel extends ViewModel {

    FirebaseFirestore db;
    //FirestoreRecyclerAdapter<NoteUI, MainActivity.NoteViewHolder> noteAdapter;
    FirebaseUser user;
    private Activity activity;
    private Context context;

    private MutableLiveData<List<NoteUI>> notesData;

    public MainActivityViewModel() {
        notesData = new MutableLiveData<>();
        this.db = FirebaseFirestore.getInstance();
        this.user = FirebaseAuth.getInstance().getCurrentUser();
    }

    public LiveData<List<NoteUI>> init(Activity activity) {
        this.activity = activity;
        this.context = activity.getApplicationContext();
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

    public void sync(){
        if (user.isAnonymous()) {
            activity.startActivity(new Intent(context, Register.class));
        } else {
            Toast.makeText(context, "You are connected", Toast.LENGTH_SHORT).show();
        }
    }

    public void checkUser() {
        // if user is real or not
        if (user.isAnonymous()) {
            displayAlert();
        } else {
            FirebaseAuth.getInstance().signOut();
            activity.startActivity( new Intent(context.getApplicationContext(), LoadScreen.class));
            activity.finish();
        }
    }

    public void displayAlert() {
        AlertDialog.Builder warning = new AlertDialog.Builder(activity)
                .setTitle("Are you sure?")
                .setMessage("You are logged in with Temporary Account. Loggin out will Delete All the notes.")
                .setPositiveButton("Sync NoteUI", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        activity.startActivity( new Intent(context, Register.class));
                        activity.finish();
                    }
                }).setNegativeButton("Logout", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        user.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                activity.startActivity(new Intent(context, LoadScreen.class));
                                activity.finish();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        });
                    }
                });
        warning.show();
    }

    public void menuConf(TextView email, TextView username) {
        if (user.isAnonymous()) {
            email.setVisibility( View.INVISIBLE);
            username.setText("Temporal account");
        } else {
            email.setText(user.getEmail());
            username.setText(user.getDisplayName());
        }
    }

}
