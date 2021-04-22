package com.example.takeanote.auth;

public class RegistrerViewModel {


    boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}
