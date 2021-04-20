package com.example.takeanote.notes;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.takeanote.MainActivity;
import com.example.takeanote.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import io.github.yavski.fabspeeddial.FabSpeedDial;
import io.github.yavski.fabspeeddial.SimpleMenuListenerAdapter;

public class NoteDetails extends AppCompatActivity {
    Intent data;
    FirebaseFirestore db;
    ProgressBar progressBarSave;
    FirebaseUser user;
    MaterialToolbar toolbar;
    private NoteDetailsViewModel noteDetailsViewModel;
    private TextInputEditText content, title;
    private Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_details);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        noteDetailsViewModel = new ViewModelProvider( this ).get(NoteDetailsViewModel.class);
        db = FirebaseFirestore.getInstance();
        progressBarSave = findViewById(R.id.noteDetails_progressBar);
        user = FirebaseAuth.getInstance().getCurrentUser();

        this.activity = (MainActivity) getParent();
        data = getIntent();

        noteDetailsViewModel = new ViewModelProvider( this ).get( NoteDetailsViewModel.class );

        //Aixo si es canvia a ¿materialedittext? segurament sha de canviar
        content = findViewById(R.id.noteDetailsContent);
        title = findViewById(R.id.noteDetailsTitle);
        content.setMovementMethod(new ScrollingMovementMethod());

        content.setText(data.getStringExtra("content"));
        title.setText(data.getStringExtra("title"));
        //Aixo pel color de la nota al color de dins
        //content.setBackgroundColor(getResources().getColor(data.getIntExtra("code",0));

        FabSpeedDial fabSpeedDial = (FabSpeedDial) findViewById(R.id.fab_speed_dial);
        fabSpeedDial.setMenuListener(new SimpleMenuListenerAdapter() {
            @Override
            public boolean onMenuItemSelected(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.action_image:
                        Toast.makeText(NoteDetails.this, "Add IMAGE clicked.", Toast.LENGTH_SHORT).show();
                        break;
                }
                return false;
            }
        });
    }

    private void saveNote(){
        noteDetailsViewModel.saveNote( NoteDetails.this, user, db, data, title, content,
                progressBarSave).observe( this, new Observer<List<String>>() {
            @Override
            public void onChanged(List<String> strings) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        } );


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
                saveNote();
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
        noteDetailsViewModel.deleteNote(this, docId, db, user).observe( this, new Observer<List<String>>() {
            @Override
            public void onChanged(List<String> strings) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        } );

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.details_top_bar, menu);
        return true;
    }
}