package com.example.takeanote.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.takeanote.MainActivity;
import com.example.takeanote.R;


public class Register extends AppCompatActivity {

    Button sync;
    TextView loginAct;
    ProgressBar progressBar;
    private RegistrerViewModel registrerViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getSupportActionBar().setTitle("Create New Account");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        registrerViewModel = new ViewModelProvider( this ).get( RegistrerViewModel.class );

        sync = findViewById(R.id.createAccount);
        loginAct = findViewById(R.id.login);

        //TODO
        progressBar = findViewById(R.id.progressBar2);

        sync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registrerViewModel.register(Register.this ).observe(Register.this, new Observer<Boolean>() {
                    @Override
                    public void onChanged(Boolean aBoolean) {
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        finish();
                    }
                });
            }
        });

        loginAct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(v.getContext(), Login.class));
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        startActivity(new Intent(this, MainActivity.class));
        finish();
        return super.onOptionsItemSelected(item);
    }

}