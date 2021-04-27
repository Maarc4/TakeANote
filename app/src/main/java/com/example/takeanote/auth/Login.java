package com.example.takeanote.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.takeanote.MainActivity;
import com.example.takeanote.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {
    TextInputEditText lEmail, lPassword;
    TextInputLayout emailLayout, pwdLayout;
    Button loginNow;
    TextView forgetPass, createAcc;
    ProgressBar progressBar;
    private LoginViewModel loginViewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Login to TakeANote");

        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        lEmail = findViewById(R.id.email);
        lPassword = findViewById(R.id.lPassword);
        loginNow = findViewById(R.id.loginBtn);

        emailLayout = findViewById(R.id.emailLayout);
        pwdLayout = findViewById(R.id.pwdLayout);

        progressBar = findViewById(R.id.progressBar3);

        forgetPass = findViewById(R.id.forgotPasword);
        createAcc = findViewById(R.id.createAccount);

        loginNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginViewModel.login(Login.this, lEmail, lPassword, emailLayout, pwdLayout,
                        progressBar).observe(Login.this, new Observer<FirebaseUser>() {
                    @Override
                    public void onChanged(FirebaseUser user) {
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        finish();
                    }
                });
            }
        });

        createAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Register.class));
            }
        });

        //TODO: Fer forgot password que envii un correu al user amb la pwd olvidada amb link per recuperar pwd al app(potser es molt dificil)
        forgetPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Login.this, "Coming soon.", Toast.LENGTH_SHORT).show();
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