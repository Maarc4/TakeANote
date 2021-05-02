package com.example.takeanote.notes;

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

import java.util.List;

public class NoteDetails extends AppCompatActivity {
    Intent data;
    ProgressBar progressBarSave;
    MaterialToolbar toolbar;
    private NoteDetailsViewModel noteDetailsViewModel;
    private TextInputEditText content, title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_note_details );
        toolbar = findViewById( R.id.toolbar );
        setSupportActionBar( toolbar );
        getSupportActionBar().setDisplayHomeAsUpEnabled( true );
        noteDetailsViewModel = new ViewModelProvider( this ).get( NoteDetailsViewModel.class );
        progressBarSave = findViewById( R.id.noteDetails_progressBar );
        data = getIntent();

        noteDetailsViewModel = new ViewModelProvider( this ).get( NoteDetailsViewModel.class );

        //Aixo si es canvia a ¿materialedittext? segurament sha de canviar
        content = findViewById( R.id.noteDetailsContent );
        title = findViewById( R.id.noteDetailsTitle );
        content.setMovementMethod( new ScrollingMovementMethod() );

        content.setText( data.getStringExtra( "content" ) );
        title.setText( data.getStringExtra( "title" ) );
        //Aixo pel color de la nota al color de dins
        //content.setBackgroundColor(getResources().getColor(data.getIntExtra("code",0));

    }

    private void saveNote() {
        noteDetailsViewModel.saveNote( data, title, content,
                progressBarSave ).observe( this, new Observer<List<String>>() {
            @Override
            public void onChanged(List<String> strings) {
                startActivity( new Intent( getApplicationContext(), MainActivity.class ) );
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
                Toast.makeText( this, "Share button clicked.", Toast.LENGTH_SHORT ).show();
                break;

            case R.id.save:
                saveNote();
                break;

            case android.R.id.home:
                onBackPressed();
                break;

            default:
                throw new IllegalStateException( "Unexpected value: " + item.getItemId() );
        }
        return super.onOptionsItemSelected( item );
    }

    public void deleteNote() {
        String docId = data.getStringExtra( "noteId" );
        noteDetailsViewModel.deleteNote( docId ).observe( this, new Observer<List<String>>() {
            @Override
            public void onChanged(List<String> strings) {
                startActivity( new Intent( getApplicationContext(), MainActivity.class ) );
            }
        } );

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate( R.menu.details_top_bar, menu );
        return true;
    }
}