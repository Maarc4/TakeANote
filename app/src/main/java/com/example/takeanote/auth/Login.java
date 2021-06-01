package com.example.takeanote.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.takeanote.MainActivity;
import com.example.takeanote.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class Login extends AppCompatActivity {

    private TextInputEditText lEmail, lPassword;
    private TextInputLayout emailLayout, pwdLayout;
    private ProgressBar progressBar;
    private LoginViewModel loginViewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.login_takeANote);

        loginViewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        Button loginNow = findViewById(R.id.loginBtn);

        TextView forgetPass = findViewById(R.id.forgotPasword);
        TextView createAcc = findViewById(R.id.createAccount);

        lEmail = findViewById(R.id.email);
        lPassword = findViewById(R.id.lPassword);

        emailLayout = findViewById(R.id.emailLayout);
        pwdLayout = findViewById(R.id.pwdLayout);

        progressBar = findViewById(R.id.progressBar3);

        loginNow.setOnClickListener(v -> loginViewModel.login(lEmail, lPassword, emailLayout, pwdLayout, progressBar)
                .observe(Login.this, user -> {
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    finish();
                }));

        createAcc.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), Register.class)));

        forgetPass.setOnClickListener(v -> Toast.makeText(Login.this, getApplication().getResources().getString(R.string.toast_coming_soon), Toast.LENGTH_SHORT).show());
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        startActivity(new Intent(this, MainActivity.class));
        finish();
        return super.onOptionsItemSelected(item);
    }

}