package com.example.takeanote;

import android.app.Application;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


public class ImageActivityViewModel extends AndroidViewModel {

    FirebaseStorage storage;
    private final FirebaseFirestore db;
    FirebaseUser user;
    private final String userUID;
    StorageReference storageReference;
    private MutableLiveData<Uri> newUri;
    public static Uri ImageUri;
    public static String filePath;


    public ImageActivityViewModel(@NonNull Application application) {
        super( application );
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        newUri = new MutableLiveData<>();
        userUID = FirebaseAuth.getInstance().getCurrentUser().getUid();

    }

    public void uploadImage(ProgressDialog progressDialog, String paintTitle) {
        filePath = "content://media/external/images/media/" + UUID.randomUUID().toString() +"/";

        if (filePath != null) {
            progressDialog.setTitle( "Uploading..." );
            progressDialog.show();
            String saveUrl = "images/" + userUID + "/" + UUID.randomUUID().toString() + ".jpg";

            // Guardem la nota a firebase per
            DocumentReference docref = db.collection( "notes" ).document( userUID ).collection( "imageNotes" ).document();
            Map<String, Object> newNote = new HashMap<>();
            newNote.put( "title", paintTitle );
            newNote.put( "url", saveUrl );

            docref.set( newNote ).addOnSuccessListener( new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d( "PAVM", "ONSUCCESS docref.setNote" );
                }
            } ).addOnFailureListener( new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d( "PAVM", "FAILURE docref.setNote" );
                }
            });

            ImageUri = Uri.parse(filePath);

            StorageReference ref = storageReference.child( saveUrl );

            ref.putFile( Uri.parse( filePath ) )
                    .addOnSuccessListener( new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText( getApplication().getApplicationContext(), "Uploaded", Toast.LENGTH_SHORT ).show();
                        }
                    } )
                    .addOnFailureListener( new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText( getApplication().getApplicationContext(), "Failed " + e.getMessage(), Toast.LENGTH_SHORT ).show();
                        }
                    } )
                    .addOnProgressListener( new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage( "Uploaded " + (int) progress + "%" );
                        }
                    } );

        }
    }



    public LiveData<Uri> uploadFile(Uri mImageUri, String extension, ProgressDialog progressDialog, String title) {
        ImageUri = mImageUri;
        if (mImageUri != null) {
            progressDialog.setTitle( "Uploading..." );
            progressDialog.show();
            //StorageReference ref = storageReference.child( "images/" + user.getUid() + "/" + UUID.randomUUID().toString() + "." + extension );

            String saveUrl = "images/" + userUID + "/" + UUID.randomUUID().toString() + ".jpg";
            DocumentReference docref = db.collection( "notes" ).document( userUID ).collection( "imageNotes" ).document();
            Map<String, Object> newNote = new HashMap<>();
            newNote.put( "title", title );
            newNote.put( "url", saveUrl );

            docref.set( newNote ).addOnSuccessListener( new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d( "PAVM", "ONSUCCESS docref.setNote" );
                }
            } ).addOnFailureListener( new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d( "PAVM", "FAILURE docref.setNote" );
                }
            });

            StorageReference ref = storageReference.child( saveUrl );

            ref.putFile( mImageUri )
                    .addOnSuccessListener( new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            newUri.setValue( mImageUri );
                            Toast.makeText( getApplication().getApplicationContext(), "Uploaded", Toast.LENGTH_SHORT ).show();
                        }
                    } )
                    .addOnFailureListener( new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText( getApplication().getApplicationContext(), "Failed " + e.getMessage(), Toast.LENGTH_SHORT ).show();
                        }
                    } )
                    .addOnProgressListener( new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                            double progress = (100.0 * snapshot.getBytesTransferred() / snapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage( "Uploaded " + (int) progress + "%" );
                        }
                    } );

        } else {
            Toast.makeText( getApplication().getApplicationContext(), "No file selected!", Toast.LENGTH_SHORT ).show();
        }

        return newUri;
    }

}
