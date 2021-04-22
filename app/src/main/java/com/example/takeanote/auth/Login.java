package com.example.takeanote.auth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.takeanote.LoadScreenViewModel;
import com.example.takeanote.MainActivity;
import com.example.takeanote.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

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

        loginViewModel = new ViewModelProvider( this ).get( LoginViewModel.class );

        lEmail = findViewById(R.id.email);
        lPassword = findViewById(R.id.lPassword);
        loginNow = findViewById(R.id.loginBtn);

        emailLayout = findViewById(R.id.emailLayout);
        pwdLayout = findViewById(R.id.pwdLayout);

        progressBar = findViewById(R.id.progressBar3);

        forgetPass = findViewById(R.id.forgotPasword);
        createAcc = findViewById(R.id.createAccount);



        showWarning();

        loginNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               loginViewModel.login( Login.this, lEmail, lPassword, emailLayout, pwdLayout, loginNow, forgetPass,
                       createAcc, progressBar).observe( Login.this, new Observer<FirebaseUser>() {
                   @Override
                   public void onChanged(FirebaseUser user) {
                       startActivity(new Intent(getApplicationContext(), MainActivity.class));
                       finish();
                   }
               } );
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

    public void showWarning() {
        final AlertDialog.Builder warning = new AlertDialog.Builder( this)
                .setMessage("Linking Existing Account Will delete all the temp notes. Create New Account To Save them.")
                .setPositiveButton("Save Notes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(getApplicationContext(), Register.class));
                        finish();
                    }
                }).setNegativeButton("Its Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                });

        warning.show();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        startActivity(new Intent(this, MainActivity.class));
        finish();
        return super.onOptionsItemSelected(item);
    }

}