package com.example.takeanote.notes;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.takeanote.R;
import com.google.android.material.appbar.MaterialToolbar;

public class AddNote extends AppCompatActivity {
    private EditText noteTitle, noteContent;
    private ProgressBar progressBarSave;
    private AddNoteViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);
        MaterialToolbar toolbar = findViewById(R.id.addImage_toolbar);
        setSupportActionBar(toolbar);

        viewModel = new ViewModelProvider(this).get(AddNoteViewModel.class);
        noteContent = findViewById(R.id.addNoteContent);
        noteTitle = findViewById(R.id.addImageTitle);

        progressBarSave = findViewById(R.id.addNote_progressBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save:
                viewModel.saveNote(noteTitle, noteContent, progressBarSave).observe(this, stringObjectMap -> onBackPressed());
                break;

            case android.R.id.home:
                onBackPressed();
                break;

            default:
                Toast.makeText(this, getApplication().getResources().getString(R.string.toast_coming_soon), Toast.LENGTH_SHORT).show();
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