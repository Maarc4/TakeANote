package com.example.takeanote;

import android.app.Activity;
import android.app.Application;
import android.app.ProgressDialog;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.nio.charset.MalformedInputException;
import java.util.UUID;

public class PaintActivityViewModel extends AndroidViewModel {

    FirebaseStorage storage;
    StorageReference storageReference;
    String user;
    String filePath;

    private MutableLiveData<String> observableFilePath;

    public PaintActivityViewModel(@NonNull Application application) {
        super( application );
        observableFilePath = new MutableLiveData<>();
    }

    // TODO Treure comentaris loco
    public void uploadImage() {

        if (filePath != null) {
            final ProgressDialog progressDialog = new ProgressDialog( getApplication() );
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            StorageReference ref = storageReference.child("images/" + user + "/" + UUID.randomUUID().toString() + ".jpg");
            Log.d("STATE","FILEPATHHHHHHHHHHHH: " + filePath);
            Log.d("STATE","REFFFFFFFFFFFFFFFFFFFFF: " + ref);

            ref.putFile( Uri.parse(filePath))
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(getApplication().getApplicationContext(), "Uploaded", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(getApplication().getApplicationContext(), "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded " + (int) progress + "%");
                        }
                    });
        }
    }

    //TODO: mirar de ferho sense depracated method
    public LiveData<String> saveView(PaintView paintView) {

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        user = FirebaseAuth.getInstance().getCurrentUser().getUid();

        //AIXI VA PERO DEPRACATED
        paintView.setDrawingCacheEnabled(true);
        String imgSaved = MediaStore.Images.Media.insertImage(
                getApplication().getContentResolver(), paintView.getDrawingCache(),
                UUID.randomUUID().toString() + ".jpg", "drawing");

        //Guillem
        filePath = imgSaved;

        if (imgSaved != null) {
            observableFilePath.setValue( imgSaved );
            Toast.makeText(getApplication().getApplicationContext(), "IMAGE SAVED: " + imgSaved, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplication().getApplicationContext(), "ERROR NOT SAVED", Toast.LENGTH_SHORT).show();
        }
        paintView.destroyDrawingCache();
        return observableFilePath;
    }

}
