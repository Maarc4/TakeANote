package com.example.takeanote;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.telephony.SubscriptionPlan;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class LoadScreen extends AppCompatActivity {
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_screen);

        auth = FirebaseAuth.getInstance();

        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed( new Runnable()  {

            @Override
            public void run() {
                // check if user is logged in
                if(auth.getCurrentUser() != null){
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    finish();
                }else{
                    // create new anonymous acount
                    auth.signInAnonymously().addOnSuccessListener( new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            Toast.makeText( LoadScreen.this, "Logged in With Temporary Account.", Toast.LENGTH_SHORT ).show();
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            finish();
                        }
                    } ).addOnFailureListener( new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText( LoadScreen.this, "Error ! " + e.getMessage(), Toast.LENGTH_SHORT ).show();
                            finish();
                        }
                    } );
                }

            }
        },2000);
    }
}