package com.example.takeanote;

import android.os.Bundle;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.Toast;

public class AddNote extends AppCompatActivity {
    FirebaseFirestore fStore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);
        MaterialToolbar toolbar = findViewById(R.id.addNote_toolbar);
        setSupportActionBar(toolbar);

        fStore = FirebaseFirestore.getInstance();


        //TODO: Fer que el fab sigui per escollir tipus de input o fer una bottombar
        //TODO: posar el save button a la top bar (amb share?)
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(AddNote.this, "Save btn clicked.", Toast.LENGTH_SHORT).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}