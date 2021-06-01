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

import com.example.takeanote.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddNoteViewModel extends AndroidViewModel {

    private final FirebaseFirestore db;
    private final FirebaseUser user;

    private final MutableLiveData<Map<String, Object>> note;

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
            Toast.makeText( getApplication().getApplicationContext(), getApplication().getResources().getString(R.string.toast_empty_field), Toast.LENGTH_SHORT ).show();
            return note;
        }

        progressBarSave.setVisibility( View.VISIBLE );
        //save note
        DocumentReference docref = db.collection( "notes" ).document( user.getUid() ).collection( "myNotes" ).document();
        Map<String, Object> newNote = new HashMap<>();
        newNote.put( "title", nTitle );
        newNote.put( "content", nContent );
        newNote.put( "type", "textNote" );

        docref.set( newNote ).addOnSuccessListener(aVoid -> {
            Toast.makeText( getApplication().getApplicationContext(), getApplication().getResources().getString(R.string.toast_note_added_db), Toast.LENGTH_SHORT ).show();
            note.setValue( newNote );
            progressBarSave.setVisibility( View.INVISIBLE );
        }).addOnFailureListener(e -> {
            Toast.makeText( getApplication().getApplicationContext(), getApplication().getResources().getString(R.string.toast_note_add_failed_db), Toast.LENGTH_SHORT ).show();
            progressBarSave.setVisibility( View.VISIBLE );
        });
        return note;
    }

}
