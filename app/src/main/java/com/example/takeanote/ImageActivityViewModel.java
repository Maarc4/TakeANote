package com.example.takeanote;

import android.app.Application;
import android.app.ProgressDialog;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ImageActivityViewModel extends AndroidViewModel {

    private final FirebaseFirestore db;
    private final String userUID;
    private final StorageReference storageReference;
    private final MutableLiveData<Uri> newUri;
    public static Uri ImageUri;
    public static String filePath;


    public ImageActivityViewModel(@NonNull Application application) {
        super(application);
        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        db = FirebaseFirestore.getInstance();
        newUri = new MutableLiveData<>();
        userUID = FirebaseAuth.getInstance().getCurrentUser().getUid();

    }

    public void uploadImage(ProgressDialog progressDialog, String paintTitle) {
        filePath = "content://media/external/images/media/" + UUID.randomUUID().toString() + "/";

        if (filePath != null) {
            progressDialog.setTitle(getApplication().getResources().getString(R.string.uploading));
            progressDialog.show();
            String saveUrl = getApplication().getResources().getString(R.string.image_type) + userUID + "/" + UUID.randomUUID().toString() + getApplication().getResources().getString(R.string.suffix_jpg);

            // Guardem la nota a firebase per
            DocumentReference docref = db.collection("notes").document(userUID).collection("imageNotes").document();
            Map<String, Object> newNote = new HashMap<>();
            newNote.put("title", paintTitle);
            newNote.put("url", saveUrl);

            docref.set(newNote).addOnSuccessListener(aVoid -> Log.d("PAVM", "ONSUCCESS docref.setNote"))
                    .addOnFailureListener(e -> Log.d("PAVM", "FAILURE docref.setNote"));

            ImageUri = Uri.parse(filePath);

            StorageReference ref = storageReference.child(saveUrl);

            ref.putFile(Uri.parse(filePath))
                    .addOnSuccessListener(taskSnapshot -> {
                        progressDialog.dismiss();
                        Toast.makeText(getApplication().getApplicationContext(), getApplication().getResources().getString(R.string.toast_uploaded), Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        progressDialog.dismiss();
                        Toast.makeText(getApplication().getApplicationContext(), getApplication().getResources().getString(R.string.toast_failed) + e.getMessage(), Toast.LENGTH_SHORT).show();
                    })
                    .addOnProgressListener(taskSnapshot -> {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                                .getTotalByteCount());
                        progressDialog.setMessage(getApplication().getResources().getString(R.string.toast_uploaded) + (int) progress + "%");
                    });

        }
    }


    public LiveData<Uri> uploadFile(Uri mImageUri, String extension, ProgressDialog progressDialog, String title) {
        ImageUri = mImageUri;
        if (mImageUri != null) {
            progressDialog.setTitle(getApplication().getResources().getString(R.string.uploading));
            progressDialog.show();
            String saveUrl = getApplication().getResources().getString(R.string.image_type) + userUID + "/" + UUID.randomUUID().toString() + getApplication().getResources().getString(R.string.suffix_jpg);
            DocumentReference docref = db.collection("notes").document(userUID).collection("imageNotes").document();
            Map<String, Object> newNote = new HashMap<>();
            newNote.put("title", title);
            newNote.put("url", saveUrl);

            docref.set(newNote).addOnSuccessListener(aVoid -> Log.d("PAVM", "ONSUCCESS docref.setNote"))
                    .addOnFailureListener(e -> Log.d("PAVM", "FAILURE docref.setNote"));

            StorageReference ref = storageReference.child(saveUrl);

            ref.putFile(mImageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        progressDialog.dismiss();
                        newUri.setValue(mImageUri);
                        Toast.makeText(getApplication().getApplicationContext(), getApplication().getResources().getString(R.string.toast_uploaded), Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        progressDialog.dismiss();
                        Toast.makeText(getApplication().getApplicationContext(), getApplication().getResources().getString(R.string.toast_failed) + e.getMessage(), Toast.LENGTH_SHORT).show();
                    })
                    .addOnProgressListener(snapshot -> {
                        double progress = (100.0 * snapshot.getBytesTransferred() / snapshot
                                .getTotalByteCount());
                        progressDialog.setMessage(getApplication().getResources().getString(R.string.uploading) + (int) progress + "%");
                    });

        } else {
            Toast.makeText(getApplication().getApplicationContext(), getApplication().getResources().getString(R.string.toast_file_not_selected), Toast.LENGTH_SHORT).show();
        }

        return newUri;
    }

}
