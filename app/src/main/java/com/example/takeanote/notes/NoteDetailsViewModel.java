package com.example.takeanote.notes;

import android.app.Application;
import android.content.Intent;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.takeanote.R;
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

public class NoteDetailsViewModel extends AndroidViewModel {

    private final FirebaseFirestore db;
    private final FirebaseUser user;
    protected Intent intent;
    ProgressBar progressBarSave;

    private final MutableLiveData<List<String>> info;
    private String nContent, nTitle;

    public NoteDetailsViewModel(@NonNull Application application) {
        super(application);
        info = new MutableLiveData<>();
        this.db = FirebaseFirestore.getInstance();
        this.user = FirebaseAuth.getInstance().getCurrentUser();
    }


    public LiveData<List<String>> saveNote(Intent intent, TextInputEditText title,
                                           TextInputEditText content, ProgressBar progressBarSave) {

        this.intent = intent;
        this.progressBarSave = progressBarSave;

        nTitle = title.getText().toString();
        nContent = content.getText().toString();

        if (nTitle.isEmpty() || nContent.isEmpty()) {
            Toast.makeText(getApplication().getApplicationContext(), getApplication().getResources().getString(R.string.toast_empty_field), Toast.LENGTH_SHORT).show();
            return info;
        }

        progressBarSave.setVisibility(View.VISIBLE);
        //save note
        DocumentReference docref = db.collection("notes").document(user.getUid()).collection("myNotes").document(intent.getStringExtra("noteId"));

        Map<String, Object> note = new HashMap<>();
        note.put("title", nTitle);
        note.put("content", nContent);

        docref.update(note).addOnSuccessListener(aVoid -> {
            Toast.makeText(getApplication().getApplicationContext(), getApplication().getResources().getString(R.string.toast_note_add_failed_db), Toast.LENGTH_SHORT).show();
            List<String> list = new ArrayList<>();
            list.add(nTitle);
            list.add(nContent);
            info.setValue(list);
        }).addOnFailureListener(e -> {
            Toast.makeText(getApplication().getApplicationContext(), getApplication().getResources().getString(R.string.toast_note_add_failed_db), Toast.LENGTH_SHORT).show();
            progressBarSave.setVisibility(View.VISIBLE);
        });
        return info;

    }

    public LiveData<List<String>> deleteNote(String docID) {

        List<String> listEmpty = new ArrayList<>();
        DocumentReference docRef = db.collection("notes").document(user.getUid()).collection("myNotes").document(docID);
        docRef.delete().addOnSuccessListener(aVoid -> {
            Toast.makeText(getApplication().getApplicationContext(), getApplication().getResources().getString(R.string.toast_note_deleted), Toast.LENGTH_SHORT).show();
            info.setValue(listEmpty);
        }).addOnFailureListener(e -> Toast.makeText(getApplication().getApplicationContext(), getApplication().getResources().getString(R.string.toast_failed_delete), Toast.LENGTH_SHORT).show());
        return info;
    }

}
