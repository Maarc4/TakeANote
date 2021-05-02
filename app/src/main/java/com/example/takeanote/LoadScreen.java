package com.example.takeanote;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.auth.FirebaseAuth;


public class LoadScreen extends AppCompatActivity {

    private LoadScreenViewModel viewModel;

    private void setUpViewModel() {
        viewModel = new ViewModelProvider( this ).get( LoadScreenViewModel.class );
        viewModel.login().observe( this, new Observer<FirebaseAuth>() {
            @Override
            public void onChanged(FirebaseAuth firebaseAuth) {
                if (viewModel.getUser().isAnonymous()) {
                    Toast.makeText( LoadScreen.this, "Logged in With Temporary Account.", Toast.LENGTH_LONG ).show();
                } else {
                    Toast.makeText( LoadScreen.this, "Wellcome back " + viewModel.getUser().getDisplayName() + "!!", Toast.LENGTH_LONG ).show();
                }

                startActivity( new Intent( getApplicationContext(), MainActivity.class ) );
                finish();
            }
        } );
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_load_screen );
        setUpViewModel();
    }
}