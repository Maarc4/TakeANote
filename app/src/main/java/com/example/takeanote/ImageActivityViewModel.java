package com.example.takeanote;

import android.app.Application;
import android.app.ProgressDialog;
import android.net.Uri;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;

public class ImageActivityViewModel extends AndroidViewModel {

    FirebaseStorage storage;
    FirebaseUser user;
    StorageReference storageReference;
    private MutableLiveData<Uri> newUri;

    public ImageActivityViewModel(@NonNull Application application) {
        super( application );
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        user = FirebaseAuth.getInstance().getCurrentUser();
        newUri = new MutableLiveData<>();
    }


    public LiveData<Uri> uploadFile(Uri mImageUri, String extension, ProgressDialog progressDialog) {
        if (mImageUri != null) {
            progressDialog.setTitle( "Uploading..." );
            progressDialog.show();
            StorageReference ref = storageReference.child( "images/" + user.getUid() + "/" + UUID.randomUUID().toString() + "." + extension );

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
