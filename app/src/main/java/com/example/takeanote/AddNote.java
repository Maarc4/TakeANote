package com.example.takeanote;

import android.os.Bundle;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

public class AddNote extends AppCompatActivity {
    FirebaseFirestore db;
    EditText noteTitle, noteContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);
        MaterialToolbar toolbar = findViewById(R.id.addNote_toolbar);
        setSupportActionBar(toolbar);

        db = FirebaseFirestore.getInstance();
        noteContent = findViewById(R.id.addNoteContent);
        noteTitle = findViewById(R.id.addNoteTitle);

        //TODO: Fer que el fab sigui per escollir tipus de input(text,foto,audio...) o fer una bottombar
        //TODO: posar el save button a la top bar (amb share?)
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(AddNote.this, "Save btn clicked.", Toast.LENGTH_SHORT).show();
                String nTitle = noteTitle.getText().toString();
                String nContent = noteContent.getText().toString();
                Log.d("nTitle ->",nTitle);
                Log.d("nContent ->",nContent);
                if(nTitle.isEmpty() || nContent.isEmpty()){
                    Toast.makeText(AddNote.this, "Cannot SAVE with an empty field.", Toast.LENGTH_SHORT).show();
                    return;
                }
                //save note
                DocumentReference docref = db.collection("notes").document();
                Map<String, Object> note = new HashMap<>();
                note.put("title",nTitle);
                note.put("content",nContent);
                Log.d("noteTitle->",note.get("title").toString());
                Log.d("noteContent->",note.get("content").toString());

                docref.set(note).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(AddNote.this, "Note Added to database.", Toast.LENGTH_SHORT).show();
                        onBackPressed();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AddNote.this, "FAILED to add note to database.", Toast.LENGTH_SHORT).show();
                        onBackPressed();
                    }
                });
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}