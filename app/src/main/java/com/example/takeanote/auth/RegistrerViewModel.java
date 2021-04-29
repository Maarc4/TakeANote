package com.example.takeanote.auth;

import android.app.Activity;
import android.app.Application;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

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

import java.util.regex.Pattern;


public class RegistrerViewModel extends AndroidViewModel {

    FirebaseAuth auth;
    private MutableLiveData<Boolean> registrat;

    public RegistrerViewModel(@NonNull Application application) {
        super( application );
        auth = FirebaseAuth.getInstance();
        registrat = new MutableLiveData<>();
    }

    public LiveData<Boolean> register(TextInputEditText name, TextInputEditText email,
                                      TextInputEditText pwd, TextInputEditText  pwdConf,
                                      TextInputLayout emailLayout, TextInputLayout pwdConfLayout,
                                      TextInputLayout pwdLayout, TextInputLayout nameLayout){

        String userName = name.getText().toString();
        String userEmail = email.getText().toString();
        String userPass = pwd.getText().toString();
        String userConfPass = pwdConf.getText().toString();
        int errors = 0;
        nameLayout.setError( null );
        emailLayout.setError( null );
        pwdLayout.setError( null );
        pwdConfLayout.setError( null );
        // Comprovar i setejar errors
        if (userName.isEmpty()) {
            nameLayout.setError( "Cannot be empty." );
            errors++;
        }
        //TODO: fer que error i toggle pwd no es sobreposin o posar un on text changed o algo aixi
        if (userPass.isEmpty()) {
            pwdLayout.setError( "Cannot be empty." );
            errors++;
        }
        else if (!isPwdSecure( userPass )) {
            pwdLayout.setError( "Password not secure enought. Must contain at least 8 characters," +
                    " one upercase letter, one lower case letter, one digit and no blank space" );
        }
        if (userConfPass.isEmpty()) {
            pwdConfLayout.setError( "Cannot be empty." );
            errors++;
        } else if (!userPass.equals( userConfPass )) {
            pwdConfLayout.setError( "Passwords do not match" );
            errors++;
        }
        if (userEmail.isEmpty()){
            emailLayout.setError( "Cannot be empty." );
            errors++;
        } else if (!isEmailValid(userEmail)) {
            emailLayout.setError("Email not valid");
            errors++;
        }
        if (errors != 0) {
            //Toast.makeText(Register.this, "ESTA PASANT AMB ERROR REGISTER", Toast.LENGTH_SHORT).show();
            return registrat;
        }

        AuthCredential credential = EmailAuthProvider.getCredential(userEmail, userPass);
        auth.getCurrentUser().linkWithCredential(credential).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                Toast.makeText(getApplication().getApplicationContext(), "Notes are Syncced", Toast.LENGTH_SHORT).show();
                //activity.startActivity(new Intent(activity.getApplicationContext(), MainActivity.class));
                FirebaseUser usr = auth.getCurrentUser();
                UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                        .setDisplayName(userName)
                        .build();
                usr.updateProfile(request);
                registrat.setValue( true );
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //TODO: canviar per comprovar pwd abans
                if (e.getMessage().contains("email")) emailLayout.setError(e.getMessage());
                //if (e.getMessage().contains("password")) pwdLayout.setError(e.getMessage());
            }
        });
        return registrat;
    }

    private boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isPwdSecure(String pwd){
        Pattern password = Pattern.compile("^" +
                "(?=.*[0-9])" +         //at least 1 digit
                "(?=.*[a-z])" +         //at least 1 lower case letter
                "(?=.*[A-Z])" +         //at least 1 upper case letter
                "(?=.*[a-zA-Z])" +      //any letter
                //"(?=.*[@#$%^&+=])" +    //at least 1 special character
                "(?=\\S+$)" +           //no white spaces
                ".{8,}" +               //at least 8 characters
                "$");
        return password.matcher( pwd ).matches();
    }

}
