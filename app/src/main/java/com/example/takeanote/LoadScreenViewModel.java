package com.example.takeanote;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoadScreenViewModel extends ViewModel {

    FirebaseAuth auth;
    Activity activity;

    private MutableLiveData<FirebaseAuth> tempAuth;

    public LoadScreenViewModel(){
        tempAuth = new MutableLiveData<>();
    }

    public LiveData<FirebaseAuth> login(Activity activity, FirebaseAuth auth){
        this.auth = auth;
        this.activity = activity;

        auth = FirebaseAuth.getInstance();

        Handler handler = new Handler( Looper.getMainLooper());
        FirebaseAuth finalAuth = auth;
        handler.postDelayed( new Runnable()  {

            @Override
            public void run() {
                // check if user is logged in
                if(finalAuth.getCurrentUser() != null){
                    activity.startActivity(new Intent(activity.getApplicationContext(), MainActivity.class));
                    activity.finish();
                }else{
                    // create new anonymous acount
                    finalAuth.signInAnonymously().addOnSuccessListener( new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            tempAuth.setValue( finalAuth );
                        }
                    } ).addOnFailureListener( new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText( activity.getApplicationContext(), "Error ! " + e.getMessage(), Toast.LENGTH_SHORT ).show();
                            activity.finish();
                        }
                    } );
                }

            }
        },2000);

        return tempAuth;
    }

}
