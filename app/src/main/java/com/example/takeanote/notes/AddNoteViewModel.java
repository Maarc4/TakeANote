package com.example.takeanote.notes;

import android.app.Application;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddNoteViewModel extends AndroidViewModel {

    FirebaseFirestore db;
    FirebaseUser user;

    private MutableLiveData<Map<String, Object>> note;

    public AddNoteViewModel(@NonNull Application application) {
        super( application );
        note = new MutableLiveData<>();
        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
    }


    public LiveData<Map<String, Object>> saveNote(EditText title, EditText content, ProgressBar progressBarSave) {
        String nTitle = title.getText().toString();
        String nContent = content.getText().toString();


        if (nTitle.isEmpty() || nContent.isEmpty()) {
            Toast.makeText(getApplication().getApplicationContext(), "Cannot SAVE with an empty field.", Toast.LENGTH_SHORT).show();
            //Intent intent = new Intent(getApplication().getApplicationContext(), AddNote.class);
            //activity.startActivity(intent);
            //activity.finish();
            return note;
        }

        progressBarSave.setVisibility(View.VISIBLE);
        //save note
        DocumentReference docref = db.collection("notes").document(user.getUid()).collection("myNotes").document();
        Map<String, Object> newNote = new HashMap<>();
        newNote.put("title", nTitle);
        newNote.put("content", nContent);
        newNote.put("type","textNote");

        docref.set(newNote).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getApplication().getApplicationContext(), "Note Added to database.", Toast.LENGTH_SHORT).show();
                note.setValue(newNote);
                progressBarSave.setVisibility(View.INVISIBLE);
                //onBackPressed();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplication().getApplicationContext(), "FAILED to add note to database.", Toast.LENGTH_SHORT).show();
                progressBarSave.setVisibility(View.VISIBLE);
            }
        });
        return note;
    }

}
