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
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

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
    //No canviar a TextInputEditText (hint surt i no volem q surti)
    EditText noteTitle, noteContent;
    ProgressBar progressBarSave;
    MaterialToolbar toolbar;
    //Intent data;
    private AddNoteViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);
        toolbar = findViewById(R.id.addNote_toolbar);
        setSupportActionBar(toolbar);

        viewModel = new ViewModelProvider( this ).get( AddNoteViewModel.class );
        //data = getIntent();
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


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save:
                viewModel.saveNote( this, noteTitle, noteContent, progressBarSave ).observe( this, new Observer<Map<String, Object>>() {
                    @Override
                    public void onChanged(Map<String, Object> stringObjectMap) {
                        onBackPressed();
                    }
                } );
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