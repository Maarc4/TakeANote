package com.example.takeanote.auth;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.takeanote.LoadScreen;
import com.example.takeanote.MainActivity;
import com.example.takeanote.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginViewModel extends ViewModel {
    FirebaseAuth auth;
    FirebaseFirestore db;
    private MutableLiveData<FirebaseUser> user;
    private Activity activity;

    public LoginViewModel (){
        user = new MutableLiveData<>();
    }


    public LiveData<FirebaseUser> login(Activity activity, TextInputEditText lEmail, TextInputEditText lPassword,
                                        TextInputLayout emailLayout, TextInputLayout pwdLayout, Button loginNow,
                                        TextView forgetPass, TextView createAcc, ProgressBar progressBar){
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        this.activity = activity;
        int errors = 0;
        String mEmail = lEmail.getText().toString();
        String mPassword = lPassword.getText().toString();
        emailLayout.setError(null);
        pwdLayout.setError(null);

        if (mPassword.isEmpty()) {
            pwdLayout.setError("Cannot be empty.");
            errors++;
        }
        if (!isEmailValid(mEmail)) {
            emailLayout.setError("Email is not valid.");
            errors++;
        }
        if (errors != 0) {
            //activity.startActivity(new Intent(activity.getApplicationContext(), Login.class));
            //activity.finish();//Tornar a new Login
            Toast.makeText(activity.getApplicationContext(), "ESTA PASANT AMB ERROR LOGIN", Toast.LENGTH_SHORT).show();
            return user;
        }


        progressBar.setVisibility(View.VISIBLE);

        //TODO: arreglar login fallit i intentar tornar a fer login
        //TODO: mirar de fer que si el login sera ok, llavors borrar notes/user temp (POTSER s'arregla al def VMMV)
        if (auth.getCurrentUser().isAnonymous()) {
            //delete temp notes
            FirebaseUser user = auth.getCurrentUser();

            db.collection("notes").document(currentUser.getUid()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(activity.getApplicationContext(), "All Temp Notes are Deleted.", Toast.LENGTH_SHORT).show();
                }
            });

            // delete Temp user
            currentUser.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(activity.getApplicationContext(), "Temp user Deleted.", Toast.LENGTH_SHORT).show();
                }
            });
        }

        auth.signInWithEmailAndPassword(mEmail, mPassword)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        Toast.makeText(activity.getApplicationContext(), "Success !", Toast.LENGTH_SHORT).show();
                        user.setValue( FirebaseAuth.getInstance().getCurrentUser() );
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(activity.getApplicationContext(), "Login Failed. " + e.getMessage(), Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        });
        return user;
    }

    private boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

}
