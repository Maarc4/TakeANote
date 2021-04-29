package com.example.takeanote;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoadScreenViewModel extends AndroidViewModel {

    FirebaseAuth auth;

    private MutableLiveData<FirebaseAuth> tempAuth;

    public LoadScreenViewModel(@NonNull Application application) {
        super( application );
        tempAuth = new MutableLiveData<>();
    }


    public LiveData<FirebaseAuth> login() {
        this.auth = FirebaseAuth.getInstance();

        auth = FirebaseAuth.getInstance();

        Handler handler = new Handler(Looper.getMainLooper());
        FirebaseAuth finalAuth = auth;
        handler.postDelayed(new Runnable() {

            @Override
            public void run() {
                // check if user is logged in
                if (finalAuth.getCurrentUser() != null) {
                    tempAuth.setValue(finalAuth);
                } else {
                    // create new anonymous acount
                    finalAuth.signInAnonymously().addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            tempAuth.setValue(finalAuth);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplication().getApplicationContext(), "Error ! " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }

            }
        }, 2000);

        return tempAuth;
    }

    public FirebaseUser getUser() {
        return auth.getCurrentUser();
    }

}
