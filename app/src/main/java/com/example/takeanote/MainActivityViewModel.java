package com.example.takeanote;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.takeanote.auth.Login;
import com.example.takeanote.auth.Register;
import com.example.takeanote.model.NoteUI;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class MainActivityViewModel extends AndroidViewModel {

    FirebaseFirestore db;
    FirebaseUser user;

    private MutableLiveData<List<NoteUI>> notesData;

    public MainActivityViewModel(@NonNull Application application) {
        super( application );
        notesData = new MutableLiveData<>();
        this.db = FirebaseFirestore.getInstance();
    }

    public LiveData<List<NoteUI>> init() {

        //user = auth.getCurrentUser();
        NoteUI note = new NoteUI();
        this.user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseUser finalUser = user;
        db.collection("notes").document(user.getUid()).collection("myNotes")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (queryDocumentSnapshots.isEmpty()) {
                            Log.d("TAG", "List Empty");
                            return;
                        } else {
                            List<NoteUI> notes = new ArrayList<>();
                            for (DocumentSnapshot q : queryDocumentSnapshots) {
                                String id = q.getId();
                                NoteUI note = q.toObject(NoteUI.class);
                                note.setId(id);
                                note.setUser(finalUser.getUid());
                                notes.add(note);
                            }
                            notesData.setValue(notes);
                            Log.d("TAG", "List Loaded");
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplication().getApplicationContext(), "Error loading notes!", Toast.LENGTH_SHORT).show();
            }
        });
        return notesData;
    }

    public void checkUserNav(NavigationView nav) {
        if (!user.isAnonymous()){
            nav.getMenu().clear();
            nav.inflateMenu( R.menu.nav_menu_loged_user );
        } else {
            nav.getMenu().clear();
            nav.inflateMenu( R.menu.nav_menu );
        }
    }

    public List<NoteUI> getData() {
        return notesData.getValue();
    }

    public void deleteNote(NoteUI deleteNoteUI) {
        //Log.d("docId ->", docId);
        List<NoteUI> oldData = notesData.getValue();
        List<NoteUI> newData = new ArrayList<NoteUI>();
        for (NoteUI note : oldData) {
            if (!note.getId().equals(deleteNoteUI.getId())) {
                newData.add(note);
            }
        }
        notesData.setValue(newData);

        DocumentReference docRef = db.collection("notes").document(user.getUid()).collection("myNotes").document(deleteNoteUI.getId());
        docRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getApplication().getApplicationContext(), "NoteUI deleted.", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplication().getApplicationContext(), "FAILED to delete the note.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public boolean sync() {
        if (user.isAnonymous()) {
            return true;
        } else {
            Toast.makeText(getApplication().getApplicationContext(), "You are connected", Toast.LENGTH_SHORT).show();
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
            email.setVisibility(View.INVISIBLE);
            username.setText("Temporal account");
        } else {
            email.setText(user.getEmail());
            username.setText(user.getDisplayName());
        }
    }


    public FirebaseUser getUser() {
        return user;
    }

}
