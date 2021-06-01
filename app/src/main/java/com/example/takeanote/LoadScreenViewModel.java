package com.example.takeanote;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoadScreenViewModel extends AndroidViewModel {

    private FirebaseAuth auth;
    private final MutableLiveData<FirebaseAuth> tempAuth;

    public LoadScreenViewModel(@NonNull Application application) {
        super(application);
        tempAuth = new MutableLiveData<>();
    }


    public LiveData<FirebaseAuth> login() {
        this.auth = FirebaseAuth.getInstance();
        auth = FirebaseAuth.getInstance();

        Handler handler = new Handler(Looper.getMainLooper());
        FirebaseAuth finalAuth = auth;
        handler.postDelayed(() -> {
            // check if user is logged in
            if (finalAuth.getCurrentUser() != null) {
                tempAuth.setValue(finalAuth);
            } else {
                // create new anonymous acount
                finalAuth.signInAnonymously().addOnSuccessListener(authResult -> tempAuth.setValue(finalAuth))
                        .addOnFailureListener(e -> Toast.makeText(getApplication().getApplicationContext(), getApplication().getResources().getString(R.string.toast_error) + e.getMessage(), Toast.LENGTH_SHORT).show());
            }

        }, 2000);

        return tempAuth;
    }

    public FirebaseUser getUser() {
        return auth.getCurrentUser();
    }

}
