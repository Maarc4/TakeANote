package com.example.takeanote.notes;

import android.content.Intent;
import android.os.Bundle;

import com.example.takeanote.MainActivity;
import com.example.takeanote.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

public class NoteDetails extends AppCompatActivity {
    Intent data;
    FirebaseFirestore db;
    ProgressBar progressBarSave;
    FirebaseUser user;
    MaterialToolbar toolbar;
    TextInputEditText content, title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_details);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        db = FirebaseFirestore.getInstance();
        progressBarSave = findViewById(R.id.noteDetails_progressBar);
        user = FirebaseAuth.getInstance().getCurrentUser();

        data = getIntent();


        //Aixo si es canvia a ¿materialedittext? segurament sha de canviar
        content = findViewById(R.id.noteDetailsContent);
        title = findViewById(R.id.noteDetailsTitle);
        content.setMovementMethod(new ScrollingMovementMethod());

        content.setText(data.getStringExtra("content"));
        title.setText(data.getStringExtra("title"));
        //Aixo pel color de la nota al color de dins
        //content.setBackgroundColor(getResources().getColor(data.getIntExtra("code",0));

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveNote(title, content);
            }
        });
    }

    private void saveNote(EditText title, EditText content) {
        //Toast.makeText(AddNote.this, "Save btn clicked.", Toast.LENGTH_SHORT).show();
        String nTitle = title.getText().toString();
        String nContent = content.getText().toString();

        if (nTitle.isEmpty() || nContent.isEmpty()) {
            Toast.makeText(NoteDetails.this, "Cannot SAVE with an empty field.", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBarSave.setVisibility(View.VISIBLE);
        //save note
        DocumentReference docref = db.collection("notes").document(user.getUid()).collection("myNotes").document(data.getStringExtra("docId"));

        Map<String, Object> note = new HashMap<>();
        note.put("title", nTitle);
        note.put("content", nContent);

        docref.update(note).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(NoteDetails.this, "Note SAVED", Toast.LENGTH_SHORT).show();
                //progressBarSave.setVisibility(View.INVISIBLE);
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(NoteDetails.this, "FAILED to save", Toast.LENGTH_SHORT).show();
                progressBarSave.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete:
                deleteNote();
                break;

            case R.id.share:
                //TODO: Fer share un cop haguem fet tot lo que toca
                Toast.makeText(this, "Share button clicked.", Toast.LENGTH_SHORT).show();
                break;

            case R.id.save:
                saveNote(title, content);
                break;

            case android.R.id.home:
                onBackPressed();
                break;

            default:
                throw new IllegalStateException("Unexpected value: " + item.getItemId());
        }
        return super.onOptionsItemSelected(item);
    }

    public void deleteNote() {
        String docId = data.getStringExtra("noteId");

        DocumentReference docRef = db.collection("notes").document(user.getUid()).collection("myNotes").document(docId);
        docRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(NoteDetails.this, "Note deleted.", Toast.LENGTH_SHORT).show();
                onBackPressed();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(NoteDetails.this, "FAILED to delete the note.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.details_top_bar, menu);
        return true;
    }
}