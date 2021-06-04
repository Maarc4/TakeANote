package com.example.takeanote;

import android.app.Application;
import android.app.ProgressDialog;
import android.net.Uri;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AddAudioViewModel extends AndroidViewModel {

    FirebaseStorage storage;
    StorageReference storageReference;
    FirebaseFirestore db;
    String userid;
    private MutableLiveData<String> newAudio;

    public AddAudioViewModel(@NonNull Application application) {
        super( application );
        this.storage = FirebaseStorage.getInstance();
        this.userid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        storageReference = storage.getReference();
        db = FirebaseFirestore.getInstance();
        newAudio = new MutableLiveData<>();
    }

    public LiveData<String> uploadAudio(String fileName, String title) {

        String saveUrl = "audios/" + userid + "/" + UUID.randomUUID().toString() + ".3gp";
        DocumentReference docref = db.collection( "notes" ).document( userid ).collection( "AudioNotes" ).document();
        StorageReference filepath = storageReference.child( saveUrl );
        Uri uri = Uri.fromFile( new File( fileName ) );
        Map<String, Object> newNote = new HashMap<>();
        newNote.put( "title", title );
        newNote.put( "url", saveUrl );

        docref.set( newNote ).addOnSuccessListener( new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                newAudio.setValue( fileName );
                Log.d( "PAVM", "ONSUCCESS docref.setNote" );
            }
        } ).addOnFailureListener( new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d( "PAVM", "FAILURE docref.setNote" );
            }
        } );

        filepath.putFile( uri ).addOnSuccessListener( new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(getApplication().getApplicationContext(), getApplication().getResources().getString( R.string.audio_saved), Toast.LENGTH_SHORT).show();
            }
        } );
        return newAudio;
    }
}
