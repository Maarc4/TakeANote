package com.example.takeanote.auth;

import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginViewModel extends ViewModel {
    FirebaseAuth auth;
    FirebaseFirestore db;
    private MutableLiveData<FirebaseUser> user;
    private Activity activity;

    public LoginViewModel() {
        user = new MutableLiveData<>();
    }


    public LiveData<FirebaseUser> login(Activity activity, TextInputEditText lEmail, TextInputEditText lPassword,
                                        TextInputLayout emailLayout, TextInputLayout pwdLayout, Button loginNow,
                                        TextView forgetPass, TextView createAcc, ProgressBar progressBar) {
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
            //Toast.makeText(activity.getApplicationContext(), "ESTA PASANT AMB ERROR LOGIN", Toast.LENGTH_SHORT).show();
            return user;
        }


        progressBar.setVisibility(View.VISIBLE);

        auth.signInWithEmailAndPassword(mEmail, mPassword)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        if (currentUser.isAnonymous()) {

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
                        Toast.makeText(activity.getApplicationContext(), "Success !", Toast.LENGTH_SHORT).show();
                        user.setValue(FirebaseAuth.getInstance().getCurrentUser());
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        if (e.getClass().equals( FirebaseAuthInvalidUserException.class )){
                            emailLayout.setError( "the email doesn't match with a registered user email" );
                        } else {
                            pwdLayout.setError( "the password is not correct" );
                        }
                        progressBar.setVisibility( View.GONE );
                    }
        });
        return user;
    }

    private boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

}
