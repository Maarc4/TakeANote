package com.example.takeanote;

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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PaintActivityViewModel extends AndroidViewModel {

    private FirebaseStorage storage;
    private final FirebaseFirestore db;
    private StorageReference storageReference;
    private final String userUID;
    public static String filePath;
    private final String STORAGE_URL = "gs://takeanote-a0e9a.appspot.com/";
    public static Uri ImageUri;


    private final MutableLiveData<String> observableFilePath;

    public PaintActivityViewModel(@NonNull Application application) {
        super( application );
        observableFilePath = new MutableLiveData<>();
        db = FirebaseFirestore.getInstance();
        userUID = FirebaseAuth.getInstance().getCurrentUser().getUid();

    }

    public void uploadImage(ProgressDialog progressDialog, String paintTitle) {

        if (filePath != null) {

                progressDialog.setTitle("Uploading...");
                progressDialog.show();
                String saveUrl = "images/" + userUID + "/" + UUID.randomUUID().toString() + ".jpg";

                // Guardem la nota a firebase per
                DocumentReference docref = db.collection("notes").document(userUID).collection("paintNotes").document();
                Map<String, Object> newNote = new HashMap<>();
                newNote.put("title", paintTitle);
                newNote.put("url", saveUrl);
                newNote.put("filepath", filePath);

                docref.set(newNote).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("PAVM", "ONSUCCESS docref.setNote");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("PAVM", "FAILURE docref.setNote");
                    }
                });

                ImageUri = Uri.parse(filePath);

                StorageReference ref = storageReference.child(saveUrl);


                ref.putFile(Uri.parse(filePath))
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
        storageReference = storage.getReferenceFromUrl( STORAGE_URL );


        //AIXI VA PERO DEPRACATED
        paintView.setDrawingCacheEnabled( true );
        String imgSaved = MediaStore.Images.Media.insertImage(
                getApplication().getContentResolver(), paintView.getDrawingCache(),
                UUID.randomUUID().toString() + ".jpg", "drawing" );


        if (imgSaved != null) {
            filePath = imgSaved;
            observableFilePath.setValue( imgSaved );
            //Toast.makeText( getApplication().getApplicationContext(), "IMAGE SAVED: " + imgSaved, Toast.LENGTH_SHORT ).show();
        } else {
            Toast.makeText( getApplication().getApplicationContext(), "ERROR NOT SAVED", Toast.LENGTH_SHORT ).show();
        }
        paintView.destroyDrawingCache();
        return observableFilePath;
    }

}
