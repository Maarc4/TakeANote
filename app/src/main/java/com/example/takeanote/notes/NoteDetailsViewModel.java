package com.example.takeanote.notes;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NoteDetailsViewModel extends ViewModel {

    FirebaseFirestore db;
    FirebaseUser user;
    Context context;
    Intent intent;
    ProgressBar progressBarSave;

    private String docId;
    private MutableLiveData<List<String>> info;
    private String nContent, nTitle;

    public NoteDetailsViewModel() {
        info = new MutableLiveData<>();
        this.db = FirebaseFirestore.getInstance();
        this.user = FirebaseAuth.getInstance().getCurrentUser();
    }

    public LiveData<List<String>> saveNote(Context context, Intent data, TextInputEditText title,
                                           TextInputEditText content, ProgressBar progressBarSave) {
        this.context = context;
        this.intent = data;
        this.progressBarSave = progressBarSave;

        List<String> list = new ArrayList<>();

        nTitle = title.getText().toString();
        nContent= content.getText().toString();

        if (nTitle.isEmpty() || nContent.isEmpty()) {
            Toast.makeText(context, "Cannot SAVE with an empty field.", Toast.LENGTH_SHORT).show();
            return info;
        }

        progressBarSave.setVisibility( View.VISIBLE);
        //save note
        DocumentReference docref = db.collection("notes").document(user.getUid()).collection("myNotes").document(intent.getStringExtra("noteId"));

        Map<String, Object> note = new HashMap<>();
        note.put("title", nTitle);
        note.put("content", nContent);

        docref.update(note).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(context, "Note SAVED", Toast.LENGTH_SHORT).show();
                List<String> list = new ArrayList<>();
                list.add(nTitle);
                list.add(nContent);
                info.setValue( list );
                //progressBarSave.setVisibility(View.INVISIBLE);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, "FAILED to save", Toast.LENGTH_SHORT).show();
                progressBarSave.setVisibility(View.VISIBLE);
            }
        });
        return info;

    }

    public LiveData<List<String>> deleteNote(Context context, String docID) {
        this.context = context;

        List<String> listEmpty = new ArrayList<>();
        this.docId = docID;
        DocumentReference docRef = db.collection("notes").document(user.getUid()).collection("myNotes").document(this.docId);
        docRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(context, "NoteUI deleted.", Toast.LENGTH_SHORT).show();
                info.setValue( listEmpty );
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, "FAILED to delete the note.", Toast.LENGTH_SHORT).show();
            }
        });
        return info;
    }

}
