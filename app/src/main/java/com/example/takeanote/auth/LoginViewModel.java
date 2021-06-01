package com.example.takeanote.auth;

import android.app.Application;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.takeanote.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginViewModel extends AndroidViewModel {

    private FirebaseFirestore db;
    private final MutableLiveData<FirebaseUser> user;

    public LoginViewModel(@NonNull Application application) {
        super(application);
        user = new MutableLiveData<>();
    }


    public LiveData<FirebaseUser> login(TextInputEditText lEmail, TextInputEditText lPassword,
                                        TextInputLayout emailLayout, TextInputLayout pwdLayout,
                                        ProgressBar progressBar) {

        FirebaseAuth auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        int errors = 0;
        String mEmail = lEmail.getText().toString();
        String mPassword = lPassword.getText().toString();
        emailLayout.setError(null);
        pwdLayout.setError(null);

        if (mPassword.isEmpty()) {
            pwdLayout.setError(getApplication().getResources().getString(R.string.error_empty));
            errors++;
        }
        if (!isEmailValid(mEmail)) {
            emailLayout.setError(getApplication().getResources().getString(R.string.error_email_not_valid));
            errors++;
        }
        if (errors != 0) {
            return user;
        }

        progressBar.setVisibility(View.VISIBLE);

        auth.signInWithEmailAndPassword(mEmail, mPassword)
                .addOnSuccessListener(authResult -> {
                    if (currentUser.isAnonymous()) {

                        db.collection("notes").document(currentUser.getUid()).delete().addOnSuccessListener(aVoid ->
                                Toast.makeText(getApplication().getApplicationContext(), getApplication().getResources().getString(R.string.toast_temp_notes_deleted), Toast.LENGTH_SHORT).show());
                        // delete Temp user
                        currentUser.delete().addOnSuccessListener(aVoid ->
                                Toast.makeText(getApplication().getApplicationContext(), getApplication().getResources().getString(R.string.toast_temp_user_deleted), Toast.LENGTH_SHORT).show());
                    }
                    Toast.makeText(getApplication().getApplicationContext(), getApplication().getResources().getString(R.string.toast_success), Toast.LENGTH_SHORT).show();
                    user.setValue(FirebaseAuth.getInstance().getCurrentUser());
                }).addOnFailureListener(e -> {
            if (e.getClass().equals(FirebaseAuthInvalidUserException.class)) {
                emailLayout.setError(getApplication().getResources().getString(R.string.error_email_not_match_registered));
            } else {
                pwdLayout.setError(getApplication().getResources().getString(R.string.error_pwd_not_correct));
            }
            progressBar.setVisibility(View.GONE);
        });
        return user;
    }

    private boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

}
