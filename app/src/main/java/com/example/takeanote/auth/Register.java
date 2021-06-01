package com.example.takeanote.auth;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.takeanote.MainActivity;
import com.example.takeanote.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;


public class Register extends AppCompatActivity {

    protected ProgressBar progressBar;
    private TextInputEditText name, email, pwd, pwdConf;
    private TextInputLayout emailLayout, pwdConfLayout, pwdLayout, nameLayout;
    private RegistrerViewModel registrerViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getSupportActionBar().setTitle(getApplication().getResources().getString(R.string.create_new_account));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        registrerViewModel = new ViewModelProvider(this).get(RegistrerViewModel.class);

        Button sync = findViewById(R.id.createAccount);
        TextView loginAct = findViewById(R.id.login);
        name = findViewById(R.id.userName);
        email = findViewById(R.id.userEmail);
        pwd = findViewById(R.id.password);
        pwdConf = findViewById(R.id.passwordConfirm);

        emailLayout = findViewById(R.id.emailLayout);
        pwdConfLayout = findViewById(R.id.pwdConfirmLayout);
        pwdLayout = findViewById(R.id.pwdLayout);
        nameLayout = findViewById(R.id.nameLayout);

        progressBar = findViewById(R.id.progressBar2);

        sync.setOnClickListener(v ->
                registrerViewModel.register(name, email, pwd, pwdConf, emailLayout, pwdConfLayout,
                        pwdLayout, nameLayout).observe(Register.this, aBoolean -> {
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.postDelayed(() -> {
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        finish();
                    }, 500);
                }));

        loginAct.setOnClickListener(v -> startActivity(new Intent(v.getContext(), Login.class)));
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        startActivity(new Intent(this, MainActivity.class));
        finish();
        return super.onOptionsItemSelected(item);
    }

}