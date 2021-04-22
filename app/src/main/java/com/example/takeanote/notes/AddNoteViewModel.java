package com.example.takeanote.notes;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddNoteViewModel extends ViewModel {

    FirebaseFirestore db;
    FirebaseUser user;
    Context context;

    private MutableLiveData<Map<String, Object>> note;

    public AddNoteViewModel() {
        note = new MutableLiveData<>();
        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
    }

    public LiveData<Map<String, Object>> saveNote(Activity activity, EditText title, EditText content, ProgressBar progressBarSave) {
        String nTitle = title.getText().toString();
        String nContent = content.getText().toString();
        this.context = activity.getApplicationContext();

        if (nTitle.isEmpty() || nContent.isEmpty()) {
            Toast.makeText(context, "Cannot SAVE with an empty field.", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(context, AddNote.class);
            activity.startActivity(intent);
            activity.finish();
            return note;
        }

        progressBarSave.setVisibility( View.VISIBLE);
        //save note
        DocumentReference docref = db.collection("notes").document(user.getUid()).collection("myNotes").document();
        Map<String, Object> newNote = new HashMap<>();
        newNote.put("title", nTitle);
        newNote.put("content", nContent);

        docref.set(newNote).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(context, "Note Added to database.", Toast.LENGTH_SHORT).show();
                note.setValue( newNote );
                progressBarSave.setVisibility(View.INVISIBLE);
                //onBackPressed();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(context, "FAILED to add note to database.", Toast.LENGTH_SHORT).show();
                progressBarSave.setVisibility(View.VISIBLE);
            }
        });
        return note;
    }

}
