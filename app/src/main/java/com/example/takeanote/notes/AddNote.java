package com.example.takeanote.notes;

import android.content.Intent;
import android.os.Bundle;

import com.example.takeanote.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

import io.github.yavski.fabspeeddial.FabSpeedDial;
import io.github.yavski.fabspeeddial.SimpleMenuListenerAdapter;

public class AddNote extends AppCompatActivity {
    FirebaseFirestore db;
    //No canviar a TextInputEditText (hint surt i no volem q surti)
    EditText noteTitle, noteContent;
    ProgressBar progressBarSave;
    FirebaseUser user;
    MaterialToolbar toolbar;
    Intent data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);
        toolbar = findViewById(R.id.addNote_toolbar);
        setSupportActionBar(toolbar);

        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        data = getIntent();
        noteContent = findViewById(R.id.addNoteContent);
        noteTitle = findViewById(R.id.addNoteTitle);


        progressBarSave = findViewById(R.id.addNote_progressBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FabSpeedDial fabSpeedDial = (FabSpeedDial) findViewById(R.id.fab_addNote);
        fabSpeedDial.setMenuListener(new SimpleMenuListenerAdapter() {
            @Override
            public boolean onMenuItemSelected(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.action_image:
                        Toast.makeText(AddNote.this, "Add IMAGE clicked.", Toast.LENGTH_SHORT).show();
                        break;
                }
                return false;
            }
        });
    }


    private void saveNote(EditText title, EditText content) {
        //Toast.makeText(AddNote.this, "Save btn clicked.", Toast.LENGTH_SHORT).show();
        String nTitle = noteTitle.getText().toString();
        String nContent = noteContent.getText().toString();

        if (nTitle.isEmpty() || nContent.isEmpty()) {
            Toast.makeText(AddNote.this, "Cannot SAVE with an empty field.", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBarSave.setVisibility(View.VISIBLE);
        //save note
        DocumentReference docref = db.collection("notes").document(user.getUid()).collection("myNotes").document();
        Map<String, Object> note = new HashMap<>();
        note.put("title", nTitle);
        note.put("content", nContent);

        docref.set(note).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(AddNote.this, "Note Added to database.", Toast.LENGTH_SHORT).show();
                //progressBarSave.setVisibility(View.INVISIBLE);
                onBackPressed();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AddNote.this, "FAILED to add note to database.", Toast.LENGTH_SHORT).show();
                progressBarSave.setVisibility(View.VISIBLE);
            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save:
                saveNote(noteTitle, noteContent);
                break;

            case android.R.id.home:
                onBackPressed();
                break;

            default:
                Toast.makeText(this, "Coming soon.", Toast.LENGTH_SHORT).show();

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.add_note_top_bar, menu);
        return true;
    }
}