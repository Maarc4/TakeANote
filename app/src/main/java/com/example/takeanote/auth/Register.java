package com.example.takeanote.auth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.takeanote.MainActivity;
import com.example.takeanote.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class Register extends AppCompatActivity {
    TextInputEditText name, email, pwd, pwdConf;
    TextInputLayout emailLayout, pwdConfLayout, pwdLayout, nameLayout;
    Button sync;
    TextView loginAct;
    ProgressBar progressBar;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getSupportActionBar().setTitle("Create New Account");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        name = findViewById(R.id.userName);
        email = findViewById(R.id.userEmail);
        pwd = findViewById(R.id.password);
        pwdConf = findViewById(R.id.passwordConfirm);

        emailLayout = findViewById(R.id.emailLayout);
        pwdConfLayout = findViewById(R.id.pwdConfirmLayout);
        pwdLayout = findViewById(R.id.pwdLayout);
        nameLayout = findViewById(R.id.nameLayout);

        sync = findViewById(R.id.createAccount);
        loginAct = findViewById(R.id.login);
        progressBar = findViewById(R.id.progressBar2);

        auth = FirebaseAuth.getInstance();

        sync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userName = name.getText().toString();
                String userEmail = email.getText().toString();
                String userPass = pwd.getText().toString();
                String userConfPass = pwdConf.getText().toString();
                int errors = 0;
                nameLayout.setError(null);
                emailLayout.setError(null);
                pwdLayout.setError(null);
                pwdConfLayout.setError(null);
                //TODO: posar metode per comprovació password i email

                // Comprovar i setejar errors
                if (userName.isEmpty()) {
                    nameLayout.setError("Cannot be empty.");
                    errors++;
                }
                //TODO: fer que error i toggle pwd no es sobreposin o posar un on text changed o algo aixi
                if (userPass.isEmpty()) {
                    pwdLayout.setError("Cannot be empty.");
                    errors++;
                }
                if (userConfPass.isEmpty()) {
                    pwdConfLayout.setError("Cannot be empty.");
                    errors++;
                }
                if (!isEmailValid(userEmail)) {
                    emailLayout.setError("Email not valid");
                    errors++;
                }
                if (!userPass.equals(userConfPass)) {
                    pwdConfLayout.setError("Passwords do not match");
                    errors++;
                }
                if (errors != 0) return;
                Toast.makeText(Register.this, "ESTA PASANT AMB ERROR REGISTER", Toast.LENGTH_SHORT).show();


                AuthCredential credential = EmailAuthProvider.getCredential(userEmail, userPass);
                auth.getCurrentUser().linkWithCredential(credential).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        Toast.makeText(Register.this, "Notes are Syncced", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        FirebaseUser usr = auth.getCurrentUser();
                        UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                                .setDisplayName(userName)
                                .build();
                        usr.updateProfile(request);
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //TODO: canviar per comprovar pwd abans
                        if (e.getMessage().contains("email")) emailLayout.setError(e.getMessage());
                        else if (e.getMessage().contains("password"))
                            pwdLayout.setError(e.getMessage());
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

    boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}